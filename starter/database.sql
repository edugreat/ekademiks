DROP SCHEMA IF EXISTS `e_database`;
CREATE SCHEMA `e_database`;
USE `e_database`;

 
-- Each academic test has a particular academic level suited for the test
create table `level`(
ID INTEGER UNSIGNED AUTO_INCREMENT,
category VARCHAR(255) NOT NULL UNIQUE,
PRIMARY KEY (ID)
);

-- student table
create table `student`(
ID INTEGER UNSIGNED AUTO_INCREMENT, 
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
ID INTEGER UNSIGNED AUTO_INCREMENT,
subject_id INTEGER UNSIGNED NOT NULL,
level_id INTEGER UNSIGNED NOT NULL,
test_name VARCHAR(255) NOT NULL,
duration TINYINT UNSIGNED,
CONSTRAINT fk_test_level_id FOREIGN KEY(level_id) REFERENCES `level`(id) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_test_subject_id FOREIGN KEY(subject_id) REFERENCES `subject`(id) ON DELETE RESTRICT ON UPDATE CASCADE,
PRIMARY KEY(ID)
);

-- the question table models a real world question suitable for assessement
create table `question`(
ID INTEGER UNSIGNED AUTO_INCREMENT,
test_id INTEGER UNSIGNED DEFAULT NULL,
question_number SMALLINT UNSIGNED NOT NULL,
topic VARCHAR(255) NOT NULL,
question_text TEXT NOT NULL,
answer TEXT NOT NULL,
CONSTRAINT fk_question_test_id FOREIGN KEY(test_id) REFERENCES `test`(id)  ON UPDATE CASCADE,
PRIMARY KEY(ID)
);

-- each test has options from which students should select
create table `option`(
ID INTEGER UNSIGNED AUTO_INCREMENT,
question_id INTEGER UNSIGNED NOT NULL,
option_text TEXT NOT NULL,
option_letter CHAR(1) NOT NULL,
CONSTRAINT fk_option_question_id  FOREIGN KEY (question_id) REFERENCES `question`(ID) ON DELETE RESTRICT ON UPDATE CASCADE,
PRIMARY KEY(ID)
);

-- the student_test table holds the information of the student who has participated in the test
-- or currently taking a test
create table `student_test`(
ID INTEGER UNSIGNED AUTO_INCREMENT,
student_id INTEGER UNSIGNED DEFAULT NULL,
test_id INTEGER UNSIGNED DEFAULT NULL,
score DOUBLE NOT NULL,
date_started TIMESTAMP DEFAULT NOW(),
last_resumed_on TIMESTAMP DEFAULT NOW(),
CONSTRAINT fk_student_test_test_id FOREIGN KEY(test_id) REFERENCES `test`(id)  ON UPDATE CASCADE,
CONSTRAINT fk_student_test_student_id FOREIGN KEY(student_id) REFERENCES `student`(id) ON UPDATE CASCADE,
PRIMARY KEY(ID)
);

-- the student_selected_option holds information about a student who has taken a test,
-- the options selected, as well as the correctness of the options
create table `student_selected_option`(
ID INTEGER UNSIGNED AUTO_INCREMENT,
student_test_id INTEGER UNSIGNED NOT NULL,

option_id INTEGER UNSIGNED NOT NULL,
is_correct BOOL NOT NULL,
CONSTRAINT fk_student_selected_option_student_test_id FOREIGN KEY(student_test_id) REFERENCES `student_test`(ID) ON DELETE RESTRICT ON UPDATE CASCADE,

CONSTRAINT fk_student_selected_option_option_id FOREIGN KEY(option_id) REFERENCES `option`(ID) ON DELETE RESTRICT ON UPDATE CASCADE,
PRIMARY KEY(ID)
);

