use lms;

create view StudentAccreditationDetails as
select
	s.email, s.fullname, s.firstname, s.lastname, s.company, s.companycode, s.region, s.subregion, s.country, s.partnertype, s.sfdcid, s.sumtotalid, s.tier,
    case when s.email like '%redhat.com' or ifnull(s.company,'') like '%red hat%' then 'Yes' else 'No' end as redhat,
    sa.accreditation, sa.enddate as accreditationdate
from 
	StudentAccreditations sa 
	join Students s 
		on sa.email = s.email 
	join Accreditations a
		on a.accreditation = sa.accreditation;


create view StudentCourseCompletionDetails as
select
	s.email, s.fullname, s.firstname, s.lastname, s.company, s.companycode, s.region, s.subregion, s.country, s.partnertype, s.sfdcid, s.sumtotalid, s.tier,
    case when s.email like '%redhat.com' or ifnull(s.company,'') like '%red hat%' then 'Yes' else 'No' end as redhat,
    cc.coursename, cc.coursetype, cc.enddate as coursedate
from 
	CompletedCourses cc 
	join Students s 
		on cc.email = s.email;

drop table if exists StudentCourseCompletionAccreditationDetails;

create table StudentCourseCompletionAccreditationDetails as 
select distinct
	s.email, s.fullname, s.firstname, s.lastname, s.company, s.companycode, ifnull(s.region,'Unknown') as region, s.subregion, s.country, s.partnertype, s.sfdcid, s.sumtotalid, s.tier,     
    case when s.email like '%redhat.com' or ifnull(s.company,'') like '%red hat%' then 'Yes' else 'No' end as redhat,     
    cc.coursename as coursename, cc.coursetype, cast(cc.enddate as date) as coursedate, concat_ws('-',cc.id,cc.coursetype) completedcourseid,     
    a.accreditation, a.specialization, a.role, a.track, cast(sa.enddate as date) as accreditationdate,  
    case when a.accreditation is not null then 1 else 0 end as AccreditationComplete,     
    concat(s.email, '-', a.accreditation) accreditationid 
/*  
	case    
		when a.accreditation is not null and sa.enddate >= '2013-03-01' and sa.enddate < '2014-03-01' then 2014   when a.accreditation is not null and sa.enddate >= '2014-03-01' and sa.enddate < '2015-03-01' then 2015   when a.accreditation is not null and sa.enddate >= '2015-03-01' and sa.enddate < '2016-03-01' then 2016  end as AccreditationFiscalYear,      
	case    
		when cc.enddate >= '2013-03-01' and cc.enddate < '2014-03-01' then 2014   when cc.enddate >= '2014-03-01' and cc.enddate < '2015-03-01' then 2105   when cc.enddate >= '2015-03-01' and cc.enddate < '2016-03-01' then 2016  end as CourseCompletionFiscalYear 
        */  
from   
	CompletedCourses cc   
    join Students s    
		on cc.email = s.email  
	left join  StudentAccreditations sa          
		on cc.email = sa.email   
		and cc.id = sa.keepid         
        and cc.coursetype = sa.keeptype  
	left join Accreditations a         
		on sa.accreditation = a.accreditation;

alter table StudentCourseCompletionAccreditationDetails add primary key (email, completedcourseid, accreditationid);

alter table StudentCourseCompletionAccreditationDetails   
	add RoleCode char(3),  
    add SpecializationCode char(5),     
    add TrackCode char(30),     
    add SpecializationTrack char(50),  
    add AccreditationTotalFY14 int,   
    add AccreditationTotalFY15 int,      
    add AccreditationTotalFY16 int,     
    add AccreditationFiscalYear int,     
    add AccreditationFiscalQuarter int,     
    add AccreditationFiscalMonth char(7),     
    add AccreditationFiscalYearQuarter char(7),     
    add CourseCompletionFiscalYear int,     
    add CourseCompletionFiscalQuarter int,     
    add CourseCompletionFiscalMonth char(7),  
    add RegionRoleSpecializationTrack varchar(250);

