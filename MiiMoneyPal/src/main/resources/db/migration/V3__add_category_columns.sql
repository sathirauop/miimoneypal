-- V3: Add color, icon, and updated_at columns to categories table
-- These columns support UI display customization

-- Add color column for category badge/chip display
ALTER TABLE categories ADD COLUMN color VARCHAR(20);

-- Add icon column for category icon display (stores icon name/identifier)
ALTER TABLE categories ADD COLUMN icon VARCHAR(50);

-- Add updated_at column for tracking modifications
ALTER TABLE categories ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Backfill updated_at with created_at for existing rows
UPDATE categories SET updated_at = created_at WHERE updated_at IS NULL;

-- Add comments for documentation
COMMENT ON COLUMN categories.color IS 'Hex color code or color name for UI display (e.g., #FF5733, red)';
COMMENT ON COLUMN categories.icon IS 'Icon identifier for UI display (e.g., shopping-cart, briefcase)';
