-- V5: Remove is_active column from trip table
-- isActive is now a computed property based on endDate in the Trip model

ALTER TABLE trip DROP COLUMN is_active;

