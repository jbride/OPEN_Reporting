use lms;

-------------------------------------------------- 
-- SumTotal eLearning Processing
-------------------------------------------------- 
-- create clustered index
-- create clustered index idx1 on SumTotal (dimuser_empemaildetail, dimactivity_codedetail, factattempt_attemptenddtdetail desc)
-- drop index idx1 on SumTotal 
-- create index idx1 on SumTotal (deleteflag desc, dimuser_empemaildetail, dimactivity_codedetail, factattempt_attemptenddtdetail desc)

/*
-- create view stv
alter view stv as
SELECT 
	`dimUser_EmpEmailDetail` as email
	,`dimUser_EmpFullDetail` as fullname
	,dimUser_EmpFNameDetail firstname
	,dimUser_EmpLNameDetail lastname
	,newfirstname
	,newlastname
	,newfullname
	,newcoursecode
	,accreditation
	,`dimActivity_CodeDetail` as activitycode
	,`dimActivity_RootActivityNameDetail` as activity
	, deleteflag
	,newpartnertype
	,`dimUser_OptEmp_Txt3Detail` as txt3
	,newcompany
	,newgeo
	,`dimUser_EmpCntryDetail` as country
	,newcountry
	,left(datename(mm, '1/1/2014'),3)+'-'+cast(year(`factAttempt_attemptEndDtDetail`) as varchar) as Month
	,year(`factAttempt_attemptEndDtDetail`) as Year
	,'FY '+right(cast(year(dateadd(mm, 10, `factAttempt_attemptEndDtDetail`)) as varchar),2) as FY
	,`dimUser_EmpNoDetail` as empno
	-- ,`dimUser_OptEmp_Txt2Detail` as txt2
	-- 	,case when dimUser_PrimaryJobNameDetail = 'Unknown' then 'Unkown' else left(dimUser_PrimaryJobNameDetail,patindex('%/%',dimUser_PrimaryJobNameDetail)-2) end role
	-- 	,case when dimUser_PrimaryJobNameDetail = 'Unknown' then 'Unkown' else ltrim(rtrim(substring(dimUser_PrimaryJobNameDetail,patindex('%/%',dimUser_PrimaryJobNameDetail)+2,50))) end specialization
	,`dimUser_PrimaryJobNameDetail` as jobname
	,`dimUser_PrimaryOrgNameDetail` as orgname
	,`dimUser_PrimaryDomNameDetail` as domname
	,`factAttempt_attemptStartDtDetail` as startdate
	,`factAttempt_attemptEndDtDetail` as enddate
	,`factAttempt_attemptExprDtDetail` as expdate
	-- ,`L10NAct_NameDetail` as l10act
	,`dimActivity_IsCertificationDetail` as iscert
	, id
	, modifynote
	, tempflag

FROM 
	SumTotal
where 
	deleteflag = 'N'
go
*/

-------------------------------------------------- 
-- SumTotal eLearning Cleanup
-------------------------------------------------- 
update SumTotal set deleteflag = 'N', modifynote = null;
update SumTotal set deleteflag = 'Y', modifynote = '0 - eLab Module' where dimActivity_CodeDetail like 'DCI-SE-PLT-TCH%' or dimActivity_CodeDetail like 'DCI-TECH-VIR-FST%%' or dimActivity_CodeDetail like 'MWS-TECH-BPA-ELAB%';

-- fix null expdate
update stv set expdate = null where expdate = '1899-12-30 00:00:00.000';
update stv set startdate = null where startdate = '1899-12-30 00:00:00.000';
update stv set enddate = null where enddate = '1899-12-30 00:00:00.000';

-------------------------------------------------- 
-- Create Courses
-------------------------------------------------- 
-- if exists (select * from sys.objects where OBJECT_ID = OBJECT_ID(N'`dbo`.`Courses`') and type in (N'U'))
drop table if exists Courses;

create table Courses as
select 
	distinct
	ifnull(v.newcode, s.activitycode) as CourseCode
from	
	stv s
	left join coursemappingv v
		on s.activitycode = v.oldcode
where
	length(ifnull(v.newcode, s.activitycode)) > 0
    and left(activity,1) not in ('1','2','3','4','5','6','7','8','9')
order by 
	1;

-- delete from Courses where coursecode not like '%exam%' and coursecode not like '%asm%';

alter table Courses 
	 add column CourseCodeNoLanguage varchar(50)
	,add column CourseName varchar(200)
	,add column Specialization varchar(50) 
	,add column SpecializationName varchar(50)
	,add column Role varchar(50)
	,add column RoleName varchar(50)
	,add column Track varchar(50)
	,add column TrackName varchar(50)
	,add column Module varchar(50)
	,add column Product varchar(50)
	,add column Language varchar(50)
	,add column ParseString varchar(200)	
;

update 
	Courses
set
	Courses.CourseName = (
		select distinct s.activity from stv s join 
			(select activitycode, max(enddate) enddate from stv where length(ltrim(rtrim(activity))) > 0 group by activitycode) t
			on s.activitycode = t.activitycode and s.enddate = t.enddate
            where Courses.CourseCode = t.activitycode
            ) 
where
	Courses.Coursename is null;
    
-- Update ParseString
update Courses set parsestring = coursecode;

-- Specialization
update
	Courses
set
	specialization = left(parsestring,locate('-',parsestring)-1),
	parsestring = substring(parsestring, locate('-',parsestring)+1, 50)
where
	parsestring is not null
	and parsestring like '%-%';

-- Role
update
	Courses
set
	role = left(parsestring,locate('-',parsestring)-1),
	parsestring = substring(parsestring, locate('-',parsestring)+1, 50)
where
	parsestring is not null
	and parsestring like '%-%';

-- Track
update
	Courses
set
	track = left(parsestring,locate('-',parsestring)-1),
	parsestring = substring(parsestring, locate('-',parsestring)+1, 50)
where
	parsestring is not null
	and parsestring like '%-%';

-- Module
update
	Courses
set
	module = left(parsestring,locate('-',parsestring)-1),
	parsestring = substring(parsestring, locate('-',parsestring)+1, 50)
where
	parsestring is not null
	and parsestring like '%-%';

update
	Courses
set
	module = left(parsestring, length(parsestring)-3),
	parsestring = right(parsestring, 2)
where
	parsestring is not null
	and parsestring like '%_%'
	and module is null;

-- Product
update
	Courses
set
	product = left(parsestring,locate('-',parsestring)-1),
	parsestring = substring(parsestring, locate('-',parsestring)+1, 50)
where
	parsestring is not null
	and parsestring like '%-%';

-- Language
update
	Courses
set
	language = ltrim(rtrim(parsestring))
where
	parsestring is not null;

-- Update coursecodenolanguage
update
	Courses
set
	CourseCodeNoLanguage = concat_ws('-', ltrim(rtrim(specialization)), ltrim(rtrim(role)), ltrim(rtrim(track)), ltrim(rtrim(module)), ltrim(rtrim(product)));

update
	Courses
set
	CourseCodeNoLanguage = left(coursecode, length(coursecode)-3) 
where
	CourseCodeNoLanguage is null;

update
	Courses
set
	product = 'RHL'
where
	product is null
	and coursecodenolanguage in ('DCI-S-PLT-L2','','DCI-S-PLT-PRIPOS','DCI-S-PLT-SAL','DCI-SE-PLT-L2','DCI-SE-PLT-PRIPOS','DCI-SE-PLT-SAL');

update
	Courses
set
	product = 'INT'
where
	product is null
	and coursecodenolanguage in ('MWS-S-MWM-ASM','MWS-S-MWM-BAS','MWS-SE-MWM-ASM','MWS-SE-MWM-BAS');

update
	Courses
set
	CourseCodeNoLanguage = concat_ws('-', ltrim(rtrim(specialization)), ltrim(rtrim(role)), ltrim(rtrim(track)), ltrim(rtrim(module)), ltrim(rtrim(product)))
where
	coursecodenolanguage in ('DCI-S-PLT-L2','','DCI-S-PLT-PRIPOS','DCI-S-PLT-SAL','DCI-SE-PLT-L2','DCI-SE-PLT-PRIPOS','DCI-SE-PLT-SAL','MWS-S-MWM-ASM','MWS-S-MWM-BAS','MWS-SE-MWM-ASM','MWS-SE-MWM-BAS');

update Courses set coursecode = 'DCI-S-PLT-PRIPOS-RHL-EN_US', coursecodenolanguage = 'DCI-S-PLT-PRIPOS-RHL', module = 'PRIPOS', product = 'RHL' where coursecode = 'DCI-S-PLT-PRI-POS-EN_US';

-- Names
update 
	Courses
set
	specialization = 
		case 
			when specialization = 'CI' then 'CLI' 
            when specialization in ('DCI','V1DCI','VDCI') then 'DCI'
            else specialization
		end;

update
	Courses
set
	specializationname = 
		case 
			when specialization in ('CI','CLI') then 'Cloud Infrastructure'
			when specialization in ('MWS') then 'Middleware Solutions'
			when specialization in ('DCI','V1DCI','VDCI') then 'Data Center Infrastructure'
		end,
	rolename = 
		case 
			when role in ('S') then 'Sales'
			when role in ('SE') then 'Sales Engineer'
		end,
	trackname = 
		case 
			when track in ('APD') then 'Middleware Application Development'
			when track in ('BPA') then 'Business Process Automation'
			when track in ('CMT') then 'Cloud Management'
			when track in ('IAAS') then 'Infrastructure as a Service'
			when track in ('IAS') then 'IAS'
			when track in ('MWI') then 'Middleware Integration Services'
			when track in ('MWM') then 'Middleware Migration'
			when track in ('PAS') then 'PaaS Development'
			when track in ('PLM') then 'Platform Migration'
			when track in ('PLT') then 'Platform'
			when track in ('STR') then 'Storage'
			when track in ('VIR') then 'Virtualization'
		end;

alter table Courses modify coursecode varchar(50) not null;
alter table Courses add primary key (coursecode);
create index idx_coursecodenolanguage on Courses (coursecodenolanguage);
create index idx_coursename on Courses (coursename);

-------------------------------------------------- 
-- Names
-------------------------------------------------- 
update 
	stv
set 
	email = ltrim(rtrim(replace(replace(email,char(10),''),char(13),''))),
	fullname = ltrim(rtrim(replace(replace(fullname,char(10),''),char(13),''))),
	firstname = ltrim(rtrim(replace(replace(firstname,char(10),''),char(13),''))),
	lastname = ltrim(rtrim(replace(replace(lastname,char(10),''),char(13),''))),
	activitycode = ltrim(rtrim(replace(replace(activitycode,char(10),''),char(13),''))),
	activity = ltrim(rtrim(replace(replace(activity,char(10),''),char(13),''))),
	txt3 = ltrim(rtrim(replace(replace(txt3,char(10),''),char(13),''))),
	country = ltrim(rtrim(replace(replace(country,char(10),''),char(13),''))),
	jobname = ltrim(rtrim(replace(replace(jobname,char(10),''),char(13),''))),
	orgname = ltrim(rtrim(replace(replace(orgname,char(10),''),char(13),''))),
	domname = ltrim(rtrim(replace(replace(domname,char(10),''),char(13),''))),
	iscert = ltrim(rtrim(replace(replace(iscert,char(10),''),char(13),'')));

update
	elabs
set
	fullname = ltrim(rtrim(replace(replace(fullname,char(10),''),char(13),''))),
	company = ltrim(rtrim(replace(replace(company,char(10),''),char(13),''))),
	geo = ltrim(rtrim(replace(replace(geo,char(10),''),char(13),''))),
	country = ltrim(rtrim(replace(replace(country,char(10),''),char(13),''))),
	email = ltrim(rtrim(replace(replace(email,char(10),''),char(13),''))),
	rhnid = ltrim(rtrim(replace(replace(rhnid,char(10),''),char(13),''))),
	sfdcid = ltrim(rtrim(replace(replace(sfdcid,char(10),''),char(13),''))),
	coursename = ltrim(rtrim(replace(replace(coursename,char(10),''),char(13),''))),
	role = ltrim(rtrim(replace(replace(role,char(10),''),char(13),''))),
	passfailwaive = ltrim(rtrim(replace(replace(passfailwaive,char(10),''),char(13),''))),
	note = ltrim(rtrim(replace(replace(note,char(10),''),char(13),'')));


-- Dedupe
update SumTotal st join SumTotalMapping sm on st.dimUser_EmpEmailDetail = sm.oldemail set st.oldemail = st.dimUser_EmpEmailDetail, st.dimUser_EmpEmailDetail = sm.newemail where st.oldemail is null;
update SumTotal st join SumTotalMapping sm on st.dimUser_EmpEmailDetail = sm.oldemail set st.dimUser_EmpEmailDetail = sm.newemail where st.oldemail is not null; 
update SumTotal st join Duplicates d on st.dimUser_EmpEmailDetail = d.oldemail set st.oldemail = st.dimUser_EmpEmailDetail, st.dimUser_EmpEmailDetail = d.newemail where st.oldemail is null; 
update SumTotal st join Duplicates d on st.dimUser_EmpEmailDetail = d.oldemail set st.dimUser_EmpEmailDetail = d.newemail where st.oldemail is not null; 

-- Manual fixes
update stv set email = 'toshiyuki.fukuda@mediamart.jp', fullname = 'Fukuda, Toshiyuki' where email in ('toshiyuki.fukuda@mediamart.jp','email-bounce/toshiyuki.fukuda@mediamart.jp');

-- Update names using most common name
drop table if exists temp;
create table temp as select email, fullname, firstname, lastname, count(*) cnt from stv where newfullname is null and fullname not in ('?, ??', '? ??') group by email, fullname, firstname, lastname;

drop table if exists temp2;

create table temp2 as select email, max(cnt) cnt from temp group by email;
alter table temp2 add column fullname varchar(100), add column firstname varchar(100), add column lastname varchar(100);

update temp2 join temp on temp2.email = temp.email and temp2.cnt = temp.cnt
set temp2.fullname = temp.fullname,
	temp2.firstname = temp.firstname, 
	temp2.lastname = temp.lastname; 

update stv join temp2 on stv.email = temp2.email 
set stv.newfullname = temp2.fullname,
	stv.newfirstname = temp2.firstname, 
	stv.newlastname = temp2.lastname
where
	stv.newfullname is null; 

drop table if exists temp;
drop table if exists temp2;

-- get fullname from SF
update stv s join SfdcUsers sf on s.email = sf.email
set s.newfullname = sf.`full name`
where s.newfullname is null and sf.`full name` not in ('?, ??', '? ??');

update stv s join SfdcUsers2 sf on s.email = sf.email
set s.newfullname = sf.`full name`
where s.newfullname like '%?%' or s.newfullname is null;

-- get fullname from eLabs
update stv s join elabs e on s.email = e.email
set s.newfullname = ifnull(e.n_fullname, e.fullname)
where s.newfullname is null;

-------------------------------------------------- 
-- Companies
-------------------------------------------------- 
-- update NewCompany with most frequently populated company per email address

drop table if exists temp;
create table temp as select email, txt3 as company, count(*) as cnt from stv group by email, txt3 order by email, 3 desc, length(txt3) desc;
alter table temp add index idx1 (email, cnt desc);
alter table temp add column id int not null auto_increment primary key;

drop table if exists temp2;
create table temp2 as select temp.email, company from temp join (select email, min(id) id from temp group by email) t on temp.id = t.id;

update stv s join temp2 t on s.email = t.email
set s.newcompany = t.company
where s.newcompany is null;

drop table if exists temp;
drop table if exists temp2;

create table temp as select email, `account name` as company, count(*) cnt from SfdcUsers where length(ltrim(rtrim(ifnull(`account name`,'')))) > 0 group by email, `account name`;
create table temp2 as select email, max(cnt) cnt from temp group by email;

create index idx1 on temp (email, cnt);
create index idx1 on temp2 (email, cnt);

alter table temp2 add company varchar(100);

update temp2 join temp on temp.email = temp2.email and temp.cnt = temp2.cnt
set temp2.company = temp.company;

update stv s join temp2 t on s.email = t.email 
set newcompany = t.company
where length(ltrim(rtrim(ifnull(newcompany,'')))) = 0 ;

drop table if exists temp;
drop table if exists temp2;

-------------------------------------------------- 
-- Countries
-------------------------------------------------- 
-- update invalid country code using country names
update stv s join Countries c on c.countryname  = s.country 
set newcountry = c.code
where newcountry is null;

