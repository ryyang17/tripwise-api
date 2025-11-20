-- V14: Ensure activity table has activity_date column and proper index
-- This migration safely adds the activity_date column if it doesn't exist

-- Check if the activity_date column exists and add it if it doesn't
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'activity' AND column_name = 'activity_date'
    ) THEN
        ALTER TABLE activity ADD COLUMN activity_date DATE;
    END IF;
END $$;

-- Create index if it doesn't exist
CREATE INDEX IF NOT EXISTS idx_activity_trip_date ON activity(trip_id, activity_date);
