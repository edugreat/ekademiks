DROP SCHEMA IF EXISTS `edukademiks`;
CREATE SCHEMA `edukademiks`;
USE `edukademiks`;

CREATE TABLE `Category`(
`id` integer not null primary key auto_increment,
`name` varchar(64) not null unique
)engine=InnoDB;

CREATE TABLE `Subject`(
`name` varchar(64) not null,
`question` varchar(255) default null,
`id` integer not null primary key auto_increment,
`question_number` integer default 0,
`exam_year` DATE default null,
`course_category` integer not null references `Category`(`id`),
`answer` varchar(128) default null
)engine=InnoDB;

CREATE TABLE `Options`(
`id` integer not null primary key auto_increment,
`A` varchar(255) default null,
`B` varchar(255) default null,
`C` varchar(255) default null,
`D` varchar(255) default null,
`E` varchar(255) default null,
`subject_id` integer not null references `Subject`(`id`)
)engine = InnoDB;


CREATE TABLE `Solution`(
`id` integer not null primary key auto_increment,
`subject` varchar(64) default null,
`exam_year` DATE default null,
`link` varchar(255) default null
)engine = InnoDB;


INSERT INTO `Category`(`name`) values('WAEC');
INSERT INTO `Category`(`name`) values('GCE');
INSERT INTO `Category`(`name`) values('JAMB');

INSERT INTO `Subject`(`name`, `question`, `question_number`,`exam_year`, `course_category`,`answer`) values(
'Physics', 'What is the value for gravitational force on the surface of the earth?',1, str_to_date('1-2-2002', '%m-%d-%Y'), 1, '10m/s');
INSERT INTO `Subject`(`name`, `question`, `question_number`,`exam_year`, `course_category`,`answer`) values(
'Physics', 'Who discovered the universal law of gravity?',2, str_to_date('1-2-2002', '%m-%d-%Y'), 1, 'Isaac Newton');


INSERT INTO `Subject`(`name`, `question`, `question_number`,`exam_year`, `course_category`,`answer`) values(
'Mathematics', 'How many sides does a cuboid have',1, str_to_date('1-2-2001', '%m-%d-%Y'), 1, '6 sides');

INSERT INTO `Subject`(`name`, `question`, `question_number`,`exam_year`, `course_category`,`answer`) values(
'Chemistry', 'What is the valence number of Potasium?',3, str_to_date('1-2-2001', '%m-%d-%Y'), 3, '1');
Insert into `Solution`(`exam_year`,`subject`, `link`) values(str_to_date('1-2-2001', '%m-%d-%Y'), 'Physics', 'physic/link');



Insert into `Solution`(`exam_year`,`subject`, `link`) values(str_to_date('1-2-2001', '%m-%d-%Y'), 'Physics', 'physic/link');

INSERT INTO `Subject`(`name`, `question`, `question_number`,`exam_year`, `course_category`,`answer`) values(
'Physics', 'Name a popular Physicist you know',1, str_to_date('01-02-2003', '%m-%d-%Y'), 1, 'Newton');

INSERT INTO `Subject`(`name`, `question`, `question_number`,`exam_year`, `course_category`,`answer`) values(
'Physics', 'Who discovered relativity?',2, str_to_date('01-02-2003', '%m-%d-%Y'), 1, 'Albert Einstein');

INSERT INTO `Subject`(`name`, `question`, `question_number`,`exam_year`, `course_category`,`answer`) values(
'Physics', 'Which of these does not affect boiling point of liquid?',3, str_to_date('01-02-2003', '%m-%d-%Y'), 1, 'Color of substance');

INSERT INTO `Subject`(`name`, `question`, `question_number`,`exam_year`, `course_category`,`answer`) values(
'Physics', 'The following is a property of electromagnetic waves',4, str_to_date('01-02-2003', '%m-%d-%Y'), 1, 'short wavelength');



Insert into `Solution`(`exam_year`,`subject`, `link`) values(str_to_date('1-2-2001', '%m-%d-%Y'), 'Physics', 'physic/link');

Insert into `Options`(`A`,`B`, `C`, `D`, `E`, `subject_id`) values('10m/s\u00B2', '2.5m/s\u00B2', '98m/s\u00B2', '7.8m/s\u00B2', 'None of the above',1);
Insert into `Options`(`A`,`B`, `C`, `D`, `E`, `subject_id`) values('Isaac Newton', 'James Clerk', 'Paschal', 'Galileo', 'None of the above',2);
Insert into `Options`(`A`,`B`, `C`, `D`, `E`, `subject_id`) values('6 sides', '12 sides', '4 sides', '13 sides', 'None of the above',3);
Insert into `Options`(`A`,`B`, `C`, `D`, `E`, `subject_id`) values('2', '1', '3', '4', '6',3);
Insert into `Options`(`A`,`B`, `C`, `D`, `E`, `subject_id`) values('James', 'Joule', 'Newton', 'Ronaldo', 'None of the above',4);
Insert into `Options`(`A`,`B`, `C`, `D`, `E`, `subject_id`) values('Maxwell', 'Joule', 'Albert Einstein', 'Faraday', 'Achimedes',5);
Insert into `Options`(`A`,`B`, `C`, `D`, `E`, `subject_id`) values('Pressure', 'Temparature', 'Nature of substance', 'Color of substance', 'None of the above',6);
Insert into `Options`(`A`,`B`, `C`, `D`, `E`, `subject_id`) values('Color substance', 'Prwssure', 'Impurities', 'Temperature', 'Humidity',7);
Insert into `Options`(`A`,`B`, `C`, `D`, `E`, `subject_id`) values('short wavelength', 'Long wavelength', 'Low penetrating power', 'Refactive index', 'Critical angle of refractive',8);




