DROP SCHEMA IF EXISTS `e_database`;
CREATE SCHEMA `e_database`;
USE `e_database`;

 
-- Each academic test has a particular academic level suited for the test
create table `level`(
ID INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
category VARCHAR(255) NOT NULL UNIQUE,
PRIMARY KEY (ID)
);

-- student table
create table `student`(
ID INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
first_name VARCHAR(255) NOT NULL,
last_name VARCHAR(255) NOT NULL,
email VARCHAR(255) NOT NULL UNIQUE,
username VARCHAR(255) NOT NULL,
password VARCHAR(255) NOT NULL,
PRIMARY KEY(ID)
);


-- subject table models a real world academic subject
create table `subject`(
ID INTEGER UNSIGNED AUTO_INCREMENT,
subject_name VARCHAR(255) NOT NULL,
level_id INTEGER UNSIGNED NOT NULL,
CONSTRAINT fk_subject_level_id FOREIGN KEY(level_id) REFERENCES `Level`(ID) ON DELETE RESTRICT ON UPDATE CASCADE,
PRIMARY KEY(ID)
);

-- the test table model the actual academic test wit the necessay fields,including test duration
create table `test`(
ID INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
subject_id INTEGER UNSIGNED NOT NULL,
test_name VARCHAR(255) NOT NULL,
duration TINYINT UNSIGNED,
CONSTRAINT fk_test_subject_id FOREIGN KEY(subject_id) REFERENCES `subject`(id) ON DELETE RESTRICT ON UPDATE CASCADE,
PRIMARY KEY(ID)
);

-- the question table models a real world question suitable for assessement
create table `question`(
ID INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
test_id INTEGER UNSIGNED NOT NULL,
question_number SMALLINT UNSIGNED NOT NULL,
topic VARCHAR(255) NOT NULL,
question_text TEXT NOT NULL,
`options` json,
answer TEXT NOT NULL,
CONSTRAINT fk_question_test_id FOREIGN KEY(test_id) REFERENCES `test`(id) ON DELETE RESTRICT ON UPDATE CASCADE,
PRIMARY KEY(ID)
);





-- the student_test table holds the information of the student who has participated in the test
-- or currently taking a test
create table `student_test`(
ID INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
student_id INTEGER UNSIGNED NOT NULL,
test_id_student INTEGER UNSIGNED DEFAULT NULL,
score DOUBLE DEFAULT NULL,
date_started TIMESTAMP DEFAULT NULL,
last_resumed_on TIMESTAMP DEFAULT NULL,
CONSTRAINT fk_student_test_test_id FOREIGN KEY(test_id_student) REFERENCES `test`(id)  ON UPDATE CASCADE,
CONSTRAINT fk_student_test_student_id FOREIGN KEY(student_id) REFERENCES `student`(id) ON DELETE RESTRICT ON UPDATE CASCADE,
PRIMARY KEY(ID)
);

-- the student_selected_option holds information about a student who has taken a test,
-- the options selected, as well as the correctness of the options
create table `student_selected_option`(
ID INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
student_test_id INTEGER UNSIGNED NOT NULL,
selected CHAR(1) DEFAULT NULL,
question_id INTEGER UNSIGNED DEFAULT NULL,
is_correct BOOL DEFAULT FALSE,
CONSTRAINT fk_student_selected_option_student_test_id FOREIGN KEY(student_test_id) REFERENCES `student_test`(ID) ON DELETE RESTRICT ON UPDATE CASCADE,

CONSTRAINT fk_student_selected_option_question_id FOREIGN KEY(question_id) REFERENCES `question`(ID) ON UPDATE CASCADE,
PRIMARY KEY(ID)
);

-- indexes for performance optimization purpose
CREATE INDEX idx_subject_id ON test (subject_id);
CREATE INDEX idx_student_id ON student_test (student_id);
 CREATE INDEX idx_test_id ON question (test_id);

-- Insert into Level table
INSERT INTO `level` (category) VALUES
('JUNIOR'),
('SENIOR');

-- Subjects for Senior Level
INSERT INTO `subject` (subject_name, level_id) VALUES
('Mathematics', (SELECT ID FROM `level` WHERE category = 'SENIOR')),
('Physics', (SELECT ID FROM `level` WHERE category = 'SENIOR'));

-- Subjects for Junior Level
INSERT INTO `subject` (subject_name, level_id) VALUES
('Chemistry', (SELECT ID FROM `level` WHERE category = 'JUNIOR')),
('Programming', (SELECT ID FROM `level` WHERE category = 'JUNIOR'));

