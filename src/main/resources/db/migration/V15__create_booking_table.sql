-- Create booking table for storing flight and hotel bookings from Amadeus API
CREATE TABLE booking (
    booking_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    booking_type VARCHAR(20),
    provider VARCHAR(100),
    booking_reference VARCHAR(200),
    price NUMERIC(19, 4),
    currency VARCHAR(10),
    search_params TEXT,
    raw_payload TEXT,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES app_user(user_id) ON DELETE CASCADE
);

-- Create index for faster lookups by user
CREATE INDEX idx_booking_user_id ON booking(user_id);
CREATE INDEX idx_booking_created_date ON booking(created_date);
