-- Initial database setup for Finance Tracker
-- This script runs when the PostgreSQL container starts for the first time

-- Create additional schemas if needed
-- CREATE SCHEMA IF NOT EXISTS finance;

-- Enable necessary extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    enabled BOOLEAN DEFAULT TRUE,
    account_non_expired BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE
);

-- Update transactions table to add user_id foreign key
-- Note: This will be handled by Hibernate DDL auto-update
-- But we can prepare the constraint here for manual setup
-- ALTER TABLE transactions ADD COLUMN IF NOT EXISTS user_id UUID;
-- ALTER TABLE transactions ADD CONSTRAINT IF NOT EXISTS fk_transactions_user 
--     FOREIGN KEY (user_id) REFERENCES users(id);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions(user_id);

-- You can add initial data here
-- INSERT INTO your_table (column1, column2) VALUES ('value1', 'value2');
