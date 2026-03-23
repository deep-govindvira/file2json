INSERT INTO exam_boards (id, board_full_name, board_short_name, board_state, board_city, created_at, updated_at)
VALUES
(gen_random_uuid(), 'Gujarat Secondary and Higher Secondary Education Board', 'GSEB', 'Gujarat', 'Gandhinagar', now(), now()),
(gen_random_uuid(), 'Central Board of Secondary Education', 'CBSE', 'New Delhi', 'New Delhi', now(), now()),
(gen_random_uuid(), 'Indian Certificate of Secondary Education', 'ICSE', 'New Delhi', 'New Delhi', now(), now());