update stv set newcountry = 'BR' where country = 'Brasil' and newcountry is null;
update stv set newcountry = 'SA' where country = 'KINDOM OF SAUDI ARABIA' and newcountry is null;
update stv set newcountry = 'RU' where country = 'Russia' and newcountry is null;
update stv set newcountry = 'LK' where country = 'SRI LANKA (formerly Ceylon)' and newcountry is null;
update stv set newcountry = 'TW' where country = 'Taiwan' and newcountry is null;
update stv set newcountry = 'GB' where country = 'United Kingdom' and newcountry is null;
update stv set newcountry = 'GB' where country = 'UK' and newcountry is null;
update stv set newcountry = 'AE' where country = 'UAE' and newcountry is null;
update stv set newcountry = 'US' where country = 'FedSled' and newcountry is null;

-- update missing country code using most recent for each email
update 
	stv s
	join (
	select distinct s.email, ifnull(s.newcountry, s.country) country, t.enddate from stv s join (
		select email, max(enddate) enddate from stv where ifnull(newcountry, country) in (select code from Countries) group by email 
		) t on s.email = t.email and s.enddate = t.enddate
	) t on t.email = s.email
set 
	newcountry = t.country,
	tempflag = '1'
where
	(s.country = '' or s.country is null) 
	and s.newcountry is null;

-- update missing country code using email address extension
update
	stv s 
	join Countries c
		on right(email,3) = cctld
set 
	newcountry = code,
	tempflag = '2'
where
	(s.country = '' or s.country is null) 
	and country = '';
	
-- update missing country code using SF
drop table if exists SubRegion;

create table SubRegion as select distinct `global region` as globalregion, SubRegion from SfdcUsers where length(SubRegion) > 0;
alter table SubRegion add Country varchar(50);

update SubRegion s join Countries c on c.CountryName = s.SubRegion set country = c.code;
update SubRegion set country = 'US' where SubRegion in ('Northeast','Southeast','North Central','National', 'Mid-Atlantic', 'Soutwest', 'Southwest', 'Northwest', 'South Central', 'NA');
update SubRegion set country = 'CN' where SubRegion in ('China', 'Greater China', 'GCG');
update SubRegion set country = 'TW' where SubRegion in ('Taiwan');
update SubRegion set country = 'KR' where SubRegion in ('Korea');
update SubRegion set country = 'DE' where SubRegion in ('DACH');
update SubRegion set country = 'CO' where SubRegion in ('NOLA');
update SubRegion set country = 'CA' where SubRegion in ('Canada');
update SubRegion set country = 'DE' where SubRegion in ('DACH');

drop table if exists temp;

create table temp as 
select 
	email, 
	max(cnt) cnt 
from ( 
	select email, ifnull(newcountry, country) country, count(*) cnt from stv where ltrim(rtrim(ifnull(newcountry, country))) in (select code from Countries) and length(ltrim(rtrim(ifnull(newcountry, country)))) > 0 group by email, ifnull(newcountry, country) 
	) t
group by 
	email
order by 1;

alter table temp add country varchar(3);

update temp t join (
select email, ifnull(newcountry, country) country, count(*) cnt from stv where ltrim(rtrim(ifnull(newcountry, country))) in (select code from Countries) and length(ltrim(rtrim(ifnull(newcountry, country)))) > 0 group by email, ifnull(newcountry, country)
) t2 on t.email = t2.email and t.cnt = t2.cnt
set t.country = t2.country;

update stv s join temp t on s.email = t.email 
set newcountry = t.country;

drop table if exists temp;
create table temp as select distinct s.email from stv s join elabs e on s.email = e.email join Countries c on c.countryname = e.country where newcountry is null and e.country is not null;

alter table temp add country varchar(2);

update temp t join elabs e on t.email = e.email join Countries c on c.code = e.country 
set t.country = code
where t.country is null;

update temp t join elabs e on t.email = e.email join Countries c on c.CountryName = e.country 
set t.country = code
where t.country is null;

update stv s join temp t on s.email = t.email 
set s.newcountry = t.country
where s.newcountry is null;

drop table if exists temp;

-------------------------------------------------- 
-- Geo/Region
-------------------------------------------------- 
update stv set newgeo = 'Asia Pacific' where domname in ('APAC','Asia Pacific');
update stv set newgeo = 'Europe, Middle East and Africa' where domname in ('Europe, Middle East and Africa','Europe, Middle East and A', 'EMEA');
update stv set newgeo = 'Latin America' where domname in ('LATAM', 'Latin America');
update stv set newgeo = 'North America' where domname in ('NA', 'North America');
update stv set newgeo = 'Fed/Sled' where domname in ('Fed/Sled');

-------------------------------------------------- 
-- Partner Type
-------------------------------------------------- 
-- Update NewPartnerType with most recent populated partner from SumTotal
update
	stv s 
	join (
		select 
			distinct s.email, s.orgname
		from
			stv s
			join (
				select 
					email, 
					max(enddate) enddate 
				from 
					stv 
				where 
					orgname <> '' 
				group by 
					email
				) t 
				on s.email = t.email
		where
			orgname <> ''
		) t
			on s.email = t.email
set
	newpartnertype = t.orgname
where
	s.orgname = ''
	and newpartnertype is null;
		

-- Update NewPartnerType with SalesForce
update
	stv s 
	join (
		select 
			email, max(`partner type`) partnertype
		from
			SfdcUsers
		where
			`partner type` <> ''
		group by 
			email
		) t
			on s.email = t.email
set
	newpartnertype = t.partnertype
where
	s.orgname = ''
	and newpartnertype is null;

update stv set newpartnertype = 'Solution Provider' where newpartnertype = 'Reseller';
update stv set newpartnertype = 'Service Provider' where newpartnertype = 'Service/Cloud Provider';

-------------------------------------------------- 
-- Deduplicate
-------------------------------------------------- 
update stv set newcoursecode = null, deleteflag = 'N';
update stv set deleteflag = 'Y' where activitycode = 'SingleCourseManifest';

update stv s join coursemappingv c on s.activitycode = c.oldcode set newcoursecode = newcode;
update stv s set newcoursecode = activitycode where newcoursecode is null;

/*

update CourseMapping set newcode = 'CLI-ALL-IAS-ASM-OSP-EN_US', oldcode = 'CLI-S-IAS-ASM-OSE-EN_US' where oldcode = 'CLI-S-IAS-ASM-OSE';
update CourseMapping set newcode = 'CLI-ALL-IAS-ASM-OSP-EN_US', oldcode = 'CLI-SE-IAS-ASM-EXAM-EN_US' where oldcode = 'CLI-SE-IAS-ASM-EXAM';
*/

-- flag duplicate Courses by email (SumTotal)
drop table if exists temp;


create table temp as 
select 
	email, coursecodenolanguage as activitycode, max(enddate) as enddate, count(*) as cnt 
from 
	stv 
	join Courses c
		on stv.newcoursecode = c.coursecode
where
	deleteflag = 'N'
group by 
	email, coursecodenolanguage 
having 
	count(*) > 1 
order by 
	count(*) desc; 


alter table temp add keepid int;
update temp t join stv s on t.email = s.email and t.enddate = s.enddate join Courses c on s.newcoursecode = c.CourseCode set keepid = id where t.activitycode = c.coursecodenolanguage;

update 
	stv 
	join Courses c	
		on c.coursecode = stv.newcoursecode
	join temp 
		on stv.email = temp.email
		and c.coursecodenolanguage = temp.activitycode
set
	deleteflag = 'Y'
	, modifynote = '1 - duplicate record'
where
	deleteflag = 'N'
	and stv.id <> keepid;

drop table if exists temp;

-------------------------------------------------- 
-- eLabs Processing
-------------------------------------------------- 
alter view elabs as 
select
	Fullname,
	n_fullname,
	company,
	n_company,
	region geo,
	n_geo,
	country,
	n_country,
	email,
	o_email,
	rhnid,
	sfdcid,
	newcoursename coursename,
	accreditation,
	role,
	enddate assessdate,
	pfw passfailwaive,
	note,
	auto id,
	deleteflag,
	modifynote
from
	eLabsImport;

-- create clustered index
drop index idx1 on eLabsImport;
create index idx1 on eLabsImport (email, coursename, enddate desc, pfw desc);

