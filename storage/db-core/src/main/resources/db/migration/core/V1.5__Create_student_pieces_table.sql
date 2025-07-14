CREATE TABLE student_pieces
(
    id         BIGINT    NOT NULL AUTO_INCREMENT,
    student_id BIGINT    NOT NULL,
    piece_id   BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_student_piece UNIQUE (student_id, piece_id)
);

CREATE INDEX idx_student_pieces_student_id ON student_pieces(student_id);
