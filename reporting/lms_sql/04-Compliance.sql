-- --------------------------------------------------
-- Compliance
-- --------------------------------------------------
use lms;

drop table if exists temp;
create table temp as
select
	company, region, s.email, a.*
from
	Students s 
    join StudentAccreditations sa
		on s.email = sa.email
	join Accreditations a
		on sa.accreditation = a.accreditation;

drop table if exists temp2;
create table temp2 as 
select 
	region,
	company, 
	sum(case when specialization = 'Data Center Infrastructure' and track = 'PLATFORM' and role = 'Sales' then 1 else 0 end) dcisales,
	sum(case when specialization = 'Data Center Infrastructure' and track = 'PLATFORM' and role = 'Sales Engineer' then 1 else 0 end) dcise,
	sum(case when specialization = 'Data Center Infrastructure' and track = 'PLATFORM' and role = 'Delivery' then 1 else 0 end) dcidelivery,
	sum(case when specialization = 'Data Center Infrastructure' and track <> 'PLATFORM' and role = 'Sales' then 1 else 0 end) dciothersales,
	sum(case when specialization = 'Data Center Infrastructure' and track <> 'PLATFORM' and role = 'Sales Engineer' then 1 else 0 end) dciotherse,
	sum(case when specialization = 'Data Center Infrastructure' and track <> 'PLATFORM' and role = 'Delivery' then 1 else 0 end) dciotherdelivery,

	sum(case when specialization = 'Middleware Solutions' and track = 'MIDDLEWARE APPLICATION DEVELOPMENT' and role = 'Sales' then 1 else 0 end) mwsales,
	sum(case when specialization = 'Middleware Solutions' and track = 'MIDDLEWARE APPLICATION DEVELOPMENT' and role = 'Sales Engineer' then 1 else 0 end) mwse,
	sum(case when specialization = 'Middleware Solutions' and track = 'MIDDLEWARE APPLICATION DEVELOPMENT' and role = 'Delivery' then 1 else 0 end) mwdelivery,
	sum(case when specialization = 'Middleware Solutions' and track <> 'MIDDLEWARE APPLICATION DEVELOPMENT' and role = 'Sales' then 1 else 0 end) mwothersales,
	sum(case when specialization = 'Middleware Solutions' and track <> 'MIDDLEWARE APPLICATION DEVELOPMENT' and role = 'Sales Engineer' then 1 else 0 end) mwotherse,
	sum(case when specialization = 'Middleware Solutions' and track <> 'MIDDLEWARE APPLICATION DEVELOPMENT' and role = 'Delivery' then 1 else 0 end) mwotherdelivery,
    
	sum(case when specialization = 'Cloud Infrastructure' and track = 'CLOUD MANAGEMENT' and role = 'Sales' then 1 else 0 end) cloudcloudsales,
	sum(case when specialization = 'Cloud Infrastructure' and track = 'CLOUD MANAGEMENT' and role = 'Sales Engineer' then 1 else 0 end) cloudcloudse,
	sum(case when specialization = 'Cloud Infrastructure' and track = 'CLOUD MANAGEMENT' and role = 'Delivery' then 1 else 0 end) cloudclouddelivery,
	sum(case when specialization = 'Cloud Infrastructure' and track = 'IAAS' and role = 'Sales' then 1 else 0 end) cloudiaassales,
	sum(case when specialization = 'Cloud Infrastructure' and track = 'IAAS' and role = 'Sales Engineer' then 1 else 0 end) cloudiaasse,
	sum(case when specialization = 'Cloud Infrastructure' and track = 'IAAS' and role = 'Delivery' then 1 else 0 end) cloudiaasdelivery,
	sum(case when specialization = 'Cloud Infrastructure' and track = 'PAAS DEVELOPMENT' and role = 'Sales' then 1 else 0 end) cloudpaassales,
	sum(case when specialization = 'Cloud Infrastructure' and track = 'PAAS DEVELOPMENT' and role = 'Sales Engineer' then 1 else 0 end) cloudpaasse,
	sum(case when specialization = 'Cloud Infrastructure' and track = 'PAAS DEVELOPMENT' and role = 'Delivery' then 1 else 0 end) cloudpaasdelivery
from 
	temp 
group by 
	region, 
	company;
    
drop table if exists temp3;
create table temp3 (
	region varchar(100),
    company varchar(100),
	email varchar(200),
	dcisales int (0),
	dcise int (0),
    dcidelivery int (0),
    dcitotal int (0),
	dciothersales int (0),
	dciotherse int (0),
    dciotherdelivery int (0),
    dciothertotal int (0),
	mwsales int (0),
	mwse int (0),
    mwdelivery int (0),
    mwtotal int (0),
	mwothersales int (0),
	mwotherse int (0),
    mwotherdelivery int (0),
    mwothertotal int (0),
    id int not null auto_increment primary key
    );
    
