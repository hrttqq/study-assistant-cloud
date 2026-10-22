-- Repeatable seed data for a fresh study-exam database. IDs are fixed only for system seed rows.
INSERT INTO study_exam_category (id,category_code,category_name,description,sort) VALUES
(1001,'CIVIL_SERVICE','公务员考试','国家及地方公务员招录考试',10),
(1002,'POSTGRADUATE','研究生考试','研究生招生考试',20),
(1003,'PROFESSIONAL','职业资格','职业资格及相关考试',30),
(1004,'LANGUAGE','外语考试','语言水平考试',40)
ON DUPLICATE KEY UPDATE id=id;

INSERT INTO study_exam (id,category_id,exam_code,exam_name,description,sort) VALUES
(2001,1001,'NATIONAL_CIVIL_SERVICE','国家公务员考试','国家公务员招录考试',10),
(2002,1001,'GUANGDONG_CIVIL_SERVICE','广东省公务员考试','广东省公务员招录考试',20),
(2003,1002,'POSTGRADUATE_ENTRANCE','全国硕士研究生招生考试','硕士研究生招生考试',10),
(2004,1003,'TEACHER_QUALIFICATION','教师资格考试','教师资格相关考试',10),
(2005,1004,'CET4','大学英语四级考试','大学英语四级',10),
(2006,1004,'CET6','大学英语六级考试','大学英语六级',20)
ON DUPLICATE KEY UPDATE id=id;

INSERT INTO study_exam_attribute (id,exam_id,attribute_code,attribute_name,input_type,options_json,required,sort) VALUES
(3001,2001,'region','报考地区','text',NULL,0,10),
(3002,2002,'region','报考地区','text',NULL,0,10),
(3003,2003,'target_school','目标院校','text',NULL,0,10),
(3004,2003,'target_major','目标专业','text',NULL,0,20),
(3005,2004,'school_stage','学段','select',JSON_ARRAY(JSON_OBJECT('label','幼儿园','value','kindergarten'),JSON_OBJECT('label','小学','value','primary'),JSON_OBJECT('label','初中','value','junior'),JSON_OBJECT('label','高中','value','senior')),0,10),
(3006,2004,'subject','学科','text',NULL,0,20)
ON DUPLICATE KEY UPDATE id=id;