-- /* svm1
-------------------------------------------------- 
-- eLabs Cleanup
-------------------------------------------------- 
-- Deduplicate 
update eLabsImport e join SumTotalMapping sm on e.email = sm.oldemail set e.o_email = e.email, e.email = sm.newemail where o_email is null;
update eLabsImport e join SumTotalMapping sm on e.email = sm.oldemail set e.email = sm.newemail where o_email is not null;
update eLabsImport e join Duplicates d on e.email = d.oldemail set e.o_email = e.email, e.email = d.newemail where o_email is null; 
update eLabsImport e join Duplicates d on e.email = d.oldemail set e.email = d.newemail where o_email is not null; 

-- Course Cleanup
-- alter table eLabsImport add NewCourseName varchar(100)
update Accreditations set role = 'Advance Training' where specialization = 'Advance Training';
update AccreditationCourses set role = 'Advance Training' where specialization = 'Advance Training';
update Accreditations set track = 'RHEL for SAP Hana' where accreditation = 'Red Hat Delivery Specialist - RHEL for SAP Hana';
update AccreditationCourses set track = 'RHEL for SAP Hana' where accreditation = 'Red Hat Delivery Specialist - RHEL for SAP Hana';
-- update Accreditations set specialization = 'Advanced Delivery', role = 'Delivery' where accreditation = 'Red Hat Advance Delivery Specialist - Cloud Management';
-- update AccreditationCourses set specialization = 'Advanced Delivery', role = 'Delivery' where accreditation = 'Red Hat Advance Delivery Specialist - Cloud Management';

update AccreditationCourses set role = 'Sales Engineer' where role = 'SE';
update AccreditationCourses set code = 'RHEV 3.4 Implementation' where code = 'RHEV Implementation';
update AccreditationCourses set code = 'RHEV 3.4 FASTRAX' where code = 'RHEV FASTRAX';
update AccreditationCourses set code = 'DCI-SE-VIR-BAS-RHV-EN_US' where code like 'dci-se-vir-pri-rhv%';
update AccreditationCourses set codenolanguage = 'MWS-S-BPA-ASM-BPMS' where auto = 162;

update eLabsImport set coursename = ltrim(rtrim(replace(replace(coursename,char(10),''),char(13),'')));
update eLabsImport set enddate = null where enddate = '1899-12-30 00:00:00.000';
update eLabsImport set NewCourseName = coursename where newcoursename is null;
update eLabsImport set newcoursename = replace(coursename,'Final Assessment','') where coursename like '% final assessment%';
update eLabsImport set NewCourseName = 'Application Development with EAP 6' where coursename in ('Application Development with EAP6','Application Development with EAP 6 Final Exam');
update eLabsImport set NewCourseName = 'Application Migration to EAP 6' where coursename in ('Application Migration to EAP6','Application Migration to EAP 6 Exam');
update eLabsImport set NewCourseName = 'Application Migration using Windup' where coursename in ('Application Migration with Windup','Application Migration with Windup Exam');
update eLabsImport set NewCourseName = 'Business Logic Development with BPMS 6' where coursename in ('Business Logic Development with BPMS6','Business Logic Development with BPM Suite 6 Exam','Business Logic Development with BPM Suite 6','Business Logic Development with BPMS 6');
update eLabsImport set NewCourseName = 'Camel Development with JBoss FUSE' where coursename = 'Camel Development';
update eLabsImport set NewCourseName = 'Camel Development with JBoss FUSE' where coursename = 'Camel Development with JBoss FUSE [VT]';
update eLabsImport set NewCourseName = 'CloudForms Customization and Automation' where coursename in ('Cloudforms 3.0 Customization', 'CloudForms 3.1 Customization & Automation');
update eLabsImport set NewCourseName = 'CloudForms FASTRAX' where coursename = 'CloudForms 3.1 FASTRAX';
update eLabsImport set NewCourseName = 'CloudForms Implementation' where coursename in ('CloudForms 3.1 Implementation','CloudForms 3.1 Implementation Final Assessment');
update eLabsImport set NewCourseName = 'CloudForms Customization and Automation' where coursename = 'CloudForms Customization & Automation';
update eLabsImport set NewCourseName = 'CloudForms FASTRAX' where coursename = 'CloudForms FASTAX';
update eLabsImport set NewCourseName = 'CloudForms FASTRAX' where coursename = 'CloudForms FASTRAX';
update eLabsImport set NewCourseName = 'CloudForms FASTRAX' where coursename = 'CloudForms FASTTRAX';
update eLabsImport set NewCourseName = 'CloudForms FASTRAX' where coursename in ('CloudForms 3.0 FASTRAX','CloudForms 3.0 FASTRAX Assessment','CloudForms 3.1 FASTRAX Assessment');
update eLabsImport set NewCourseName = 'Enterprise Development with Red Hat OpenShift' where coursename in ('Enterprise Development with OpenShift by Red Hat','Enterprise Software Development with Red Hat OpenShift');

-- update eLabsImport set NewCourseName = 'OpenStack Implementation' where coursename = 'OpenStack Sales'
update eLabsImport set NewCourseName = 'CLI-S-IAS-ASM-OSE' where coursename = 'OpenStack Sales';
update eLabsImport set NewCourseName = 'RHEL (6) Implementation' where coursename in ('RHEL 6 Implementation','RHEL (6) Implementation');
update eLabsImport set NewCourseName = 'RHEL (7) Implementation Part 1' where coursename in ('RHEL 7 Implementation Part 1');
update eLabsImport set NewCourseName = 'RHEL (7) Implementation Part 2' where coursename in ('RHEL 7 Implementation Part 2','RHEL 7 Implementation Volume 2','RHEL 7 Implementation Volume 2 Exam');
update eLabsImport set NewCourseName = 'RHEL (7) Implementation Part 2' where coursename = 'RHEL7 Implementation Part 2';
update eLabsImport set NewCourseName = 'RHEV 3.4 FASTRAX' where left(coursename,12) = 'RHEV FASTRAX';
update eLabsImport set NewCourseName = 'Satellite (5) FASTRAX' where coursename = 'Satellilte FASTRAX';
update eLabsImport set NewCourseName = 'Satellite (6) Implementation' where coursename = 'Satellite 6 Implementation';
update eLabsImport set NewCourseName = 'Services Development with Fuse Service Works' where coursename = 'Service Development with Fuse Service Works';
update eLabsImport set NewCourseName = 'Services Development with Fuse Service Works' where coursename = 'Services Development with FSW';
update eLabsImport set NewCourseName = 'Storage Implementation' where coursename in ('Storage Implementaion','Storage Implementation Exam');
update eLabsImport set NewCourseName = 'RHEV 3.4 Implementation' where coursename = 'RHEL 3.4 Implementation';
update eLabsImport set NewCourseName = 'RHEL Implementation' where coursename = 'RHEL Implemenation';
update eLabsImport set NewCourseName = 'RHEL (6) Troubleshooting' where coursename = 'RHEL Troubleshooting';
update eLabsImport set NewCourseName = 'Services Development with Fuse Service Works' where coursename = 'Enterprise Development with Fuse Service Works';
update eLabsImport set NewCourseName = 'CLI-S-IAS-ASM-OSE' where coursename = 'IaaS Solution for Salesperson';
update eLabsImport set NewCourseName = 'RHEL (6) Implementation' where coursename = 'RHEL Implementation' and enddate < '2015-01-01';
update eLabsImport set NewCourseName = 'RHEL (7) Implementation' where coursename = 'RHEL Implementation' and enddate >= '2015-01-01';
update eLabsImport set NewCourseName = 'RHEV 3.4 FASTRAX' where coursename = 'RHEV FASTRAX';
update eLabsImport set NewCourseName = 'RHEV 3.4 Implementation' where coursename in ('RHEV Implementation','RHEV 3.4 Implementation Exam');
update eLabsImport set NewCourseName = 'Satellite (5) FASTRAX' where coursename = 'Satellite FASTRAX';
update eLabsImport set NewCourseName = 'Satellite (6) FASTRAX' where coursename in ('Satellite 6 FASTRAX','Satellite FASTRAX Final Exam');
update eLabsImport set NewCourseName = 'OpenStack (6) FASTRAX' where coursename in ('OpenStack 6 FASTRAX','OpenStack 6','OpenStack 6 FASTRAX ');
update eLabsImport set NewCourseName = 'Satellite (6) Implementation' where coursename = 'Satellite Implementation';
update eLabsImport set NewCourseName = 'Satellite (6) Implementation' where coursename = 'Satellite 6 Implementation Final Exam';
update eLabsImport set newcoursename = 'CLI-S-IAS-ASM-OSE' where auto = 1940;

update eLabsImport set newcoursename = 'Satellite (6) Implementation' where coursename in ('Satellite 6 Implementation','Satellite 6 Implementation Final Exam','Satellite Implementation');
update eLabsImport set newcoursename = 'RHEL (6) Implementation' where coursename in ('RHEL Implementation', 'RHEL 6 Implementation', 'RHEL Implementation Exam');
update eLabsImport set newcoursename = 'RHEL (7) Implementation' where coursename in ('RHEL 7 Implementation');
update eLabsImport set newcoursename = 'RHEL (7) Implementation Part 1' where coursename in ('RHEL 7 Implementation Part 1', 'RHEL 7 Implementation Volume 1 Exam','RHEL 7 Implementation Volume 1');
update eLabsImport set newcoursename = 'RHEL (7) Implementation Part 2' where coursename in ('RHEL7 Implementation Part 2', 'RHEL 7 Implementation Part 2', 'RHEL 7 Implementation Volume 2 Exam');
update eLabsImport set newcoursename = 'RHEL (7) Implementation Parts 1 and 2' where coursename in ('RHEL (7) Implementation parts 1 and 2');
update eLabsImport set newcoursename = 'RHEL (7) Implementation Parts 1 and 2' where coursename in ('RHEL (7) Implementation Parts 1 and 2');
update eLabsImport set newcoursename = 'RHEL (7) Implementation Parts 1 and 2' where coursename in ('RHEL Implementation Exam');
update eLabsImport set newcoursename = 'RHEL (7) Troubleshooting' where coursename in ('RHEL 7 Troubleshooting');
update eLabsImport set newcoursename = 'CloudForms FASTRAX' where coursename in ('CloudForms FASTTRAX', 'CloudForms FASTAX');
update eLabsImport set newcoursename = 'CloudForms 3.1 Implementation' where coursename in ('CloudForms 3.1 Implementation Final Assessment');
update eLabsImport set newcoursename = 'CloudForms 3.1 Customization and Automation' where coursename in ('CloudForms Customization & Automation', 'CloudForms 3.1 Customization & Automation');
update eLabsImport set newcoursename = 'CloudForms Customization and Automation' where coursename in ('CloudForms Customization and Automation', 'CloudForms Customization & Automation', 'Cloudforms 3.0 Customization');
update eLabsImport set newcoursename = 'OpenStack Implementation' where coursename in ('OpenStack Implementation Exam', 'OpenStack (6) Implementation');
update eLabsImport set newcoursename = 'OpenShift (3) Implementation' where coursename in ('OpenShift 3 Implementation','OpenShift 3 Implementation ');
update eLabsImport set newcoursename = 'OpenShift (3) FASTRAX' where coursename in ('OpenShift 3 FASTRAX','OpenShift 3 FASTRAX ','OpenShift 3 FASTRAX ');
update eLabsImport set newcoursename = 'RHEL Atomic Host and Containers' where coursename in ('Containers with RHEL 7 Atomic');
update eLabsImport set newcoursename = 'A-MQ [VT]' where coursename in ('JBoss A-MQ');
update eLabsImport set newcoursename = 'Data Virtualization' where coursename in ('JBoss Data Virtualization');
update eLabsImport set newcoursename = 'OpenStack 6 Implementation' where coursename in ('OpenStack 6 Final Assessment');
-- update eLabsImport set newcoursename = 'CLI-DEL-IAS-EXAM-OSP6.1IMP' where newcoursename = 'CLI-S-IAS-ASM-OSE';

insert into eLabsImport (
	`eLabsImport`.`Auto`,
    `eLabsImport`.`FullName`,
    `eLabsImport`.`Email`,
    `eLabsImport`.`RhnID`,
    `eLabsImport`.`CourseName`,
    `eLabsImport`.`Role`,
    `eLabsImport`.`EndDateOld`,
    `eLabsImport`.`EndDate`,
    `eLabsImport`.`PFWold`,
    `eLabsImport`.`PFW`,
    `eLabsImport`.`WaiverNote`,
    `eLabsImport`.`Note`,
    `eLabsImport`.`Company`,
    `eLabsImport`.`Region`,
    `eLabsImport`.`SubRegion`,
    `eLabsImport`.`Country`,
    `eLabsImport`.`PartnerType`,
    `eLabsImport`.`SfdcID`,
    `eLabsImport`.`ReportingName`,
    `eLabsImport`.`CodeFrom02152015Mapping`,
    `eLabsImport`.`Code`,
    `eLabsImport`.`NewCourseName`,
    `eLabsImport`.`n_fullname`,
    `eLabsImport`.`n_geo`,
    `eLabsImport`.`n_country`,
    `eLabsImport`.`deleteflag`,
    `eLabsImport`.`modifynote`,
    `eLabsImport`.`n_company`,
    `eLabsImport`.`o_email`,
    `eLabsImport`.`accreditation`,
--    `eLabsImport`.`autoid`,
    `eLabsImport`.`SumTotalID`
)

SELECT 
	null, -- `eLabsImport`.`Auto`,
    `eLabsImport`.`FullName`,
    `eLabsImport`.`Email`,
    `eLabsImport`.`RhnID`,
    `eLabsImport`.`CourseName`,
    `eLabsImport`.`Role`,
    `eLabsImport`.`EndDateOld`,
    `eLabsImport`.`EndDate`,
    `eLabsImport`.`PFWold`,
    `eLabsImport`.`PFW`,
    `eLabsImport`.`WaiverNote`,
    `eLabsImport`.`Note`,
    `eLabsImport`.`Company`,
    `eLabsImport`.`Region`,
    `eLabsImport`.`SubRegion`,
    `eLabsImport`.`Country`,
    `eLabsImport`.`PartnerType`,
    `eLabsImport`.`SfdcID`,
    `eLabsImport`.`ReportingName`,
    `eLabsImport`.`CodeFrom02152015Mapping`,
    `eLabsImport`.`Code`,
    'RHEL (7) Implementation Part 2',
    `eLabsImport`.`n_fullname`,
    `eLabsImport`.`n_geo`,
    `eLabsImport`.`n_country`,
    `eLabsImport`.`deleteflag`,
    `eLabsImport`.`modifynote`,
    `eLabsImport`.`n_company`,
    `eLabsImport`.`o_email`,
    `eLabsImport`.`accreditation`,
--    `eLabsImport`.`autoid`,
    `eLabsImport`.`SumTotalID`
FROM `lms`.`eLabsImport`
where newcoursename = 'RHEL (7) Implementation' and email not in (select email from eLabsImport where newcoursename = 'RHEL (7) Implementation Part 2');

update eLabsImport set newcoursename = 'RHEL (7) Implementation Part 1' where newcoursename = 'RHEL (7) Implementation';
update eLabsImport set auto = autoid where auto is null;

-- hansolo
-- select coursename, newcoursename, count(*) from eLabsImport where coursename like '%rhel%7%imp%' group by coursename, newcoursename;

drop table if exists temp;

create table temp as 
select email, max(auto) auto from eLabsImport where newcoursename = 'RHEL (7) Implementation Parts 1 and 2' or 
	(newcoursename = 'RHEL (7) Implementation Part 2' and email in (select email from eLabsImport where newcoursename = 'RHEL (7) Implementation Part 1'))
group by email;

update eLabsImport set deleteflag = 'N', modifynote = null;
update eLabsImport set deleteflag = 'Y' where newcoursename in ('RHEL (7) Implementation Part 1', 'RHEL (7) Implementation Part 2', 'RHEL (7) Implementation Parts 1 and 2');
update eLabsImport e join temp t on e.auto = t.auto set e.deleteflag = 'N', e.modifynote = null;
drop table if exists temp;

-- duplicate email and course
create table temp as 
select 
	email, coursename, min(id) as id, count(*) cnt 
from 
	elabs
where
	passfailwaive in ('Pass','Waive') 
    and deleteflag = 'N'
group by 
	email, coursename 
having 
	count(*) > 1 
order by 
	count(*) desc;
    
update 
	elabs 
	join temp 
		on elabs.email = temp.email
		and elabs.coursename = temp.coursename
set
	deleteflag = 'Y'
	, modifynote = '1 - duplicate record'
where
	deleteflag = 'N'
-- 	and passfailwaive in ('Pass','Waive')
	and elabs.id <> temp.id;

drop table if exists temp;

-- duplicate email and company 
update elabs set n_company = null;

drop table if exists temp;

create table temp as 
select email, count(*) cnt from (
select email, ifnull(n_company, company) as company, count(*) cnt from elabs group by email, ifnull(n_company, company)
) t group by email having count(*) > 1;

drop table if exists temp2;
create table temp2 as select email, company, count(*) cnt from elabs where email in (select distinct email from temp) group by email, company order by email, cnt desc;

drop table if exists temp3;
create table temp3 as select *, length(ltrim(rtrim(company))) companylen from temp2 order by email, cnt desc, length(ltrim(rtrim(company))) desc;

-- create clustered index idx1 on SumTotal (dimuser_empemaildetail, dimactivity_codedetail, factattempt_attemptenddtdetail desc)

create index idx1 on temp3 (email, cnt desc, companylen desc);
alter table temp3 add column id int not null auto_increment primary key;

drop table if exists temp4;
create table temp4 as select t.email, company from temp3 t join (select email, min(id) id from temp3 group by email) t2 on t.id = t2.id;

update 
	elabs e 
	join temp4 t
		on e.email = t.email
set
	n_company = t.company
where
	e.company <> t.company;

drop table if exists temp;
drop table if exists temp2;
drop table if exists temp3;
drop table if exists temp4;

update elabs set n_fullname = null;

drop table if exists temp;

create table temp as select email, count(*) cnt from (
select email, fullname, count(*) cnt from elabs group by email, fullname 
) t group by email having count(*) > 1;

drop table if exists temp2;
create table temp2 as select email, fullname, count(*) cnt from elabs where email in (select distinct email from temp) group by email, fullname order by email, cnt desc;

drop table if exists temp3;
create table temp3 as select *, length(ltrim(rtrim(fullname))) fullnamelen from temp2 order by email, cnt desc, length(ltrim(rtrim(fullname))) desc;
create index idx1 on temp3 (email, cnt desc, fullnamelen desc);
alter table temp3 add column id int not null auto_increment primary key;

drop table if exists temp4;
create table temp4 as select t.email, fullname from temp3 t join (select email, min(id) id from temp3 group by email) t2 on t.id = t2.id;

update 
	elabs e 
	join temp4 t
		on e.email = t.email
set
	n_fullname = t.fullname
where
	e.fullname <> t.fullname;

drop table if exists temp;
drop table if exists temp2;
drop table if exists temp3;
drop table if exists temp4;

-- Duplicate Countries
update elabs set country = ltrim(rtrim(country));
update elabs set n_country = null;
update elabs e join Countries c on e.country = c.code set n_country = c.code where n_country is null;
update elabs e join Countries c on e.country = c.CountryName set n_country = c.code where n_country is null;
update elabs e join Countries c on e.country = c.CountryName set n_country = c.code where n_country is null;

-- Duplicate geos
drop table if exists Regions ;

create table Regions (
	RegionCode varchar(10) primary key,
	RegionName varchar(100)
	);

insert into Regions
select 'APAC', 'Asia Pacific'
union
select 'EMEA','Europe, Middle East and Africa'
union
select 'LATAM','Latin America'
union
select 'Fed/Sled','Fed/Sled'
union
select 'NA','North America';


update elabs set geo = ltrim(rtrim(geo));
update elabs set n_geo = null;

update elabs e join SfdcUsers s on e.fullname = s.`full name` set e.email = s.email, o_email = e.email where e.email not like '%@%.%' and s.email like '%@%.%' and o_email is null;
-- update e set e.email = s.email, o_email = e.email from elabs e join SfdcUsers s on e.fullname = s.`full name` where s.email like '%@%.%' and o_email is null
-- update e set e.email = s.email, o_email = e.email from elabs e join SfdcUsers s on e.fullname = s.`full name` where s.email like '%@%.%' and o_email is null and e.email not in (select email from SfdcUsers where email like '%@%.%') and n_geo is null
update eLabsImport set email = 'hbeltran@jaltana.com', o_email = null where auto = 1933;

update elabs e join Regions r on e.geo = r.RegionName set n_geo = geo where n_geo is null;
update elabs e join Regions r on e.geo = r.RegionCode set n_geo = geo where n_geo is null;
update elabs e set n_geo = 'Asia Pacific' where geo in ('APAC') and n_geo is null;
update elabs e set n_geo = 'Europe, Middle East and Africa' where geo in ('EMEA') and n_geo is null;

update elabs set n_geo = 'Latin America' where geo in ('LATAM') and n_geo is null;
update elabs set n_geo = 'Fed/Sled' where geo in ('North America Fed/SLED','North America FedSled') and n_geo is null;
update elabs set n_geo = 'North America' where geo in ('NA') and n_geo is null;

update elabs e join SfdcUsers s on e.email = s.email join Regions r on s.`global region` = r.RegionCode set n_geo = RegionName where n_geo is null;
update elabs e join stv s on e.email = s.email join Regions r on s.newgeo = r.RegionName set n_geo = RegionName where n_geo is null;
update elabs e join SfdcUsers s on e.rhnid = s.`rhn entitlement login` join Regions r on r.RegionCode = s.`global region` set n_geo = RegionName where n_geo is null and rhnid not in ('N/A') and length(ltrim(rtrim(s.`rhn entitlement login`)))>2;

-- */  -- svm1


-------------------------------------------------- 
-- Students
-------------------------------------------------- 
drop table if exists Students;

create table Students as 
select * 
from (
select distinct ltrim(rtrim(Email)) as Email from elabs where deleteflag = 'N'
union
select distinct email from stv where deleteflag = 'N'
) t 
where length(ltrim(rtrim(ifnull(email,'')))) > 0 order by email
;

alter table Students 
	add fullname varchar(200),
	add firstname varchar(200),
	add lastname varchar(200),
	add company varchar(200),
    add companycode varchar(100),
	add region varchar(200),
	add SubRegion varchar(200),
	add country varchar(200),
	add partnertype varchar(200),
	add sfdcid varchar(200),
	add sumtotalid varchar(100),
	add tempstring varchar(200);

alter table Students modify column email varchar(100) not null;
alter table Students add primary key (email);

create index idx_sfdcid on Students (sfdcid);
create index idx_sumtotalid on Students (sumtotalid);
create index idx_region on Students (region);
create index idx_firstname on Students (firstname);
create index idx_lastname on Students (lastname);
create index idx_fullname on Students (fullname);

-- dedupe SumTotalUsers
drop table if exists temp;
create table temp as select distinct * from SumTotalUsers;
drop table SumTotalUsers;
create table SumTotalUsers as select * from temp;
drop table temp;

-- FullName
update Students set fullname = null;

-- use name from elabs and st when they are the same
update Students p join stv s on p.email = s.email join elabs e on p.email = e.email set p.fullname = ltrim(rtrim(newfullname)) where p.fullname is null and ltrim(rtrim(newfullname)) = ifnull(e.n_fullname, e.fullname) and left(ltrim(rtrim(newfullname)),1) <> '?';
-- use name from SumTotalUsers (active)
update Students p join SumTotalUsers s on p.email = s.email set p.fullname = ltrim(rtrim(s.fullname)) where p.fullname is null and left(ltrim(rtrim(s.fullname)),1) <> '?' and active = 'Yes';
-- use name from st
update Students p join stv s on p.email = s.email set p.fullname = ltrim(rtrim(newfullname)) where p.fullname is null and left(ltrim(rtrim(newfullname)),1) <> '?';
-- use name from elab
update Students p join elabs s on p.email = s.email set p.fullname = ifnull(s.n_fullname, s.fullname) where p.fullname is null and left(ltrim(rtrim(ifnull(s.n_fullname, s.fullname))),1) <> '?';
-- use name from sf
update Students p join SfdcUsers s on p.email = s.email set p.fullname = s.`full name` where p.fullname is null and left(ltrim(rtrim(`full name`)),1) <> '?';
-- use name from SumTotalUsers (active)
update Students p join SumTotalUsers s on p.email = s.email set p.fullname = ltrim(rtrim(s.fullname)) where p.fullname is null and left(ltrim(rtrim(s.fullname)),1) <> '?' and active = 'No';


-- First Name / Last name
update Students set firstname = 'John', lastname = 'van Dijk', fullname = 'John van Dijk' where email = 'john.van.dijk2@ordina.nl';
update Students set firstname = 'Marco', lastname = 'van der Kolk', fullname = 'Marco van der Kolk' where email = 'marco.van.der.kolk@proxy.nl';
update Students set firstname = 'Atsuhiro', lastname = 'Inoue', fullname = 'Atsuhiro Inoue' where email = 'redhat.itbusiness@comsys.co.jp';
update Students set firstname = 'Stefan', lastname = 'van Oostrum', fullname = 'Stefan van Oostrum' where email = 'stefan.van.oostrum@ordina.nl';
update Students set firstname = 'Keeyong', lastname = 'Jun', fullname = 'Keeyong Jun' where email in ('Keeyong_jun@dell.com');
update Students set firstname = 'Vitaliy', lastname = 'Samolovskikh', fullname = 'Vitaliy Samolovskikh' where email in ('Vitaliy.Samolovskikh@aplana.com');

update 
	Students 