-- indexes for performance optimization purpose
CREATE INDEX idx_subject_id ON test (subject_id);
CREATE INDEX idx_student_id ON student_test (student_id);
CREATE INDEX idx_test_id ON student_test (test_id);
CREATE INDEX idx_test_id ON question (test_id);
CREATE INDEX idx_student_test_id ON student_selected_option (student_test_id);
CREATE INDEX idx_option_id ON student_selected_option(option_id);





---------------------------------------------------------------------------------
-- Insert records into 'Level' table
INSERT INTO `level` (category) VALUES ('SENIOR'), ('JUNIOR');

-- Insert records into 'Subject' table
INSERT INTO `subject` (subject_name, level_id) VALUES
    ('Mathematics', (SELECT ID FROM `level` WHERE category = 'SENIOR')),
    ('Chemistry', (SELECT ID FROM `level` WHERE category = 'SENIOR')),
    ('Programming', (SELECT ID FROM `level` WHERE category = 'SENIOR')),
    ('Physics', (SELECT ID FROM `level` WHERE category = 'SENIOR')),
    ('Mathematics', (SELECT ID FROM `level` WHERE category = 'JUNIOR')),
    ('Chemistry', (SELECT ID FROM `level` WHERE category = 'JUNIOR')),
    ('Programming', (SELECT ID FROM `level` WHERE category = 'JUNIOR')),
    ('Physics', (SELECT ID FROM `level` WHERE category = 'JUNIOR'));

-- Insert records into 'Student' table
INSERT INTO `student` (first_name, last_name, email, username, password) VALUES
    ('Chijioke', 'Okoye', 'chijioke.okoye@example.com', 'chijioke', 'password123'),
    ('Ngozi', 'Okafor', 'ngozi.okafor@example.com', 'ngozi', 'securepass'),
    ('Uche', 'Nwabueze', 'uche.nwabueze@example.com', 'uche', 'mypassword'),
    ('Chinonso', 'Eze', 'chinonso.eze@example.com', 'chinonso', 'pass123'),
    ('John', 'Doe', 'john.doe@example.com', 'john_doe', 'password456'),
    ('Jane', 'Smith', 'jane.smith@example.com', 'jane_smith', 'securepass789'),
    ('Alex', 'Johnson', 'alex.johnson@example.com', 'alex_j', 'mypassword789'),
    ('Emily', 'Williams', 'emily.williams@example.com', 'emily_w', 'pass789');

-- Insert records into 'Test' table
INSERT INTO `test` (subject_id, level_id, test_name, duration) VALUES
    -- SENIOR level tests
    ((SELECT ID FROM `subject` WHERE subject_name = 'Mathematics' AND level_id = (SELECT ID FROM `level` WHERE category = 'SENIOR')), (SELECT ID FROM `level` WHERE category = 'SENIOR'), 'SENIOR Mathematics Test 1', 120),
    ((SELECT ID FROM `subject` WHERE subject_name = 'Chemistry' AND level_id = (SELECT ID FROM `level` WHERE category = 'SENIOR')), (SELECT ID FROM `level` WHERE category = 'SENIOR'), 'SENIOR Chemistry Test 1', 90),
    ((SELECT ID FROM `subject` WHERE subject_name = 'Programming' AND level_id = (SELECT ID FROM `level` WHERE category = 'SENIOR')), (SELECT ID FROM `level` WHERE category = 'SENIOR'), 'SENIOR Programming Test 1', 60),
    ((SELECT ID FROM `subject` WHERE subject_name = 'Physics' AND level_id = (SELECT ID FROM `level` WHERE category = 'SENIOR')), (SELECT ID FROM `level` WHERE category = 'SENIOR'), 'SENIOR Physics Test 1', 75),
    
    -- JUNIOR level tests
    ((SELECT ID FROM `subject` WHERE subject_name = 'Mathematics' AND level_id = (SELECT ID FROM `level` WHERE category = 'JUNIOR')), (SELECT ID FROM `level` WHERE category = 'JUNIOR'), 'JUNIOR Mathematics Test 1', 90),
    ((SELECT ID FROM `subject` WHERE subject_name = 'Chemistry' AND level_id = (SELECT ID FROM `level` WHERE category = 'JUNIOR')), (SELECT ID FROM `level` WHERE category = 'JUNIOR'), 'JUNIOR Chemistry Test 1', 75),
    ((SELECT ID FROM `subject` WHERE subject_name = 'Programming' AND level_id = (SELECT ID FROM `level` WHERE category = 'JUNIOR')), (SELECT ID FROM `level` WHERE category = 'JUNIOR'), 'JUNIOR Programming Test 1', 45),
    ((SELECT ID FROM `subject` WHERE subject_name = 'Physics' AND level_id = (SELECT ID FROM `level` WHERE category = 'JUNIOR')), (SELECT ID FROM `level` WHERE category = 'JUNIOR'), 'JUNIOR Physics Test 1', 60);


