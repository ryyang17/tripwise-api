-- Add paid_by column to expense table
ALTER TABLE expense ADD COLUMN paid_by BIGINT;

-- Create trip_participant table
CREATE TABLE trip_participant (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    trip_id INT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(50),
    joined_date TIMESTAMP,
    CONSTRAINT fk_trip FOREIGN KEY (trip_id) REFERENCES trip(trip_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES app_user(user_id)
);

-- Create expense_share table
CREATE TABLE expense_share (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    expense_id INT NOT NULL,
    user_id BIGINT NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    is_settled BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_expense FOREIGN KEY (expense_id) REFERENCES expense(expense_id),
    CONSTRAINT fk_user_share FOREIGN KEY (user_id) REFERENCES app_user(user_id)
);
