-- find students not sent to skills bases
select distinct email, first_name, last_name from student_assessment where processed_flag='N' and email not in (select student_email from student_qualification) order by last_name