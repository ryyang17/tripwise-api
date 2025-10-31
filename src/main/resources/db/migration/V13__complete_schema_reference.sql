-- V13: Complete schema with all current features
-- This script creates the complete database schema as it should exist after all migrations

-- Create base tables
CREATE TABLE IF NOT EXISTS app_user (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(30) NOT NULL UNIQUE,
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL DEFAULT NOW(),
    bio VARCHAR(200),
    location VARCHAR(100),
    favorite_destination VARCHAR(100),
    travel_style VARCHAR(50),
    notifications BOOLEAN DEFAULT TRUE,
    currency VARCHAR(10) DEFAULT 'EUR'
);

CREATE TABLE IF NOT EXISTS trip (
    trip_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES app_user(user_id) ON DELETE CASCADE,
    trip_name VARCHAR(255) NOT NULL,
    start_date DATE,
    end_date DATE,
    destination VARCHAR(255),
    description TEXT,
    created_date TIMESTAMP DEFAULT NOW(),
    travel_mode VARCHAR(50),
    transportation VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS expensecategory (
    category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL UNIQUE,
    category_description VARCHAR(200),
    is_active BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS expense (
    expense_id SERIAL PRIMARY KEY,
    trip_id INT NOT NULL REFERENCES trip(trip_id) ON DELETE CASCADE,
    description TEXT,
    amount NUMERIC(12,2),
    expense_date DATE,
    created_date TIMESTAMP DEFAULT NOW(),
    receipt_url VARCHAR(255),
    is_validated BOOLEAN DEFAULT FALSE,
    category_id INT REFERENCES expensecategory(category_id) ON DELETE SET NULL,
    original_amount DECIMAL(19,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'EUR',
    exchange_rate DECIMAL(19,6) DEFAULT 1.0,
    paid_by INT REFERENCES app_user(user_id)
);

CREATE TABLE IF NOT EXISTS budget (
    budget_id SERIAL PRIMARY KEY,
    trip_id INT NOT NULL REFERENCES trip(trip_id) ON DELETE CASCADE,
    total_budget NUMERIC(12,2),
    currency VARCHAR(10),
    created_date TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS activity (
    activity_id SERIAL PRIMARY KEY,
    trip_id INT NOT NULL REFERENCES trip(trip_id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    time VARCHAR(50),
    location VARCHAR(255),
    estimated_cost NUMERIC(12,2),
    created_date TIMESTAMP DEFAULT NOW(),
    date DATE
);

CREATE TABLE IF NOT EXISTS trip_participant (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    trip_id INT NOT NULL REFERENCES trip(trip_id),
    user_id INT NOT NULL REFERENCES app_user(user_id),
    role VARCHAR(50),
    joined_date TIMESTAMP
);

CREATE TABLE IF NOT EXISTS expense_share (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    expense_id INT NOT NULL REFERENCES expense(expense_id),
    user_id INT NOT NULL REFERENCES app_user(user_id),
    amount DECIMAL(19,4) NOT NULL,
    is_settled BOOLEAN DEFAULT FALSE
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_activity_trip_date ON activity(trip_id, date);
CREATE INDEX IF NOT EXISTS idx_expense_trip_id ON expense(trip_id);
CREATE INDEX IF NOT EXISTS idx_trip_user_id ON trip(user_id);
CREATE INDEX IF NOT EXISTS idx_trip_participant_trip_user ON trip_participant(trip_id, user_id);
CREATE INDEX IF NOT EXISTS idx_expense_share_expense ON expense_share(expense_id);

-- Insert default expense categories if they don't exist
INSERT INTO expensecategory (category_name, category_description)
VALUES
    ('Food', 'Meals and dining expenses'),
    ('Transportation', 'Travel and transport costs'),
    ('Accommodation', 'Hotel and lodging expenses'),
    ('Activities', 'Entertainment and activity costs'),
    ('Shopping', 'Purchases and souvenirs'),
    ('Other', 'Miscellaneous expenses')
ON CONFLICT (category_name) DO NOTHING;
