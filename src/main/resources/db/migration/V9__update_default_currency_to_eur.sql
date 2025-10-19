-- Update default currency from USD to EUR for existing records
UPDATE expense
SET currency = 'EUR'
WHERE currency = 'USD';

-- Update default currency for user table if needed
UPDATE app_user
SET currency = 'EUR'
WHERE currency = 'USD';
