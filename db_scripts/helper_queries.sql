# select * from Companies where CompanyName = "Red Hat"

# alter table Students ADD Column SkillsbaseStatus TINYINT(1) NOT NULL DEFAULT 0 after SumTotalActive
# alter table Students ADD Column IpaStatus TINYINT(1) NOT NULL DEFAULT 0 after SkillsbaseStatus

# alter table Students drop column ActivationDate
alter table Students Add Column ActivationDate DATETIME DEFAULT CURRENT_TIMESTAMP after IpaStatus;
alter table Students Add Column DeActivationDate DATETIME after ActivationDate

# update StudentCourses set Processed=0 where StudentId=10000

# select StudentID from StudentCourses WHERE Processed=0 GROUP BY StudentID

# SELECT sc.StudentCourseID, sc.StudentID, sc.CourseID, sc.LanguageID, sc.AssessmentDate, sc.AssessmentResult, sc.AssessmentScore, sc.CourseName, sc.Processed, s.StudentID,s.Email,s.FirstName,s.LastName,s.CompanyID,s.Region,s.SubRegion,s.Country,s.Roles,s.SalesForceContactID,s.SalesForceActive,s.SumTotalID,s.SumTotalActive,s.SkillsbaseStatus,s.IpaStatus, l.languageid,l.languagename
# FROM StudentCourses sc, Students s, Languages l 
# WHERE sc.StudentID = s.StudentID AND sc.LanguageID = l.LanguageID AND sc.StudentID=10392 AND sc.AssessmentResult="Pass"
# order by sc.AssessmentDate DESC


# select * from Students where email like '%redhat.com%';
# delete from Students where studentID=42772

# insert into CourseMappings values ( "Graphical Data Mapping using JBoss Fuse Data Mapper", "Graphical Data Mapping using JBoss Fuse Data Mapper", "MWS-DEL-ADD-DM-EXAM", "na", NOW()  );

# select sa.studentId, sa.AccreditationDate, ad.AccreditationName from StudentAccreditations sa , AccreditationDefinitions ad where  sa.AccreditationId=ad.AccreditationId and sa.AccreditationDate < DATE_SUB(NOW(),INTERVAL 2 YEAR);
# select count(studentId), studentId from StudentAccreditations group by studentId order by count(studentId) asc;
# select ad.AccreditationName, sa.AccreditationDate, sa.AccreditationType, sa.CourseId from StudentAccreditations sa, AccreditationDefinitions ad where sa.studentid=18836 and sa.AccreditationId=ad.AccreditationId;
# select count(ad.AccreditationName), ad.AccreditationName from StudentAccreditations sa, AccreditationDefinitions ad where sa.AccreditationId=ad.AccreditationId group by ad.AccreditationName order by count(ad.AccreditationName) asc;
# select count(sc.studentId) from StudentCourses sc, Students s where s.StudentId=sc.StudentId and s.email like "%@redhat.com";
# select s.email, sa.studentId, sa.AccreditationDate, ad.AccreditationName from Students s, StudentAccreditations sa , AccreditationDefinitions ad where s.studentId = sa.StudentId and sa.AccreditationId=ad.AccreditationId and ad.AccreditationName = "Red Hat Delivery Specialist - Business Process Automation";

# select count(sc.studentId) from StudentCourses sc, Courses c where c.CourseId=sc.CourseId and c.CourseName="Red Hat Sales Specialist - Platform";

# select sc.studentId, sc.courseId, sc.assessmentdate, sc.createdate, s.email, s.firstname, s.lastname  from StudentCourses sc, Students s where sc.studentId = s.studentId order by sc.createdate desc limit 10;

# update Students set ipaStatus=0 where createdate > DATE_SUB(NOW(),INTERVAL 1 DAY);

# select c.courseName, sc.courseId, sc.assessmentdate, sc.createdate, s.email, s.firstname, s.lastname  from Courses c, StudentCourses sc, Students s where c.courseId=sc.courseId and sc.studentId = s.studentId and s.email="wdovey@redhat.com" order by sc.createdate

# List latest middleware course completions
# select c.courseName, sc.courseId, sc.assessmentdate, sc.createdate, s.email, sc.AssessmentResult, sc.AssessmentScore  from Courses c, StudentCourses sc, Students s where c.courseId=sc.courseId and sc.studentId = s.studentId and c.CourseId like "MWS-%" order by sc.createdate desc limit 100;

# view attributes of latest RHT students
# select email, roles, region, createDate from Students where email like '%redhat.com%' order by createDate desc limit 30;

# Totara
# select count(u.email), c.fullname from mdl_course_info_data ci, mdl_course c, mdl_course_completions cc, mdl_user u where cc.course=ci.courseid and cc.course=c.id and u.id = cc.userid and status=50 and ci.data='GPTE' group by c.fullname;
# select cc.id, u.email, u.firstname, u.lastname, cc.userid, cc.course, c.fullname, c.shortname from mdl_course_info_data ci, mdl_course c, mdl_course_completions cc, mdl_user u where cc.course=ci.courseid and cc.course=c.id and u.id = cc.userid and status=50 and ci.data='GPTE' order by cc.id desc limit 10;