-- Tests referencing Subjects
INSERT INTO `test` (subject_id, test_name, duration) VALUES
((SELECT ID FROM `subject` WHERE subject_name = 'Mathematics'), 'Math Test 1', 60),
((SELECT ID FROM `subject` WHERE subject_name = 'Physics'), 'Physics Exam', 90),
((SELECT ID FROM `subject` WHERE subject_name = 'Chemistry'), 'Chemistry Quiz', 45),
((SELECT ID FROM `subject` WHERE subject_name = 'Programming'), 'Programming Test 1', 75);

-- Questions for each Test
INSERT INTO `question` (test_id, question_number, topic, question_text, `options`, answer) VALUES
-- Math Test 1
((SELECT ID FROM `test` WHERE test_name = 'Math Test 1'), 1, 'Algebra', 'Solve for x: 2x + 5 = 15', '{"A": "5", "B": "7", "C": "8", "D": "10"}', 'D'),
((SELECT ID FROM `test` WHERE test_name = 'Math Test 1'), 2, 'Geometry', 'What is the area of a triangle with base 8 and height 6?', '{"A": "16", "B": "24", "C": "30", "D": "48"}', 'B'),
-- Physics Exam
((SELECT ID FROM `test` WHERE test_name = 'Physics Exam'), 1, 'Motion', 'What is the formula for calculating velocity?', '{"A": "v = d/t", "B": "v = a/t", "C": "v = d*a", "D": "v = d + t"}', 'A'),
((SELECT ID FROM `test` WHERE test_name = 'Physics Exam'), 2, 'Optics', 'What happens to the focal length when a convex lens is placed in water?', '{"A": "Increases", "B": "Decreases", "C": "Remains the same", "D": "Becomes infinite"}', 'B'),
-- Chemistry Quiz
((SELECT ID FROM `test` WHERE test_name = 'Chemistry Quiz'), 1, 'Periodic Table', 'Which element has the chemical symbol "Na"?', '{"A": "Sodium", "B": "Nitrogen", "C": "Neon", "D": "Nickel"}', 'A'),
((SELECT ID FROM `test` WHERE test_name = 'Chemistry Quiz'), 2, 'Chemical Reactions', 'What is the product of the reaction between hydrochloric acid (HCl) and sodium hydroxide (NaOH)?', '{"A": "NaCl + H2O", "B": "HClO", "C": "NaOH + H2O", "D": "H2 + O2"}', 'A'),
-- Programming Test 1
((SELECT ID FROM `test` WHERE test_name = 'Programming Test 1'), 1, 'Java Basics', 'What is the purpose of the "break" statement in Java?', '{"A": "Terminate the loop", "B": "Skip the loop", "C": "Continue to the next iteration", "D": "Jump to a specific label"}', 'A'),
((SELECT ID FROM `test` WHERE test_name = 'Programming Test 1'), 2, 'Data Structures', 'Which data structure follows the Last In, First Out (LIFO) principle?', '{"A": "Queue", "B": "Stack", "C": "Linked List", "D": "Array"}', 'B');

-- Students
INSERT INTO `student` (first_name, last_name, email, username, password) VALUES
('John', 'Doe', 'john.doe@example.com', 'john_doe', 'password123'),
('Chidimma', 'Eze', 'chidimma.eze@example.com', 'chidimma_eze', 'securepass'),
('Uchenna', 'Onyekachi', 'uchenna.onyekachi@example.com', 'uchenna_onyekachi', 'pass123'),
('Michael', 'Smith', 'michael.smith@example.com', 'michael_smith', 'letmein'),
('Chioma', 'Nwankwo', 'chioma.nwankwo@example.com', 'chioma_nwankwo', 'password456');

-- Student Tests
INSERT INTO `student_test` (student_id, test_id_student, score, date_started, last_resumed_on) VALUES
(1, 1, 85.5, '2023-11-13 10:00:00', '2023-11-13 12:30:00'),
(2, 2, 92.0, '2023-11-14 09:30:00', '2023-11-14 11:45:00'),
(3, 3, 78.5, '2023-11-15 11:15:00', '2023-11-15 14:00:00'),
(4, 4, 95.5, '2023-11-16 08:45:00', '2023-11-16 10:30:00');
-- (5, 5, 89.0, '2023-11-17 13:00:00', '2023-11-17 15:15:00');

-- Student Selected Options
INSERT INTO `student_selected_option` (student_test_id, selected, question_id, is_correct) VALUES
(1, 'A', 1, TRUE),
(1, 'B', 2, FALSE),
(2, 'C', 3, TRUE),
(2, 'D', 4, TRUE),
(3, 'A', 5, FALSE),
(3, 'B', 6, FALSE),
(4, 'C', 7, TRUE);
-- (5, 'A', 9, TRUE),
-- (5, 'B', 10, FALSE);