insert into temp3 (region, company, email, dcisales, dcise, dcidelivery, dcitotal, dciothersales, dciotherse, dciotherdelivery, dciothertotal, mwsales, mwse, mwdelivery, mwtotal, mwothersales, mwotherse, mwotherdelivery, mwothertotal)
select 
	region,
	company, 
    email,
	sum(case when specialization = 'Data Center Infrastructure' and track = 'PLATFORM' and role = 'Sales' then 1 else 0 end) dcisales,
	sum(case when specialization = 'Data Center Infrastructure' and track = 'PLATFORM' and role = 'Sales Engineer' then 1 else 0 end) dcise,
	sum(case when specialization = 'Data Center Infrastructure' and track = 'PLATFORM' and role = 'Delivery' then 1 else 0 end) dcidelivery,
    sum(case when specialization = 'Data Center Infrastructure' and track = 'PLATFORM' and role = 'Sales' then 1 else 0 end)+sum(case when specialization = 'Data Center Infrastructure' and track = 'PLATFORM' and role = 'Sales Engineer' then 1 else 0 end)+sum(case when specialization = 'Data Center Infrastructure' and track = 'PLATFORM' and role = 'Delivery' then 1 else 0 end),

	sum(case when specialization = 'Data Center Infrastructure' and track <> 'PLATFORM' and role = 'Sales' then 1 else 0 end) dciothersales,
	sum(case when specialization = 'Data Center Infrastructure' and track <> 'PLATFORM' and role = 'Sales Engineer' then 1 else 0 end) dciotherse,
	sum(case when specialization = 'Data Center Infrastructure' and track <> 'PLATFORM' and role = 'Delivery' then 1 else 0 end) dciotherdelivery,
	sum(case when specialization = 'Data Center Infrastructure' and track <> 'PLATFORM' and role = 'Sales' then 1 else 0 end)+sum(case when specialization = 'Data Center Infrastructure' and track <> 'PLATFORM' and role = 'Sales Engineer' then 1 else 0 end)+sum(case when specialization = 'Data Center Infrastructure' and track <> 'PLATFORM' and role = 'Delivery' then 1 else 0 end),

	sum(case when specialization = 'Middleware Solutions' and track = 'MIDDLEWARE APPLICATION DEVELOPMENT' and role = 'Sales' then 1 else 0 end) mwsales,
	sum(case when specialization = 'Middleware Solutions' and track = 'MIDDLEWARE APPLICATION DEVELOPMENT' and role = 'Sales Engineer' then 1 else 0 end) mwse,
	sum(case when specialization = 'Middleware Solutions' and track = 'MIDDLEWARE APPLICATION DEVELOPMENT' and role = 'Delivery' then 1 else 0 end) mwdelivery,
	0 as mwtotal,
    sum(case when specialization = 'Middleware Solutions' and track <> 'MIDDLEWARE APPLICATION DEVELOPMENT' and role = 'Sales' then 1 else 0 end) mwothersales,
	sum(case when specialization = 'Middleware Solutions' and track <> 'MIDDLEWARE APPLICATION DEVELOPMENT' and role = 'Sales Engineer' then 1 else 0 end) mwotherse,
	sum(case when specialization = 'Middleware Solutions' and track <> 'MIDDLEWARE APPLICATION DEVELOPMENT' and role = 'Delivery' then 1 else 0 end) mwotherdelivery,
	0 as mwothertotal
/*
	sum(case when specialization = 'Cloud Infrastructure' and track = 'CLOUD MANAGEMENT' and role = 'Sales' then 1 else 0 end) cloudcloudsales,
	sum(case when specialization = 'Cloud Infrastructure' and track = 'CLOUD MANAGEMENT' and role = 'Sales Engineer' then 1 else 0 end) cloudcloudse,
	sum(case when specialization = 'Cloud Infrastructure' and track = 'CLOUD MANAGEMENT' and role = 'Delivery' then 1 else 0 end) cloudclouddelivery,
	sum(case when specialization = 'Cloud Infrastructure' and track = 'IAAS' and role = 'Sales' then 1 else 0 end) cloudiaassales,
	sum(case when specialization = 'Cloud Infrastructure' and track = 'IAAS' and role = 'Sales Engineer' then 1 else 0 end) cloudiaasse,
	sum(case when specialization = 'Cloud Infrastructure' and track = 'IAAS' and role = 'Delivery' then 1 else 0 end) cloudiaasdelivery,
	sum(case when specialization = 'Cloud Infrastructure' and track = 'PAAS DEVELOPMENT' and role = 'Sales' then 1 else 0 end) cloudpaassales,
	sum(case when specialization = 'Cloud Infrastructure' and track = 'PAAS DEVELOPMENT' and role = 'Sales Engineer' then 1 else 0 end) cloudpaasse,
	sum(case when specialization = 'Cloud Infrastructure' and track = 'PAAS DEVELOPMENT' and role = 'Delivery' then 1 else 0 end) cloudpaasdelivery
    */
from 
	temp 
where
	region in ('APAC','EMEA')
    and length(ltrim(rtrim(company))) > 0
group by 
	region, company, email
order by region, company, 11 desc, 7 asc;

update 
	temp3 
set 
	dcitotal = dcisales+dcise+dcidelivery,
    dciothertotal = dciothersales+dciotherse+dciotherdelivery,
	mwtotal = mwsales+mwse+mwdelivery,
 	mwothertotal = mwothersales+mwotherse+mwotherdelivery;
--  cloudcloudtotal = cloudcloudsales+cloudcloudse+cloudclouddelivery,
-- 	cloudiaastotal = cloudiaassales+cloudiaasse+cloudiaasdelivery,
-- 	cloudpaastotal = cloudpaassales+cloudpaasse+cloudiaasdelivery

/*
create index temp3idx key on temp3 (dciothertotal desc, dcitotal asc, email asc);
alter table temp3 add primary key (dciothertotal desc, dcitotal asc, email asc);
alter table temp3 add id int not null auto_increment primary key (dciothertotal desc, dcitotal asc, email asc);
addd primary key (dciothertotal desc, dcitotal asc, email asc);

*/ 
   
