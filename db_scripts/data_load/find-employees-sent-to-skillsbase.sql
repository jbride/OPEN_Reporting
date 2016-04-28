-- find students sent to skills base
select distinct email, first_name, last_name from student_assessment where processed_flag='Y' order by last_name