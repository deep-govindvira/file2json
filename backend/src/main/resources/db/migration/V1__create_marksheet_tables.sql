CREATE TABLE marksheets (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    board VARCHAR(255),
    exam_year_month VARCHAR(255),
    seat_no VARCHAR(255) NOT NULL,
    centre_no VARCHAR(255),
    school_index_no VARCHAR(255),
    student_group VARCHAR(50),
    student_name VARCHAR(255),
    total_marks INTEGER,
    obtained_marks INTEGER,
    obtained_marks_in_words VARCHAR(255),
    exam_date DATE,
    result VARCHAR(50),
    processing_corrections TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_seat_no UNIQUE (seat_no)
);

CREATE TABLE subject_results (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    marksheet_id BIGINT NOT NULL,
    subject_code VARCHAR(50),
    subject_name VARCHAR(255),
    total_marks INTEGER,
    obtained_marks INTEGER,
    obtained_marks_in_words VARCHAR(255),
    grade VARCHAR(50),
    CONSTRAINT fk_marksheet
        FOREIGN KEY(marksheet_id)
        REFERENCES marksheets(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_marksheet_id ON subject_results(marksheet_id);

INSERT INTO marksheets (
    board, exam_year_month, seat_no, centre_no, school_index_no, student_group,
    student_name, total_marks, obtained_marks, obtained_marks_in_words, exam_date, result, processing_corrections
) VALUES
('CBSE', '2026-03', 'CBSE12345', '101', 'SCH001', 'Science', 'Deep Govindvira', 500, 450, 'Four Hundred Fifty', '2026-03-15', 'Pass', NULL),
('ICSE', '2026-03', 'ICSE54321', '102', 'SCH002', 'Commerce', 'Riya Sharma', 500, 420, 'Four Hundred Twenty', '2026-03-16', 'Pass', NULL),
('State Board', '2026-03', 'STB67890', '103', 'SCH003', 'Arts', 'Amit Patel', 500, 390, 'Three Hundred Ninety', '2026-03-17', 'Pass', 'Re-evaluated');
