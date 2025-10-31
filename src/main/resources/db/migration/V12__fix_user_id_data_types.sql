-- Fix data type inconsistencies for user_id fields
-- Change BIGINT to INT to match app_user.user_id type

ALTER TABLE trip_participant ALTER COLUMN user_id TYPE INT;
ALTER TABLE expense_share ALTER COLUMN user_id TYPE INT;