set 
	firstname = ltrim(rtrim(substring(fullname, locate(',',fullname)+1,100))), 
	lastname = ltrim(rtrim(left(fullname, locate(',', fullname)-1)))
where 
	fullname like '%,%' 
	and firstname is null
	and lastname is null;

update 
	Students 
set 
	lastname = ltrim(rtrim(substring(fullname, locate(' ',fullname)+1,100))), 
	firstname = ltrim(rtrim(left(fullname, locate(' ', fullname)-1)))
where 
	fullname like '% %' 
	and firstname is null
	and lastname is null;

update Students set lastname = fullname where firstname is null and lastname is null;

-- Create new fullname using parsed name fields
update Students set fullname = concat_ws(', ',ltrim(rtrim(ifnull(lastname,''))),ltrim(rtrim(ifnull(firstname,''))));

-- Company
update Students set company = null;
-- use name from SumTotalUsers (active)
update Students p join SumTotalUsers s on p.email = s.email set p.company = ltrim(rtrim(s.company)) where p.company is null and active = 'Yes';
-- use name from SumTotalUsers (inactive)
update Students p join SumTotalUsers s on p.email = s.email set p.company = ltrim(rtrim(s.company)) where p.company is null and active = 'No';
-- use name from elabs and st when they are the same
update Students p join stv s on p.email = s.email join elabs e on p.email = e.email set p.company = ltrim(rtrim(s.newcompany)) where p.company is null and ltrim(rtrim(s.newcompany)) = ifnull(e.n_company, e.company) ;
-- use name from st 
update Students p join stv s on p.email = s.email set p.company = ltrim(rtrim(s.newcompany)) where p.company is null;
-- use name from elabs 
update Students p join elabs e on p.email = e.email set p.company = ltrim(rtrim(ifnull(n_company, e.company))) where p.company is null;
-- use email for redhat
update Students p set p.company = 'Red Hat' where company is null and email like '%@redhat.com';

-- CompanyCode (alliance) 
update Students set companycode = 'ACCENTURE' where (company like 'accenture%' or email like '%@accenture.com');
update Students set companycode = 'ATOS' where (company like 'atos%' or email like '%@atos.net');
update Students set companycode = 'CAPGEMINI' where (company like 'capgemini%' or email like '%@cpmbraxis.com' or email like '%@capgemini.com');
update Students set companycode = 'CGI' where (company like 'cgi%' or email like '%@cgi.com');
update Students set companycode = 'CISCO' where (company like 'cisco%' or email like '%@cisco.com');
update Students set companycode = 'CSC' where company like 'csc%' or company like 'computer science%' or email like '%csc.com';
update Students set companycode = 'DELL' where company like 'dell%' or email like '%@dell.com' ;
update Students set companycode = 'HP' where (company like 'HP%' or company like 'Hewlett%Packard%' or email like '%@hp.com') and company not like 'HPC%' and company not like 'HPM%' ;
update Students set companycode = 'IBM' where company like 'IBM%' or email like '%.ibm.com';
update Students set companycode = 'INFOSYS' where (company like 'INFOSYS%' or email like '%@infosys.com') and company not like 'infosyst%' ;
update Students set companycode = 'NETAPP' where (company like 'NETAPP%' or email like '%@netapp.com') ;
update Students set companycode = 'SAP' where (company like 'SAP%' or email like '%@sap.com') ;
update Students set companycode = 'TATA' where (company like 'TATA%' or email like '%@tcs.com');
update Students set companycode = 'WIPRO' where (company like 'wipro%' or email like '%@wipro.com');

/*
drop index idx_st on SumTotalUsers;
create unique index idx_st on SumTotalUsers (active desc, email, domain, fullname, job, company, organization, country, usernumber);
*/


/*
create index IDX_newgeo on SumTotal (newgeo); 
create index IDX_newgeo on eLabsImport (n_geo); 
create index IDX_regioncode on Regions (regioncode); 
create index IDX_regionname on Regions (regionname); 
*/

-- Country
update Students p set country = null;
-- use name from elabs and st when they are the same
update Students p join stv s on p.email = s.email join Countries c on c.Code = s.newcountry join elabs e on p.email = e.email set p.country = c.code where s.newcountry = ifnull(e.n_country, e.country) and p.country is null;
-- use name from SumTotalUsers
update Students p join SumTotalUsers s on p.email = s.email join Countries c on c.Code = s.country set p.country = c.code where p.country is null;
-- use name from st
update Students p join stv s on p.email = s.email join Countries c on c.Code = s.newcountry set p.country = c.code where p.country is null;
-- use name from elabs
update Students p join elabs e on p.email = e.email join Countries c on c.Code = ifnull(e.n_country, e.country) set p.country = c.code where p.country is null;
update Students p join elabs e on p.email = e.email join Countries c on c.CountryName = ifnull(e.n_country, e.country) set p.country = c.code where p.country is null;
update Students p join elabs e on p.email = e.email join Countries c on left(c.CountryName,8) = left(ifnull(e.n_country, e.country),8) set p.country = c.code where p.country is null;
update Students p join elabs e on p.email = e.email join Countries c on left(c.CountryName,7) = left(ifnull(e.n_country, e.country),7) set p.country = c.code where p.country is null;
update Students p set p.country = 'CO' where email in (select email from elabs where country = 'Columbia');
update Students p set p.country = 'GB' where email in (select email from elabs where country in ('Great Britain','Great Britain (UK)','Great Britian','Great Briton (UK)'));
update Students p set p.country = 'IL' where email in (select email from elabs where country = 'Isreal');
update Students p set p.country = 'RU' where email in (select email from elabs where country = 'Russia');

-- PartnerType
update Students s join SumTotalUsers st on s.email = st.email set s.partnertype = organization where s.partnertype is null and st.organization is not null and active = 'Yes';
update Students s join stv on s.email = stv.email set s.partnertype = newpartnertype where s.partnertype is null and stv.newpartnertype is not null;
update Students s join SfdcUsers on s.email = SfdcUsers.email set s.partnertype = `partner type` where s.partnertype is null and SfdcUsers.`partner type` is not null;
update Students s join eLabsImport e on s.email = e.email set s.partnertype = e.partnertype where s.partnertype is null and e.partnertype not in ('0', '#N/A', '');
update Students s join SumTotalUsers st on s.email = st.email set s.partnertype = organization where s.partnertype is null and st.organization is not null and active = 'No';
update Students s set partnertype = 'Systems Integrator' where partnertype = 'SI';

-- SfcID
update Students s set sfdcid = null;
update Students s join SfdcUsers sf on s.email = sf.Email set sfdcid = `contact id`;
update Students s join SfdcUsers sf on s.email = sf.`RHN Entitlement Login` set sfdcid = `contact id` where sfdcid is null;
update Students s join SfdcUsers sf on company = `account name` and `full name` = fullname set sfdcid = `contact id` where sfdcid is null;
update Students s join SfdcUsers sf on company = `account name` and `full name` = ltrim(rtrim(substring(fullname, locate(', ', fullname)+2, 50) +' '+left(fullname, locate(', ', fullname)-1))) set sfdcid = `contact id` where sfdcid is null and fullname like '%, %';
update Students s join elabs e on s.email = e.email join SfdcUsers sf on e.sfdcid = `contact id` set s.sfdcid = `contact id` where s.sfdcid is null;
update Students s join elabs e on s.email = e.email join SfdcUsers sf on e.rhnid = `RHN Entitlement Login` set s.sfdcid = `contact id` where s.sfdcid is null;
update Students s join elabs e on s.email = e.o_email join SfdcUsers sf on e.sfdcid = `contact id` set s.sfdcid = `contact id` where s.sfdcid is null;
update Students s join elabs e on s.email = e.o_email join SfdcUsers sf on e.rhnid = `RHN Entitlement Login` set s.sfdcid = `contact id` where s.sfdcid is null;
update Students s join elabs e on s.email = e.o_email set s.sfdcid = e.sfdcid where e.sfdcid is not null and s.sfdcid is null;

-- SumTotalID
update Students s set sumtotalid = null;

drop table if exists temp;
create table temp as select email, max(enddate) enddate from stv where length(ltrim(rtrim(empno))) >= 15 group by email; 
alter table temp add sumtotalid varchar(100);

update temp t join stv s on t.email = s.email and length(ltrim(rtrim(empno))) >= 15 and t.enddate = s.enddate and left(s.empno,3) = '005' set t.sumtotalid = s.empno;
update Students s join temp t on s.email = t.email set s.sumtotalid = t.sumtotalid where s.sumtotalid is null and left(t.sumtotalid,3) = '005';
drop table if exists temp;

update Students s join SumTotalUsers st on s.email = st.email set s.sumtotalid = st.usernumber where s.sumtotalid is null and active = 'Yes' and left(st.usernumber,3) = '005';
update Students s join (select distinct email, left(text2, locate(':', text2)-1) sumtotalid from SumTotalRegistrations2 where text2 like '%:%') t on s.email = t.email set s.sumtotalid = t.sumtotalid where s.sumtotalid is null;
update Students s join SfdcUsers2 sf on s.email = sf.email set s.sumtotalid = sf.`user number` where s.sumtotalid is null and `active(user)` = 1 and left(sf.`user number`,3) = '005';
update Students s join SfdcUsers2 sf on s.email = sf.email set s.sumtotalid = sf.`user number` where s.sumtotalid is null and `active(user)` = 0 and left(sf.`user number`,3) = '005';
update Students s join sfdcusers3n sf on s.email = sf.email set s.sumtotalid = sf.sumtotalid where s.sumtotalid is null and left(sf.sumtotalid,3) = '005';
update Students s join SumTotalUsers st on s.email = st.email set s.sumtotalid = st.usernumber where s.sumtotalid is null and active = 'No' and left(st.usernumber,3) = '005';

update Students s join SfIDs sf	on s.email = sf.email and s.sfdcid = sf.sfcontactid2 set sumtotalid = sf1 where sumtotalid is null;
update Students s join SfIDs sf on s.email = sf.email set sumtotalid = sf1, sfdcid = sf.sfcontactid2 where sumtotalid is null;
update Students s join SfIDs sf	on s.sfdcid = sf.sfcontactid2 set sumtotalid = sf1 where sumtotalid is null;

update Students s join SumTotalMary sm on s.email = sm.email and s.sfdcid = sm.sfdcid set s.sumtotalid = sm.sumtotalid where s.sumtotalid is null and length(ltrim(rtrim(sm.sumtotalid)))> 14;
update Students s join SumTotalMary sm on s.email = sm.email set s.sumtotalid = sm.sumtotalid where s.sumtotalid is null and length(ltrim(rtrim(sm.sumtotalid)))> 14;
update Students s join SumTotalMary sm on s.email = sm.email and s.sfdcid = sm.sfdcid set s.sumtotalid = sm.sumtotalid where s.sumtotalid <> sm.sumtotalid and length(ltrim(rtrim(sm.sumtotalid)))> 14;
update Students s join SumTotalMary sm on s.email = sm.email set s.sumtotalid = sm.sumtotalid where s.sumtotalid <> sm.sumtotalid and length(ltrim(rtrim(sm.sumtotalid)))> 14;

create index idx_SumTotalID on Students (sumtotalid);

-- Region and SubRegion
-- update Students s join SfdcUsers sf on s.email = sf.email set s.SubRegion = sf.SubRegion where sf.SubRegion is not null and s.SubRegion is null;
-- update Students s join SfdcUsers sf on s.sfdcid = sf.`contact id` set s.SubRegion = sf.SubRegion where s.fullname = sf.`full name` and sf.SubRegion is not null and s.SubRegion is null;

-- Region
update Students set Region = null, SubRegion = null;
-- use name from SumTotalUsers (active)
update Students p join SumTotalUsers s on p.email = s.email join Regions r on r.regionname = s.domain set p.region = regioncode where p.region is null and active = 'Yes';
-- use name from SumTotalUsers (inactive)
update Students p join SumTotalUsers s on p.email = s.email join Regions r on r.regionname = s.domain set p.region = regioncode where p.region is null and active = 'No';
-- use name from SumTotal
update Students p join (select distinct s.email, s.newgeo from stv s join (select email, max(enddate) enddate from stv where newgeo is not null and deleteflag <> 'Y' group by email) s2 on s.email = s2.email and s.enddate = s2.enddate and deleteflag <> 'Y') s on p.email = s.email join Regions r on r.regionname = s.newgeo set p.region = regioncode  where p.region is null;
-- use SF
update Students s join SfdcUsers sf on left(s.sumtotalid,15) = sf.`contact id` set s.SubRegion = sf.SubRegion, s.Region = sf.`Global Region` where length(ifnull(sf.`global region`,'')) > 1 and left(`contact id`,3) = '005' and s.region is null;
-- use name from elabs and st when they are the same
update Students p join stv s on p.email = s.email join Regions r on r.regionname = s.newgeo join elabs e on p.email = e.email set p.region = regioncode where s.newgeo = e.n_geo and p.region is null;
-- use name from elabs
update Students p join elabs e on p.email = e.email join Regions r on r.regionname = e.n_geo set p.region = regioncode where p.region is null;
-- use name from sf
-- shit update Students p join SalesForceUsers2 sf on p.email = sf.email join Regions r on r.regioncode = sf.`global region` set p.region = regioncode where p.region is null;
update Students p join SfdcUsers sf on p.email = sf.email join Regions r on r.regioncode = sf.`global region` set p.region = regioncode where p.region is null;
-- use SF for subregion
update Students s join SfdcUsers sf on left(s.sumtotalid,15) = sf.`contact id` set s.SubRegion = sf.SubRegion where length(ifnull(sf.`global region`,'')) > 1 and left(`contact id`,3) = '005' and s.Region = sf.`Global Region`;

update Students set subregion = null where subregion= '';
update Students set region = null where region= '';

-- Tier
alter table Students add tier varchar(50);

update Students s 
join SumTotalUsers st on s.email = st.email and s.sumtotalid = st.usernumber
set s.tier = st.tier
where s.tier is null and st.tier in ('AFFILIATED','PREMIER','ADVANCED','READY');

update Students s 
join SumTotalUsers st on s.email = st.email 
set s.tier = st.tier
where s.tier is null and st.tier in ('AFFILIATED','PREMIER','ADVANCED','READY');

update Students s 
join SumTotalUsers st on s.sumtotalid = st.usernumber
set s.tier = st.tier
where s.tier is null and st.tier in ('AFFILIATED','PREMIER','ADVANCED','READY');

/*
starwars

select * from SumTotalUsers where tier = 'premier' and country = 'ca' and domain = 'north america' and organization = 'solution provider'
select organization, count(*) from SumTotalUsers group by organization
*/

-- SumTotalActive
alter table Students add SumTotalActive char(3);
update Students set SumTotalActive = 'No';
update Students set SumTotalActive = 'Yes' where sumtotalid in (select usernumber from SumTotalUsers where active = 'Yes');

-------------------------------------------------- 
-- Accreditations
-------------------------------------------------- 
-- select distinct Accreditation, Specialization, Role, Track, SpecialRule into Accreditations from AccreditationCourses

update AccreditationCourses set accreditation = replace(accreditation, '-',' - ') where accreditation like '%-%' and accreditation not like '% - %' ;
update AccreditationCourses set specialrule = 'N';
update AccreditationCourses set specialrule = 'Y' where accreditation in (
	'Red Hat Sales Specialist – Platform',
	'Red Hat Sales Engineer Specialist – Platform',
	'Red Hat Delivery Specialist – Platform',
	'Red Hat Sales Engineer Specialist – Middleware Integration Services',
	'Red Hat Delivery Specialist – Middleware Integration Services',
	'Red Hat Sales Specialist - PaaS Development',
    'Red Hat Sales Engineer Specialist – Business Process Automation');

-- Fix fields
delete from AccreditationCourses where auto = 0;
update AccreditationCourses set coursetype = 'eLab' where coursetype = 'eLab';
update AccreditationCourses set coursetype = 'eLearning' where coursetype = 'eLearning';
update AccreditationCourses set `course retired (yyyy mm)` = null where `course retired (yyyy mm)` = '1899-12-30 00:00:00.000';
update AccreditationCourses set startdate = null where StartDate = '1899-12-30 00:00:00.000';
update AccreditationCourses set code = 'A-MQ [VT]' where code = 'A-MQ[VT]';
update AccreditationCourses set code = 'RHEL (7) Implementation Part 1' where code = 'RHEL (7) Implementation part I';
update AccreditationCourses set code = 'RHEL (7) Implementation Part 2' where code = 'RHEL (7) Implementation part 2';
update AccreditationCourses set coursetype = 'eLearning' where code like 'CI-%' or code like 'CLI-%' and coursetype = 'eLab';

