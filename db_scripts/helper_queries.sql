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

# insert into CourseMappings values ("Application Development with EAP6 Final Assessment", null, "MWS-TECH-APD-EXAM-EAP6", "dokeos")

# select c.CourseID, c.CourseName from Courses c, CourseMappings cm where c.CourseID = cm.CourseID and cm.Source="dokeos" and cm.OldCourseCode="Application Development with EAP6 Final Assessment"