-- Fed/Sled
drop table if exists Compliance;
create table Compliance as
select 
	region,
	company,
    case 
		when dciothersales >=4 and dciotherse >= 2 and dciotherdelivery >= 2 and dcisales >= 2 and dcise >= 1 and dcidelivery >= 1 then 'Premier'
		when dcisales >= 2 and dcise >= 1 and dcidelivery >= 1 then 'Advanced' 
        when dcisales >= 1 then 'Ready'
	end as dci,  
    case 
		when mwothersales >=4 and mwotherse >= 2 and mwotherdelivery >= 2 and mwsales >= 2 and mwse >= 1 and mwdelivery >= 1 then 'Premier'
		when mwsales >= 2 and mwse >= 1 and mwdelivery >= 1 then 'Advanced'
        when mwsales >= 1 then 'Ready'
	end as mw,
    case 
		when 
			(
            (cloudcloudsales >= 4 and cloudcloudse >= 2 and cloudclouddelivery >= 2) and ((cloudiaassales >=2 and cloudiaasse >= 1 and cloudiaasdelivery >= 1) or (cloudpaassales >=2 and cloudpaasse >= 1 and cloudpaasdelivery >= 1))
			or
            (cloudiaassales >= 4 and cloudiaasse >= 2 and cloudiaasdelivery >= 2) and ((cloudcloudsales >=2 and cloudcloudse >= 1 and cloudclouddelivery >= 1) or (cloudpaassales >=2 and cloudpaasse >= 1 and cloudpaasdelivery >= 1))
            or 
            (cloudpaassales >= 4 and cloudpaasse >= 2 and cloudpaasdelivery >= 2) and ((cloudcloudsales >=2 and cloudcloudse >= 1 and cloudclouddelivery >= 1) or (cloudiaassales >=2 and cloudiaasse >= 1 and cloudiaasdelivery >= 1))
            )
			then 'Premier'
 		when 
 			(cloudiaassales >=2 and cloudiaasse >= 1 and cloudiaasdelivery >= 1) or (cloudiaassales >=2 and cloudiaasse >= 1 and cloudiaasdelivery >= 1) or (cloudpaassales >=2 and cloudpaasse >= 1 and cloudpaasdelivery >= 1)
 			then 'Advanced'
	end as cloud     
from
	temp2
where
	region = 'Fed/Sled'
group by
	region, company
order by 
	company;

-- NA
insert into Compliance
select 
	region,
	company,
    case 
		when dciothersales >=2 and dciotherse >= 1 and dciotherdelivery >= 1 and dcisales >= 2 and dcise >= 1 then 'Premier'
		when dcisales >= 2 and dcise >= 1 and dcidelivery >= 1 then 'Advanced' 
        when dcisales >= 1 then 'Ready'
	end as dci,  
    case 
		when mwothersales >=2 and mwotherse >= 1 and mwotherdelivery >= 1 and mwsales >= 2 and mwse >= 1 and mwdelivery >= 1 then 'Premier'
		when mwsales >= 2 and mwse >= 1 and mwdelivery >= 1 then 'Advanced'
        when mwsales >= 1 then 'Ready'
	end as mw,
    case 
		when 
			(
            (cloudcloudsales >= 2 and cloudcloudse >= 1 and cloudclouddelivery >= 1) and ((cloudiaassales >=2 and cloudiaasse >= 1 and cloudiaasdelivery >= 1) or (cloudpaassales >=2 and cloudpaasse >= 1 and cloudpaasdelivery >= 1))
			or
            (cloudiaassales >= 2 and cloudiaasse >= 1 and cloudiaasdelivery >= 1) and ((cloudcloudsales >=2 and cloudcloudse >= 1 and cloudclouddelivery >= 1) or (cloudpaassales >=2 and cloudpaasse >= 1 and cloudpaasdelivery >= 1))
            or 
            (cloudpaassales >= 2 and cloudpaasse >= 1 and cloudpaasdelivery >= 1) and ((cloudcloudsales >=2 and cloudcloudse >= 1 and cloudclouddelivery >= 1) or (cloudiaassales >=2 and cloudiaasse >= 1 and cloudiaasdelivery >= 1))
            )
			then 'Premier'
 		when 
 			(cloudiaassales >=2 and cloudiaasse >= 1 and cloudiaasdelivery >= 1) or (cloudiaassales >=2 and cloudiaasse >= 1 and cloudiaasdelivery >= 1) or (cloudpaassales >=2 and cloudpaasse >= 1 and cloudpaasdelivery >= 1)
 			then 'Advanced'
	end as cloud     
from
	temp2
where
	region = 'NA'
group by
	region, company
order by 
	company;

-- LATAM
insert into Compliance
select 
	region,
	company,
    case 
-- 		when dciothersales >=2 and dciotherse >= 1 and dciotherdelivery >= 1 and dcisales >= 2 and dcise >= 1 then 'Premier'
		when dcisales >= 2 and dcise >= 1 and dcidelivery >= 0 then 'Advanced' 
        when dcisales >= 1 and dcidelivery >= 1 then 'Ready'
	end as dci,  
    case 
-- 		when mwothersales >=2 and mwotherse >= 1 and mwotherdelivery >= 1 and mwsales >= 2 and mwse >= 1 and mwdelivery >= 1 then 'Premier'
		when mwsales >= 2 and mwse >= 1 and mwdelivery >= 1 then 'Advanced'
        when mwsales >= 1 and mwdelivery >= 1 then 'Ready'
	end as mw,
    case 
 		when 
 			(cloudiaassales >=2 and cloudiaasse >= 1 and cloudiaasdelivery >= 1) or (cloudiaassales >=2 and cloudiaasse >= 1 and cloudiaasdelivery >= 1) or (cloudpaassales >=2 and cloudpaasse >= 1 and cloudpaasdelivery >= 1)
 			then 'Advanced'
 		when 
 			(cloudiaassales >=1 and cloudiaasse >= 0 and cloudiaasdelivery >= 1) or (cloudiaassales >=1 and cloudiaasse >= 0 and cloudiaasdelivery >= 1) or (cloudpaassales >=1 and cloudpaasse >= 0 and cloudpaasdelivery >= 1)
 			then 'Ready'
	end as cloud     
from
	temp2
where
	region = 'LATAM'
group by
	region, company
order by 
	company;


-- EMEA
insert into Compliance
select 
	region,
	company,
    case 
-- 		when dciothersales >=2 and dciotherse >= 2 and dciotherdelivery >= 2 and dcisales >= 2 and dcise >= 2 and dcidelivery >= 2 then 'Premier'
		when dcisales >= 2 and dcise >= 2 and dcidelivery >= 2 then 'Advanced' 
        when dcisales >= 1 then 'Ready'
	end as dci,  
    case 
