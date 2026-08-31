-- Events now belong to a Course, not a Meeting (an event no longer has to
-- fall on a day the course happens to meet). This drops the column the old
-- model left behind.
ALTER TABLE events DROP CONSTRAINT IF EXISTS fkamjw9d0n1sdfmau2jfsayy6ka;
ALTER TABLE events DROP COLUMN IF EXISTS meeting_id;
