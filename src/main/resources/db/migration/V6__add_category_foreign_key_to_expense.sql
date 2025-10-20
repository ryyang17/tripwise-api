-- Update expense table to use foreign key to expensecategory instead of varchar category
ALTER TABLE expense DROP COLUMN IF EXISTS category;

ALTER TABLE expense ADD COLUMN category_id INT;

ALTER TABLE expense ADD CONSTRAINT fk_expense_category
    FOREIGN KEY (category_id) REFERENCES expensecategory(category_id) ON DELETE SET NULL;