-- 		when mwothersales >=2 and mwotherse >= 2 and mwotherdelivery >= 2 and mwsales >= 2 and mwse >= 2 and mwdelivery >= 2 then 'Premier'
		when mwsales >= 2 and mwse >= 2 and mwdelivery >= 2 then 'Advanced'
        when mwsales >= 1 then 'Ready'
	end as mw,
    case 
/*
		when 
			(
            (cloudcloudsales >= 2 and cloudcloudse >= 2 and cloudclouddelivery >= 2) and ((cloudiaassales >=2 and cloudiaasse >= 2 and cloudiaasdelivery >= 2) or (cloudpaassales >=2 and cloudpaasse >= 2 and cloudpaasdelivery >= 2))
			or
            (cloudiaassales >= 2 and cloudiaasse >= 2 and cloudiaasdelivery >= 2) and ((cloudcloudsales >=2 and cloudcloudse >= 2 and cloudclouddelivery >= 2) or (cloudpaassales >=2 and cloudpaasse >= 2 and cloudpaasdelivery >= 2))
            or 
            (cloudpaassales >= 2 and cloudpaasse >= 2 and cloudpaasdelivery >= 2) and ((cloudcloudsales >=2 and cloudcloudse >= 2 and cloudclouddelivery >= 2) or (cloudiaassales >=2 and cloudiaasse >= 2 and cloudiaasdelivery >= 2))
            )
			then 'Premier'
*/
 		when 
 			(cloudiaassales >=2 and cloudiaasse >= 2 and cloudiaasdelivery >= 2) or (cloudiaassales >=2 and cloudiaasse >= 2 and cloudiaasdelivery >= 2) or (cloudpaassales >=2 and cloudpaasse >= 2 and cloudpaasdelivery >= 2)
 			then 'Advanced'
		when
			(cloudiaassales >= 1 or cloudiaassales >= 1 or cloudpaassales >= 1)
            then 'Ready'
	end as cloud     
from
	temp2
where
	region = 'EMEA'
group by
	region, company
order by 
	company;


-- APAC
insert into Compliance
select 
	region,
	company,
    case 
-- 		when dciothersales >= 7 and dciotherse >= 2 and dciotherdelivery >= 2 and dcisales >= 2 and dcise >= 1 then 'Premier'
		when dcisales >= 4 and dcise >= 1 and dcidelivery >= 1 then 'Advanced' 
        when dcisales >= 2 then 'Ready'
	end as dci,  
    case 
-- 		when mwothersales >=2 and mwotherse >= 1 and mwotherdelivery >= 1 and mwsales >= 2 and mwse >= 1 and mwdelivery >= 1 then 'Premier'
		when mwsales >= 2 and mwse >= 1 and mwdelivery >= 1 then 'Advanced'
        when mwsales >= 1 then 'Ready'
	end as mw,
    case 
/*
		when 
			(
            (cloudcloudsales >= 2 and cloudcloudse >= 1 and cloudclouddelivery >= 1) and ((cloudiaassales >=2 and cloudiaasse >= 1 and cloudiaasdelivery >= 1) or (cloudpaassales >=2 and cloudpaasse >= 1 and cloudpaasdelivery >= 1))
			or
            (cloudiaassales >= 2 and cloudiaasse >= 1 and cloudiaasdelivery >= 1) and ((cloudcloudsales >=2 and cloudcloudse >= 1 and cloudclouddelivery >= 1) or (cloudpaassales >=2 and cloudpaasse >= 1 and cloudpaasdelivery >= 1))
            or 
            (cloudpaassales >= 2 and cloudpaasse >= 1 and cloudpaasdelivery >= 1) and ((cloudcloudsales >=2 and cloudcloudse >= 1 and cloudclouddelivery >= 1) or (cloudiaassales >=2 and cloudiaasse >= 1 and cloudiaasdelivery >= 1))
            )
			then 'Premier'
*/
 		when 
 			(cloudiaassales >=2 and cloudiaasse >= 1 and cloudiaasdelivery >= 1) or (cloudiaassales >=2 and cloudiaasse >= 1 and cloudiaasdelivery >= 1) or (cloudpaassales >=2 and cloudpaasse >= 1 and cloudpaasdelivery >= 1)
 			then 'Advanced'
	end as cloud     
from
	temp2
where
	region = 'APAC'
group by
	region, company
order by 
	company;


-- compliance2
-- --------------------------------------------------
-- APAC DCI
-- --------------------------------------------------

delimiter $$

drop procedure if exists apac_dci $$

create procedure apac_dci ()
begin

declare i int default 0;
declare p1_1 int default 0;
declare p1_2 int default 0;
declare p1_3 int default 0;
declare p2_1 int default 0;
declare p3_1 int default 0;
declare a1_1 int default 0;
declare a1_2 int default 0;
declare a1_3 int default 0;
declare a1_4 int default 0;
declare a2_1 int default 0;
declare a3_1 int default 0;

declare c varchar(200) default null;
declare f int default 0;
declare e int default 0;
declare r int default 0;

truncate table temp5;

-- select distinct company from temp3 order by 1
-- select distinct company from temp3 where company not in (select company from temp4)

/*
select * from temp3 where company in (
 'Dell Asia Pacific'
-- 'IT Group, Inc.'
-- 'Jardine OneSolution (HK) Limited',
-- 'Netcom Infotech Pvt Ltd'
)
*/

drop table if exists temp4;

create table temp4 (
	region varchar(100),
    company varchar(200),
    dci varchar(100),
    mw varchar (100),
    cloud varchar (100));

