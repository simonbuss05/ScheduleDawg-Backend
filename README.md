# ScheduleDawg — Backend

A Spring Boot API for a class-schedule and grade-tracking app built for UGA
students. It's the backend half of [ScheduleDawg](https://schedule-dawg-frontend.vercel.app);
the frontend lives in a separate repo at
[scheduledawg-frontend](https://github.com/simonbuss05/scheduledawg-frontend).

Deployed on [Railway](https://railway.app) (Spring Boot + Postgres). Live API:
`https://scheduledawg-backend-production.up.railway.app`

## What it does

- **Courses, meetings, assignments, events** — standard CRUD, scoped per user
  and per semester. A course has weekly meeting times (day/time/building),
  assignments and events with due dates, and belongs to one semester.
- **Grades** — weighted grade categories, a letter-grade scale, and
  individual graded items (by points or percent). The current grade and a
  "what do I need on everything remaining" target calculator are computed
  from these.
- **Syllabus upload + AI grading extraction** — upload a syllabus PDF and
  Claude (Anthropic API, `claude-haiku-4-5`) reads it and extracts the
  grading categories and letter scale automatically. Extraction results are
  cached by a hash of the PDF's text, so re-uploading (or two different
  users uploading) the same document is free after the first pass.
- **Plan Ahead** — before registering for a future semester, look up a
  course by subject/number and see who's currently teaching it, according to
  UGA's public course bulletin (`bulletin.uga.edu`), with a link to that
  instructor's syllabus (parsed the same way as above) and a link out to
  their RateMyProfessors page. Scraped bulletin data is cached globally
  (it's public data, not user data) with a TTL, not scraped per request.
- **Campus building autocomplete + walking directions** — building name
  lookups are proxied through this backend to OpenStreetMap's Overpass API
  (with a descriptive User-Agent, cached for 30 days) rather than called
  directly from the browser, since Overpass doesn't reliably send CORS
  headers for arbitrary origins. Geocoding and walking-route directions
  themselves are called client-side against Mapbox.
- **Auth** — email/password with BCrypt, JWTs (30-day expiry), password
  reset via emailed one-time tokens (Resend), and full account deletion.
- **Semesters** — each account always has one active semester; "end
  semester and start new" archives the old one without deleting anything —
  past semesters and their courses stay fully browsable.

## Architecture notes

A few decisions worth calling out if you're reading this as a portfolio
piece rather than just running it:

- **Migrations are explicit, not `ddl-auto`.** Schema changes live in
  `src/main/resources/db/migration` as versioned Flyway SQL, applied
  explicitly in `ScheduleDawgApplication.main()` before the Spring context
  starts (Spring Boot 4.1's autoconfigure module dropped built-in Flyway
  wiring). `spring.jpa.hibernate.ddl-auto=validate` — Hibernate can check the
  schema matches what the entities expect, but never mutate it.
- **Cascade deletes are enforced at the database, not just in JPA.**
  Deleting a course or a whole account needs to clean up a fairly deep
  object graph (meetings, assignments, events, grade categories, grade
  scale, graded items, syllabi, settings, plan-ahead rows, reset tokens).
  Rather than hand-writing that cleanup order in every service method
  (fragile, easy to miss a table when a new one is added), every relevant
  foreign key has `ON DELETE CASCADE` — see
  `V5__cascade_deletes.sql`, which looks up each constraint by table/column
  via `information_schema` rather than assuming a name, since a
  Hibernate-`ddl-auto`-managed database (this one, originally) and a fresh
  one created straight from `V1__baseline.sql` don't necessarily generate
  the same constraint names.
- **Rate limiting is in-memory** (`RateLimiterService`), keyed per
  IP/email/user on the auth and scraping endpoints specifically — login,
  register, forgot-password, and bulletin scraping all have separate
  limits. It's a sliding-window counter in a `ConcurrentHashMap`; fine for a
  single instance, and would need a shared store (Redis) behind a load
  balancer.
- **Third-party calls are proxied server-side, not called from the
  browser**, wherever the provider doesn't support arbitrary browser
  origins — Overpass and UGA's bulletin. Mapbox is the exception, since its
  public tokens are designed to be used client-side.
- **Forgot-password responses don't leak whether an email is registered.**
  `POST /api/auth/forgot-password` always returns `204` and only sends an
  email if the address matches an account; the token is single-use and
  expires in 30 minutes.

## Stack

Java 21 · Spring Boot 4.1 (Web MVC, Security, Data JPA) · PostgreSQL ·
Flyway · JWT (`jjwt`) · BCrypt · Apache PDFBox (syllabus text extraction) ·
Jsoup (bulletin scraping) · Anthropic API (grading-schema extraction)

## Running locally

Requires Java 21, Maven (or use the bundled `./mvnw`), and a local Postgres.

```bash
createdb scheduledawg
```

Copy `.env.example` and set the required values (or export them directly —
this app reads real environment variables, there's no dotenv loader):

```bash
cp .env.example .env   # for reference; export the values into your shell
```

At minimum you need `JWT_SECRET` (`openssl rand -base64 32`) and
`ANTHROPIC_API_KEY` — the app won't boot without them. `RESEND_API_KEY` is
optional; without it, password-reset requests still succeed but the email
send is skipped (logged, not thrown).

```bash
export JWT_SECRET=$(openssl rand -base64 32)
export ANTHROPIC_API_KEY=your-key-here
./mvnw spring-boot:run
```

The API comes up on `http://localhost:8080`, with Flyway migrations applied
automatically on startup.

## Project structure

```
src/main/java/com/simon/scheduledawg/
├── controller/    REST endpoints, one per resource
├── service/       business logic, ownership checks, external API calls
├── entity/        JPA entities
├── repository/    Spring Data JPA repositories
├── dto/           request/response bodies
├── security/      JWT filter, SecurityConfig, JwtService
├── exception/      custom exceptions + GlobalExceptionHandler
└── config/        RestClient bean, CORS config
src/main/resources/
├── application.properties
└── db/migration/  versioned Flyway SQL
```