-- CodeNoLanguage
update AccreditationCourses set codenolanguage = null, newcoursecode = null;
update AccreditationCourses set CodeNoLanguage = code where coursetype = 'eLab';
update AccreditationCourses a join coursemappingv cm on a.code = cm.oldcode set NewCourseCode = cm.newcode where coursetype = 'eLearning' and cm.newcode in (select coursecode from Courses);

update AccreditationCourses a join Courses c on a.code = c.coursecode set newcoursecode = c.coursecode where coursetype = 'eLearning' and newcoursecode is null;
update AccreditationCourses a join Courses c on replace(a.code,'_','-') = c.coursecode set newcoursecode = c.coursecode where coursetype = 'eLearning' and newcoursecode is null;
update AccreditationCourses a join Courses c on replace(c.coursecode,'_','-') = a.code set newcoursecode = c.coursecode where coursetype = 'eLearning' and newcoursecode is null;
update AccreditationCourses a join Courses c on replace(c.coursecode,'_','-') = replace(a.code,'_','-') set newcoursecode = c.coursecode where coursetype = 'eLearning' and newcoursecode is null;
update AccreditationCourses a join Courses c on ifnull(a.newcoursecode, code) = coursecode and codenolanguage is null set codenolanguage = coursecodenolanguage;

update AccreditationCourses set codenolanguage = 'DCI-S-PLM-ASM-PLT' where code like 'DCI-S-PLM-ASM-PLT%' and codenolanguage is null;
update AccreditationCourses set codenolanguage = 'DCI-S-PLT-ASM-RHL' where code like 'DCI-S-PLT-ASM%' and codenolanguage is null;
update AccreditationCourses set codenolanguage = 'DCI-S-PLT-L2-RHL' where code like 'DCI-S-PLT-L2%' and codenolanguage is null;
update AccreditationCourses set codenolanguage = 'DCI-S-PLT-PRIPOS-RHL' where code like 'DCI-S-PLT-PRIPOS%' and codenolanguage is null;
update AccreditationCourses set codenolanguage = 'DCI-S-PLT-SAL-RHL' where code like 'DCI-S-PLT-SAL%' and codenolanguage is null;
update AccreditationCourses set codenolanguage = 'DCI-S-VIR-ASM-RHV' where code like 'DCI-S-VIR-ASM-RHV-PT%' and codenolanguage is null;
update AccreditationCourses set codenolanguage = 'DCI-S-VIR-PRI-RHV' where code like 'DCI-S-VIR-PRI-RHV%' and codenolanguage is null;
update AccreditationCourses set codenolanguage = 'DCI-SE-PLM-EXAM-PLT' where code like 'DCI-SE-PLM-EXAM-PLT%' and codenolanguage is null;
-- update a set codenolanguage = 'DCI-SE-PLT-EXAM-RHL' from AccreditationCourses a where code like 'DCI-SE-PLT-EXAM-RHLIMPTECH%' and codenolanguage is null
update AccreditationCourses set codenolanguage = 'DCI-SE-PLT-L2-RHL' where code like 'DCI-SE-PLT-L2%' and codenolanguage is null;
update AccreditationCourses set codenolanguage = 'DCI-SE-PLT-PRIPOS-RHL' where code like 'DCI-SE-PLT-PRIPOS%' and codenolanguage is null;
update AccreditationCourses set codenolanguage = 'DCI-SE-PLT-SAL-RHL' where code like 'DCI-SE-PLT-SAL%' and codenolanguage is null;
update AccreditationCourses set codenolanguage = 'DCI-SE-VIR-EXAM-RHV' where code like 'DCI-SE-VIR-EXAM-RHV%' and codenolanguage is null;
update AccreditationCourses set codenolanguage = 'MWS-S-APD-ASM-APD' where code like 'MWS-S-APD-ASM%' and codenolanguage is null;
update AccreditationCourses set codenolanguage = 'MWS-S-MWM-ASM-MWM' where code like 'MWS-S-MWM-ASM%' and codenolanguage is null;
update AccreditationCourses set codenolanguage = 'MWS-SE-APD-EXAM-APD' where code like 'MWS-SE-APD-EXAM%' and codenolanguage is null;
update AccreditationCourses set codenolanguage = 'MWS-SE-MWM-EXAM-INT' where code like 'MWS-SE-MWM-EXAM%' and codenolanguage is null;
update AccreditationCourses set codenolanguage = 'CLI-S-IAS-ASM-OSE' where auto = 241;

update AccreditationCourses set code = 'DCI-S-PLT-SAL-SAT', codenolanguage = 'DCI-S-PLT-SAL-SAT' where code = 'DCI-S-PLT-RHL';
update AccreditationCourses set code = 'DCI-S-PLT-SAL-SAT', codenolanguage = 'DCI-S-PLT-SAL-SAT' where code = 'DCI-S-PLT-SAL-RHL';
update AccreditationCourses set code = 'DCI-SE-PLT-SAL-SAT', codenolanguage = 'DCI-SE-PLT-SAL-SAT' where code = 'DCI-SE-PLT-RHL';
update AccreditationCourses set code = 'DCI-SE-PLT-SAL-SAT', codenolanguage = 'DCI-SE-PLT-SAL-SAT' where code = 'DCI-SE-PLT-SAL-RHL';
update AccreditationCourses set code = 'DCI-S-PLT-SAL-SAT', codenolanguage = 'DCI-S-PLT-SAL-SAT' where code = 'DCI-S-PLT-SAT';
update AccreditationCourses set code = 'DCI-S-PLT-SAL-SAT', codenolanguage = 'DCI-S-PLT-SAL-SAT' where code = 'DCI-S-PLT-SAL-SAT';
update AccreditationCourses set code = 'DCI-SE-PLT-SAL-SAT', codenolanguage = 'DCI-SE-PLT-SAL-SAT' where code = 'DCI-SE-PLT-SAT';
update AccreditationCourses set code = 'DCI-SE-PLT-SAL-SAT', codenolanguage = 'DCI-SE-PLT-SAL-SAT' where code = 'DCI-SE-PLT-SAL-SAT';

update AccreditationCourses e join eLabMapping m on m.coursename = e.codenolanguage set e.courseid = m.coursecode;
update AccreditationCourses set courseid = codenolanguage where coursetype = 'elearning' and courseid is null;
update AccreditationCourses set courseid = 'CLI-S-IAS-ASM-OSE', codenolanguage = 'CLI-S-IAS-ASM-OSE' where code = 'CLI-S-IAS-ASM-OSE-EN_US';
update AccreditationCourses set courseid = 'CLI-SE-IAS-ASM-EXAM', codenolanguage = 'CLI-SE-IAS-ASM-EXAM' where code = 'CLI-SE-IAS-ASM-EXAM-EN_US';
update AccreditationCourses set CourseId = 'CLI-ALL-IAS-ASM-OSP' where courseid in ('CLI-S-IAS-ASM-OSE','CLI-SE-IAS-ASM-EXAM');

update AccreditationCourses set role = 'Delivery' where accreditation like '%deliv%data%virt%';

update eLabsImport set CourseId = 'CLI-ALL-IAS-ASM-OSP' where courseid in ('CLI-S-IAS-ASM-OSE','CLI-SE-IAS-ASM-EXAM');
update eLabsImport set CourseId = 'CLI-ALL-IAS-ASM-OSP' where code in ('CLI-S-IAS-ASM-OSE','CLI-SE-IAS-ASM-EXAM');

------------------------------------------------- 
-- Completed Courses
-------------------------------------------------- 
drop table if exists CompletedCourses;

create table CompletedCourses as
select 
	*
from
	(
	select max(e2.auto) id, e.email, e.courseid, e.enddate, e.coursetype from eLabsImport e2 join (
		select e.email, courseid, max(e.enddate) enddate, 'eLab' as coursetype from eLabsImport e 
		where pfw in ('Pass','Waive') and length(ltrim(rtrim(email))) > 0 and enddate is not null and deleteflag = 'N' 
        and courseid is not null and courseid <> '' 
        group by email, e.courseid
		) e on e.email = e2.email and e.enddate = e2.enddate and e.courseid = e2.courseid
	where 
		deleteflag = 'N' and pfw in ('Pass','Waive') and e.courseid is not null and e.courseid <>''
	group by
		e.email, e.courseid, e.enddate, e.coursetype
	union
	select max(s2.id) id, s.email, c.coursecodenolanguage, s.enddate, s.coursetype from stv s2 join Courses c on s2.newcoursecode = c.CourseCode join (
		select email, coursecodenolanguage, max(enddate) enddate, 'eLearning' as coursetype 
        from stv s join Courses c on s.newcoursecode = c.CourseCode 
        where deleteflag = 'N' and left(ifnull(activity,''),1) not in ('1','2','3','4','5','6','7','8','9') 
        and coursecodenolanguage not like '%-cyk%' and coursecodenolanguage not like '%-chp%' and coursecodenolanguage not like '%-doc%'
        group by email, coursecodenolanguage
		) s on s2.email = s.email and c.coursecodenolanguage = s.coursecodenolanguage and s2.enddate = s.enddate
	group by s.email, c.coursecodenolanguage, s.enddate, s.coursetype
	) t;

update CompletedCourses set courseid = ltrim(rtrim(replace(replace(courseid,char(10),''),char(13),'')));

drop table if exists temp2;
create table temp2 as select email, courseid, max(enddate) enddate from CompletedCourses group by email, courseid;
alter table temp2 add id integer, add coursetype varchar(10);

create index idx_enddate on CompletedCourses (enddate);
create index idx_email on CompletedCourses (email);
create index idx_id on CompletedCourses (id);
create index idx_id2 on CompletedCourses (email, courseid, enddate);

create index idx_enddate on temp2 (enddate);
create index idx_email on temp2 (email);
create index idx_id on temp2 (id);
create index idx_id2 on temp2 (email, courseid, enddate);

update temp2 t join CompletedCourses cc on t.email = cc.email and t.courseid = cc.courseid and t.enddate = cc.enddate set t.id = cc.id, t.coursetype = cc.coursetype;

truncate table CompletedCourses;
insert into CompletedCourses select id, email, courseid, enddate, coursetype from temp2;
drop table temp2;

/*
update CompletedCourses set courseid = 'CLI-ALL-IAS-ASM-OSP' where courseid = 'CLI-SE-IAS-ASM-EXAM';
update CompletedCourses set courseid = 'CLI-ALL-IAS-ASM-OSP' where courseid = 'CLI-S-IAS-ASM-OSE';
*/
   
alter table CompletedCourses modify column email varchar (100) not null;
alter table CompletedCourses modify column courseid varchar (50) not null;
alter table CompletedCourses modify column enddate datetime not null;
alter table CompletedCourses add primary key PK_CompletedCourses(email, courseid);

-- CreateCourseNames
/*
create table CourseNames as select courseid, coursetype, count(*) from CompletedCourses group by courseid, coursetype order by 1, 2;
alter table CourseNames add coursename varchar(100) null;

update LmsCodes set lmscode = ltrim(rtrim(replace(replace(lmscode,char(10),''),char(13),'')));
update LmsCodes set lmscoursename = ltrim(rtrim(replace(replace(lmscoursename,char(10),''),char(13),'')));

update CourseNames cn join eLabMapping e on cn.courseid = e.coursecode set cn.coursename = e.coursename;
update CourseNames cn join LmsCodes e on cn.courseid = left(e.lmscode, length(e.lmscode)-6) set cn.coursename = e.lmscoursename where cn.coursename is null;
*/

drop table if exists temp;
create table temp as select distinct courseid from eLabsImport where length(ltrim(rtrim(ifnull(courseid, '')))) > 1;
alter table temp add coursename varchar(200) null;
drop table if exists temp2;
create table temp2 as select courseid, newcoursename, count(*) cnt from eLabsImport where deleteflag = 'N' group by courseid, newcoursename;
alter table temp2 add column auto int not null auto_increment primary key;
drop table if exists temp3;
create table temp3 as select courseid, max(cnt) cnt from temp2 where length(ltrim(rtrim(ifnull(courseid, '')))) > 1 group by courseid;
alter table temp3 add column keep int null;
update temp3 join temp2 on temp3.courseid = temp2.courseid and temp3.cnt = temp2.cnt set keep = temp2.auto ;
update temp join temp3 on temp.courseid = temp3.courseid join temp2 on temp2.auto = temp3.keep set temp.coursename = temp2.newcoursename;

insert into temp
select left(lmscode,length(lmscode)-6), lmscoursename from LmsCodes where left(lmscode,length(lmscode)-6) not in (select courseid from temp);

update temp set coursename = 'Satellite (6) FASTRAX ' where coursename = 'Satellite 6 FASTRAX ';
update temp set coursename = 'RHEL (7) Troubleshooting' where coursename = 'RHEL 7 Troubleshooting ';
update temp set coursename = 'A-MQ' where coursename = 'A-MQ [VT]';
update temp set coursename = 'OpenStack (6) Implementation' where coursename = 'CLI-S-IAS-ASM-OSE';
update temp set coursename = 'OpenStack (6) Implementation' where coursename = 'OpenStack 6 Implementation';
update temp set coursename = 'OpenShift (3) Implementation' where coursename = 'OpenShift 3 Implementation ';
update temp set coursename = 'RHEL (7) Implementation' where coursename = 'RHEL (7) Implementation Part 2';

alter table CompletedCourses add coursename varchar(200) null;
update CompletedCourses set coursename = null;
update CompletedCourses cc join temp t on cc.courseid = t.courseid set cc.coursename = t.coursename where cc.coursename is null;
update CompletedCourses cc join Courses c on cc.courseid = c.coursecodenolanguage set cc.coursename = c.coursename where language = 'EN_US' and coursetype = 'elearning' and cc.coursename is null;
update CompletedCourses cc join stv s on s.id = cc.id set cc.coursename = s.activity where coursetype = 'elearning' and cc.coursename is null;
update CompletedCourses cc join Courses c on cc.courseid = c.coursecodenolanguage set cc.coursename = c.coursename where language <> 'EN_US' and coursetype = 'elearning' and cc.coursename is null;
update CompletedCourses cc join eLabsImport e on e.auto = cc.id set cc.coursename = e.newcoursename where coursetype = 'elab' and newcoursename is not null and cc.coursename is null;
update CompletedCourses set coursename = 'Red Hat Sales Specialist - Platform' where courseid = 'DCI-S-PLT-ASM-RHL';

-- select count(distinct coursename) from CompletedCourses;
drop table if exists temp;
drop table if exists temp2;
drop table if exists temp3;

-------------------------------------------------- 
-- Accreditation Rules
-------------------------------------------------- 
update stv set accreditation = null;

-- AccreditationCourses2 - Contains all Courses required for accreditation (minus language)
drop table if exists AccreditationCourses2;
create table AccreditationCourses2 as select distinct courseid code, spec, coursetype, accreditation from AccreditationCourses where courseid is not null and specialrule = 'N';

-- AccreditationCourses3 - Contains all completions for Courses required for accreditation
drop table if exists AccreditationCourses3; 
create table AccreditationCourses3 as
select
	email,
	courseid,
	a2.coursetype,
	accreditation,
	max(enddate) enddate
from
	AccreditationCourses2 a2
	join CompletedCourses c
		on c.courseid = a2.code
group by
	email,
	courseid,
	a2.coursetype,
	accreditation;

-- Accreditation4 
drop table if exists AccreditationCourses4;

create table AccreditationCourses4 as
select 
	distinct email, a2.coursetype, a2.accreditation, a2.code 
from 
	AccreditationCourses3 a3 
	join AccreditationCourses2 a2 
		on a3.Accreditation = a2.Accreditation 
order by email;

alter table AccreditationCourses4 add enddate datetime;
create index idx1 on AccreditationCourses4 (email);
create index idx2 on AccreditationCourses4 (accreditation);
create index idx3 on AccreditationCourses4 (code);

update 
	AccreditationCourses4 a4
	join AccreditationCourses3 a3
		on a4.email = a3.email 
		and a4.Accreditation = a3.Accreditation
		and a4.Code = a3.CourseId
set
	a4.enddate = a3.enddate;

delete 
	a3
from 
	AccreditationCourses3 a3
	join (select distinct email, accreditation from AccreditationCourses4 a4 where enddate is null) a5
		on a3.email = a5.email and a3.Accreditation = a5.Accreditation;

delete 
	a4
from 
	AccreditationCourses4 a4
	join (select distinct email, accreditation from AccreditationCourses4 a4 where enddate is null) a5
		on a4.email = a5.email and a4.Accreditation = a5.Accreditation;

drop table if exists AccreditationCourses5;

create table AccreditationCourses5 as
select
	email, accreditation, max(enddate) enddate
from 
	AccreditationCourses3
group by
	email, accreditation;
    
alter table AccreditationCourses5 add keepid int, add keeptype varchar(10), add usedflag varchar(1);

update
	CompletedCourses cc
	join AccreditationCourses a 
		on a.courseid = cc.courseid
	join AccreditationCourses5 a5
		on a.Accreditation = a5.Accreditation
		and a5.email = cc.email 
		and a5.enddate = cc.enddate