-- Insert records into 'student_test' table
INSERT INTO `student_test` (student_id, test_id, score, date_started, last_resumed_on) VALUES
    -- Students taking SENIOR level tests
    ((SELECT ID FROM `student` WHERE first_name = 'Chijioke'), (SELECT ID FROM `test` WHERE level_id = (SELECT ID FROM `level` WHERE category = 'SENIOR') LIMIT 1), 85.5, '2023-11-15 10:00:00', '2023-11-15 12:30:00'),
    ((SELECT ID FROM `student` WHERE first_name = 'Ngozi'), (SELECT ID FROM `test` WHERE level_id = (SELECT ID FROM `level` WHERE category = 'SENIOR') LIMIT 1), 78.2, '2023-11-15 11:30:00', '2023-11-15 14:15:00'),
    ((SELECT ID FROM `student` WHERE first_name = 'Uche'), (SELECT ID FROM `test` WHERE level_id = (SELECT ID FROM `level` WHERE category = 'SENIOR') LIMIT 1), 92.0, '2023-11-15 09:45:00', '2023-11-15 12:00:00'),
    ((SELECT ID FROM `student` WHERE first_name = 'Chinonso'), (SELECT ID FROM `test` WHERE level_id = (SELECT ID FROM `level` WHERE category = 'SENIOR') LIMIT 1), 88.7, '2023-11-15 10:15:00', '2023-11-15 13:00:00'),
    ((SELECT ID FROM `student` WHERE first_name = 'John'), (SELECT ID FROM `test` WHERE level_id = (SELECT ID FROM `level` WHERE category = 'SENIOR') LIMIT 1), 76.8, '2023-11-15 12:00:00', '2023-11-15 15:00:00'),

    -- Students taking multiple SENIOR level tests
    ((SELECT ID FROM `student` WHERE first_name = 'Ngozi'), (SELECT ID FROM `test` WHERE level_id = (SELECT ID FROM `level` WHERE category = 'SENIOR') ORDER BY RAND() LIMIT 1), 68.5, '2023-11-15 14:30:00', '2023-11-15 16:45:00'),
    ((SELECT ID FROM `student` WHERE first_name = 'Uche'), (SELECT ID FROM `test` WHERE level_id = (SELECT ID FROM `level` WHERE category = 'SENIOR') ORDER BY RAND() LIMIT 1), 80.2, '2023-11-15 13:45:00', '2023-11-15 16:00:00'),
    ((SELECT ID FROM `student` WHERE first_name = 'Chinonso'), (SELECT ID FROM `test` WHERE level_id = (SELECT ID FROM `level` WHERE category = 'SENIOR') ORDER BY RAND() LIMIT 1), 89.5, '2023-11-15 13:15:00', '2023-11-15 15:30:00'),
    ((SELECT ID FROM `student` WHERE first_name = 'John'), (SELECT ID FROM `test` WHERE level_id = (SELECT ID FROM `level` WHERE category = 'SENIOR') ORDER BY RAND() LIMIT 1), 75.0, '2023-11-15 15:30:00', '2023-11-15 18:00:00'),

    -- Students taking JUNIOR level tests
    ((SELECT ID FROM `student` WHERE first_name = 'Jane'), (SELECT ID FROM `test` WHERE level_id = (SELECT ID FROM `level` WHERE category = 'JUNIOR') LIMIT 1), 82.4, '2023-11-15 10:30:00', '2023-11-15 13:45:00'),
    ((SELECT ID FROM `student` WHERE first_name = 'Alex'), (SELECT ID FROM `test` WHERE level_id = (SELECT ID FROM `level` WHERE category = 'JUNIOR') LIMIT 1), 93.1, '2023-11-15 11:15:00', '2023-11-15 14:30:00'),
    ((SELECT ID FROM `student` WHERE first_name = 'Emily'), (SELECT ID FROM `test` WHERE level_id = (SELECT ID FROM `level` WHERE category = 'JUNIOR') LIMIT 1), 79.8, '2023-11-15 12:30:00', '2023-11-15 15:45:00'),

    -- Students taking multiple JUNIOR level tests
    ((SELECT ID FROM `student` WHERE first_name = 'Jane'), (SELECT ID FROM `test` WHERE level_id = (SELECT ID FROM `level` WHERE category = 'JUNIOR') ORDER BY RAND() LIMIT 1), 88.2, '2023-11-15 14:00:00', '2023-11-15 16:15:00'),
    ((SELECT ID FROM `student` WHERE first_name = 'Alex'), (SELECT ID FROM `test` WHERE level_id = (SELECT ID FROM `level` WHERE category = 'JUNIOR') ORDER BY RAND() LIMIT 1), 95.0, '2023-11-15 13:30:00', '2023-11-15 15:45:00'),
    ((SELECT ID FROM `student` WHERE first_name = 'Emily'), (SELECT ID FROM `test` WHERE level_id = (SELECT ID FROM `level` WHERE category = 'JUNIOR') ORDER BY RAND() LIMIT 1), 81.7, '2023-11-15 15:00:00', '2023-11-15 17:15:00');