update StudentCourseCompletionAccreditationDetails set   
	AccreditationTotalFY14 = case when accreditationdate >= '2013-03-01' and accreditationdate < '2014-03-01' then 1 else 0 end,  
    AccreditationTotalFY15 = case when accreditationdate >= '2014-03-01' and accreditationdate < '2015-03-01' then 1 else 0 end,  
    AccreditationTotalFY16 = case when accreditationdate >= '2015-03-01' and accreditationdate < '2016-03-01' then 1 else 0 end,
    AccreditationFiscalYear = case when accreditationdate >= '2013-03-01' and accreditationdate < '2014-03-01' then 2014 when accreditationdate >= '2014-03-01' and accreditationdate < '2015-03-01' then 2015 when accreditationdate >= '2015-03-01' and accreditationdate < '2016-03-01' then 2016 end,     
    AccreditationFiscalQuarter = case when month(accreditationdate) in (3,4,5) then 1 when month(accreditationdate) in (6,7,8) then 2 when month(accreditationdate) in (9,10,11) then 3 when month(accreditationdate) in (12,1,2) then 4 end,     
    AccreditationFiscalMonth = case when accreditationdate is not null then concat(year(accreditationdate),'-',right(concat('0',cast(month(accreditationdate) as char)),2)) else null end, 
    --    CourseCompletionFiscalYear = case when coursedate >= '2013-03-01' and coursedate < '2014-03-01' then 2014 when coursedate >= '2014-03-01' and coursedate < '2015-03-01' then 2015 when coursedate >= '2015-03-01' and coursedate < '2016-03-01' then 2016 end,     
    CourseCompletionFiscalQuarter = case when month(coursedate) in (3,4,5) then 1 when month(coursedate) in (6,7,8) then 2 when month(coursedate) in (9,10,11) then 3 when month(coursedate) in (12,1,2) then 4 end,     
    CourseCompletionFiscalMonth = case when coursedate is not null then concat(year(coursedate),'-',right(concat('0',cast(month(coursedate) as char)),2)) else null end,  RoleCode = case role when 'Sales' then 'S' when 'Delivery' then 'D' when 'Sales Engineer' then 'SE' when 'Advanced Training' then 'AT' end,  
    SpecializationCode = case specialization when 'Data Center Infrastructure' then 'DCI' when 'Middleware Solutions' then 'MW' when 'Cloud Infrastructure' then 'CI' when 'Advanced Training' then 'AT' else specialization end,  
    TrackCode = case track    when 'Business Process Automation' then 'BPA'         when 'Cloud Management' then 'Cloud Mgmt'         when 'Enterprise Messaging with jBoss A-MQ' then 'Ent Messaging'         when 'IAAS' then 'IaaS'         when 'Middleware Application Development' then 'MW App Dev'         when 'Middleware Integration Services' then 'MW Int Services'         when 'Middleware Migration' then 'MW Migration'         when 'Mobile Development' then 'Mobile Dev'         when 'PAAS' then 'PaaS'         when 'PAAS Development' then 'Paas Dev'         when 'Platform' then 'Platform'         when 'Platform Migration' then 'Platform Migration'         when 'RHEL for SAP Hana' then 'SAP Hana'         when 'RHEL Atomic Host and Containers' then 'RHEL Atomic'                 when 'Storage' then 'Storage'         when 'Virtualization' then 'Virtualization' end,  
    RegionRoleSpecializationTrack = concat(Region, ' - ', role, ' - ', specialization, ' - ', track);
    
update StudentCourseCompletionAccreditationDetails set partnertype = 'Unknown' where partnertype not in ('Corporate Reseller','Distributor','DMR','ISV','OEM','Reseller','Service Provider','SI','Solution Provider','Systems Integrator','Training','VAR') or partnertype is null;
update StudentCourseCompletionAccreditationDetails set tier = 'Unknown' where tier is null;
update StudentCourseCompletionAccreditationDetails set specializationtrack = concat(specializationcode, ' - ', trackcode);
update StudentCourseCompletionAccreditationDetails set AccreditationFiscalYearQuarter = concat(AccreditationFiscalYear,'-',AccreditationFiscalQuarter);