set
	keepid = cc.id,
	keeptype = cc.coursetype,
	usedflag = 'Y'
where
	keepid is null
	and usedflag is null;
    
drop table if exists StudentAccreditations;

create table StudentAccreditations as 
select email, accreditation, enddate, keepid, keeptype from AccreditationCourses5; 
-- where enddate >= concat(year(curdate())-2, '-', right(concat('0',month(curdate())),2),'-01');

drop table if exists AccreditationCourses2;
drop table if exists AccreditationCourses3;
drop table if exists AccreditationCourses4;
drop table if exists AccreditationCourses5;

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -
-- Special Accreditations
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -
-- truncate table StudentAccreditations;

-- Red Hat Sales Engineer Specialist – Platform
-- SNOTE - coded rule good thru 7/31/15
-- satellite (5) fastrax is now satellite (6) fastrax
-- satellite (6) implementation not a course for SE anymore (only delivery)
-- still need rhel (7) implementation
-- NEW RULE = ((1 and 3 and 4) or (1 and 5)) AND (c11 and c12 and new 7)


drop table if exists AccreditationCourses2;

create table AccreditationCourses2 as 
select 
	* 
from 	
	CompletedCourses
where 
	courseid in (select distinct codenolanguage from AccreditationCourses where Accreditation in ('Red Hat Sales Engineer Specialist – Platform', 'Red Hat Sales Specialist – Platform'))
	or courseid in ('V1DCI-S-PLT-ASM-RHL6','V1DCI-S-PLT-ASM-RHL')
    or courseid like 'DCI-%PLT%EXAM%';
 
drop table if exists AccreditationCourses3;

create table AccreditationCourses3 as 
select 
	distinct email
from 
	AccreditationCourses2;

alter table AccreditationCourses3 add c1 datetime, add c2 datetime, add c3 datetime, add c4 datetime, add c5 datetime, add c6 datetime, add c7 datetime, add c8 datetime, add c9 datetime, add c10 datetime, add c11 datetime, add c12 datetime, add c13 datetime;

/*
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where coursename like 'DCI-S%-PLT-SAL-RHL' or coursename like 'DCI-S%-PLT-SAL-SAT' group by email) a2 on a3.email = a2.email set c1 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where coursename like 'DCI-S%-PLT-PRIPOS-RHL' group by email) a2 on a3.email = a2.email set c2 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where coursename like 'DCI-S%-PLT-L2-RHL' group by email) a2 on a3.email = a2.email set c3 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where coursename like 'DCI-S%-PLT-ASM-RHL' or coursename like 'DCI-S%-PLT-EXAM-RHL' or coursename = 'V1DCI-S%-PLT-ASM-RHL6' or coursename like 'V1DCI-S%-PLT-ASM-RHL' group by email) a2 on a3.email = a2.email set c4 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where coursename like 'DCI-S%-PLT-ASM-RHL7%' or coursename like 'DCI-S%-PLT-EXAM-RHL7%' group by email) a2 on a3.email = a2.email set c5 = a2.enddate;
-- update a3 set c6 = a2.enddate from AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where coursename like 'DCI-S%-PLT-EXAM-RHLIMPTECH_EN_US' group by email) a2 on a3.email = a2.email
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where coursename = 'Satellite (5) FASTRAX' group by email) a2 on a3.email = a2.email set c7 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where coursename = 'Satellite (6) Implementation' group by email) a2 on a3.email = a2.email set c8 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where coursename = 'RHEL (6) Implementation' group by email) a2 on a3.email = a2.email set c9 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where coursename = 'RHEL (6) Troubleshooting' group by email) a2 on a3.email = a2.email set c10 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where coursename = 'RHEL (7) Implementation part 1' group by email) a2 on a3.email = a2.email set c11 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where coursename = 'RHEL (7) Implementation part 2' group by email) a2 on a3.email = a2.email set c12 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where coursename = 'RHEL Troubleshooting' group by email) a2 on a3.email = a2.email set c13 = a2.enddate;

insert into StudentAccreditations
select email, 'Red Hat Sales Engineer Specialist – Platform', enddate, keepid, keeptype from AccreditationCourses3 a3
where 
	((c1 is not null and c2 is not null and c3 is not null and c4 is not null) or (c1 is not null and c5 is not null))
	and (c7 is not null or c8 is not null)
	and ((c9 is not null and c10 is not null) or (c11 is not null and c12 is not null)) -- and c13 is not null)) -- add c13 for july roadmap
    and enddate >= concat(year(curdate())-2, '-', right(concat('0',month(curdate())),2),'-01');

*/
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid like 'DCI-S%-PLT-SAL-RHL' or courseid like 'DCI-S%-PLT-SAL-SAT' group by email) a2 on a3.email = a2.email set c1 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid like 'DCI-S%-PLT-PRIPOS-RHL' group by email) a2 on a3.email = a2.email set c2 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid like 'DCI-S%-PLT-L2-RHL' group by email) a2 on a3.email = a2.email set c3 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid like 'DCI-S%-PLT-ASM-RHL' or courseid like 'DCI-S%-PLT-EXAM-RHL' or courseid = 'V1DCI-S%-PLT-ASM-RHL6' or courseid like 'V1DCI-S%-PLT-ASM-RHL' group by email) a2 on a3.email = a2.email set c4 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid like 'DCI-S%-PLT-ASM-RHL7%' or courseid like 'DCI-S%-PLT-EXAM-RHL7%' group by email) a2 on a3.email = a2.email set c5 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'DCI-TECH-PLT-EXAM-SAT6.1FST' group by email) a2 on a3.email = a2.email set c6 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'DCI-TECH-PLT-EXAM-SAT5.1FST' group by email) a2 on a3.email = a2.email set c7 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'DCI-DEL-PLT-EXAM-SAT6.1IMP' group by email) a2 on a3.email = a2.email set c8 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'DCI-TECH-PLT-EXAM-RHL6IMP' group by email) a2 on a3.email = a2.email set c9 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'DCI-DEL-PLT-EXAM-RHL6TRB' group by email) a2 on a3.email = a2.email set c10 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'DCI-TECH-PLT-EXAM-RHL7IMP' group by email) a2 on a3.email = a2.email set c11 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'DCI-TECH-PLT-EXAM-RHL7IMP' group by email) a2 on a3.email = a2.email set c12 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'DCI-DEL-PLT-EXAM-RHL7TRB' group by email) a2 on a3.email = a2.email set c13 = a2.enddate;

alter table AccreditationCourses3 add enddate datetime, add keepid int, add keeptype varchar(10);

update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 group by email) a2 on a3.email = a2.email set a3.enddate = a2.enddate; -- where a3.enddate is null or a3.enddate < a2.enddate
update AccreditationCourses3 a3 join (select id, email, coursetype, enddate from AccreditationCourses2 ) a2 on a3.email = a2.email set a3.keepid = a2.id, keeptype = coursetype where a3.enddate = a2.enddate;


-- New Rule
insert into StudentAccreditations
select email, 'Red Hat Sales Engineer Specialist – Platform', enddate, keepid, keeptype from AccreditationCourses3 a3
where 
	((c1 is not null and c3 is not null and c4 is not null) or (c1 is not null and c5 is not null))
	and (c11 is not null and c12 is not null and c6 is not null);
--     and enddate >= concat(year(curdate())-2, '-', right(concat('0',month(curdate())),2),'-01');
-- 	and enddate >= '2015-08-01';

-- Old Rule
insert into StudentAccreditations
select email, 'Red Hat Sales Engineer Specialist – Platform', enddate, keepid, keeptype from AccreditationCourses3 a3
where 
	((c1 is not null and c2 is not null and c3 is not null and c4 is not null) or (c1 is not null and c5 is not null))
	and (c7 is not null or c8 is not null)
	and ((c9 is not null and c10 is not null) or (c11 is not null and c12 is not null)) -- and c13 is not null)) -- add c13 for july roadmap
--    and enddate >= concat(year(curdate())-2, '-', right(concat('0',month(curdate())),2),'-01')
	and email not in (select email from StudentAccreditations where accreditation = 'Red Hat Sales Engineer Specialist – Platform');
--    and enddate >= concat(year(curdate())-2, '-', right(concat('0',month(curdate())),2),'-01');

/*
select count(*) from StudentCourseCompletionAccreditationDetails where accreditation = 'Red Hat Delivery Specialist – Middleware Integration Services' and accreditationdate >= '2013-11-01' 
select count(*) from StudentAccreditations where accreditation = 'Red Hat Delivery Specialist – Middleware Integration Services' 

admin@nisi-comtec.com
ahein@ccpsoft.de

select * from StudentAccreditations where email = 'admin@nisi-comtec.com'
select * from CompletedCourses where email = 'admin@nisi-comtec.com'
select * from StudentAccreditations where email = 'ahein@ccpsoft.de'
select * from CompletedCourses where email = 'Abhishek5.Chatterjee5@cognizant.com'
select * from eLabsImport where email = 'Abhishek5.Chatterjee5@cognizant.com'
select * from stv where email = 'Abhishek5.Chatterjee5@cognizant.com'

Camel Development with JBoss FUSE
MWS-TECH-MWI-EXAM-FUS

select * from Courses

Abhishek5.Chatterjee5@cognizant.com
albert@lsd.co.za


select * from StudentCourseCompletionAccreditationDetails where accreditation = 'Red Hat Delivery Specialist – Middleware Integration Services' and accreditationdate >= '2013-11-01' and email not in 
(select email from StudentAccreditations where accreditation = 'Red Hat Delivery Specialist – Middleware Integration Services' )

select * from StudentCourseCompletionAccreditationDetails

*/

-- Red Hat Sales Specialist - Platform
drop table if exists AccreditationCourses2;
create table AccreditationCourses2 as
select 
	*
from 	
	CompletedCourses
where 
	courseid in (select distinct codenolanguage from AccreditationCourses where Accreditation in ('Red Hat Sales Engineer Specialist – Platform', 'Red Hat Sales Specialist – Platform'))
	or courseid = 'V1DCI-S-PLT-ASM-RHL6'
	or courseid like 'DCI-S%-PLT-PRIPOS-RHL';

/*
V1DCI-S-PLT-ASM-RHL6
DCI-S-PLT-SAL-SAT
select * from stv where newcoursecode like 'DCI-S-PLT-ASM-RHL%'
select * from stv where email = 'admin@nisi-comtec.com'
select * from stv where email = 'ahein@ccpsoft.de'
select * from CompletedCourses where email = 'ahein@ccpsoft.de' -- V1DCI-S-PLT-ASM-RHL6-EN_US
*/
       
drop table if exists AccreditationCourses3;
create table AccreditationCourses3 as 
select 
	distinct email
from 
	AccreditationCourses2;

alter table AccreditationCourses3 add c1 datetime, add c2 datetime, add c3 datetime, add c4 datetime, add c5 datetime, add c6 datetime, add c7 datetime, add c8 datetime, add c9 datetime, add c10 datetime, add c11 datetime, add c12 datetime, add c13 datetime;

update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid like 'DCI-S%-PLT-SAL-RHL' or courseid like 'DCI-S%-PLT-SAL-SAT' group by email) a2 on a3.email = a2.email set c1 = a2.enddate ;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid like 'DCI-S%-PLT-PRIPOS-RHL' group by email) a2 on a3.email = a2.email set c2 = a2.enddate; 
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid like 'DCI-S%-PLT-L2-RHL' group by email) a2 on a3.email = a2.email set c3 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid in ('DCI-S-PLT-ASM-RHL','V1DCI-S-PLT-ASM-RHL6') group by email) a2 on a3.email = a2.email set c4 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid like 'DCI-S%-PLT-ASM-RHL7%' group by email) a2 on a3.email = a2.email set c5 = a2.enddate;

alter table AccreditationCourses3 add enddate datetime, add keepid int, add keeptype varchar(10);

update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 group by email) a2 on a3.email = a2.email set a3.enddate = a2.enddate;-- where a3.enddate is null or a3.enddate < a2.enddate
update AccreditationCourses3 a3 join (select id, email, coursetype, enddate from AccreditationCourses2 ) a2 on a3.email = a2.email set a3.keepid = a2.id, keeptype = coursetype where a3.enddate = a2.enddate;

insert into StudentAccreditations
select email, 'Red Hat Sales Specialist – Platform', enddate, keepid, keeptype from AccreditationCourses3 a3
where 
	((c1 is not null and c2 is not null and c3 is not null and c4 is not null) or (c1 is not null and c5 is not null));
--    and enddate >= concat(year(curdate())-2, '-', right(concat('0',month(curdate())),2),'-01');
-- 	and enddate >= '5/1/13'

drop table if exists AccreditationCourses2;
drop table if exists AccreditationCourses3;


-- Red Hat Sales Engineer Specialist – Middleware Integration Services

-- SNOTE - Current rule good thru 8/30/15
-- SNOTE - New rule is (1 and 2) and (4) after 8/30/15
-- grandfather in past completions for new rule
-- 6 replaced 5 and became advanced training (name change - fuse messaging became enterprise messaging with jboss a-mq
-- 7 replaced by "services development with switchyard" and became advanced training (grandfather in past completions for accreditation)


drop table if exists AccreditationCourses2;

create table AccreditationCourses2 as
select 
	* 
from 	
	CompletedCourses
where 
	courseid in (select distinct codenolanguage from AccreditationCourses where Accreditation in ('Red Hat Sales Engineer Specialist – Middleware Integration Services'))
    or courseid like 'MWS-%-%exam%';
    
drop table if exists AccreditationCourses3;

create table AccreditationCourses3 as
select 
	distinct email
from 
	AccreditationCourses2;
    
alter table AccreditationCourses3 add c1 datetime, add c2 datetime, add c3 datetime, add c4 datetime, add c5 datetime, add c6 datetime, add c7 datetime, add c8 datetime, add c9 datetime, add c10 datetime, add c11 datetime, add c12 datetime, add c13 datetime;

update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid like 'MWS-SE-MWI-EXAM-FUS%' group by email) a2 on a3.email = a2.email set c1 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid like 'MWS-SE-MWI-EXAM-AMQ%' group by email) a2 on a3.email = a2.email set c2 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'MWS-TECH-MWI-EXAM-FUS' group by email) a2 on a3.email = a2.email set c3 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'MWS-TECH-MWI-EXAM-FUS' group by email) a2 on a3.email = a2.email set c4 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'MWS-S-MWI-EXAM-AMQ' group by email) a2 on a3.email = a2.email set c5 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'Fuse Messaging' group by email) a2 on a3.email = a2.email set c6 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'MWS-DEL-ADD-SDSY-EXAM' group by email) a2 on a3.email = a2.email set c7 = a2.enddate;

alter table AccreditationCourses3 add enddate datetime, add keepid int, add keeptype varchar(10);

update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 group by email) a2 on a3.email = a2.email set a3.enddate = a2.enddate; -- where a3.enddate is null or a3.enddate < a2.enddate
update AccreditationCourses3 a3 join (select id, email, coursetype, enddate from AccreditationCourses2 ) a2 on a3.email = a2.email set a3.keepid = a2.id, keeptype = coursetype where a3.enddate = a2.enddate;

-- New Rule
insert into StudentAccreditations
select email, 'Red Hat Sales Engineer Specialist – Middleware Integration Services', enddate, keepid, keeptype from AccreditationCourses3 a3
where 
	enddate >= concat(year(curdate())-2, '-', right(concat('0',month(curdate())),2),'-01')
	and (c1 is not null and c2 is not null and c4 is not null);

-- Old Rule
insert into StudentAccreditations
select email, 'Red Hat Sales Engineer Specialist – Middleware Integration Services', enddate, keepid, keeptype from AccreditationCourses3 a3
where 
	-- enddate >= concat(year(curdate())-2, '-', right(concat('0',month(curdate())),2),'-01')
		(
		(
		(c1 is not null and c2 is not null)
		and (c3 is not null or c4 is not null) 
		and (c5 is not null or c6 is not null)
		)
		or
		(
		(c1 is not null and c2 is not null)
		and (c3 is not null or c4 is not null) 
		and (c7 is not null)
		)
		or
		(
		(c1 is not null and c2 is not null)
		and (c5 is not null or c6 is not null)
		and (c7 is not null)
		)
		)
	and email not in (select email from StudentAccreditations where accreditation = 'Red Hat Sales Engineer Specialist – Middleware Integration Services');

drop table if exists AccreditationCourses2;
drop table if exists AccreditationCourses3;


-- Red Hat Delivery Specialist – Middleware Integration Services

-- SNOTE - current rule thru 8/30/15
-- SNOTE - 1 or 2 earns accreditation after 8/30/15 (JUST CAMEL)
-- anyone who took amq in the past will get delivery accreditation

drop table if exists AccreditationCourses2;