-- Insert records into 'question' table
INSERT INTO `question` (test_id, question_number, topic, question_text, answer) VALUES
    -- Questions for the first test
    ((SELECT ID FROM `test` WHERE test_name = 'SENIOR Mathematics Test 1'), 1, 'Algebra', 'Solve for x: 2x + 5 = 15', 'x = 5'),
    ((SELECT ID FROM `test` WHERE test_name = 'SENIOR Mathematics Test 1'), 2, 'Geometry', 'Calculate the area of a triangle with base 8 and height 12', '48'),
    ((SELECT ID FROM `test` WHERE test_name = 'SENIOR Mathematics Test 1'), 3, 'Calculus', 'Find the derivative of f(x) = 3x^2 + 2x - 7', 'f\'(x) = 6x + 2'),
    ((SELECT ID FROM `test` WHERE test_name = 'SENIOR Mathematics Test 1'), 4, 'Statistics', 'What is the mean of the following data set: 10, 15, 20, 25, 30', '20'),

    -- Questions for the second test
    ((SELECT ID FROM `test` WHERE test_name = 'JUNIOR Chemistry Test 1'), 1, 'Chemical Reactions', 'Balance the chemical equation: H2 + O2 → H2O', '2H2 + O2 → 2H2O'),
    ((SELECT ID FROM `test` WHERE test_name = 'JUNIOR Chemistry Test 1'), 2, 'Atomic Structure', 'How many protons are there in a carbon atom?', '6'),
    ((SELECT ID FROM `test` WHERE test_name = 'JUNIOR Chemistry Test 1'), 3, 'Periodic Table', 'Which element has the symbol Na?', 'Sodium'),
    ((SELECT ID FROM `test` WHERE test_name = 'JUNIOR Chemistry Test 1'), 4, 'Chemical Bonds', 'What type of bond is formed when electrons are shared between atoms?', 'Covalent Bond');