create index idx_email on StudentCourseCompletionAccreditationDetails (email);
create index idx_fullname on StudentCourseCompletionAccreditationDetails (fullname);
create index idx_firstname on StudentCourseCompletionAccreditationDetails (firstname);
create index idx_lastname on StudentCourseCompletionAccreditationDetails (lastname);
create index idx_company on StudentCourseCompletionAccreditationDetails (company);
create index idx_companycode on StudentCourseCompletionAccreditationDetails (companycode);
create index idx_region on StudentCourseCompletionAccreditationDetails (region);
create index idx_subregion on StudentCourseCompletionAccreditationDetails (subregion);
create index idx_country on StudentCourseCompletionAccreditationDetails (country);
create index idx_regionsubregioncountrycompany on StudentCourseCompletionAccreditationDetails (region, subregion, country, company);
create index idx_partnertype on StudentCourseCompletionAccreditationDetails (partnertype);
create index idx_sfdcid on StudentCourseCompletionAccreditationDetails (sfdcid);
create index idx_sumtotalid on StudentCourseCompletionAccreditationDetails (sumtotalid);
create index idx_tier on StudentCourseCompletionAccreditationDetails (tier);
create index idx_redhat on StudentCourseCompletionAccreditationDetails (redhat);
create index idx_coursename on StudentCourseCompletionAccreditationDetails (coursename);
create index idx_coursetype on StudentCourseCompletionAccreditationDetails (coursetype);
create index idx_coursedate on StudentCourseCompletionAccreditationDetails (coursedate);
create index idx_completedcourseid on StudentCourseCompletionAccreditationDetails (completedcourseid);
create index idx_accreditation on StudentCourseCompletionAccreditationDetails (accreditation);
create index idx_role on StudentCourseCompletionAccreditationDetails (role);
create index idx_specialization on StudentCourseCompletionAccreditationDetails (specialization);
create index idx_track on StudentCourseCompletionAccreditationDetails (track);
create index idx_specializationtrack on StudentCourseCompletionAccreditationDetails (specializationtrack);
create index idx_rolespecializationtrack on StudentCourseCompletionAccreditationDetails (role, specialization, track);
create index idx_accreditationdate on StudentCourseCompletionAccreditationDetails (accreditationdate);
create index idx_accreditationcomplete on StudentCourseCompletionAccreditationDetails (accreditationcomplete);
create index idx_accreditationid on StudentCourseCompletionAccreditationDetails (accreditationid);
create index idx_AccreditationFiscalYear on StudentCourseCompletionAccreditationDetails (AccreditationFiscalYear);
create index idx_AccreditationFiscalQuarter on StudentCourseCompletionAccreditationDetails (AccreditationFiscalQuarter);
create index idx_AccreditationFiscalMonth on StudentCourseCompletionAccreditationDetails (AccreditationFiscalMonth);
create index idx_AccreditationFiscalYQM on StudentCourseCompletionAccreditationDetails (AccreditationFiscalYear,AccreditationFiscalQuarter,AccreditationFiscalMonth);
create index idx_CourseCompletionFiscalYear on StudentCourseCompletionAccreditationDetails (CourseCompletionFiscalYear);
create index idx_CourseCompletionFiscalQuarter on StudentCourseCompletionAccreditationDetails (CourseCompletionFiscalQuarter);
create index idx_CourseCompletionFiscalMonth on StudentCourseCompletionAccreditationDetails (CourseCompletionFiscalMonth);
create index idx_CourseCompletionFiscalYQM on StudentCourseCompletionAccreditationDetails (CourseCompletionFiscalYear,CourseCompletionFiscalQuarter,CourseCompletionFiscalMonth);


-- --------------------------------------------------
-- Expired
-- --------------------------------------------------
drop table if exists StudentCourseCompletionAccreditationDetailsExpired;

create table StudentCourseCompletionAccreditationDetailsExpired as 
select  
	s.email, s.fullname, s.firstname, s.lastname, s.company, s.companycode, ifnull(s.region,'Unknown') as region, s.subregion, s.country, s.partnertype, s.sfdcid, s.sumtotalid, s.tier,     
	case when s.email like '%redhat.com' or ifnull(s.company,'') like '%red hat%' then 'Yes' else 'No' end as redhat,     
    cc.coursename as coursename, cc.coursetype, cast(cc.enddate as date) as coursedate, concat_ws('-',cc.id,cc.coursetype) completedcourseid,     
    a.accreditation, a.specialization, a.role, a.track, cast(sa.enddate as date) as accreditationdate,  
    case when a.accreditation is not null then 1 else 0 end as AccreditationComplete,     
    concat(s.email, '-', a.accreditation) accreditationid 
    /*  case    when a.accreditation is not null and sa.enddate >= '2013-03-01' and sa.enddate < '2014-03-01' then 2014   when a.accreditation is not null and sa.enddate >= '2014-03-01' and sa.enddate < '2015-03-01' then 2015   when a.accreditation is not null and sa.enddate >= '2015-03-01' and sa.enddate < '2016-03-01' then 2016  end as AccreditationFiscalYear,      case    when cc.enddate >= '2013-03-01' and cc.enddate < '2014-03-01' then 2014   when cc.enddate >= '2014-03-01' and cc.enddate < '2015-03-01' then 2105   when cc.enddate >= '2015-03-01' and cc.enddate < '2016-03-01' then 2016  end as CourseCompletionFiscalYear */  
from   
	CompletedCourses cc   
    join Students s    
		on cc.email = s.email  
	left join   
		StudentAccreditationsExpired sa          
        on cc.email = sa.email   and cc.id = sa.keepid         
        and cc.coursetype = sa.keeptype  
	left join    
		Accreditations a         
        on sa.accreditation = a.accreditation;

