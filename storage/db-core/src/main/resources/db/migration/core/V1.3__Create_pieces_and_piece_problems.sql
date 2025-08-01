CREATE TABLE IF NOT EXISTS pieces
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(255) NOT NULL,
    teacher_id BIGINT       NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS piece_problems
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    piece_id   BIGINT  NOT NULL,
    problem_id BIGINT  NOT NULL,
    sequence   INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