insert into temp4 (region, company, dci)
select region, company, '' as dci from temp3 where region = 'APAC' group by company having sum(case when dciothersales > 0 then 1 else 0 end) < 3 or sum(case when dciotherse > 0 then 1 else 0 end) < 1 or sum(case when dciotherdelivery > 0 then 1 else 0 end) < 1 or sum(case when dcisales > 0 then 1 else 0 end) < 4 or sum(case when dcise >0 then 1 else 0 end) < 1 or sum(case when dcidelivery > 0 then 1 else 0 end) < 1
union
select region, company, '' as dci from temp3 where region = 'APAC' group by company having sum(case when dciotherdelivery > 0 then 1 else 0 end) = 1 and sum(case when dcidelivery > 0 then 1 else 0 end) = 1 ;

set c = (select min(company) from temp3 where region = 'APAC' and company not in (select company from temp4));
set r = (select min(id) id from temp3 where region = 'APAC' and id >= r and company = c and dciothersales >= 1);

while (select count(*) from temp3 where region = 'APAC' and id >= r and company not in (select company from temp4)) > 1 do

set i = i + 1;    

set p1_1 = (select min(id) id from temp3 where region = 'APAC' and id >= r and company = c and dciothersales >= 1);
set p1_2 = (select min(id) id from temp3 where region = 'APAC' and company = c and dciothersales >= 1 and id <> p1_1);
set p1_3 = (select min(id) id from temp3 where region = 'APAC' and company = c and dciothersales >= 1 and id <> p1_1 and id <> p1_2);
set p2_1 = (select min(id) id from temp3 where region = 'APAC' and company = c and dciotherse >= 1);
set p3_1 = (select min(id) id from temp3 where region = 'APAC' and company = c and dciotherdelivery >= 1);
set a1_1 = (select min(id) id from temp3 where region = 'APAC' and company = c and dcisales >= 1 and id <> p1_1 and id <> p1_2 and id <> p1_3 and id <> p2_1 and id <> p3_1);
set a1_2 = (select min(id) id from temp3 where region = 'APAC' and company = c and dcisales >= 1 and id <> p1_1 and id <> p1_2 and id <> p1_3 and id <> p2_1 and id <> p3_1);
set a1_3 = (select min(id) id from temp3 where region = 'APAC' and company = c and dcisales >= 1 and id <> p1_1 and id <> p1_2 and id <> p1_3 and id <> p2_1 and id <> p3_1);
set a1_4 = (select min(id) id from temp3 where region = 'APAC' and company = c and dcisales >= 1 and id <> p1_1 and id <> p1_2 and id <> p1_3 and id <> p2_1 and id <> p3_1);
set a2_1 = (select min(id) id from temp3 where region = 'APAC' and company = c and dciotherse >= 1 and id <> p1_1 and id <> p1_2 and id <> p1_3 and id <> p2_1 and id <> p3_1);
set a3_1 = (select min(id) id from temp3 where region = 'APAC' and company = c and dciotherdelivery >= 1 and id <> p1_1 and id <> p1_2 and id <> p1_3 and id <> p2_1 and id <> p3_1);

-- insert into temp5 select p1_1;

if (p1_1 > 0 and p1_2 > 0 and p1_3 > 0 and p2_1 > 0 and p3_1 > 0 and a1_1 > 0 and a1_2 > 0 and a1_3 > 0 and a1_4 > 0 and a2_1 > 0 and a3_1 > 0) 
	then
		insert into temp4 (region, company, dci) select 'APAC', c, 'Premier';
		set f = 1;
		set c = (select min(company) from temp3 where region = 'APAC' and company not in (select company from temp4) and c <> company);
        -- set r = r + 1; -- shit
	else 
        set f = 0;
end if;

if (f = 0 and (p1_1 is null or p1_2 is null or p1_3 is null)) then 
set e = 1; 
insert into temp4 (region, company, dci) select 'APAC', c, null;
set c = (select region, min(company) from temp3 where region = 'APAC' and company not in (select company from temp4) and c <> company);
end if;

if (f = 0 and e = 0 and (p1_1 is not null and p1_2 is not null and p1_3 is not null)) then 
set r = r + 1;
end if;

-- select c, e, f, p1_1, p1_2, p1_3, p2_1, p3_1, a1_1, a1_2;

end while;

update Compliance c join temp4 t on c.company = t.company and c.region = t.region set c.dci = 'Premier' where t.dci = 'Premier';

-- insert into Compliance (region, company, dci)
-- select 'APAC', company, 'Premier' from temp4 where region = 'APAC' and dci = 'Premier';

end $$
delimiter ;

call apac_dci();

-- --------------------------------------------------
-- APAC MW
-- --------------------------------------------------

delimiter $$

drop procedure if exists apac_mw $$

create procedure apac_mw ()
begin

declare i int default 0;
declare p1_1 int default 0;
declare p1_2 int default 0;
declare p1_3 int default 0;
declare p2_1 int default 0;
declare p3_1 int default 0;
declare a1_1 int default 0;
declare a1_2 int default 0;
declare a1_3 int default 0;
declare a1_4 int default 0;
declare a2_1 int default 0;
declare a3_1 int default 0;

declare c varchar(200) default null;
declare f int default 0;
declare e int default 0;
declare r int default 0;

truncate table temp4;
truncate table temp5;

-- select distinct company from temp3 order by 1
-- select distinct company from temp3 where company not in (select company from temp4)

/*
select * from temp3 where company in (
 'Dell Asia Pacific'
-- 'IT Group, Inc.'
-- 'Jardine OneSolution (HK) Limited',
-- 'Netcom Infotech Pvt Ltd'
)
*/

truncate table temp4;
insert into temp4 (region, company, mw)
select region, company, '' as mw from temp3 where region = 'APAC' group by company having sum(case when mwothersales > 0 then 1 else 0 end) < 3 or sum(case when mwotherse > 0 then 1 else 0 end) < 1 or sum(case when mwotherdelivery > 0 then 1 else 0 end) < 1 or sum(case when mwsales > 0 then 1 else 0 end) < 2 or sum(case when mwse >0 then 1 else 0 end) < 1 or sum(case when mwdelivery > 0 then 1 else 0 end) < 1
union
select region, company, '' as mw from temp3 where region = 'APAC' group by company having sum(case when mwotherdelivery > 0 then 1 else 0 end) = 1 and sum(case when mwdelivery > 0 then 1 else 0 end) = 1 ;

