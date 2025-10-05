-- Verwijder oude hoofdlettertabellen
DROP TABLE IF EXISTS "TRIP" CASCADE;
DROP TABLE IF EXISTS "USER" CASCADE;
DROP TABLE IF EXISTS "EXPENSE" CASCADE;
DROP TABLE IF EXISTS "EXPENSECATEGORY" CASCADE;
DROP TABLE IF EXISTS "BUDGET" CASCADE;
DROP TABLE IF EXISTS "ACTIVITY" CASCADE;

-- Verwijder lowercase tabellen als ze al bestaan (idempotent)
DROP TABLE IF EXISTS activity CASCADE;
DROP TABLE IF EXISTS budget CASCADE;
DROP TABLE IF EXISTS expense CASCADE;
DROP TABLE IF EXISTS expensecategory CASCADE;
DROP TABLE IF EXISTS trip CASCADE;
DROP TABLE IF EXISTS app_user CASCADE;

-- USER tabel
CREATE TABLE app_user (
                          user_id SERIAL PRIMARY KEY,
                          username VARCHAR(30) NOT NULL UNIQUE,
                          email VARCHAR(50) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL,
                          created_date TIMESTAMP NOT NULL DEFAULT NOW()
);

-- TRIP tabel
CREATE TABLE trip (
                      trip_id SERIAL PRIMARY KEY,
                      user_id INT NOT NULL REFERENCES app_user(user_id) ON DELETE CASCADE,
                      trip_name VARCHAR(255) NOT NULL,
                      start_date DATE,
                      end_date DATE,
                      destination VARCHAR(255),
                      description TEXT,
                      created_date TIMESTAMP DEFAULT NOW(),
                      is_active BOOLEAN DEFAULT TRUE
);

-- EXPENSECATEGORY tabel
CREATE TABLE expensecategory (
                                 category_id SERIAL PRIMARY KEY,
                                 category_name VARCHAR(50) NOT NULL UNIQUE,
                                 category_description VARCHAR(200),
                                 is_active BOOLEAN DEFAULT TRUE,
                                 created_date TIMESTAMP DEFAULT NOW()
);

-- EXPENSE tabel
CREATE TABLE expense (
                         expense_id SERIAL PRIMARY KEY,
                         trip_id INT NOT NULL REFERENCES trip(trip_id) ON DELETE CASCADE,
                         description TEXT,
                         amount NUMERIC(12,2),
                         category VARCHAR(50),
                         expense_date DATE,
                         created_date TIMESTAMP DEFAULT NOW(),
                         receipt_url VARCHAR(255),
                         is_validated BOOLEAN DEFAULT FALSE
);

-- BUDGET tabel
CREATE TABLE budget (
                        budget_id SERIAL PRIMARY KEY,
                        trip_id INT NOT NULL REFERENCES trip(trip_id) ON DELETE CASCADE,
                        total_budget NUMERIC(12,2),
                        currency VARCHAR(10),
                        created_date TIMESTAMP DEFAULT NOW()
);

-- ACTIVITY tabel
CREATE TABLE activity (
                          activity_id SERIAL PRIMARY KEY,
                          trip_id INT NOT NULL REFERENCES trip(trip_id) ON DELETE CASCADE,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          time VARCHAR(50),
                          location VARCHAR(255),
                          estimated_cost NUMERIC(12,2),
                          created_date TIMESTAMP DEFAULT NOW()
);