-- Insert records into 'question' table for Programming test
INSERT INTO `question` (test_id, question_number, topic, question_text, answer) VALUES
    ((SELECT ID FROM `test` WHERE test_name = 'SENIOR Programming Test 1'), 1, 'Programming Basics', 'What is the output of the following code snippet in Python: print("Hello, World!")', 'Hello, World!'),
    ((SELECT ID FROM `test` WHERE test_name = 'SENIOR Programming Test 1'), 2, 'Data Structures', 'What is the time complexity of a binary search algorithm?', 'O(log n)'),
    ((SELECT ID FROM `test` WHERE test_name = 'SENIOR Programming Test 1'), 3, 'Object-Oriented Programming', 'Explain the concept of inheritance in object-oriented programming.', 'Inheritance allows a class to inherit properties and behaviors from another class.'),
    ((SELECT ID FROM `test` WHERE test_name = 'SENIOR Programming Test 1'), 4, 'Algorithm Design', 'Write a pseudocode for a bubble sort algorithm.', 'BEGIN BubbleSort(arr)\n  FOR i FROM 0 TO len(arr)-1 DO\n    FOR j FROM 0 TO len(arr)-1-i DO\n      IF arr[j] > arr[j+1] THEN\n        SWAP(arr[j], arr[j+1])\n      END IF\n    END FOR\n  END FOR\nEND BubbleSort'),

-- Insert records into 'question' table for Physics test
    ((SELECT ID FROM `test` WHERE test_name = 'JUNIOR Physics Test 1'), 1, 'Mechanics', 'What is Newton\'s second law of motion?', 'The force acting on an object is equal to the mass of that object multiplied by its acceleration (F = ma).'),
    ((SELECT ID FROM `test` WHERE test_name = 'JUNIOR Physics Test 1'), 2, 'Optics', 'Explain the phenomenon of total internal reflection in optics.', 'Total internal reflection occurs when light traveling in a denser medium strikes a boundary with a less dense medium at an angle greater than the critical angle, causing the light to be entirely reflected back into the denser medium.'),
    ((SELECT ID FROM `test` WHERE test_name = 'JUNIOR Physics Test 1'), 3, 'Electricity and Magnetism', 'What is Ohm\'s Law?', 'Ohm\'s Law states that the current passing through a conductor between two points is directly proportional to the voltage across the two points, given a constant temperature (I = V/R).'),
    ((SELECT ID FROM `test` WHERE test_name = 'JUNIOR Physics Test 1'), 4, 'Thermodynamics', 'Define the first law of thermodynamics.', 'The first law of thermodynamics, also known as the law of energy conservation, states that energy cannot be created or destroyed in an isolated system. It can only change forms.');


