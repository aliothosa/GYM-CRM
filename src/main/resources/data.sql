-- Initial seed data for Gym CRM
-- Target DB: PostgreSQL
-- Assumes Spring Boot/Hibernate creates tables first via ddl-auto.

-- 1) Constant catalog: Training Types
INSERT INTO training_types (id, name) VALUES
    (1, 'FITNESS'),
    (2, 'YOGA'),
    (3, 'ZUMBA'),
    (4, 'STRETCHING'),
    (5, 'RESISTANCE')
    ON CONFLICT DO NOTHING;

-- 2) Users
    INSERT INTO users (user_id, first_name, last_name, username, password, active) VALUES
    (1, 'John', 'Doe', 'John.Doe', 'pass123', true),
    (2, 'Jane', 'Smith', 'Jane.Smith', 'pass123', true),
    (3, 'Mike', 'Brown', 'Mike.Brown', 'pass123', true),
    (4, 'Emily', 'Davis', 'Emily.Davis', 'pass123', true),
    (5, 'Carlos', 'Lopez', 'Carlos.Lopez', 'pass123', true)
    ON CONFLICT DO NOTHING;

-- 3) Trainers
INSERT INTO trainers (trainer_id, user_id, training_type_id) VALUES
    (1, 1, 1),
    (2, 2, 2),
    (3, 3, 5)
    ON CONFLICT DO NOTHING;

-- 4) Trainees
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
    (2, 5, '2000-09-25', 'Oak Avenue', 'Guadalajara', 'Jalisco', '44100', 25)
    ON CONFLICT DO NOTHING;

-- 5) Many-to-many assignment: trainee_trainers
-- If your join table has no primary/unique constraint, repeated execution could duplicate rows.
INSERT INTO trainee_trainers (trainee_id, trainer_id) VALUES
                                                          (1, 1),
                                                          (1, 2),
                                                          (2, 2),
                                                          (2, 3)
    ON CONFLICT DO NOTHING;

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
      (3, 2, 3, 5, 'Resistance Training', '2026-06-08', 50)
    ON CONFLICT DO NOTHING;

-- 7) Reset PostgreSQL sequences after explicit IDs
SELECT setval(pg_get_serial_sequence('training_types', 'id'), COALESCE((SELECT MAX(id) FROM training_types), 1), true);
SELECT setval(pg_get_serial_sequence('users', 'user_id'), COALESCE((SELECT MAX(user_id) FROM users), 1), true);
SELECT setval(pg_get_serial_sequence('trainers', 'trainer_id'), COALESCE((SELECT MAX(trainer_id) FROM trainers), 1), true);
SELECT setval(pg_get_serial_sequence('trainees', 'trainee_id'), COALESCE((SELECT MAX(trainee_id) FROM trainees), 1), true);
SELECT setval(pg_get_serial_sequence('trainings', 'training_id'), COALESCE((SELECT MAX(training_id) FROM trainings), 1), true);
