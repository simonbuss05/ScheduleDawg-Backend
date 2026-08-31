package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.ExternalCourse;
import com.simon.scheduledawg.entity.ExternalCourseInstructor;
import com.simon.scheduledawg.entity.ExternalSyllabus;
import com.simon.scheduledawg.exception.ResourceNotFoundException;
import com.simon.scheduledawg.repository.ExternalCourseInstructorRepository;
import com.simon.scheduledawg.repository.ExternalCourseRepository;
import com.simon.scheduledawg.repository.ExternalSyllabusRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Scrapes the public UGA course bulletin (bulletin.uga.edu) for which
// instructors currently have a syllabus on file for a given course, and lets
// the caller download that syllabus. No login is required for any of this —
// verified directly against the live site. Results are cached in the
// external_* tables, shared across every user, with a simple TTL.
@Service
public class BulletinScraperService {

    private static final String BASE_URL = "https://bulletin.uga.edu";
    private static final long CACHE_TTL_DAYS = 14;
    private static final Pattern DETAILS_ID_PATTERN = Pattern.compile("/Course/Details/(\\d+)");

    private final RestClient restClient;
    private final ExternalCourseRepository externalCourseRepository;
    private final ExternalCourseInstructorRepository externalCourseInstructorRepository;
    private final ExternalSyllabusRepository externalSyllabusRepository;

    public BulletinScraperService(
            RestClient restClient,
            ExternalCourseRepository externalCourseRepository,
            ExternalCourseInstructorRepository externalCourseInstructorRepository,
            ExternalSyllabusRepository externalSyllabusRepository
    ) {
        this.restClient = restClient;
        this.externalCourseRepository = externalCourseRepository;
        this.externalCourseInstructorRepository = externalCourseInstructorRepository;
        this.externalSyllabusRepository = externalSyllabusRepository;
    }

    public ExternalCourse findOrScrapeExternalCourse(String subjectCode, String courseNumber) {
        ExternalCourse existing = externalCourseRepository
                .findBySubjectCodeIgnoreCaseAndCourseNumberIgnoreCase(subjectCode, courseNumber)
                .orElse(null);

        if (existing != null && !isStale(existing.getLastScrapedAt())) {
            return existing;
        }

        ExternalCourse scraped = scrapeCourse(subjectCode, courseNumber);
        if (existing != null) {
            scraped.setId(existing.getId());
        }
        ExternalCourse saved = externalCourseRepository.save(scraped);
        refreshInstructors(saved);
        return saved;
    }

    public List<ExternalCourseInstructor> getInstructors(ExternalCourse externalCourse) {
        List<ExternalCourseInstructor> instructors =
                externalCourseInstructorRepository.findByExternalCourseId(externalCourse.getId());
        if (instructors.isEmpty() || isStale(externalCourse.getLastScrapedAt())) {
            refreshInstructors(externalCourse);
            return externalCourseInstructorRepository.findByExternalCourseId(externalCourse.getId());
        }
        return instructors;
    }

