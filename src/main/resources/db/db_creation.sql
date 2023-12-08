CREATE DATABASE IF NOT EXISTS university_schedule;
USE university_schedule;

CREATE TABLE IF NOT EXISTS teachers
(
    id   INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS study_group
(
    id         INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    group_code VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS students
(
    id       INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    email    VARCHAR(255) UNIQUE,
    name     VARCHAR(255),
    course   VARCHAR(10),
    group_id INT UNSIGNED,
    FOREIGN KEY (group_id) REFERENCES study_group (id)
);

CREATE TABLE IF NOT EXISTS disciplines
(
    id                    INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    cipher                VARCHAR(20) UNIQUE,
    name                  VARCHAR(255),
    facility_cipher       VARCHAR(10),
    cathedra_cipher       VARCHAR(10),
    lectures_hours        INT UNSIGNED,
    practice_hours        INT UNSIGNED,
    laboratory_hours      INT UNSIGNED,
    max_flow_students     INT UNSIGNED,
    max_group_students    INT UNSIGNED,
    max_subgroup_students INT UNSIGNED,
    max_students_count    INT UNSIGNED
);

CREATE TABLE IF NOT EXISTS schedule_date
(
    id        INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    week_type VARCHAR(11),
    week_day  VARCHAR(9),
    lesson    INT UNSIGNED,
    UNIQUE SCHEDULE_DATE_UQ (week_type, week_day, lesson)
);

CREATE TABLE IF NOT EXISTS teachers_disciplines
(
    teacher_id    INT UNSIGNED,
    discipline_id INT UNSIGNED,
    FOREIGN KEY (teacher_id) REFERENCES teachers (id),
    FOREIGN KEY (discipline_id) REFERENCES disciplines (id)
);

CREATE TABLE IF NOT EXISTS students_disciplines
(
    student_id    INT UNSIGNED,
    discipline_id INT UNSIGNED,
    FOREIGN KEY (student_id) REFERENCES students (id),
    FOREIGN KEY (discipline_id) REFERENCES disciplines (id)
);



CREATE TABLE IF NOT EXISTS schedule
(
    id               INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    discipline_id    INT UNSIGNED,
    schedule_date_id INT UNSIGNED,
    lesson_type      VARCHAR(10),
    students_count   INT UNSIGNED,
    group_no         INT UNSIGNED,
    subgroup_no      INT UNSIGNED,
    facility_type    VARCHAR(8),
    facility_address VARCHAR(255),
    FOREIGN KEY (discipline_id) REFERENCES disciplines (id),
    FOREIGN KEY (schedule_date_id) REFERENCES schedule_date (id)
);


CREATE TABLE IF NOT EXISTS groups_schedule
(
    id          INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    group_id    INT UNSIGNED,
    schedule_id INT UNSIGNED,
    FOREIGN KEY (group_id) REFERENCES study_group (id),
    FOREIGN KEY (schedule_id) REFERENCES schedule (id)
);

CREATE TABLE IF NOT EXISTS teachers_schedule
(
    id          INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    teacher_id  INT UNSIGNED,
    schedule_id INT UNSIGNED,
    FOREIGN KEY (teacher_id) REFERENCES teachers (id),
    FOREIGN KEY (schedule_id) REFERENCES schedule (id)
);