alter table StudentCourseCompletionAccreditationDetailsExpired add primary key (email, completedcourseid, accreditationid);

alter table StudentCourseCompletionAccreditationDetailsExpired   
	add RoleCode char(3),  
    add SpecializationCode char(5),     
    add TrackCode char(30),     
    add SpecializationTrack char(50),  
    add AccreditationTotalFY14 int,   
    add AccreditationTotalFY15 int,      
    add AccreditationTotalFY16 int,     
    add AccreditationFiscalYear int,     
    add AccreditationFiscalQuarter int,     
    add AccreditationFiscalMonth char(7),     
    add AccreditationFiscalYearQuarter char(7),     
    add CourseCompletionFiscalYear int,     
    add CourseCompletionFiscalQuarter int,     
    add CourseCompletionFiscalMonth char(7),  
    add RegionRoleSpecializationTrack varchar(250);

update StudentCourseCompletionAccreditationDetailsExpired set   
	AccreditationTotalFY14 = case when accreditationdate >= '2013-03-01' and accreditationdate < '2014-03-01' then 1 else 0 end,  
    AccreditationTotalFY15 = case when accreditationdate >= '2014-03-01' and accreditationdate < '2015-03-01' then 1 else 0 end,  
    AccreditationTotalFY16 = case when accreditationdate >= '2015-03-01' and accreditationdate < '2016-03-01' then 1 else 0 end,
    AccreditationFiscalYear = case when accreditationdate >= '2013-03-01' and accreditationdate < '2014-03-01' then 2014 when accreditationdate >= '2014-03-01' and accreditationdate < '2015-03-01' then 2015 when accreditationdate >= '2015-03-01' and accreditationdate < '2016-03-01' then 2016 end,     
    AccreditationFiscalQuarter = case when month(accreditationdate) in (3,4,5) then 1 when month(accreditationdate) in (6,7,8) then 2 when month(accreditationdate) in (9,10,11) then 3 when month(accreditationdate) in (12,1,2) then 4 end,     
    AccreditationFiscalMonth = case when accreditationdate is not null then concat(year(accreditationdate),'-',right(concat('0',cast(month(accreditationdate) as char)),2)) else null end, 
    --    CourseCompletionFiscalYear = case when coursedate >= '2013-03-01' and coursedate < '2014-03-01' then 2014 when coursedate >= '2014-03-01' and coursedate < '2015-03-01' then 2015 when coursedate >= '2015-03-01' and coursedate < '2016-03-01' then 2016 end,     
    CourseCompletionFiscalQuarter = case when month(coursedate) in (3,4,5) then 1 when month(coursedate) in (6,7,8) then 2 when month(coursedate) in (9,10,11) then 3 when month(coursedate) in (12,1,2) then 4 end,     
    CourseCompletionFiscalMonth = case when coursedate is not null then concat(year(coursedate),'-',right(concat('0',cast(month(coursedate) as char)),2)) else null end,  RoleCode = case role when 'Sales' then 'S' when 'Delivery' then 'D' when 'Sales Engineer' then 'SE' when 'Advanced Training' then 'AT' end,  
    SpecializationCode = case specialization when 'Data Center Infrastructure' then 'DCI' when 'Middleware Solutions' then 'MW' when 'Cloud Infrastructure' then 'CI' when 'Advanced Training' then 'AT' else specialization end,  
    TrackCode = case track    when 'Business Process Automation' then 'BPA'         when 'Cloud Management' then 'Cloud Mgmt'         when 'Enterprise Messaging with jBoss A-MQ' then 'Ent Messaging'         when 'IAAS' then 'IaaS'         when 'Middleware Application Development' then 'MW App Dev'         when 'Middleware Integration Services' then 'MW Int Services'         when 'Middleware Migration' then 'MW Migration'         when 'Mobile Development' then 'Mobile Dev'         when 'PAAS' then 'PaaS'         when 'PAAS Development' then 'Paas Dev'         when 'Platform' then 'Platform'         when 'Platform Migration' then 'Platform Migration'         when 'RHEL for SAP Hana' then 'SAP Hana'         when 'RHEL Atomic Host and Containers' then 'RHEL Atomic'                 when 'Storage' then 'Storage'         when 'Virtualization' then 'Virtualization' end,  
    RegionRoleSpecializationTrack = concat(Region, ' - ', role, ' - ', specialization, ' - ', track);
    