set c = (select min(company) from temp3 where region = 'APAC' and company not in (select company from temp4));
set r = (select min(id) id from temp3 where region = 'APAC' and id >= r and company = c and mwothersales >= 1);

while (select count(*) from temp3 where region = 'APAC' and id >= r and company not in (select company from temp4)) > 1 do

set i = i + 1;    

set p1_1 = (select min(id) id from temp3 where region = 'APAC' and id >= r and company = c and mwothersales >= 1);
set p1_2 = (select min(id) id from temp3 where region = 'APAC' and company = c and mwothersales >= 1 and id <> p1_1);
set p1_3 = (select min(id) id from temp3 where region = 'APAC' and company = c and mwothersales >= 1 and id <> p1_1 and id <> p1_2);
set p2_1 = (select min(id) id from temp3 where region = 'APAC' and company = c and mwotherse >= 1);
set p3_1 = (select min(id) id from temp3 where region = 'APAC' and company = c and mwotherdelivery >= 1);
set a1_1 = (select min(id) id from temp3 where region = 'APAC' and company = c and mwsales >= 1 and id <> p1_1 and id <> p1_2 and id <> p1_3 and id <> p2_1 and id <> p3_1);
set a1_2 = (select min(id) id from temp3 where region = 'APAC' and company = c and mwsales >= 1 and id <> p1_1 and id <> p1_2 and id <> p1_3 and id <> p2_1 and id <> p3_1);
set a1_3 = (select min(id) id from temp3 where region = 'APAC' and company = c and mwsales >= 1 and id <> p1_1 and id <> p1_2 and id <> p1_3 and id <> p2_1 and id <> p3_1);
set a1_4 = (select min(id) id from temp3 where region = 'APAC' and company = c and mwsales >= 1 and id <> p1_1 and id <> p1_2 and id <> p1_3 and id <> p2_1 and id <> p3_1);
set a2_1 = (select min(id) id from temp3 where region = 'APAC' and company = c and mwotherse >= 1 and id <> p1_1 and id <> p1_2 and id <> p1_3 and id <> p2_1 and id <> p3_1);
set a3_1 = (select min(id) id from temp3 where region = 'APAC' and company = c and mwotherdelivery >= 1 and id <> p1_1 and id <> p1_2 and id <> p1_3 and id <> p2_1 and id <> p3_1);

-- insert into temp5 select p1_1;

if (p1_1 > 0 and p1_2 > 0 and p1_3 > 0 and p2_1 > 0 and p3_1 > 0 and a1_1 > 0 and a1_2 > 0 and a1_3 > 0 and a1_4 > 0 and a2_1 > 0 and a3_1 > 0) 
	then
		insert into temp4 (region, company, mw) select 'APAC', c, 'Premier';
		set f = 1;
		set c = (select min(company) from temp3 where region = 'APAC' and company not in (select company from temp4) and c <> company);
        -- set r = r + 1; -- shit
	else 
        set f = 0;
end if;

if (f = 0 and (p1_1 is null or p1_2 is null or p1_3 is null)) then 
set e = 1; 
insert into temp4 (region, company, mw) select 'APAC', c, null;
set c = (select region, min(company) from temp3 where region = 'APAC' and company not in (select company from temp4) and c <> company);
end if;

if (f = 0 and e = 0 and (p1_1 is not null and p1_2 is not null and p1_3 is not null)) then 
set r = r + 1;
end if;

-- select c, e, f, p1_1, p1_2, p1_3, p2_1, p3_1, a1_1, a1_2;

end while;

update Compliance c join temp4 t on c.company = t.company and c.region = t.region set c.mw = 'Premier' where t.mw = 'Premier';

-- insert into Compliance (region, company, mw)
-- select 'APAC', company, 'Premier' from temp4 where region = 'APAC' and mw = 'Premier';

end $$
delimiter ;

call apac_mw();

-- compliance2
-- --------------------------------------------------
-- EMEA DCI
-- --------------------------------------------------

delimiter $$

drop procedure if exists emea_dci $$

create procedure emea_dci ()
begin

declare i int default 0;
declare p1_1 int default 0;
declare p1_2 int default 0;
declare p2_1 int default 0;
declare p2_2 int default 0;
declare p3_1 int default 0;
declare p3_2 int default 0;
declare a1_1 int default 0;
declare a1_2 int default 0;
declare a2_1 int default 0;
declare a2_2 int default 0;
declare a3_1 int default 0;
declare a3_2 int default 0;

declare c varchar(200) default null;
declare f int default 0;
declare e int default 0;
declare r int default 0;

truncate table temp4;
truncate table temp5;
-- select distinct company from temp3 order by 1
-- select distinct company from temp3 where company not in (select company from temp4)

/*
select * from temp3 where company in (
 'Dell Asia Pacific'
-- 'IT Group, Inc.'
-- 'Jardine OneSolution (HK) Limited',
-- 'Netcom Infotech Pvt Ltd'
)
*/


truncate table temp4;
insert into temp4 (region, company, dci)
select region, company, '' from temp3 where region = 'EMEA' group by company having sum(case when dciothersales > 0 then 1 else 0 end) < 2 or sum(case when dciotherse > 0 then 1 else 0 end) < 2 or sum(case when dciotherdelivery > 0 then 1 else 0 end) < 2 or sum(case when dcisales > 0 then 1 else 0 end) < 2 or sum(case when dcise >0 then 1 else 0 end) < 2 or sum(case when dcidelivery > 0 then 1 else 0 end) < 2
union
select region, company, '' from temp3 where region = 'EMEA' group by company having sum(case when dciotherdelivery > 0 then 1 else 0 end) <= 2 and sum(case when dcidelivery > 0 then 1 else 0 end) = 2 ;