    public ExternalCourseInstructor getInstructorById(Long id) {
        return externalCourseInstructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));
    }

    @Transactional
    public ExternalSyllabus getOrDownloadSyllabus(ExternalCourseInstructor instructor) {
        if (instructor.getSyllabusFileId() == null) {
            throw new ResourceNotFoundException("No syllabus on file for " + instructor.getInstructorName() + ".");
        }

        ExternalSyllabus syllabus = externalSyllabusRepository.findByExternalCourseInstructorId(instructor.getId())
                .orElseGet(() -> downloadAndStoreSyllabus(instructor));

        // Postgres large objects (the `oid`-backed, lazily-fetched fileData)
        // can only be streamed while this transaction's connection is open —
        // touch it now so callers get an already-materialized byte[].
        syllabus.getFileData();
        return syllabus;
    }

    private boolean isStale(LocalDateTime lastScrapedAt) {
        return lastScrapedAt == null || lastScrapedAt.isBefore(LocalDateTime.now().minus(CACHE_TTL_DAYS, ChronoUnit.DAYS));
    }

    private ExternalCourse scrapeCourse(String subjectCode, String courseNumber) {
        // The bulletin's search is case-sensitive and only matches a
        // lowercase keyword — "CSCI 1301" finds nothing, "csci 1301" does.
        String keyword = (subjectCode + " " + courseNumber).toLowerCase().replace(" ", "+");

        String html = restClient.post()
                .uri(BASE_URL + "/Course/_ViewAllCourses")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("X-Requested-With", "XMLHttpRequest")
                .body("keyword=" + keyword + "&enteredCoursePrefix=&enteredCourseNumber=")
                .retrieve()
                .body(String.class);

        Document doc = Jsoup.parse(html == null ? "" : html, BASE_URL);
        Elements cards = doc.select(".course-card");

        // The keyword search can still return near-matches (cross-listed
        // sections, etc.), so pick the exact subject+number match ourselves
        // rather than just taking the first result.
        Pattern codePattern = Pattern.compile(
                "^\\s*" + Pattern.quote(subjectCode) + "\\s+" + Pattern.quote(courseNumber) + "(?!\\d)",
                Pattern.CASE_INSENSITIVE
        );

        for (Element card : cards) {
            Element link = card.selectFirst("a.crn");
            if (link == null) continue;

            String code = link.text();
            if (!codePattern.matcher(code).find()) continue;

            Matcher idMatcher = DETAILS_ID_PATTERN.matcher(link.attr("href"));
            if (!idMatcher.find()) continue;

            Long bulletinCourseId = Long.parseLong(idMatcher.group(1));
            Element titleEl = card.selectFirst("p.large");
            String title = titleEl != null ? titleEl.text() : code;

            return new ExternalCourse(bulletinCourseId, subjectCode, courseNumber, title, LocalDateTime.now());
        }

        throw new ResourceNotFoundException(
                "Couldn't find " + subjectCode + " " + courseNumber + " on the UGA bulletin — double check the subject and number."
        );
    }

    private void refreshInstructors(ExternalCourse externalCourse) {
        String html = restClient.get()
                .uri(BASE_URL + "/Course/Details/" + externalCourse.getBulletinCourseId())
                .retrieve()
                .body(String.class);

        Document doc = Jsoup.parse(html == null ? "" : html, BASE_URL);
        Element select = doc.selectFirst("select#facultyInstructor");

        List<ExternalCourseInstructor> existing =
                externalCourseInstructorRepository.findByExternalCourseId(externalCourse.getId());

        if (select == null) {
            externalCourse.setLastScrapedAt(LocalDateTime.now());
            externalCourseRepository.save(externalCourse);
            return;
        }

        for (Element option : select.select("option")) {
            String value = option.attr("value");
            if (value.isBlank()) continue; // the "Select Faculty..." placeholder

            String name = option.text().trim();
            Long syllabusFileId = Long.parseLong(value);

            ExternalCourseInstructor instructor = existing.stream()
                    .filter(i -> i.getInstructorName().equals(name))
                    .findFirst()
                    .orElseGet(() -> new ExternalCourseInstructor(externalCourse, name, null, null));

            instructor.setSyllabusFileId(syllabusFileId);
            instructor.setLastScrapedAt(LocalDateTime.now());
            externalCourseInstructorRepository.save(instructor);
        }

        externalCourse.setLastScrapedAt(LocalDateTime.now());
        externalCourseRepository.save(externalCourse);
    }

    private ExternalSyllabus downloadAndStoreSyllabus(ExternalCourseInstructor instructor) {
        byte[] pdfBytes = restClient.get()
                .uri(BASE_URL + "/Course/DownloadSyllabusFile?ID=" + instructor.getExternalCourse().getBulletinCourseId()
                        + "&IDSyllabus=" + instructor.getSyllabusFileId())
                .retrieve()
                .body(byte[].class);

        if (pdfBytes == null || pdfBytes.length == 0) {
            throw new ResourceNotFoundException("Could not download a syllabus for " + instructor.getInstructorName() + ".");
        }

        String fileName = instructor.getInstructorName().replaceAll("[^a-zA-Z0-9]+", "_") + "_syllabus.pdf";
        ExternalSyllabus syllabus = new ExternalSyllabus(instructor, fileName, LocalDateTime.now(), pdfBytes);
        return externalSyllabusRepository.save(syllabus);
    }
}
