-- Add currency support fields to expense table
ALTER TABLE expense
ADD COLUMN original_amount DECIMAL(19,2),
ADD COLUMN currency VARCHAR(3) DEFAULT 'USD',
ADD COLUMN exchange_rate DECIMAL(19,6) DEFAULT 1.0;

-- Update existing records to have original_amount = amount and default values
UPDATE expense
SET original_amount = amount,
    currency = 'USD',
    exchange_rate = 1.0
WHERE original_amount IS NULL;

-- Make original_amount NOT NULL after setting values
ALTER TABLE expense
ALTER COLUMN original_amount SET NOT NULL;
