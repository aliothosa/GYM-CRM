-- Initial seed data for Gym CRM (PostgreSQL)
-- Applied on every application start after Hibernate create-drop builds an empty schema.
--
-- Entity / table mapping:
--   User            -> users
--   Trainer         -> trainers            (FK: user_id -> users, training_type_id -> training_types)
--   Trainee         -> trainees            (FK: user_id -> users; embedded Address columns on same table)
--   TrainingType    -> training_types      (constant catalog; not modified by the application)
--   trainee_trainers                         (join table for Trainee.trainers many-to-many)
--   Training        -> trainings           (FK: trainee_id, trainer_id, training_type_id)

-- 1) Constant catalog: training types
INSERT INTO training_types (id, name) VALUES
    (1, 'FITNESS'),
    (2, 'YOGA'),
    (3, 'ZUMBA'),
    (4, 'STRETCHING'),
    (5, 'RESISTANCE');

-- 2) Users (user_id 1-3 -> trainers, 4-5 -> trainees)
INSERT INTO users (user_id, first_name, last_name, username, password, active) VALUES
    (1, 'John', 'Doe', 'John.Doe', 'pass123', true),
    (2, 'Jane', 'Smith', 'Jane.Smith', 'pass123', true),
    (3, 'Mike', 'Brown', 'Mike.Brown', 'pass123', true),
    (4, 'Emily', 'Davis', 'Emily.Davis', 'pass123', true),
    (5, 'Carlos', 'Lopez', 'Carlos.Lopez', 'pass123', true);

-- 3) Trainers
INSERT INTO trainers (trainer_id, user_id, training_type_id) VALUES
    (1, 1, 1),
    (2, 2, 2),
    (3, 3, 5);

-- 4) Trainees (embedded Address columns on trainees table)
INSERT INTO trainees (
    trainee_id,
    user_id,
    birth_date,
    street,
    city,
    state,
    zip_code,
    number
) VALUES
    (1, 4, '1998-04-12', 'Main Street', 'Mexico City', 'CDMX', '01000', 100),
    (2, 5, '2000-09-25', 'Oak Avenue', 'Guadalajara', 'Jalisco', '44100', 25);

-- 5) Trainee <-> Trainer assignments
INSERT INTO trainee_trainers (trainee_id, trainer_id) VALUES
    (1, 1),
    (1, 2),
    (2, 2),
    (2, 3);

-- 6) Trainings
INSERT INTO trainings (
    training_id,
    trainee_id,
    trainer_id,
    training_type_id,
    training_name,
    training_date,
    training_duration_minutes
) VALUES
    (1, 1, 1, 1, 'Morning Fitness Session', '2026-06-01', 60),
    (2, 1, 2, 2, 'Yoga Basics', '2026-06-05', 45),
    (3, 2, 3, 5, 'Resistance Training', '2026-06-08', 50);

-- 7) Align identity sequences with explicit seed IDs
SELECT setval(pg_get_serial_sequence('training_types', 'id'), COALESCE((SELECT MAX(id) FROM training_types), 1), true);
SELECT setval(pg_get_serial_sequence('users', 'user_id'), COALESCE((SELECT MAX(user_id) FROM users), 1), true);
SELECT setval(pg_get_serial_sequence('trainers', 'trainer_id'), COALESCE((SELECT MAX(trainer_id) FROM trainers), 1), true);
SELECT setval(pg_get_serial_sequence('trainees', 'trainee_id'), COALESCE((SELECT MAX(trainee_id) FROM trainees), 1), true);
SELECT setval(pg_get_serial_sequence('trainings', 'training_id'), COALESCE((SELECT MAX(training_id) FROM trainings), 1), true);
