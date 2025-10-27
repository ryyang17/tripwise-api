
-- Add the new date column
ALTER TABLE activity ADD COLUMN date DATE;

-- Add index for better performance when querying by trip_id and date
CREATE INDEX idx_activity_trip_date ON activity(trip_id, date);