set c = (select min(company) from temp3 where region = 'EMEA' and company not in (select company from temp4));
set r = (select min(id) id from temp3 where region = 'EMEA' and id >= r and company = c and dciothersales >= 1);

while i < (select count(*) from temp3 where region = 'EMEA' and id >= r and company not in (select company from temp4)) > 1 do

set i = i + 1;    

set p1_1 = (select min(id) id from temp3 where region = 'EMEA' and id >= r and company = c and dciothersales >= 1);
set p1_2 = (select min(id) id from temp3 where region = 'EMEA' and company = c and dciothersales >= 1 and id <> p1_1);
set p2_1 = (select min(id) id from temp3 where region = 'EMEA' and company = c and dciotherse >= 1);
set p2_2 = (select min(id) id from temp3 where region = 'EMEA' and company = c and dciotherse >= 1 and id <> p2_1);
set p3_1 = (select min(id) id from temp3 where region = 'EMEA' and company = c and dciotherdelivery >= 1);
set p3_2 = (select min(id) id from temp3 where region = 'EMEA' and company = c and dciotherdelivery >= 1 and id <> p3_1);

set a1_1 = (select min(id) id from temp3 where region = 'EMEA' and company = c and dcisales >= 1 and id <> p1_1 and id <> p1_2 and id <> p2_1 and id <> p2_2 and id <> p3_1 and id <> p3_2);
set a1_2 = (select min(id) id from temp3 where region = 'EMEA' and company = c and dcisales >= 1 and id <> p1_1 and id <> p1_2 and id <> p2_1 and id <> p2_2 and id <> p3_1 and id <> p3_2 and id <> a1_1);
set a2_1 = (select min(id) id from temp3 where region = 'EMEA' and company = c and dciotherse >= 1 and id <> p1_1 and id <> p1_2 and id <> p2_1 and id <> p2_2 and id <> p3_1 and id <> p3_2);
set a2_2 = (select min(id) id from temp3 where region = 'EMEA' and company = c and dciotherse >= 1 and id <> p1_1 and id <> p1_2 and id <> p2_1 and id <> p2_2 and id <> p3_1 and id <> p3_2 and id <> a2_1);
set a3_1 = (select min(id) id from temp3 where region = 'EMEA' and company = c and dciotherdelivery >= 1 and id <> p1_1 and id <> p1_2 and id <> p2_1 and id <> p2_2 and id <> p3_1 and id <> p3_2);
set a3_2 = (select min(id) id from temp3 where region = 'EMEA' and company = c and dciotherdelivery >= 1 and id <> p1_1 and id <> p1_2 and id <> p2_1 and id <> p2_2 and id <> p3_1 and id <> p3_2 and id <> a3_1);

insert into temp5 select p1_1;

if (p1_1 > 0 and p1_2 > 0 and p2_1 > 0 and p2_2 > 0 and p3_1 > 0 and p3_2 > 0 and a1_1 > 0 and a1_2 > 0 and a2_1 > 0 and a2_2 > 0 and a2_1 > 0 and a3_1 > 0 and a3_2 > 0) 
	then
		insert into temp4 (region, company, dci) select 'EMEA', c, 'Premier';
		set f = 1;
		set c = (select min(company) from temp3 where region = 'EMEA' and company not in (select company from temp4) and c <> company);
        -- set r = r + 1; -- shit
	else 
        set f = 0;
end if;

if (f = 0 and (p1_1 is null or p1_2 is null)) then 
set e = 1; 
insert into temp4 (region, company, dci) select 'EMEA', c, null;
set c = (select region, min(company) from temp3 where region = 'EMEA' and company not in (select company from temp4) and c <> company);
end if;

if (f = 0 and e = 0 and (p1_1 is not null and p1_2 is not null)) then 
set r = r + 1;
end if;

-- select c, e, f, p1_1, p1_2, p1_3, p2_1, p3_1, a1_1, a1_2;

end while;

update Compliance c join temp4 t on c.company = t.company and c.region = t.region set c.dci = 'Premier' where t.dci = 'Premier';

-- insert into Compliance (region, company, dci)
-- select 'EMEA', company, 'Premier' from temp4 where region = 'EMEA' and dci = 'Premier';

end $$
delimiter ;

call emea_dci();


-- --------------------------------------------------
-- EMEA MW
-- --------------------------------------------------
delimiter $$

drop procedure if exists emea_mw $$
create procedure emea_mw ()
begin

declare i int default 0;
declare p1_1 int default 0;
declare p1_2 int default 0;
declare p2_1 int default 0;
declare p2_2 int default 0;
declare p3_1 int default 0;
declare p3_2 int default 0;
declare a1_1 int default 0;
declare a1_2 int default 0;
declare a2_1 int default 0;
declare a2_2 int default 0;
declare a3_1 int default 0;
declare a3_2 int default 0;

declare c varchar(200) default null;
declare f int default 0;
declare e int default 0;
declare r int default 0;

truncate table temp4;
truncate table temp5;
-- select distinct company from temp3 order by 1
-- select distinct company from temp3 where company not in (select company from temp4)

/*
select * from temp3 where company in (
 'Dell Asia Pacific'
-- 'IT Group, Inc.'
-- 'Jardine OneSolution (HK) Limited',
-- 'Netcom Infotech Pvt Ltd'
)
*/


