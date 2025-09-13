-- Initial data for testing (H2 Database)
-- This file will be executed after schema creation

-- Insert a test user (password is 'password123' encoded with BCrypt)
 INSERT INTO users (username, email, password, first_name, last_name, is_active, created_at, updated_at)
 VALUES ('testuser', 'test@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM0lE.JRyqOKRjbOm6Gy', 'Test', 'User', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
