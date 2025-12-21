-- Add trip_id column to booking table to link bookings to trips
ALTER TABLE booking ADD COLUMN trip_id BIGINT;

-- Add foreign key constraint
ALTER TABLE booking ADD CONSTRAINT fk_booking_trip FOREIGN KEY (trip_id) REFERENCES trip(trip_id) ON DELETE CASCADE;

-- Create index for faster lookups by trip
CREATE INDEX idx_booking_trip_id ON booking(trip_id);