-- Insert records into 'option' table
INSERT INTO `option` (question_id, option_text, option_letter) VALUES
    -- Options for SENIOR Mathematics Test 1
    ((SELECT ID FROM `question` WHERE question_number = 1 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'SENIOR Mathematics Test 1')), '5', 'A'),
    ((SELECT ID FROM `question` WHERE question_number = 1 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'SENIOR Mathematics Test 1')), '8', 'B'),
    ((SELECT ID FROM `question` WHERE question_number = 1 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'SENIOR Mathematics Test 1')), '10', 'C'),
    ((SELECT ID FROM `question` WHERE question_number = 1 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'SENIOR Mathematics Test 1')), '15', 'D'),

    -- Options for SENIOR Mathematics Test 1
    ((SELECT ID FROM `question` WHERE question_number = 2 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'SENIOR Mathematics Test 1')), '24', 'A'),
    ((SELECT ID FROM `question` WHERE question_number = 2 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'SENIOR Mathematics Test 1')), '36', 'B'),
    ((SELECT ID FROM `question` WHERE question_number = 2 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'SENIOR Mathematics Test 1')), '48', 'C'),
    ((SELECT ID FROM `question` WHERE question_number = 2 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'SENIOR Mathematics Test 1')), '72', 'D'),

    -- Options for SENIOR Chemistry Test 1
    ((SELECT ID FROM `question` WHERE question_number = 1 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'JUNIOR Chemistry Test 1')), '2H2O', 'A'),
    ((SELECT ID FROM `question` WHERE question_number = 1 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'JUNIOR Chemistry Test 1')), 'H2 + O2', 'B'),
    ((SELECT ID FROM `question` WHERE question_number = 1 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'JUNIOR Chemistry Test 1')), 'H2O', 'C'),
    ((SELECT ID FROM `question` WHERE question_number = 1 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'JUNIOR Chemistry Test 1')), '2HO', 'D'),

    -- Options for JUNIOR Physics Test 1
    ((SELECT ID FROM `question` WHERE question_number = 3 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'JUNIOR Physics Test 1')), 'Direct Current', 'A'),
    ((SELECT ID FROM `question` WHERE question_number = 3 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'JUNIOR Physics Test 1')), 'Alternating Current', 'B'),
    ((SELECT ID FROM `question` WHERE question_number = 3 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'JUNIOR Physics Test 1')), 'Voltage', 'C'),
    ((SELECT ID FROM `question` WHERE question_number = 3 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'JUNIOR Physics Test 1')), 'Resistance', 'D'),

    -- Options for JUNIOR Physics Test 1
    ((SELECT ID FROM `question` WHERE question_number = 4 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'JUNIOR Physics Test 1')), 'Potential Energy', 'A'),
    ((SELECT ID FROM `question` WHERE question_number = 4 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'JUNIOR Physics Test 1')), 'Kinetic Energy', 'B'),
    ((SELECT ID FROM `question` WHERE question_number = 4 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'JUNIOR Physics Test 1')), 'Heat Energy', 'C'),
    ((SELECT ID FROM `question` WHERE question_number = 4 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'JUNIOR Physics Test 1')), 'Mechanical Energy', 'D');


-- Insert records into 'student_selected_option' table
INSERT INTO `student_selected_option` (student_test_id, option_id, is_correct) VALUES
    -- student selections for SENIOR Mathematics Test 1
    ((SELECT ID FROM `student_test` WHERE student_id = (SELECT ID FROM `student` WHERE first_name = 'Chijioke' AND last_name = 'Okoye') LIMIT 1), 
     
    (SELECT ID FROM `option` WHERE question_id = (SELECT ID FROM `question` WHERE question_number = 1 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'SENIOR Mathematics Test 1') LIMIT 1) AND option_letter = 'A' LIMIT 1), 
    TRUE),

    ((SELECT ID FROM `student_test` WHERE student_id = (SELECT ID FROM `student` WHERE first_name = 'Ngozi' AND last_name = 'Okafor') LIMIT 1), 
     
    (SELECT ID FROM `option` WHERE question_id = (SELECT ID FROM `question` WHERE question_number = 2 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'SENIOR Mathematics Test 1') LIMIT 1) AND option_letter = 'B' LIMIT 1), 
    FALSE),

    -- Add more student selections for other questions as needed
    ((SELECT ID FROM `student_test` WHERE student_id = (SELECT ID FROM `student` WHERE first_name = 'Jane' AND last_name = 'Smith') LIMIT 1), 
 
    (SELECT ID FROM `option` WHERE question_id = (SELECT ID FROM `question` WHERE question_number = 3 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'JUNIOR Physics Test 1') LIMIT 1) AND option_letter = 'A' LIMIT 1), 
    TRUE),

    ((SELECT ID FROM `student_test` WHERE student_id = (SELECT ID FROM `student` WHERE first_name = 'Alex' AND last_name = 'Johnson') LIMIT 1), 
     
    (SELECT ID FROM `option` WHERE question_id = (SELECT ID FROM `question` WHERE question_number = 4 AND test_id = (SELECT ID FROM `test` WHERE test_name = 'JUNIOR Physics Test 1') LIMIT 1) AND option_letter = 'B' LIMIT 1), 
    FALSE);

