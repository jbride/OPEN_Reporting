DELIMITER $$
CREATE DEFINER=`lms`@`%` PROCEDURE `refresh_QvExport`()
begin

-- update StudentAccreditations set accreditationtype = 'Active' where accreditationdate >= '2014-12-01' and accreditationtype = 'Expired';
update StudentAccreditations set accreditationtype = 'Active' where accreditationdate >= '2015-01-01' and accreditationtype = 'Expired';

drop table if exists lms_transactional.QvExport;

create table QvExport as
select distinct
 	concat(cast(left(DATE_FORMAT(assessmentdate, '%b'),3) as char),'-',cast(year(assessmentdate) as char)) as Month
	,year(assessmentdate) as Year
	,concat('FY ',right(cast(year(date_add(str_to_date(concat(month(assessmentdate),'/1/',year(assessmentdate)), '%m/%d/%Y'), interval 10 month)) as char),2)) as FY
 	,concat(lastname, ', ', firstname) as 'Full Name'
	,companyname
	,roles 'User Primary Job'
 	,PartnerType as 'User Primary Organization' 
	,region 'User Primary Domain'
	,country 'Country Code'
	,Email
	,c.CourseId Code
	,assessmentdate 'Start Date'
	,assessmentdate 'End Date'
	,case when s.email like '%@redhat.com' or companyname like '%red hat%' then 'Yes' else 'No' end RedHat
	,case when sa.studentid is null then 'No' else 'Yes' end 'Is Certification'
	,case when sa.studentid is not null then 'No' when sa.studentid is null then 'Yes' else 'No' end 'In Progress'
	,a.role Role
	,upper(a.track) 'Skills Track'
	,a.specialization Specialization
	,a.AccreditationName
	,case when region = 'Fed/Sled' then 'NA PUBLIC SECTOR' when region = 'NA' then 'NA COMM' else region end Geo
	,'Direct' as 'Channel Mix'
	,'None' as 'Split Business Unit'
    ,cp.partnertier tier
    ,s.alliancecode
 
from
	lms_reporting.Students s 
    left join lms_transactional.Companies cp
		on s.companyid = cp.companyid
--    join StudentCourses sc
-- 		on s.studentid = sc.studentid
	join (select max(studentcourseid) studentcourseid, studentid, courseid, languageid, max(assessmentdate) assessmentdate, assessmentresult, max(assessmentscore) assessmentscore, processed from lms_transactional.StudentCourses  group by studentid, courseid, languageid, assessmentresult, processed) sc
		on s.studentid = sc.studentid
	join lms_transactional.Courses c
		on sc.courseid = c.courseid
	left join lms_transactional.StudentAccreditations sa
		on sc.courseid = sa.courseid
		and sc.studentid = sa.studentid
	left join lms_transactional.AccreditationDefinitions a
		on sa.accreditationid = a.accreditationid
where
	-- assessmentdate >= date_sub(str_to_date(concat(month(now()),'/1/',year(now())), '%m/%d/%Y'), interval 2 year)
	 assessmentdate >= '2015-01-01' -- and assessmentdate < '2016-07-01'
    and CompanyName not like 'Red Hat%' and s.Email not like '%@redhat.com'
;



/*

union
select
 	concat(cast(left(DATE_FORMAT(e.enddate, '%b'),3) as char),'-',cast(year(e.enddate) as char)) as Month
	,year(e.enddate) as Year
	,concat('FY ',right(cast(year(date_add(str_to_date(concat(month(e.enddate),'/1/',year(e.enddate)), '%m/%d/%Y'), interval 10 month)) as char),2)) as FY
	,s.fullname as 'Full Name'
	,s.company
	,'' as 'User Primary Job'
	,e.partnertype as 'User Primary Organization'
	,s.region 'User Primary Domain'
	,s.country 'Country Code'
	,s.email Email
	,'' as Code
-- 	,'' as Code2
	,null as 'Start Date'
	,e.enddate as 'End Date'
	,case when s.email like '%@redhat.com' or s.company like '%red hat%' then 'Yes' else 'No' end as RedHat
	,'Yes' as 'Is Certification'
	,'No'
	,a.role Role
	,upper(a.track) as 'Skills Track'
	,a.specialization Specialization
	,a.Accreditation
	,case when s.region = 'Fed/Sled' then 'NA PUBLIC SECTOR' when s.region = 'NA' then 'NA COMM' else s.region end Geo
	,'Direct' as 'Channel Mix'
	,'None' as 'Split Business Unit'
    , tier
    , alliancecode
from 
	Students s
	join StudentAccreditations sa
		on s.email = sa.email
--  join (select distinct role, specialization, upper(track) track, accreditation from AccreditationCourses) a
-- 		on sa.accreditation = a.accreditation
 	join Accreditations a
 		on sa.accreditation = a.accreditation
	join eLabsImport e
		on sa.keepid = e.auto
where
	keeptype = 'eLab'
	and deleteflag = 'N'
    
union
select
 	concat(cast(left(DATE_FORMAT(e.date, '%b'),3) as char),'-',cast(year(e.date) as char)) as Month
	,year(e.date) as Year
	,concat('FY ',right(cast(year(date_add(str_to_date(concat(month(e.date),'/1/',year(e.date)), '%m/%d/%Y'), interval 10 month)) as char),2)) as FY
	,s.fullname as 'Full Name'
	,s.company
	,'' as 'User Primary Job'
	,null as 'User Primary Organization'
	,s.region 'User Primary Domain'
	,s.country 'Country Code'
	,s.email Email
	,'' as Code
-- 	,'' as Code2
	,null as 'Start Date'
	,e.date as 'End Date'
	,case when s.email like '%@redhat.com' or s.company like '%red hat%' then 'Yes' else 'No' end as RedHat
	,'Yes' as 'Is Certification'
	,'No'
	,a.role Role
	,upper(a.track) as 'Skills Track'
	,a.specialization Specialization
	,a.Accreditation
	,case when s.region = 'Fed/Sled' then 'NA PUBLIC SECTOR' when s.region = 'NA' then 'NA COMM' else s.region end Geo
	,'Direct' as 'Channel Mix'
	,'None' as 'Split Business Unit'
    , tier
    , alliancecode
from 
	Students s
	join StudentAccreditations sa
		on s.email = sa.email
 	join Accreditations a
 		on sa.accreditation = a.accreditation
	join SkillsExchange e
		on sa.keepid = e.id
where
	keeptype = 'SkillsExchange'
	and deleteflag = 'N'
;
*/


