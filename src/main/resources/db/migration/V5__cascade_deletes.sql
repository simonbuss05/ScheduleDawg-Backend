-- Adds ON DELETE CASCADE to every foreign key that hangs (directly or
-- transitively) off app_users, so deleting a user or a course cleans up
-- everything underneath it instead of failing with a FK violation.
--
-- This fixes a live bug: deleting a course that has grade categories, a
-- grading scale, or a syllabus attached returns a 500, because those tables
-- reference courses(id) with no cascade anywhere — Course's JPA-level
-- cascade only covers meetings/assignments/events, and the DB itself never
-- had ON DELETE CASCADE to fall back on. It also makes account deletion
-- possible as a single `DELETE FROM app_users`, with Postgres cascading the
-- rest of the tree.
--
-- Constraint names are looked up by table/column via the catalog rather than
-- assumed, since the original production database (created by Hibernate's
-- ddl-auto=update before Flyway existed) has hash-style constraint names,
-- while a fresh database created by running V1's plain CREATE TABLE gets
-- Postgres's default <table>_<column>_fkey names.

DO $$
DECLARE
  fk RECORD;
  spec RECORD;
BEGIN
  FOR spec IN
    SELECT * FROM (VALUES
      ('semesters', 'user_id', 'app_users', 'id'),
      ('courses', 'user_id', 'app_users', 'id'),
      ('courses', 'semester_id', 'semesters', 'id'),
      ('meetings', 'course_id', 'courses', 'id'),
      ('assignments', 'course_id', 'courses', 'id'),
      ('events', 'course_id', 'courses', 'id'),
      ('finals', 'course_id', 'courses', 'id'),
      ('grade_categories', 'course_id', 'courses', 'id'),
      ('grade_scale_entries', 'course_id', 'courses', 'id'),
      ('graded_items', 'category_id', 'grade_categories', 'id'),
      ('syllabuses', 'course_id', 'courses', 'id'),
      ('user_settings', 'user_id', 'app_users', 'id'),
      ('planned_courses', 'user_id', 'app_users', 'id'),
      ('password_reset_tokens', 'user_id', 'app_users', 'id')
    ) AS t(table_name, column_name, ref_table, ref_column)
  LOOP
    FOR fk IN
      SELECT tc.constraint_name
      FROM information_schema.table_constraints tc
      JOIN information_schema.key_column_usage kcu
        ON tc.constraint_name = kcu.constraint_name
       AND tc.table_schema = kcu.table_schema
      WHERE tc.constraint_type = 'FOREIGN KEY'
        AND tc.table_schema = 'public'
        AND tc.table_name = spec.table_name
        AND kcu.column_name = spec.column_name
    LOOP
      EXECUTE format('ALTER TABLE %I DROP CONSTRAINT %I', spec.table_name, fk.constraint_name);
    END LOOP;

    EXECUTE format(
      'ALTER TABLE %I ADD CONSTRAINT %I FOREIGN KEY (%I) REFERENCES %I(%I) ON DELETE CASCADE',
      spec.table_name,
      spec.table_name || '_' || spec.column_name || '_fkey',
      spec.column_name,
      spec.ref_table,
      spec.ref_column
    );
  END LOOP;
END $$;
