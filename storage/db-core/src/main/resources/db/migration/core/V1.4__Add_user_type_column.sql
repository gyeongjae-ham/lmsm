ALTER TABLE users ADD COLUMN IF NOT EXISTS type VARCHAR(20) NOT NULL DEFAULT 'STUDENT';

CREATE INDEX IF NOT EXISTS idx_users_type ON users(type);

INSERT INTO users (email, username, password, first_name, last_name, type)
VALUES ('teacher@test.com', 'teacher', 'password123', 'Test', 'Teacher', 'TEACHER');

INSERT INTO users (email, username, password, first_name, last_name, type)
VALUES ('student@test.com', 'student', 'password123', 'Test', 'Student', 'STUDENT');
