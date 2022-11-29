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
'Physics', 'What is the value for gravitational on the surface of the earth?',1, str_to_date('1-2-2002', '%m-%d-%Y'), 1, '10m/s');

INSERT INTO `Subject`(`name`, `question`, `question_number`,`exam_year`, `course_category`,`answer`) values(
'Mathematics', 'How many sides does a cuboid have',1, str_to_date('1-2-2001', '%m-%d-%Y'), 1, '6 sides');

INSERT INTO `Subject`(`name`, `question`, `question_number`,`exam_year`, `course_category`,`answer`) values(
'Chemistry', 'What is the valence number of Potasium?',1, str_to_date('1-2-2001', '%m-%d-%Y'), 3, '1');
Insert into `Solution`(`exam_year`,`subject`, `link`) values(str_to_date('1-2-2001', '%m-%d-%Y'), 'Physics', 'physic/link');



Insert into `Solution`(`exam_year`,`subject`, `link`) values(str_to_date('1-2-2001', '%m-%d-%Y'), 'Physics', 'physic/link');

INSERT INTO `Subject`(`name`, `question`, `question_number`,`exam_year`, `course_category`,`answer`) values(
'Physics', 'Name a popullar Physicist you know',2, str_to_date('01-02-2003', '%m-%d-%Y'), 1, 'Newton');
Insert into `Solution`(`exam_year`,`subject`, `link`) values(str_to_date('1-2-2001', '%m-%d-%Y'), 'Physics', 'physic/link');

Insert into `Options`(`A`,`B`, `C`, `D`, `E`, `subject_id`) values('Dalton', 'Abraham Lincoln', 'Newton', 'Ronaldo', 'None of the above',4);