create table AccreditationCourses2 as
select 
	* 
from 	
	CompletedCourses
where 
	courseid in (select distinct courseid from AccreditationCourses where Accreditation in ('Red Hat Delivery Specialist – Middleware Integration Services'))
    or courseid like 'MWS-%-EXAM-%'
    or courseid = 'MWS-DEL-ADD-SDSY-EXAM';


/*
select * from AccreditationCourses where Accreditation in ('Red Hat Delivery Specialist – Middleware Integration Services')
update AccreditationCourses set courseid = 'MWS-TECH-MWI-EXAM-FUS' where codenolanguage = 'Camel Development with JBoss FUSE [VT]';
update AccreditationCourses set courseid = 'MWS-TECH-MWI-EXAM-AMQ' where codenolanguage in ('Fuse Messaging','A-MQ [VT]');

select * from AccreditationCourses where codenolanguage in ('Fuse Messaging','A-MQ [VT]');
select * from AccreditationCourses where codenolanguage = 'Fuse Messaging';
*/

drop table if exists AccreditationCourses3;

create table AccreditationCourses3 as
select 
	distinct email
from 
	AccreditationCourses2;
    
alter table AccreditationCourses3 add c1 datetime, add c2 datetime, add c3 datetime, add c4 datetime, add c5 datetime, add c6 datetime, add c7 datetime, add c8 datetime, add c9 datetime, add c10 datetime, add c11 datetime, add c12 datetime, add c13 datetime;

update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid like 'MWS-TECH-MWI-EXAM-FUS%' group by email) a2 on a3.email = a2.email set c1 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid like 'MWS-TECH-MWI-EXAM-FUS%' group by email) a2 on a3.email = a2.email set c2 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid like 'MWS-%-MWI-EXAM-AMQ' group by email) a2 on a3.email = a2.email set c3 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid like 'MWS-%-MWI-EXAM-AMQ' group by email) a2 on a3.email = a2.email set c4 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'MWS-DEL-ADD-SDSY-EXAM' group by email) a2 on a3.email = a2.email set c5 = a2.enddate;

alter table AccreditationCourses3 add enddate datetime, add keepid int, add keeptype varchar(10);

update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 group by email) a2 on a3.email = a2.email set a3.enddate = a2.enddate; -- where a3.enddate is null or a3.enddate < a2.enddate
update AccreditationCourses3 a3 join (select id, email, coursetype, enddate from AccreditationCourses2 ) a2 on a3.email = a2.email set a3.keepid = a2.id, keeptype = coursetype where a3.enddate = a2.enddate;

/*
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where coursename = 'Camel Development with JBoss FUSE [VT]' group by email) a2 on a3.email = a2.email set c1 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where coursename = 'Camel Development with JBoss FUSE' group by email) a2 on a3.email = a2.email set c2 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where coursename = 'A-MQ [VT]' group by email) a2 on a3.email = a2.email set c3 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where coursename = 'Fuse Messaging' group by email) a2 on a3.email = a2.email set c4 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where coursename = 'Services Development with Fuse Service Works' group by email) a2 on a3.email = a2.email set c5 = a2.enddate;
*/

/*
-- New Rule
insert into StudentAccreditations
select email, 'Red Hat Delivery Specialist – Middleware Integration Services', enddate, keepid, keeptype from AccreditationCourses3 a3
where 
-- enddate >= '5/1/13'
-- 	enddate >= concat(year(curdate())-2, '-', right(concat('0',month(curdate())),2),'-01')
	(c1 is not null);
--    and enddate >= '2015-09-01';
*/

-- Old Rule + New Rule
insert into StudentAccreditations
select email, 'Red Hat Delivery Specialist – Middleware Integration Services', enddate, keepid, keeptype from AccreditationCourses3 a3
where 
	c1 is not null
    or 
		(
		(c3 is not null or c4 is not null)
		and (c5 is not null)
		);

-- enddate >= '5/1/13'
	-- enddate >= concat(year(curdate())-2, '-', right(concat('0',month(curdate())),2),'-01')
/*	    (

		(
		(c1 is not null or c2 is not null)
  		and (c3 is not null or c4 is not null) 
		)
		or
		(
		(c1 is not null or c2 is not null)
		and (c5 is not null)
		)
		or
		(
		(c3 is not null or c4 is not null)
		and (c5 is not null)
		)
		)
	and email not in (select email from StudentAccreditations where accreditation = 'Red Hat Delivery Specialist – Middleware Integration Services');
*/

drop table if exists AccreditationCourses2;
drop table if exists AccreditationCourses3;

-- Red Hat Delivery Specialist - Platform
-- SNOTE - current code is rule thru 7/31/15
-- SNOTE - new rule
	-- satellite (5) fastrax replaced by satellite (6) fastrax
    -- NEW RULE = satellite (6) fastrax + satellite (6) implementation + rhel (7) implementation parts 1 & 2
-- rhel 6 replaced with rhel 7 troubleshooting and became advanced training


drop table if exists AccreditationCourses2;

create table AccreditationCourses2 as
select 
	* 
from 	
	CompletedCourses
where 
	courseid in (select distinct codenolanguage from AccreditationCourses where Accreditation in ('Red Hat Delivery Specialist – Platform')) or courseid like 'RHEL%7%Implementation%1%2%'
    or courseid like 'DCI-%-PLT-EXAM-%';
    

drop table if exists AccreditationCourses3;
create table AccreditationCourses3 as
select 
	distinct email
from 
	AccreditationCourses2;

alter table AccreditationCourses3 add c1 datetime, add c2 datetime, add c3 datetime, add c4 datetime, add c5 datetime, add c6 datetime, add c7 datetime, add c8 datetime, add c9 datetime, add c10 datetime, add c11 datetime, add c12 datetime, add c13 datetime;

update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid like 'DCI-DEL-PLT-ACC-SAT%' group by email) a2 on a3.email = a2.email set c1 = a2.enddate; 
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid like 'DCI-DEL-PLT-ACC-RHL%' group by email) a2 on a3.email = a2.email set c2 = a2.enddate; 
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'DCI-TECH-PLT-EXAM-SAT5.1FST' group by email) a2 on a3.email = a2.email set c3 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'DCI-DEL-PLT-EXAM-SAT6.1IMP' group by email) a2 on a3.email = a2.email set c4 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'DCI-TECH-PLT-EXAM-RHL6IMP' group by email) a2 on a3.email = a2.email set c5 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'DCI-DEL-PLT-EXAM-RHL6TRB' group by email) a2 on a3.email = a2.email set c6 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid like 'DCI-TECH-PLT-EXAM-RHL7IMP' group by email) a2 on a3.email = a2.email set c7 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'DCI-TECH-PLT-EXAM-SAT6.1FST' group by email) a2 on a3.email = a2.email set c8 = a2.enddate;
-- update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where coursename like 'RHEL%7%Implementation%2%' group by email) a2 on a3.email = a2.email set c9 = a2.enddate;
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'DCI-TECH-PLT-EXAM-RHL7IMP' group by email) a2 on a3.email = a2.email set c10 = a2.enddate;

alter table AccreditationCourses3 add enddate datetime, add keepid int, add keeptype varchar(10);

update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 group by email) a2 on a3.email = a2.email set a3.enddate = a2.enddate;-- where a3.enddate is null or a3.enddate < a2.enddate
update AccreditationCourses3 a3 join (select id, email, coursetype, enddate from AccreditationCourses2 ) a2 on a3.email = a2.email set a3.keepid = a2.id, keeptype = coursetype where a3.enddate = a2.enddate;

-- New Rule
insert into StudentAccreditations
select email, 'Red Hat Delivery Specialist – Platform', enddate, keepid, keeptype from AccreditationCourses3 a3
where
-- 	enddate >= concat(year(curdate())-2, '-', right(concat('0',month(curdate())),2),'-01')
-- 	enddate >= '5/1/13'
	(c4 is not null and c7 is not null and c8 is not null);

-- Old Rule
insert into StudentAccreditations
select email, 'Red Hat Delivery Specialist – Platform', enddate, keepid, keeptype from AccreditationCourses3 a3
where
-- 	enddate >= concat(year(curdate())-2, '-', right(concat('0',month(curdate())),2),'-01')
-- 	enddate >= '5/1/13'
	(
-- 	(c1 is not null and c2 is not null) and
	(c3 is not null or c4 is not null) 
	and ((c5 is not null and c6 is not null) or (c7 is not null and c8 is not null) or (c10 is not null))
	)
	and email not in (select email from StudentAccreditations where accreditation = 'Red Hat Delivery Specialist – Platform');

drop table if exists AccreditationCourses2;
drop table if exists AccreditationCourses3;

-- Red Hat Sales Engineer - Business Process Automation
drop table if exists AccreditationCourses2;

create table AccreditationCourses2 as
select 
	* 
from 	
	CompletedCourses
where 
	courseid in (select distinct codenolanguage from AccreditationCourses where Accreditation in ('Red Hat Sales Engineer Specialist – Business Process Automation'))
	or courseid = 'MWS-SE-BPA-ASM-BPMS'
    or courseid like 'MWS-%-BPA-%';

drop table if exists AccreditationCourses3;
create table AccreditationCourses3 as
select 
	distinct email
from 
	AccreditationCourses2;

alter table AccreditationCourses3 add c1 datetime, add c2 datetime, add c3 datetime, add c4 datetime, add c5 datetime, add c6 datetime, add c7 datetime, add c8 datetime, add c9 datetime, add c10 datetime, add c11 datetime, add c12 datetime, add c13 datetime;

update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid = 'MWS-TECH-BPA-EXAM-BPMS6' group by email) a2 on a3.email = a2.email set c1 = a2.enddate; 
update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 where courseid like 'MWS-S%-BPA-ASM-BPMS%' group by email) a2 on a3.email = a2.email set c2 = a2.enddate; 

alter table AccreditationCourses3 add enddate datetime, add keepid int, add keeptype varchar(10);

update AccreditationCourses3 a3 join (select email, max(enddate) enddate from AccreditationCourses2 group by email) a2 on a3.email = a2.email set a3.enddate = a2.enddate;-- where a3.enddate is null or a3.enddate < a2.enddate
update AccreditationCourses3 a3 join (select id, email, coursetype, enddate from AccreditationCourses2 ) a2 on a3.email = a2.email set a3.keepid = a2.id, keeptype = coursetype where a3.enddate = a2.enddate;

insert into StudentAccreditations
select distinct email, 'Red Hat Sales Engineer Specialist – Business Process Automation', enddate, keepid, keeptype from AccreditationCourses3 a3
where
-- 	enddate >= concat(year(curdate())-2, '-', right(concat('0',month(curdate())),2),'-01')
-- 	enddate >= '5/1/13'
	c1 is not null 
    and c2 is not null;

drop table if exists AccreditationCourses2;
drop table if exists AccreditationCourses3;

-- Expired Accreditations
drop table if exists StudentAccreditationsExpired; 
create table StudentAccreditationsExpired as 
select * from StudentAccreditations where enddate < concat(year(curdate())-2, '-', right(concat('0',month(curdate())),2),'-01');
delete from StudentAccreditations where enddate < concat(year(curdate())-2, '-', right(concat('0',month(curdate())),2),'-01');
-- where enddate >= concat(year(curdate())-2, '-', right(concat('0',month(curdate())),2),'-01');

drop table if exists AccreditationCourses2;
drop table if exists AccreditationCourses3;
drop table if exists AccreditationCourses4;
drop table if exists AccreditationCourses5;

/*
alter view QvExport as
select distinct
	left(datename(mm, `factAttempt_attemptEndDtDetail`),3)+'-'+cast(year(`factAttempt_attemptEndDtDetail`) as varchar) as Month
	,year(`factAttempt_attemptEndDtDetail`) as Year
	,cast('FY '+right(cast(year(dateadd(mm, 10, `factAttempt_attemptEndDtDetail`)) as varchar),2) as varchar)as FY
	,ifnull(cast(fullname as varchar),'') as `full name`
	,ifnull(company,'') company
	,ifnull(`dimUser_PrimaryJobNameDetail`,'') `User Primary Job`
	,ifnull(ifnull(newpartnertype, `dimUser_PrimaryOrgNameDetail`),'') `User Primary Organization`
	,ifnull(region,'') `User Primary Domain`
	,ifnull(country,'') `Country Code`
	,Students.email Email
	,ifnull(c.coursecode,'') Code
-- 	,c.CourseCodeNoLanguage Code2
	,ifnull(`factAttempt_attemptStartDtDetail`,'') `Start Date`
	,ifnull(`factAttempt_attemptEndDtDetail`,'') `End Date`
	,case when Students.email like '%@redhat.com' or company like '%red hat%' then 'Yes' else 'No' end RedHat
	,case when sa.keepid is null then 'No' else 'Yes' end `Is Certification`
	,case when sa.keepid is not null then 'No' when t.email is null then 'Yes' else 'No' end `In Progress`
	,ifnull(ifnull(a.role, c.rolename),'') Role
	,ifnull(upper(ifnull(a.track, c.trackname)),'') `Skills Track`
	,ifnull(ifnull(a.specialization, c.specializationname),'') `Specialization`
	,ifnull(a.Accreditation,'') Accreditation
	,ifnull(case when region = 'Fed/Sled' then 'NA PUBLIC SECTOR' when region = 'NA' then 'NA COMM' else region end,'') Geo
	,'Direct' `Channel Mix`
	,'None' `Split Business Unit`
	,ifnull(Tier,'') Tier
from
	Students join
	SumTotal s
		on Students.email = s.`dimUser_EmpEmailDetail`
	left join Courses c
		on s.newcoursecode = c.coursecode
	left join 
		(select * from StudentAccreditations where keeptype = 'eLearning') sa
		on s.id = sa.keepid
	left join (select distinct role, specialization, upper(track) track, accreditation from AccreditationCourses) a
		on sa.accreditation = a.accreditation
	left join 
		(select distinct sa.email, sa.accreditation, a.CodeNoLanguage from StudentAccreditations sa join AccreditationCourses a on sa.accreditation = a.Accreditation) t
		on c.CourseCodeNoLanguage = t.CodeNoLanguage
		and Students.email = t.email
-- 	left join StudentAccreditations sa2
-- 		on t.accreditation = sa2.accreditation
-- 		and t.email = sa2.email		
where
	deleteflag = 'N'
	and factAttempt_attemptEndDtDetail < cast(cast(month(getdate()) as varchar)+'/1/'+cast(year(getdate()) as varchar) as datetime)
	and factAttempt_attemptEndDtDetail >= dateadd(yy, -2, cast(cast(month(getdate()) as varchar)+'/1/'+cast(year(getdate()) as varchar) as datetime))
	and (factAttempt_attemptExprDtDetail >= cast(cast(month(getdate()) as varchar)+'/1/'+cast(year(getdate()) as varchar) as datetime) or factAttempt_attemptExprDtDetail is null)
union
select
	left(datename(mm, e.enddate),3)+'-'+cast(year(e.enddate) as varchar) as Month
	,year(e.enddate) as Year
	,'FY '+right(cast(year(dateadd(mm, 10, e.enddate)) as varchar),2) as FY
	,ifnull(s.fullname,'') as `full name`
	,ifnull(s.company,'') company
	,'' `User Primary Job`
	,ifnull(e.partnertype,'') `User Primary Organization`
	,ifnull(s.region,'') `User Primary Domain`
	,ifnull(s.country,'') `Country Code`
	,s.email Email
	,'' Code
-- 	,'' Code2
	,'' `Start Date`
	,e.enddate `End Date`
	,case when s.email like '%@redhat.com' or s.company like '%red hat%' then 'Yes' else 'No' end RedHat
	,'Yes' `Is Certification`
	,'No'
	,ifnull(a.role,'') Role
	,ifnull(a.track,'') `Skills Track`
	,ifnull(a.specialization,'') `Specialization`
	,ifnull(a.Accreditation,'')
	,case when s.region = 'Fed/Sled' then 'NA PUBLIC SECTOR' when s.region = 'NA' then 'NA COMM' else ifnull(s.region,'') end Geo
	,'Direct' `Channel Mix`
	,'None' `Split Business Unit`
	,ifnull(Tier,'') Tier
from 
	Students s
	join StudentAccreditations sa
		on s.email = sa.email
	join (select distinct role, specialization, upper(track) track, accreditation from AccreditationCourses) a
		on sa.accreditation = a.accreditation
	join eLabsImport e
		on sa.keepid = e.auto
where
	keeptype = 'eLab'
	and deleteflag = 'N'
go
*/


