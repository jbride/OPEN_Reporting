
-- --------------------------------------------------
-- SalesForceUsersImport
-- --------------------------------------------------
drop table if exists SalesForceUsersImport;

CREATE TABLE `SalesForceUsersImport` (
  `Email` varchar(100) DEFAULT NULL,
  `User ID` varchar(50) DEFAULT NULL,
  `First Name` varchar(50) DEFAULT NULL,
  `Last Name` varchar(50) DEFAULT NULL,
  `Company Name` varchar(100) DEFAULT NULL,
  `Global Region` varchar(10) DEFAULT NULL,
  `Subregion` varchar(20) DEFAULT NULL,
  `Billing Country` varchar(2) DEFAULT NULL,
  `Partner Type` varchar(30) DEFAULT NULL,
  `Role` varchar(200) DEFAULT NULL,
  `Select Specialization(s)` varchar(100) DEFAULT NULL,
  `Finder Partner Tier` varchar(30) DEFAULT NULL,
  `Profile` varchar(100) DEFAULT NULL,
  `Username` varchar(100) DEFAULT NULL,
  `Alias` varchar(20) DEFAULT NULL,
  `Active` bigint(20) DEFAULT NULL,
  `Last Login` datetime DEFAULT NULL,
  `Language` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

load data local infile 'c:/import/source/SalesForceUsers.txt' into table SalesForceUsersImport;--  fields terminated by ',';

delete from SalesForceUsersImport where `user id` not like '005%';

update SalesForceUsersImport set `company name` = ltrim(rtrim(substring(`company name`,2,length(ltrim(rtrim(`company name`)))-2))) where left(`company name`,1) = '"' and right(`company name`,1) = '"';
update SalesForceUsersImport set `profile` = ltrim(rtrim(substring(`profile`,2,length(ltrim(rtrim(`profile`)))-2))) where left(`profile`,1) = '"' and right(`profile`,1) = '"';
update SalesForceUsersImport set `first name` = ltrim(rtrim(substring(`first name`,2,length(ltrim(rtrim(`first name`)))-2))) where left(`first name`,1) = '"' and right(`first name`,1) = '"';
update SalesForceUsersImport set `last name` = ltrim(rtrim(substring(`last name`,2,length(ltrim(rtrim(`last name`)))-2))) where left(`last name`,1) = '"' and right(`last name`,1) = '"';
update SalesForceUsersImport set `global region` = ltrim(rtrim(substring(`global region`,2,length(ltrim(rtrim(`global region`)))-2))) where left(`global region`,1) = '"' and right(`global region`,1) = '"';
update SalesForceUsersImport set `subregion` = ltrim(rtrim(substring(`subregion`,2,length(ltrim(rtrim(`subregion`)))-2))) where left(`subregion`,1) = '"' and right(`subregion`,1) = '"';
update SalesForceUsersImport set `partner type` = ltrim(rtrim(substring(`partner type`,2,length(ltrim(rtrim(`partner type`)))-2))) where left(`partner type`,1) = '"' and right(`partner type`,1) = '"';
update SalesForceUsersImport set `role` = ltrim(rtrim(substring(`role`,2,length(ltrim(rtrim(`role`)))-2))) where left(`role`,1) = '"' and right(`role`,1) = '"';
update SalesForceUsersImport set `select specialization(s)` = ltrim(rtrim(substring(`select specialization(s)`,2,length(ltrim(rtrim(`select specialization(s)`)))-2))) where left(`select specialization(s)`,1) = '"' and right(`select specialization(s)`,1) = '"';
update SalesForceUsersImport set `username` = ltrim(rtrim(substring(`username`,2,length(ltrim(rtrim(`username`)))-2))) where left(`username`,1) = '"' and right(`username`,1) = '"';
update SalesForceUsersImport set `alias` = ltrim(rtrim(substring(`alias`,2,length(ltrim(rtrim(`alias`)))-2))) where left(`alias`,1) = '"' and right(`alias`,1) = '"';
update SalesForceUsersImport set `language` = ltrim(rtrim(substring(`language`,2,length(ltrim(rtrim(`language`)))-2))) where left(`language`,1) = '"' and right(`language`,1) = '"';

-- create table SfdcUsers_201511 as select * from SfdcUsers;
truncate table SfdcUsers;

insert into SfdcUsers
select email, concat(`last name`,', ',`first name`), `company name`, `partner type`, `global region`, subregion, alias, `user id`, null from SalesForceUsersImport;

create index idx_ContactID on SfdcUsers (`contact id`);

-- select * from SfdcUsers;
-- select * from SalesForceUsersImport;

-- --------------------------------------------------
-- SumTotalUsersImport
-- --------------------------------------------------
-- create table SumTotalUsers_201511 as select * from SumTotalUsers;

drop table if exists SumTotalUsersImport;

CREATE TABLE `SumTotalUsersImport` (
  `email` varchar(48) DEFAULT NULL,
  `fullname` varchar(45) DEFAULT NULL,
  `sumtotalid` varchar(19) DEFAULT NULL,
  `job` varchar(47) DEFAULT NULL,
  `company` varchar(96) DEFAULT NULL,
  `org` varchar(33) DEFAULT NULL,
  `domain` varchar(33) DEFAULT NULL,
  `country` varchar(29) DEFAULT NULL,
  `active` varchar(23) DEFAULT NULL,
  `text4` varchar(50) DEFAULT NULL  
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

load data local infile 'c:/import/source/SumTotalUsers.txt' into table SumTotalUsersImport;

delete from SumTotalUsersImport where email = 'email';
delete from SumTotalUsersImport where fullname = 'Full Name';
delete from SumTotalUsersImport where length(ltrim(rtrim(email))) = 0;
update SumTotalUsersImport set job = null where job = '(no data)';

update SumTotalUsersImport set fullname = ltrim(rtrim(substring(fullname,2,length(ltrim(rtrim(fullname)))-2))) where left(fullname,1) = '"' and right(fullname,1) = '"';
update SumTotalUsersImport set job = ltrim(rtrim(substring(job,2,length(ltrim(rtrim(job)))-2))) where left(job,1) = '"' and right(job,1) = '"';
update SumTotalUsersImport set company = ltrim(rtrim(substring(company,2,length(ltrim(rtrim(company)))-2))) where left(company,1) = '"' and right(company,1) = '"';
update SumTotalUsersImport set org = ltrim(rtrim(substring(org,2,length(ltrim(rtrim(org)))-2))) where left(org,1) = '"' and right(org,1) = '"';
update SumTotalUsersImport set domain = ltrim(rtrim(substring(domain,2,length(ltrim(rtrim(domain)))-2))) where left(domain,1) = '"' and right(domain,1) = '"';
update SumTotalUsersImport set country = ltrim(rtrim(substring(country,2,length(ltrim(rtrim(country)))-2))) where left(country,1) = '"' and right(country,1) = '"';
update SumTotalUsersImport set text4 = ltrim(rtrim(substring(text4,2,length(ltrim(rtrim(text4)))-2))) where left(text4,1) = '"' and right(text4,1) = '"';

truncate table SumTotalUsers;

insert into SumTotalUsers
select fullname, sumtotalid, email, job, company, org, domain, country, active, text4,
	case 
		when text4 like '%Premier%' then 'Premier'
		when text4 like '%Ready%' then 'Ready'
		when text4 like '%Advanced%' then 'Advanced'
		when text4 like '%Affiliated%' then 'Affiliated'
	end
from SumTotalUsersImport;

-- select * from SumTotalUsers;
-- select * from SumTotalUsersImport;

-- --------------------------------------------------
-- SumTotalCompletionsImport
-- --------------------------------------------------
drop table if exists SumTotalCompletionsImport;

CREATE TABLE `SumTotalCompletionsImport` (
  `Full Name` varchar(50) DEFAULT NULL,
  `User Number` varchar(50) DEFAULT NULL,
  `Email` varchar(100) DEFAULT NULL,
  `Text 3` varchar(100) DEFAULT NULL,
  `User Primary Job` varchar(100) DEFAULT NULL,
  `User Primary Organization` varchar(100) DEFAULT NULL,
  `User Primary Domain` varchar(100) DEFAULT NULL,
  `Country` varchar(50) DEFAULT NULL,
  `Active(User) (Yes No)` varchar(3) DEFAULT NULL,
  `Activity Label` varchar(50) DEFAULT NULL,
  `Activity Name` varchar(200) DEFAULT NULL,
  `Activity Code` varchar(100) DEFAULT NULL,
  `Is Certification (Yes No)` varchar(3) DEFAULT NULL,
  `Attendance Status` varchar(20) DEFAULT NULL,
  `Completion Status` varchar(20) DEFAULT NULL,
  `Attempt Start Date` datetime DEFAULT NULL,
  `Attempt End Date` datetime DEFAULT NULL,
  `Text 4` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

load data local infile 'c:/import/source/SumTotalCompletions.txt' into table SumTotalCompletionsImport;

delete from SumTotalCompletionsImport where `full name` in ('fullname','full name');

update SumTotalCompletionsImport set `user primary job` = null where `user primary job` = '(no data)';
update SumTotalCompletionsImport set `full name` = ltrim(rtrim(substring(`full name`,2,length(ltrim(rtrim(`full name`)))-2))) where left(`full name`,1) = '"' and right(`full name`,1) = '"';
update SumTotalCompletionsImport set `text 3` = ltrim(rtrim(substring(`text 3`,2,length(ltrim(rtrim(`text 3`)))-2))) where left(`text 3`,1) = '"' and right(`text 3`,1) = '"';
update SumTotalCompletionsImport set `text 4` = ltrim(rtrim(substring(`text 4`,2,length(ltrim(rtrim(`text 4`)))-2))) where left(`text 4`,1) = '"' and right(`text 4`,1) = '"';
update SumTotalCompletionsImport set `user primary job` = ltrim(rtrim(substring(`user primary job`,2,length(ltrim(rtrim(`user primary job`)))-2))) where left(`user primary job`,1) = '"' and right(`user primary job`,1) = '"';
update SumTotalCompletionsImport set `user primary organization` = ltrim(rtrim(substring(`user primary organization`,2,length(ltrim(rtrim(`user primary organization`)))-2))) where left(`user primary organization`,1) = '"' and right(`user primary organization`,1) = '"';
update SumTotalCompletionsImport set `user primary domain` = ltrim(rtrim(substring(`user primary domain`,2,length(ltrim(rtrim(`user primary domain`)))-2))) where left(`user primary domain`,1) = '"' and right(`user primary domain`,1) = '"';
update SumTotalCompletionsImport set `activity name` = ltrim(rtrim(substring(`activity name`,2,length(ltrim(rtrim(`activity name`)))-2))) where left(`activity name`,1) = '"' and right(`activity name`,1) = '"';
update SumTotalCompletionsImport set `activity code` = ltrim(rtrim(substring(`activity code`,2,length(ltrim(rtrim(`activity code`)))-2))) where left(`activity code`,1) = '"' and right(`activity code`,1) = '"';

insert into SumTotal (`dimUser_EmpLNameDetail`,`dimUser_EmpFNameDetail`,`dimUser_EmpFullDetail`,`dimUser_OptEmp_Txt3Detail`,`dimUser_EmpNoDetail`,
	`dimUser_OptEmp_Txt2Detail`,`dimUser_PrimaryJobNameDetail`,`dimUser_PrimaryOrgNameDetail`,`dimUser_PrimaryDomNameDetail`,`dimUser_EmpCntryDetail`,
	`dimUser_EmpEmailDetail`,`dimActivity_RootActivityNameDetail`,`factAttempt_attemptStartDtDetail`,`factAttempt_attemptEndDtDetail`,`factAttempt_attemptExprDtDetail`,
	`L10NAct_NameDetail`,`dimActivity_IsCertificationDetail`,`dimActivity_CodeDetail`,`dimCompletionStatus_nameDetail`,`dimAttendanceStatus_nameDetail`,
	`SFDC or SumTotals`,`Column 21`, CreateDate, text4)
	-- ,`id`,`newfullname`,`newfirstname`,`newlastname`,`newcompany`,`newcountry`,`newpartnertype`,`newgeo`,`newcoursecode`,
	-- `accreditation`,`tempflag`,`deleteflag`,`modifynote`)
select distinct	
	null,
	null,
	c.`full name`,
	`text 3`,
	c.`user number`,
	null,
	c.`user primary job`,
	c.`user primary organization`,
	c.`user primary domain`,
	c.country,
	c.email,
	`activity name`,
	`attempt start date`,
	`attempt end date`,
	null,
	`activity name`,
	`is certification (Yes No)`,
	`activity code`,
	`completion status`,
	`attendance status`,
	'SumTotal',
	null,
	now(),
	`text 4`
	from
		SumTotalCompletionsImport c;

-- select * from SumTotalCompletionsImport;
-- select * from SumTotal order by factattempt_attemptenddtdetail desc;

drop table if exists eLabsSourceImport;

CREATE TABLE `eLabsSourceImport` (
  `FullName` varchar(200) DEFAULT NULL,
  `Email` varchar(55) DEFAULT NULL,
  `CourseName` varchar(60) DEFAULT NULL,
  `Score` varchar(10) DEFAULT NULL,
   PFW varchar(6) DEFAULT NULL,
  `EndDate` datetime DEFAULT NULL,
  `Company` varchar(88) DEFAULT NULL,
  `Region` varchar(37) DEFAULT NULL,
  `Country` varchar(35) DEFAULT NULL,
  `PartnerType` varchar(27) DEFAULT NULL,  
   SumTotalID varchar(20) DEFAULT NULL,
   Active varchar(3) DEFAULT NULL,
   Tier varchar(100) DEFAULT NULL
/*
   `RhnID` varchar(55) DEFAULT NULL,
   `Role` varchar(17) DEFAULT NULL,
   `EndDateOld` varchar(12) DEFAULT NULL,
  `WaiverNote` varchar(87) DEFAULT NULL,
  `Note` varchar(137) DEFAULT NULL,
  `SubRegion` varchar(16) DEFAULT NULL,
  `SfdcID` varchar(27) DEFAULT NULL,
*/
) ENGINE=InnoDB AUTO_INCREMENT=11304 DEFAULT CHARSET=latin1;

load data local infile 'c:/import/source/eLabCompletions.txt' into table eLabsSourceImport;
delete from eLabsSourceImport where fullname = 'fullname' and email = 'email';
delete from eLabsSourceImport where fullname = 'Name' and email = 'email';


insert into eLabsImport (fullname, email, coursename, pfwold, pfw, enddate, company, region, country, partnertype, sumtotalid)
select fullname, email, coursename, score, pfw, enddate, company, region, country, partnertype, sumtotalid from eLabsSourceImport;

update eLabsImport set auto = autoid where auto is null;

select * from eLabsImport where auto is null

-- select max(auto) from eLabsImport -- 11303