/* 
-- Export Files
use lms_transactional; select * from QvExport where email <= 'h%' order by email; -- 24288
use lms_transactional; select * from QvExport where email >= 'h%' and email <= 'o%' order by email; -- 24820
use lms_transactional; select * from QvExport where email >= 'o%' order by email; -- 22242
*/

-- select 13356+14027+12389;
-- select count(*) from QvExport where `is certification` = 'yes';
drop table Summary;
create table Summary as
select 
	region, 
	case when role = 'Sales' then 1 when role = 'Sales Engineer' then 2 when role = 'Delivery' then 3 when role = 'Advance Training' then 4 end as sort1,
	case when specialization = 'Data Center Infrastructure' then 1 when specialization = 'Middleware Solutions' then 2 when specialization = 'Cloud Infrastructure' then 3 when specialization = 'Advance Training' then 4 end as sort2,
	role, specialization, track, a.accreditationname,
	count(distinct case when accreditationdate >= '2014-03-01' and accreditationdate < '2015-03-01' then sa.studentid else null end) FY15,
	count(distinct case when accreditationdate >= '2015-03-01' and accreditationdate < '2016-03-01' then sa.studentid else null end) FY16,
	count(distinct case when accreditationdate >= '2016-03-01' and accreditationdate < '2017-03-01' then sa.studentid else null end) FY17,
	count(distinct sa.studentid) total
from 
	StudentAccreditations sa 
	join Students s 
		on sa.studentid = s.studentid
	join AccreditationDefinitions a
		on a.accreditationid = sa.accreditationid 
	left join Companies c 
		on s.companyid = c.companyid
-- 	join (select distinct specialization, role, track, accreditation, specialrule from AccreditationCourses) a 
-- 		on a.accreditation = sa.accreditation 
where	
	region is not null and role in ('Sales','Sales Engineer','Delivery','Advance Training')
	and s.email not like '%@redhat.com'
	and ifnull(c.companyname,'') not like '%red hat%'
    and accreditationtype = 'Active'
--    and SumTotalActive = 'Yes'
group by 
	region, 
	role, specialization, track, a.accreditationname;

drop table SummaryRedHat;
create table SummaryRedHat as
select 
	region, 
	case when role = 'Sales' then 1 when role = 'Sales Engineer' then 2 when role = 'Delivery' then 3 when role = 'Advance Training' then 4 end as sort1,
	case when specialization = 'Data Center Infrastructure' then 1 when specialization = 'Middleware Solutions' then 2 when specialization = 'Cloud Infrastructure' then 3 when specialization = 'Advance Training' then 4 end as sort2,
	role, specialization, track, a.accreditationname,
	count(distinct case when accreditationdate >= '2014-03-01' and accreditationdate < '2015-03-01' then sa.studentid else null end) FY15,
	count(distinct case when accreditationdate >= '2015-03-01' and accreditationdate < '2016-03-01' then sa.studentid else null end) FY16,
	count(distinct case when accreditationdate >= '2016-03-01' and accreditationdate < '2017-03-01' then sa.studentid else null end) FY17,
	count(distinct sa.studentid) total
from 
	StudentAccreditations sa 
	join Students s 
		on sa.studentid = s.studentid
	join AccreditationDefinitions a
		on a.accreditationid = sa.accreditationid 
	left join Companies c 
		on s.companyid = c.companyid
-- 	join (select distinct specialization, role, track, accreditation, specialrule from AccreditationCourses) a 
-- 		on a.accreditation = sa.accreditation 
where	
	region is not null and role in ('Sales','Sales Engineer','Delivery','Advance Training')
	and (s.email like '%@redhat.com' or ifnull(c.companyname,'') like '%red hat%')
    and accreditationtype = 'Active'
--    and SumTotalActive = 'Yes'
group by 
	region, 
	role, specialization, track, a.accreditationname;

-- select region, role, specialization, upper(track), fy15, fy16, fy17, total from Summary order by region, sort1, sort2, track, accreditationname;

-- select * from QvExport;

end$$
DELIMITER ;