update StudentCourseCompletionAccreditationDetailsExpired set partnertype = 'Unknown' where partnertype not in ('Corporate Reseller','Distributor','DMR','ISV','OEM','Reseller','Service Provider','SI','Solution Provider','Systems Integrator','Training','VAR') or partnertype is null;
update StudentCourseCompletionAccreditationDetailsExpired set tier = 'Unknown' where tier is null;
update StudentCourseCompletionAccreditationDetailsExpired set specializationtrack = concat(specializationcode, ' - ', trackcode);
update StudentCourseCompletionAccreditationDetailsExpired set AccreditationFiscalYearQuarter = concat(AccreditationFiscalYear,'-',AccreditationFiscalQuarter);

create index idx_email on StudentCourseCompletionAccreditationDetailsExpired (email);
create index idx_fullname on StudentCourseCompletionAccreditationDetailsExpired (fullname);
create index idx_firstname on StudentCourseCompletionAccreditationDetailsExpired (firstname);
create index idx_lastname on StudentCourseCompletionAccreditationDetailsExpired (lastname);
create index idx_company on StudentCourseCompletionAccreditationDetailsExpired (company);
create index idx_companycode on StudentCourseCompletionAccreditationDetailsExpired (companycode);
create index idx_region on StudentCourseCompletionAccreditationDetailsExpired (region);
create index idx_subregion on StudentCourseCompletionAccreditationDetailsExpired (subregion);
create index idx_country on StudentCourseCompletionAccreditationDetailsExpired (country);
create index idx_regionsubregioncountrycompany on StudentCourseCompletionAccreditationDetailsExpired (region, subregion, country, company);
create index idx_partnertype on StudentCourseCompletionAccreditationDetailsExpired (partnertype);
create index idx_sfdcid on StudentCourseCompletionAccreditationDetailsExpired (sfdcid);
create index idx_sumtotalid on StudentCourseCompletionAccreditationDetailsExpired (sumtotalid);
create index idx_tier on StudentCourseCompletionAccreditationDetailsExpired (tier);
create index idx_redhat on StudentCourseCompletionAccreditationDetailsExpired (redhat);
create index idx_coursename on StudentCourseCompletionAccreditationDetailsExpired (coursename);
create index idx_coursetype on StudentCourseCompletionAccreditationDetailsExpired (coursetype);
create index idx_coursedate on StudentCourseCompletionAccreditationDetailsExpired (coursedate);
create index idx_completedcourseid on StudentCourseCompletionAccreditationDetailsExpired (completedcourseid);
create index idx_accreditation on StudentCourseCompletionAccreditationDetailsExpired (accreditation);
create index idx_role on StudentCourseCompletionAccreditationDetailsExpired (role);
create index idx_specialization on StudentCourseCompletionAccreditationDetailsExpired (specialization);
create index idx_track on StudentCourseCompletionAccreditationDetailsExpired (track);
create index idx_specializationtrack on StudentCourseCompletionAccreditationDetailsExpired (specializationtrack);
create index idx_rolespecializationtrack on StudentCourseCompletionAccreditationDetailsExpired (role, specialization, track);
create index idx_accreditationdate on StudentCourseCompletionAccreditationDetailsExpired (accreditationdate);
create index idx_accreditationcomplete on StudentCourseCompletionAccreditationDetailsExpired (accreditationcomplete);
create index idx_accreditationid on StudentCourseCompletionAccreditationDetailsExpired (accreditationid);
create index idx_AccreditationFiscalYear on StudentCourseCompletionAccreditationDetailsExpired (AccreditationFiscalYear);
create index idx_AccreditationFiscalQuarter on StudentCourseCompletionAccreditationDetailsExpired (AccreditationFiscalQuarter);
create index idx_AccreditationFiscalMonth on StudentCourseCompletionAccreditationDetailsExpired (AccreditationFiscalMonth);
create index idx_AccreditationFiscalYQM on StudentCourseCompletionAccreditationDetailsExpired (AccreditationFiscalYear,AccreditationFiscalQuarter,AccreditationFiscalMonth);
create index idx_CourseCompletionFiscalYear on StudentCourseCompletionAccreditationDetailsExpired (CourseCompletionFiscalYear);
create index idx_CourseCompletionFiscalQuarter on StudentCourseCompletionAccreditationDetailsExpired (CourseCompletionFiscalQuarter);
create index idx_CourseCompletionFiscalMonth on StudentCourseCompletionAccreditationDetailsExpired (CourseCompletionFiscalMonth);
create index idx_CourseCompletionFiscalYQM on StudentCourseCompletionAccreditationDetailsExpired (CourseCompletionFiscalYear,CourseCompletionFiscalQuarter,CourseCompletionFiscalMonth);