/*
select * from QvExport q join (
select * from CompletedCourses where coursetype = 'elearning' and courseid in (select left(coursecode, length(coursecode)-6) from eLabCourses)) t
on q.email = t.email and left(q.code, length(q.code)-6) = t.courseid and q.`end date` = t.enddate 

select * from QvExport


select * from eLabsImport order by enddate desc
select max(auto), max(autoid) from eLabsImport
-- 16450

select count(*) from eLabsImport where auto > 16450
select * from eLabsImport where auto > 16450 and pfw <> 'fail'

delete from eLabsImport where autoid > 16450;

select * from CompletedCourses where coursetype = 'elearning' and courseid in (select left(coursecode, length(coursecode)-6) from eLabCourses)
select * from eLabsImport

select * from eLabCourses
insert into eLabsImport (email, newcoursename, enddate, pfw, deleteflag, courseid, modifynote)
select email, coursename, enddate, 'Pass', 'N', courseid, '11/12 workaround' from CompletedCourses where coursetype = 'elearning' and courseid in (select left(coursecode, length(coursecode)-6) from eLabCourses)
*/ -- starwars

drop table if exists QVeLabsInProgress;
create table QVeLabsInProgress as
select
	t.email,
	t.fullname,
	t.company,
	t.region,
	redhat,
	max(c1) c1,
	max(c2) c2,
	max(c3) c3,
	max(c4) c4,
	max(c5) c5,
	max(c6) c6,
	max(c7) c7,
	max(c8) c8,
	max(c9) c9,
	max(c10) c10,
	max(c11) c11,
	max(c12) c12,
	max(c13) c13,
	max(c14) c14,
	max(c15) c15,
	max(c16) c16,
	max(c17) c17,
	max(c18) c18,
	max(c19) c19,
	max(c20) c20,
	max(c21) c21,
	region2,
	channelmix,
	businessunit,
	tier,
    companycode
from
	(
	select 
		s.email,
		s.fullname,
		s.company,
		s.region,
		case when s.email like '%@redhat.com' or s.company like '%red hat%' then 'Yes' else 'No' end RedHat,
		case when newcoursename in ('A-MQ [VT]') then enddate end c1,
		case when newcoursename in ('Application Development with EAP 6') then enddate end c2,
		case when newcoursename in ('Application Migration to EAP 6') then enddate end c3,
		case when newcoursename in ('Business Logic Development with BPMS 6') then enddate end c4,
		case when newcoursename in ('Camel Development with JBoss Fuse','Camel Development with JBoss FUSE [VT]') then enddate end c5,
		case when newcoursename in ('CloudForms FASTRAX') then enddate end c6,
		case when newcoursename in ('CloudForms Implementation') then enddate end c7,
		case when newcoursename in ('CloudForms Customization and Automation') then enddate end c8,
		case when newcoursename in ('Enterprise Development with Red Hat OpenShift') then enddate end c9,
		case when newcoursename in ('Services Development with Fuse Service Works') then enddate end c10,
		case when newcoursename in ('OpenShift Administration') then enddate end c11,
		case when newcoursename in ('OpenStack Implementation','CLI-S-IAS-ASM-OSE') then enddate end c12,
		case when newcoursename in ('RHEL for SAP Hana') then enddate end c13,
		case when newcoursename in ('RHEL (6) Implementation','RHEL (7) Implementation','RHEL (7) Implementation Part 1','RHEL (7) Implementation Part 2','RHEL (7) Implementation parts 1 and 2','RHEL Implementation') then enddate end c14,
		case when newcoursename in ('RHEL (6) Troubleshooting', 'RHEL (7) Troubleshooting') then enddate end c15,
		case when newcoursename in ('RHEV FASTRAX','RHEV 3.4 FASTRAX') then enddate end c16,
		case when newcoursename in ('RHEV 3.4 Implementation') then enddate end c17,
		case when newcoursename in ('Satellite (5) FASTRAX') then enddate end c18,
		case when newcoursename in ('Satellite (6) Implementation') then enddate end c19,
		case when newcoursename in ('Storage Implementation') then enddate end c20,
		case when newcoursename in ('Application Migration Using Windup') then enddate end c21,
		case when s.region = 'Fed/Sled' then 'NA PUBLIC SECTOR' when s.region = 'NA' then 'NA COMM' else s.region end region2,
		'Direct' channelmix,
		'None' businessunit,
		ifnull(Tier,'') Tier,
        ifnull(companycode,'') companycode
	from
		Students s
		join eLabsImport e
			on s.email = e.email
		left join 
			(select distinct sa.email, sa.accreditation, a.CodeNoLanguage from StudentAccreditations sa join AccreditationCourses a on sa.accreditation = a.Accreditation) t
			on e.newcoursename = t.CodeNoLanguage
			and s.email = t.email
	where
		deleteflag = 'N'
		and t.email is null
		and pfw in ('Pass','Waive')
		and enddate >= concat(year(curdate())-2, '-', right(concat('0',month(curdate())),2),'-01')
	) t
group by
	t.email,
	t.fullname,
	t.company,
	t.region,
	redhat,
	region2,
	channelmix,
	businessunit,
	tier,
    companycode;

drop table if exists QVeLabs;

create table QVeLabs as
select
	t.email,
	t.fullname,
	t.company,
	t.region,
	redhat,
	max(c1) c1,
	max(c2) c2,
	max(c3) c3,
	max(c4) c4,
	max(c5) c5,
	max(c6) c6,
	max(c7) c7,
	max(c8) c8,
	max(c9) c9,
	max(c10) c10,
	max(c11) c11,
	max(c12) c12,
	max(c13) c13,
	max(c14) c14,
	max(c15) c15,
	max(c16) c16,
	max(c17) c17,
	max(c18) c18,
	max(c19) c19,
	max(c20) c20,
	max(c21) c21,
	region2,
	channelmix,
	businessunit,
	tier,
    companycode
from
	(
	select 
		s.email,
		s.fullname,
		s.company,
		s.region,
		case when s.email like '%@redhat.com' or s.company like '%red hat%' then 'Yes' else 'No' end RedHat,
		case when newcoursename in ('A-MQ [VT]') then enddate end c1,
		case when newcoursename in ('Application Development with EAP 6') then enddate end c2,
		case when newcoursename in ('Application Migration to EAP 6') then enddate end c3,
		case when newcoursename in ('Business Logic Development with BPMS 6') then enddate end c4,
		case when newcoursename in ('Camel Development with JBoss Fuse','Camel Development with JBoss FUSE [VT]') then enddate end c5,
		case when newcoursename in ('CloudForms FASTRAX') then enddate end c6,
		case when newcoursename in ('CloudForms Implementation') then enddate end c7,
		case when newcoursename in ('CloudForms Customization and Automation') then enddate end c8,
		case when newcoursename in ('Enterprise Development with Red Hat OpenShift') then enddate end c9,
		case when newcoursename in ('Services Development with Fuse Service Works') then enddate end c10,
		case when newcoursename in ('OpenShift Administration') then enddate end c11,
		case when newcoursename in ('OpenStack Implementation','CLI-S-IAS-ASM-OSE') then enddate end c12,
		case when newcoursename in ('RHEL for SAP Hana') then enddate end c13,
		case when newcoursename in ('RHEL (6) Implementation','RHEL (7) Implementation','RHEL (7) Implementation Part 1','RHEL (7) Implementation Part 2','RHEL (7) Implementation parts 1 and 2','RHEL Implementation') then enddate end c14,
		case when newcoursename in ('RHEL (6) Troubleshooting') then enddate end c15,
		case when newcoursename in ('RHEV FASTRAX','RHEV 3.4 FASTRAX') then enddate end c16,
		case when newcoursename in ('RHEV 3.4 Implementation') then enddate end c17,
		case when newcoursename in ('Satellite (5) FASTRAX') then enddate end c18,
		case when newcoursename in ('Satellite (6) Implementation') then enddate end c19,
		case when newcoursename in ('Storage Implementation') then enddate end c20,
		case when newcoursename in ('Application Migration Using Windup') then enddate end c21,
		case when s.region = 'Fed/Sled' then 'NA PUBLIC SECTOR' when s.region = 'NA' then 'NA COMM' else s.region end region2,
		'Direct' channelmix,
		'None' businessunit,
		ifnull(Tier,'') Tier,
        ifnull(companycode,'') companycode
	from
		Students s
		join eLabsImport e
			on s.email = e.email
	where
		deleteflag = 'N'
		and pfw in ('Pass','Waive')
  		and enddate >= concat(year(curdate())-2, '-', right(concat('0',month(curdate())),2),'-01')
	) t
group by
	t.email,
	t.fullname,
	t.company,
	t.region,
	redhat,
	region2,
	channelmix,
	businessunit,
	tier,
    companycode;

alter view Summary as
select 
	region, 
	case when role = 'Sales' then 1 when role = 'Sales Engineer' then 2 when role = 'Delivery' then 3 when role = 'Advance Training' then 4 end as sort1,
	case when specialization = 'Data Center Infrastructure' then 1 when specialization = 'Middleware Solutions' then 2 when specialization = 'Cloud Infrastructure' then 3 when specialization = 'Advance Training' then 4 end as sort2,
	role, specialization, track, a.accreditation, specialrule,
	count(distinct case when enddate >= '2013-03-01' and enddate < '2014-03-01' then sa.email else null end) FY14,
	count(distinct case when enddate >= '2014-03-01' and enddate < '2015-03-01' then sa.email else null end) FY15,
	count(distinct case when enddate >= '2015-03-01' and enddate < '2016-03-01' then sa.email else null end) FY16,
	count(distinct sa.email) total
from 
	StudentAccreditations sa 
	join Students s 
		on sa.email = s.email 
	join Accreditations a
		on a.accreditation = sa.accreditation 
-- 	join (select distinct specialization, role, track, accreditation, specialrule from AccreditationCourses) a 
-- 		on a.accreditation = sa.accreditation 
where	
	region is not null and role in ('Sales','Sales Engineer','Delivery','Advance Training')
	and s.email not like '%@redhat.com'
	and ifnull(s.company,'') not like '%red hat%'
--    and SumTotalActive = 'Yes'
group by 
	region, 
	role, specialization, track, a.accreditation, specialrule;
-- order by 
-- 	case when role = 'Sales' then 1 when role = 'Sales Engineer' then 2 when role = 'Delivery' then 3 end,
-- 	case when specialization = 'Data Center Infrastructure' then 1 when specialization = 'Middleware Solutions' then 2 when specialization = 'Cloud Infrastructure' then 3 end, 
-- 	4, 5

alter view SummaryInactive as
select 
	region, 
	case when role = 'Sales' then 1 when role = 'Sales Engineer' then 2 when role = 'Delivery' then 3 when role = 'Advance Training' then 4 end as sort1,
	case when specialization = 'Data Center Infrastructure' then 1 when specialization = 'Middleware Solutions' then 2 when specialization = 'Cloud Infrastructure' then 3 when specialization = 'Advance Training' then 4 end as sort2,
	role, specialization, track, a.accreditation, specialrule,
	count(distinct case when enddate >= '2013-03-01' and enddate < '2014-03-01' then sa.email else null end) FY14,
	count(distinct case when enddate >= '2014-03-01' and enddate < '2015-03-01' then sa.email else null end) FY15,
	count(distinct case when enddate >= '2015-03-01' and enddate < '2016-03-01' then sa.email else null end) FY16,
	count(distinct sa.email) total
from 
	StudentAccreditations sa 
	join Students s 
		on sa.email = s.email 
	join Accreditations a
		on a.accreditation = sa.accreditation 
-- 	join (select distinct specialization, role, track, accreditation, specialrule from AccreditationCourses) a 
-- 		on a.accreditation = sa.accreditation 
where	
	region is not null and role in ('Sales','Sales Engineer','Delivery','Advance Training')
	and s.email not like '%@redhat.com'
	and ifnull(s.company,'') not like '%red hat%'
group by 
	region, 
	role, specialization, track, a.accreditation, specialrule;
-- order by 
-- 	case when role = 'Sales' then 1 when role = 'Sales Engineer' then 2 when role = 'Delivery' then 3 end,
-- 	case when specialization = 'Data Center Infrastructure' then 1 when specialization = 'Middleware Solutions' then 2 when specialization = 'Cloud Infrastructure' then 3 end, 
-- 	4, 5

/*

drop table if exists QvExport;

create table QvExport as
select distinct
 	concat(cast(left(DATE_FORMAT(factAttempt_attemptEndDtDetail, '%b'),3) as char),'-',cast(year(factAttempt_attemptEndDtDetail) as char)) as Month
	,year(factAttempt_attemptEndDtDetail) as Year
	,concat('FY ',right(cast(year(date_add(str_to_date(concat(month(factAttempt_attemptEndDtDetail),'/1/',year(factAttempt_attemptEndDtDetail)), '%m/%d/%Y'), interval 10 month)) as char),2)) as FY
 	,cast(fullname as char) as 'Full Name'
	,company
	,dimUser_PrimaryJobNameDetail 'User Primary Job'
	,ifnull(newpartnertype, dimUser_PrimaryOrgNameDetail) 'User Primary Organization'
	,region 'User Primary Domain'
	,country 'Country Code'
	,Students.Email Email
	,c.coursecode Code
-- 	,c.CourseCodeNoLanguage Code2
	,factAttempt_attemptStartDtDetail 'Start Date'
	,factAttempt_attemptEndDtDetail 'End Date'
	,case when Students.email like '%@redhat.com' or company like '%red hat%' then 'Yes' else 'No' end RedHat
	,case when sa.keepid is null then 'No' else 'Yes' end 'Is Certification'
	,case when sa.keepid is not null then 'No' when t.email is null then 'Yes' else 'No' end 'In Progress'
	,ifnull(a.role, c.rolename) Role
	,upper(ifnull(a.track, c.trackname)) 'Skills Track'
	,ifnull(a.specialization, c.specializationname) Specialization
	,a.Accreditation
	,case when region = 'Fed/Sled' then 'NA PUBLIC SECTOR' when region = 'NA' then 'NA COMM' else region end Geo
	,'Direct' as 'Channel Mix'
	,'None' as 'Split Business Unit'
    ,tier
    ,companycode
from
	Students join
	SumTotal s
		on Students.Email = s.dimUser_EmpEmailDetail
	left join Courses c
		on s.newcoursecode = c.coursecode
	left join 
		(select * from StudentAccreditations where keeptype = 'eLearning') sa
		on s.id = sa.keepid
	left join (select distinct role, specialization, upper(track) track, accreditation from AccreditationCourses) a
		on sa.accreditation = a.accreditation
	left join 
		(select distinct sa.email, sa.accreditation, a.CodeNoLanguage from StudentAccreditations sa join AccreditationCourses a on sa.accreditation = a.Accreditation) t
		on c.CourseCodeNoLanguage = t.CodeNoLanguage
		and Students.email = t.email
-- 	left join StudentAccreditations sa2
-- 		on t.accreditation = sa2.accreditation
-- 		and t.email = sa2.email		
where
	deleteflag = 'N'
    and factAttempt_attemptEndDtDetail < str_to_date(concat(month(now()),'/1/',year(now())), '%m/%d/%Y')
    and factAttempt_attemptEndDtDetail >= date_sub(str_to_date(concat(month(now()),'/1/',year(now())), '%m/%d/%Y'), interval 2 year)
-- 	and (factAttempt_attemptExprDtDetail >= cast(cast(month(getdate()) as varchar)+'/1/'+cast(year(getdate()) as varchar) as datetime) or factAttempt_attemptExprDtDetail is null)
    and left(ifnull(dimActivity_RootActivityNameDetail,''),1) not in ('1','2','3','4','5','6','7','8','9') 
	and dimactivity_codedetail not like '%-cyk%' and dimactivity_codedetail not like '%-chp%' and dimactivity_codedetail not like '%-doc%'
-- select * from SumTotal
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
    , companycode
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
;
*/

/* 

-- Export Files
use lms;
select * from QvExport where email <= 'h%' order by email; -- 27586
select * from QvExport where email >= 'h%' and email <= 'o%' order by email; -- 30287
select * from QvExport where email >= 'o%' order by email; -- 27417

select 27730+30661+27939
86330

select * from qvexport order by email;
select * from QVeLabsInProgress order by email;
select * from QVeLabs order by email;
select region, role, specialization, upper(track), fy14, fy15, fy16, total from Summary order by region, sort1, sort2, track, accreditation
select region, role, specialization, upper(track), fy14, fy15, fy16, total from SummaryInactive order by region, sort1, sort2, track, accreditation

select sum(total) from Summary
*/ 

/* 

select * from Students
select * from StudentAccreditations where email like '%meekings%'
select * from AccreditationCourses
select * from Courses
select * from CompletedCourses
select * from Regions
select * from Countries
select * from SubRegion

*/ 

/*
drop table t;

select * from QvExport;

-- tab 1
select fy, month, `end date`, `full name`, company, email, `user primary organization`, code, geo, `Country Code`, role, `Skills Track`, specialization, 1 from QvExport where `Is Certification` = 'Yes' order by geo;

-- tab 2
select * from QVeLabs

*/

-- 19921