truncate table temp4;
insert into temp4 (region, company, mw)
select region, company, '' from temp3 where region = 'EMEA' group by company having sum(case when mwothersales > 0 then 1 else 0 end) < 2 or sum(case when mwotherse > 0 then 1 else 0 end) < 2 or sum(case when mwotherdelivery > 0 then 1 else 0 end) < 2 or sum(case when mwsales > 0 then 1 else 0 end) < 2 or sum(case when mwse >0 then 1 else 0 end) < 2 or sum(case when mwdelivery > 0 then 1 else 0 end) < 2
union
select region, company, '' from temp3 where region = 'EMEA' group by company having sum(case when mwotherdelivery > 0 then 1 else 0 end) <= 2 and sum(case when mwdelivery > 0 then 1 else 0 end) = 2 ;

set c = (select min(company) from temp3 where region = 'EMEA' and company not in (select company from temp4));
set r = (select min(id) id from temp3 where region = 'EMEA' and id >= r and company = c and mwothersales >= 1);

while i < (select count(*) from temp3 where region = 'EMEA' and id >= r and company not in (select company from temp4)) > 1 do

set i = i + 1;    

set p1_1 = (select min(id) id from temp3 where region = 'EMEA' and id >= r and company = c and mwothersales >= 1);
set p1_2 = (select min(id) id from temp3 where region = 'EMEA' and company = c and mwothersales >= 1 and id <> p1_1);
set p2_1 = (select min(id) id from temp3 where region = 'EMEA' and company = c and mwotherse >= 1);
set p2_2 = (select min(id) id from temp3 where region = 'EMEA' and company = c and mwotherse >= 1 and id <> p2_1);
set p3_1 = (select min(id) id from temp3 where region = 'EMEA' and company = c and mwotherdelivery >= 1);
set p3_2 = (select min(id) id from temp3 where region = 'EMEA' and company = c and mwotherdelivery >= 1 and id <> p3_1);

set a1_1 = (select min(id) id from temp3 where region = 'EMEA' and company = c and mwsales >= 1 and id <> p1_1 and id <> p1_2 and id <> p2_1 and id <> p2_2 and id <> p3_1 and id <> p3_2);
set a1_2 = (select min(id) id from temp3 where region = 'EMEA' and company = c and mwsales >= 1 and id <> p1_1 and id <> p1_2 and id <> p2_1 and id <> p2_2 and id <> p3_1 and id <> p3_2 and id <> a1_1);
set a2_1 = (select min(id) id from temp3 where region = 'EMEA' and company = c and mwotherse >= 1 and id <> p1_1 and id <> p1_2 and id <> p2_1 and id <> p2_2 and id <> p3_1 and id <> p3_2);
set a2_2 = (select min(id) id from temp3 where region = 'EMEA' and company = c and mwotherse >= 1 and id <> p1_1 and id <> p1_2 and id <> p2_1 and id <> p2_2 and id <> p3_1 and id <> p3_2 and id <> a2_1);
set a3_1 = (select min(id) id from temp3 where region = 'EMEA' and company = c and mwotherdelivery >= 1 and id <> p1_1 and id <> p1_2 and id <> p2_1 and id <> p2_2 and id <> p3_1 and id <> p3_2);
set a3_2 = (select min(id) id from temp3 where region = 'EMEA' and company = c and mwotherdelivery >= 1 and id <> p1_1 and id <> p1_2 and id <> p2_1 and id <> p2_2 and id <> p3_1 and id <> p3_2 and id <> a3_1);

insert into temp5 select p1_1;

if (p1_1 > 0 and p1_2 > 0 and p2_1 > 0 and p2_2 > 0 and p3_1 > 0 and p3_2 > 0 and a1_1 > 0 and a1_2 > 0 and a2_1 > 0 and a2_2 > 0 and a2_1 > 0 and a3_1 > 0 and a3_2 > 0) 
	then
		insert into temp4 (region, company, mw) select 'EMEA', c, 'Premier';
		set f = 1;
		set c = (select min(company) from temp3 where region = 'EMEA' and company not in (select company from temp4) and c <> company);
        -- set r = r + 1; -- shit
	else 
        set f = 0;
end if;

if (f = 0 and (p1_1 is null or p1_2 is null)) then 
set e = 1; 
insert into temp4 (region, company, mw) select 'EMEA', c, null;
set c = (select region, min(company) from temp3 where region = 'EMEA' and company not in (select company from temp4) and c <> company);
end if;

if (f = 0 and e = 0 and (p1_1 is not null and p1_2 is not null)) then 
set r = r + 1;
end if;

-- select c, e, f, p1_1, p1_2, p1_3, p2_1, p3_1, a1_1, a1_2;

end while;

update Compliance c join temp4 t on c.company = t.company and c.region = t.region set c.mw = 'Premier' where t.mw = 'Premier';

-- insert into Compliance (region, company, mw)
-- select 'EMEA', company, 'Premier' from temp4 where region = 'EMEA' and mw = 'Premier';

end $$
delimiter ;

call emea_mw();

/*
select * from Compliance where dci is not null or mw is not null or cloud is not null order by 3 desc;
select * from temp3;  
select * from Compliance;

truncate table Compliance;
call apac_dci();
call emea_dci();

select region, 
	sum(case when dci = 'Premier' then 1 else 0 end) dci_premier,
	sum(case when dci = 'Advanced' then 1 else 0 end) dci_advanced,
	sum(case when dci = 'Ready' then 1 else 0 end) dci_ready,
	sum(case when mw = 'Premier' then 1 else 0 end) mw_premier,
	sum(case when mw = 'Advanced' then 1 else 0 end) mw_advanced,
	sum(case when mw = 'Ready' then 1 else 0 end) mw_ready,
	sum(case when cloud = 'Premier' then 1 else 0 end) cloud_premier,
	sum(case when cloud = 'Advanced' then 1 else 0 end) cloud_advanced,
	sum(case when cloud = 'Ready' then 1 else 0 end) cloud_ready
from
	Compliance
group by
	region
order by 
	1;

select region, company, dci, mw, cloud from Compliance where length(ltrim(rtrim(company))) > 0 and (dci is not null or mw is not null or cloud is not null) group by region, company order by 1, 2


*/

