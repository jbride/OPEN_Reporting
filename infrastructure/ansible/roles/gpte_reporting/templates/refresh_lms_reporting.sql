CREATE DEFINER=`lms`@`%` PROCEDURE `refresh_lms_reporting`()
begin

-- update StudentAccreditations set accreditationtype = 'Active' where accreditationdate >= '2014-12-01' and accreditationtype = 'Expired';
update StudentAccreditations set accreditationtype = 'Active' where accreditationdate >= '2015-01-01' and accreditationtype = 'Expired';

drop table if exists lms_reporting.Students;
create table lms_reporting.Students as
select * from lms_transactional.Students;

update lms_reporting.Students set region = ltrim(rtrim(upper(region)));
update lms_reporting.Students set region = 'FEDSLED' where region = 'FED/SLED';
update lms_reporting.Students set subregion = null where ltrim(rtrim(subregion)) = 'UNKNOWN';

alter table lms_reporting.Students 
	add CompanyCode varchar(15) null, 
    add AllianceCode varchar(15) null,
	add FocusGroup varchar(15) null;

update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set alliancecode = 'ACCENTURE' where (companyname like 'accenture%' or email like '%@accenture.com');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set alliancecode = 'ATOS' where (companyname like 'atos%' or email like '%@atos.net');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set alliancecode = 'CAPGEMINI' where (companyname like 'capgemini%' or email like '%@cpmbraxis.com' or email like '%@capgemini.com');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set alliancecode = 'CGI' where (companyname like 'cgi%' or email like '%@cgi.com');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set alliancecode = 'CISCO' where (companyname like 'cisco%' or email like '%@cisco.com');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set alliancecode = 'CSC' where companyname like 'csc%' or companyname like 'computer science%' or email like '%csc.com';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set alliancecode = 'DELL' where companyname like 'dell%' or email like '%@dell.com' ;
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set alliancecode = 'HP' where (companyname like 'HP%' or companyname like 'Hewlett%Packard%' or email like '%@hp.com' or email like '%@hpe.com') and companyname not like 'HPC%' and companyname not like 'HPM%' ;
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set alliancecode = 'IBM' where companyname like 'IBM%' or email like '%.ibm.com';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set alliancecode = 'INFOSYS' where (companyname like 'INFOSYS%' or email like '%@infosys.com') and companyname not like 'infosyst%' ;
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set alliancecode = 'NETAPP' where (companyname like 'NETAPP%' or email like '%@netapp.com') ;
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set alliancecode = 'SAP' where (companyname like 'SAP%' or email like '%@sap.com') ;
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set alliancecode = 'TATA' where (companyname like 'TATA%' or email like '%@tcs.com');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set alliancecode = 'TECH-MAHINDRA' where (companyname like 'TECH%MAHINDRA%' or email like '%@techmahindra.com');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set alliancecode = 'WIPRO' where (companyname like 'wipro%' or email like '%@wipro.com');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set alliancecode = 'LENOVO' where (companyname like '%LENOVO%' or email like '%@lenovo.com');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set alliancecode = 'FUJITSU' where (companyname like '%FUJITSU%' or email like '%@%fujitsu.com');

update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ACCENTURE' where (companyname like 'accenture%' or email like '%@accenture.com');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ATOS' where (companyname like 'atos%' or email like '%@atos.net');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'CAPGEMINI' where (companyname like 'capgemini%' or email like '%@cpmbraxis.com' or email like '%@capgemini.com');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'CGI' where (companyname like 'cgi%' or email like '%@cgi.com');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'CISCO' where (companyname like 'cisco%' or email like '%@cisco.com');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'CSC' where companyname like 'csc%' or companyname like 'computer science%' or email like '%csc.com';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'DELL' where companyname like 'dell%' or email like '%@dell.com' ;
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'HP' where (companyname like 'HP%' or companyname like 'Hewlett%Packard%' or email like '%@hp.com' or email like '%@hpe.com') and companyname not like 'HPC%' and companyname not like 'HPM%' ;
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'IBM' where companyname like 'IBM%' or email like '%.ibm.com';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'INFOSYS' where (companyname like 'INFOSYS%' or email like '%@infosys.com') and companyname not like 'infosyst%' ;
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'NETAPP' where (companyname like 'NETAPP%' or email like '%@netapp.com') ;
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SAP' where (companyname like 'SAP%' or email like '%@sap.com') ;
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'TATA' where (companyname like 'TATA%' or email like '%@tcs.com');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'TECH-MAHINDRA' where (companyname like 'TECH%MAHINDRA%' or email like '%@techmahindra.com');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'WIPRO' where (companyname like 'wipro%' or email like '%@wipro.com');

update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ACCELERIS' where (companyname like 'ACCELERIS%' or email like '%@acceleris.ch');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ADVANCED365' where (companyname like 'ADVANCED 365%' or email like '%@ADVANCED365.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ADVANCEDCLUSTERING' where (companyname like 'ADVANCED CLUSTERING%' or email like '%@ADVANCEDCLUSTERING.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ADVANCEDCOMPCON' where (companyname like 'ADVANCED COMP%CONC%' or email like '%@ACCONLINE.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ADVANCEDCOMPTECH' where (companyname like 'ADVANCED COMP%TECJ%' or email like '%@ACT-AMERICA.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ADVANCEDDIGITAL' where (companyname like 'ADVANCED DIGITAL%' or email like '%@ADDSYS.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ADVANCEDINDUST' where (companyname like 'ADVANCED INDUSTRIAL%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ADVANCEDINTEG' where (companyname like 'ADVANCED INTEGR%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ADVANCEDMICRO' where (companyname like 'ADVANCED MICRO%' or email like '%@advmcro.com');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ADVANCEDOFFICE' where (companyname like 'ADVANCED OFFICE%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ADVANCEDOPSTECH' where (companyname like 'ADVANCED OPERATIONS TECH%' or email like '%@ADVANCEDOPERATIONS.com');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ADVANCEDSOFTWARE' where (companyname like 'ADVANCED SOFTWARE%' or email like '%@ADVANCED.com.AR');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ADVANCEDSYSTEMS' where (companyname like 'ADVANCED SYSTEMS G%' or email like '%@VIRTUAL.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'APLANA' where (companyname like 'APLANA%' or email like '%@APLANA.com');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ARROWECS' where (companyname like 'ARROW %' or email like '%@arrowecs.%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ASIAPAC' where (companyname like 'ASIAPAC %' or email like '%@ASIAPAC.COM.SG');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ATEA' where (companyname like 'ATEA %' or email like '%@ATEA.%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'AVNET' where (companyname like 'AVNET %' or email like '%@AVNET.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGBODE' where (companyname like 'BEIJING BODE %' or email like '%@BJBODE.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGCHOICE' where (companyname like 'BEIJING CHOICE%' or email like '%@CHOICE-E.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGCONITECH' where (companyname like 'BEIJING CONITECH%' or email like '%@T9173.COMM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGDIGINETS' where (companyname like 'BEIJING DIGINETS%' or email like '%@DIGINETS.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGDINXUAN' where (companyname like 'BEIJING DINGXUAN%' or email like '%@DINGXUANTECH.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGEFUTURE' where (companyname like 'BEIJING EFUTURE%' or email like '%@EFUTURE-TECH.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGESOFT' where (companyname like 'BEIJING ESOFT%' or email like '%@ESOFT.COM.CN');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGFEDERAL' where (companyname like 'BEIJING FEDERAL%' or email like '%@FEDERAL.COM.CN');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGFLOURISOFT' where (companyname like 'BEIJING FLOURISOFT%' or email like '%@FLOURISOFT.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGHENGXIN' where (companyname like 'BEIJING HENGXIN%' or email like '%@HENGXINZHIYUAN.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGINITDREAM' where (companyname like 'BEIJING INITDREAM%' or email like '%@INITDREAM.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGJIAHE' where (companyname like 'BEIJING JIAHE%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGJIAXING' where (companyname like 'BEIJING JIAXING%' or email like '%@JSTARSOFT.COM.CN');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGKINGTOP' where (companyname like 'BEIJING KINGTOP%' or email like '%@KINGTOP.COM.CN');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGLAIONE' where (companyname like 'BEIJING LAIONE%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGLANWAN' where (companyname like 'BEIJING LAN%WAN%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGLEIAN' where (companyname like 'BEIJING LEIAN%' or email like '%@LEIANSOFT.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGLINUX' where (companyname like 'BEIJING LINUX%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGLYPOWER' where (companyname like 'BEIJING LYPOWER%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGMICROLINUX' where (companyname like 'BEIJING MICROLINUXSOFT%' or email like '%@MICROLINUXSOFT.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGNETRACK' where (companyname like 'BEIJING NETRACK%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGORIENTALLION' where (companyname like 'BEIJING ORIENTAL%LION%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGPRF' where (companyname like 'BEIJING P%R%FINAN%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGPOWERUNIQUE' where (companyname like 'BEIJING POWERUNIQUE%') or email like '%@powerunique.com';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGSINOBROAD' where (companyname like 'BEIJING SINOBROAD%') or email like '%@SINOBROAD.COM.CN';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGSINOTOPSOFT' where (companyname like 'BEIJING SINOTOPSOFT%') or email like '%@SINOTOPSOFT.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGTANGRUANEDU' where (companyname like 'BEIJING TANGRUAN EDU%') or email like '%@TANGRUAN.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGTECHHERO' where (companyname like 'BEIJING TECHHERO%') or email like '%@TECHHERO.COM%';

update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BULL' where (companyname like 'BULL %' or email like '%@BULL.%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'CARAHSOFT' where (companyname like 'CARAHSOFT %' or email like '%@CARAHSOFT.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'CDW' where (companyname like 'CDW %' or email like '%@CDW.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'COMPAREX' where (companyname like 'COMPAREX %' or email like '%@COMPAREX.%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'COMPUTACENTER' where (companyname like 'COMPUTACENTER %' or email like '%@COMPUTACENTER.%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'CONRES' where (companyname like 'CONTINENTAL RES%' or email like '%@CONRES.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'CONVERGLOBAL' where (companyname like 'CONVERGE GLOB%' or email like '%@CONVERGEGROUP.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'CONVERGESYSTEMS' where (companyname like 'CONTINENTAL RES%' or email like '%@CONVERGESYSTEM.%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'CORESEC' where (companyname like 'CORESEC %' or email like '%@ENERGYTELECOM.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'DATACOM' where (companyname like 'DATACOM %' or email like '%@DATACOM.%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'EMERGENT' where (companyname like 'EMERGENT%LLC%' or email like '%@EMERGENT360.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'FORSYTHE' where (companyname like 'FORSYTHE%' or email like '%@FORSYTHE.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'FUJITSU' where (companyname like '%FUJITSU%' or email like '%@FUJITSU.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'GCMICRO' where (companyname like 'GC MICRO%' or email like '%@GCMICRO.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'GULFBM' where (companyname like 'GULF BUSINESS%' or email like '%@%GBM.IHOST.COM' or email like '%@GBMME.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'INGENIEROSPC' where (companyname like 'INGENIEROS PROF%' or email like '%@IPCOM.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'INGRAMMICRO' where (companyname like 'INGRAM MICRO%' or email like '%@INGRAMMICRO%.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'INSIGHTDIRECT' where (companyname like 'INSIGHT DIRECT%' or email like '%@INSIGHT.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'KAPSCH' where (companyname like 'KAPSCH%' or email like '%@KAPSCH.NET%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'KELWAY' where (companyname like 'KELWAY%' or email like '%@KELWAY.CO%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'LINBIT' where (companyname like 'LINBIT%' or email like '%@LINBIT.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'LINSOFT' where (companyname like 'LINSOFT%' or email like '%@LINSOFT.%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'LOGICALIS' where (companyname like 'LOGICALIS %' or email like '%@%LOGICALIS.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'MEDIAMART' where (companyname like 'MEDIA%MART%' or email like '%@MEDIAMART.JP');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'MIRACLESOFT' where (companyname like 'MIRACLE SOFT%' or email like '%@MIRACLESOFT.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'MIRACLEAS' where (companyname like 'MIRACLE A/S%' or email like '%@MIRACLEAS.DK');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'NEXSYS' where (companyname like 'NEXSYS %' or email like '%@NEXSYSLA.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'NEXUSIS' where (companyname like 'NEXUS IS%' or email like '%@NEXUSIS.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'NEXUSTECH' where (companyname like 'NEXUS TECH%' or email like '%@NEXUSTECH.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'NTTCOM' where (companyname like 'NTT COM %' or email like '%@NTT.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'NTTCOMM' where (companyname like 'NTT COMM%' or email like '%@FRONTLINE.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'NTTDATA' where (companyname like 'NTT DATA%' or email like '%@NTTDATA.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'NTTDATAINT' where (companyname like 'NTT DATA INT%' or email like '%@INTELLILINK.CO%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'NTTSOFT' where (companyname like 'NTT SOFT%' or email like '%@PO.NTTS.CO%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTBAJAU' where (companyname like 'PT BAJAU%' or email like '%@BAJAU.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTITG' where (companyname like 'PT IT G%' or email like '%@ITGROUPING.ASIA');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTVISI' where (companyname like 'PT VISI%' or email like '%@VISITEK.CO%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTANABATIC' where (companyname like 'PT%ANABATIC%' or email like '%@ANABATIC.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTBAJAU' where (companyname like 'PT% BAJAU %' or email like '%@BAJAU.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTHANOMAN' where (companyname like 'PT% HANOMAN%' or email like '%@%HANOMAN.CO%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTINDOLINUX' where (companyname like 'PT% INDOLINUX%' or email like '%@INDOLINUX.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTINFRACOM' where (companyname like 'PT% INFRACOM%' or email like '%@INFRACOM-TECH.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTJABETTO' where (companyname like 'PT% JABETTO%' or email like '%@JABETTO.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTQUADRAS' where (companyname like 'PT% QUADRA%' or email like '%@QUADRAS.CO%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTSIGMA' where (companyname like 'PT% SIGMA%' or email like '%@SIGMA.CO%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTSINARSURYA' where (companyname like 'PT% SINAR SUR%' or email like '%@SSTEKNOLOGI.CO%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTTEKNOGLOBAL' where (companyname like 'PT% TEKNOGLOBAL%' or email like '%@TEKNOGLOBAL.%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTTIRTARAYA' where (companyname like 'PT% TIRTARAYA%' or email like '%@TMI.CO.ID');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTVIRTUS' where (companyname like 'PT% VIRTUS%' or email like '%@VIRTUSINDONESIA.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTLLIMITED' where (companyname like 'PTL LIMITED%' or email like '%@PTL.COM.MT%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'REDHAT' where (companyname like 'RED HAT%' or email like '%@REDHAT.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SCALAR' where (companyname like 'SCALAR%' or email like '%@SCALAR.C%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SERVICEINFO' where (companyname like 'SERVICE INFOMATICA%' or email like '%@SERVICE.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SERVICEIT' where (companyname like 'SERVICE INFO%' or email like '%@SERVICEIT.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SHADOWSOFT' where (companyname like 'SHADOW%SOFT%' or email like '%@SHADOW-SOFT.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SICHUANCHUANGLI' where (companyname like 'SICHUAN%CHUANGLI%' or email like '%@LEATC.CN%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SICHUANSYS' where (companyname like 'SICHUAN %S%Y%S%SOFT%' or email like '%@SYSSOFTWARE.CN%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SICUANYIKETECH' where (companyname like 'SICHUAN %YI%KI%TECH%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SHANGHAIACTION' where (companyname like 'SHANGHAI ACTION%') or email like '%@actionsky.com%';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SHANGHAIBETTER' where (companyname like 'SHANGHAI BETTER%') or email like '%@BSGCHINA.COM%';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SHANGHAICOMLAN' where (companyname like 'SHANGHAI COM%') or email like '%@COMLAN.COM%';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SHANGHAIGUOQIN' where (companyname like 'SHANGHAI GUOQIN%') or email like '%@GUOQIN888.COM%';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SHANGHAIHOLA' where (companyname like 'SHANGHAI HOLA%') or email like '%@HOLA-TECH.COM%';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SHANGHAIJUN' where (companyname like 'SHANGHAI JUN%') or email like '%@JZTEC.CN%';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SHANGHAISINORAIL' where (companyname like 'SHANGHAI SINORAIL%') or email like '%@SINORAIL.COM%';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SHANGHAITELEHOT' where (companyname like 'SHANGHAI TELE%HOT%') or email like '%@TELE-HOT.COM%';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SHANGHAIYUANQIANG' where (companyname like 'SHANGHAI YUAN%QIANG%') or email like '%@YQ021.NET%';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SIRIUSCOMP' where (companyname like 'SIRIUS COMP%') or email like '%@SIRIUSCOM.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SOFTWAREONE' where (companyname like 'SOFTWAREONE%') or email like '%@SOFTWAREONE.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SONDA' where (companyname like 'SONDA%') or email like '%@%SONDA.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SPECIALISTCOMP' where (companyname like 'SPECIALIST COMP%') or email like '%@%SCC.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SYNNEXCORP' where (companyname like 'SYNNEX CORP%') or email like '%@%SYNNEX.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SYNNEXINFOTEC' where (companyname like 'SYNNEX INFOTEC%') or email like '%@%SYNNEXINFOTEC.CO%';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SYONE' where (companyname like 'SYONE%SBS%') or email like '%@SYONE.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'TECHDATA' where (companyname like 'TECH DATA%') or email like '%@TECHDATA.C%';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ULTIMUMBV' where (companyname like 'ULTIMUM%B%V%') or email like '%@ULTIMUM.NL';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ULTIMUMTECH' where (companyname like 'ULTIMUM TECH%') or email like '%@ULTIMUMTECHNOLOGIES.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'WORLDWIDETECH' where (companyname like 'WORLD WIDE TECH%') or email like '%@WWT.COM';


update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'LENOVO' where (companyname like 'LENOVO%') or email like '%@LENOVO.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'NETAPP' where (companyname like 'NETAPP%') or email like '%@NETAPP.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SGI' where (companyname like 'SGI%') or email like '%@SGI.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SUPERMICRO' where (companyname like 'SUPER MICRO%') or email like '%@SUPERMICRO.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'AUGUSTSCHELL' where (companyname like 'AUGUST SCHELL%') or email like '%@AUGUSTSCHELL.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'CDWG' where (companyname like 'CDW G%') or email like '%@CDWG.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'COMPSEC' where (companyname like 'COMPSEC%') or email like '%@COMPSECINC.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'DLTSOLUTIONS' where (companyname like 'DLT SOLUTIONS%') or email like '%@DLT.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ENPOINTE' where (companyname like 'EN POINTE TECH%') or email like '%@ENPOINTE.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'FIERCESOFTWARE' where (companyname like 'FIERCE SOFTWARE%') or email like '%@FIERCESW.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'GOVERNMENTACQ' where (companyname like 'GOVERNMENT ACQUISITIONS%') or email like '%@GOV-ACQ.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'IMMIXGROUP' where (companyname like 'IMMIXGROUP%') or email like '%@IMMIXGROUP.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'IRONBOW' where (companyname like 'IRON BOW%') or email like '%@IRONBOW.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SHI' where (companyname like 'SHI INTERNATIONAL%') or email like '%@SHI.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'TABORDA' where (companyname like 'TABORDA SOLUTIONS%') or email like '%@TABORDASOLUTIONS.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'VIZURI' where (companyname like 'VIZURI%') or email like '%@VIZURI.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'REDRIVER' where (companyname like 'RED RIVER%') or email like '%@REDRIVER.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'MAINLINE' where (companyname like 'MAINLINE INFORMATION SYS%') or email like '%@MAINLINE.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ACSTECH' where (companyname like 'ACS TECH%') or email like '%@ACSTECHNOLOGIES.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BOOZALLENHAMILTON' where (companyname like 'BOOZ ALLEN HAMILTON%') or email like '%@BAH.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'CACI' where (companyname like 'CACI%') or email like '%@CACI.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'DELOITTE' where (companyname like 'DELOITTE%') or email like '%@DELOITTE.CO%';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'DRS' where (companyname like 'DRS%') or email like '%@DRS.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'EPSILONSYS' where (companyname like 'EPSILON SYS%') or email like '%@EPSILONSYSTEMS.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'GENERALDYNAMICS' where (companyname like 'GENERAL DYNAMICS%') or email like '%@GD-MS.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'LEIDOS' where (companyname like 'LEIDOS%') or email like '%@LEIDOS.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'LOCKHEEDMARTIN' where (companyname like 'LOCKHEED MARTIN%') or email like '%@LMCO.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'MANTECH' where (companyname like 'MANTECH INT%') or email like '%@MANTECH.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'MITRE' where (companyname like 'MITRE%') or email like '%@MITRE.ORG';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'NORTHROPGRUMMAN' where (companyname like 'NORTHROP GRUMMAN%') or email like '%@NGC.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'QSSI' where (companyname like 'QSSI%') or email like '%@QSSINC.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'RAYTHEON' where (companyname like 'RAYTHEON%') or email like '%@RAYTHEON.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SAIC' where (companyname like 'SAIC%') or email like '%@SAIC.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SERCO' where (companyname like 'SERCO%') or email like '%@SERCO.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SOLERS' where (companyname like 'SOLERS%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'TCSENTREPRISES' where (companyname like 'TCS ENT%') or email like '%@TCSENTREPRISES.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'TERREMARK' where (companyname like 'TERREMARK%') or email like '%@%TERREMARK.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'TEXTRON' where (companyname like 'TEXTRON SYS%') or email like '%@%TEXTRON%.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'UNISYS' where (companyname like 'UNISYS CORP%') or email like '%@UNISYS.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BAESYSTEMS' where (companyname like 'BAE SYS%') or email like '%@BAESYSTEMS.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'HARRIS' where (companyname like 'HARRIS GCSD%') or email like '%@HARRIS.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ROCKWELL' where (companyname like '%ARINC%') or email like '%@ARINC.COM';



update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SIXTREE' where (companyname like 'SIXTREE%') or email like '%@SIXTREE.COM.AU';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'INTEGRAL' where (companyname like 'Integral Technology Solutions%') or email like '%@INTEGRALTECH.COM.AU';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'RXPSERVICES' where (companyname like 'RXP Services Limited%') or email like '%@RXPSERVICES.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'EMPIREDLTD' where (companyname like 'Empired Ltd%') or email like '%@EMPIRED.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'FRONTIERGROUP' where (companyname like 'The Frontier Group%') or email like '%@THEFRONTIERGROUP.COM.AU';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'GLINTECH' where (companyname like 'Glintech%') or email like '%@GLINTECH.COM';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'UNICOCOMPSYS' where (companyname like 'Unico Computer Systems Pty Ltd%') or email like '%@UNICO.COM.AU';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'APTIRA' where (companyname like 'Aptira Pty Ltd%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'RHIPE' where (companyname like 'RHIPE%' or email like '%@rhipoe.com');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BASE2SERVICES' where (companyname like 'BASE2SERVICES%' or email like '%@BASE2SERVICES.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'INFRAWORX' where (companyname like 'INFRAWORX%' or email like '%@INFRAWORX.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SLICEDTECH' where (companyname like 'SLICEDTECH%' or email like '%@SLICEDTECH.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PERFEKT' where (companyname like 'PERFEKT%' or email like '%@PERFEKT.COM%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTEMERIO' where (companyname like 'PT. EMERIO%' or email like '%@EMERIOCORP.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTASTRAGRAPHIA' where (companyname like 'PT. ASTRA GRAPHIA INFO%' or email like '%@AG-IT.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTDIFINI' where (companyname like 'PT DIFINI%' or email like '%@DIFINITE.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTTECKNOGLOBAL' where (companyname like 'PT. TEKNOGLOBAL%' or email like '%@TEKNOGLOBAL.NET');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTPROFISIEN' where (companyname like 'PT. PROFISIEN%' or email like '%@PROFISIEN.CO.ID');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTMITRAINTREGRASI' where (companyname like 'PT. MITRA INTEGRASI%' or email like '%@MII.CO.ID');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'PTMETROCOM' where (companyname like 'PT. METROCOM GLOBAL%' or email like '%@METROCOM.CO.ID');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEANSGROUP' where (companyname like 'BEANS GROUP%' or email like '%@BEANS.COM.MY');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'REDTREE' where (companyname like 'RED TREE VENTURES%' or email like '%@REDTREEUNWIRED.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'HITACHIEBWORX' where (companyname like 'HITACHI EBWORX SDN%' or email like '%@HITACHI-EBWORX.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'CYBERVILLAGE' where (companyname like 'CYBER VILLAGE SDN%' or email like '%@CYBER-VILLAGE.NET');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'INFOPRO' where (companyname like 'INFOPRO SDN%' or email like '%@INFOPRO.COM.MY');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'INTEGRATEDAUTOMATEDWORKFLOW' where (companyname like 'INTEGRATED AUTOMATED WORKFLOW%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'APPREKA' where (companyname like 'APPREKA SDN%' or email like '%@APPREKA.COM.MY');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'WIKILABS' where (companyname like 'WIKI LABS SDN%' or email like '%@WIKILABS.ASIA');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ABYRES' where (companyname like 'ABYRES SDN%' or email like '%@ABYRES.NET');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'CTCGLOBAL' where (companyname like 'CTC GLOBAL SDN%' or email like '%@CTC-G.COM.MY');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'IMPACTBUSINESS' where (companyname like 'IMPACT BUSINESS SOL%' or email like '%@IMPACT-MULTIMEDIA.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'IZENO' where (companyname like 'IZENO PRIVATE LIM%' or email like '%@IZENO.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'NCS' where (companyname like 'NCS PTE LTD%' or email like '%@NCS.COM.SG');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'CXRUS' where (companyname like 'CXRUS SOLUTIONS SDN%' or email like '%@CXRUS.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'S&ISYSTEMS' where (companyname like 'S & I SYSTEMS PTE%' or email like '%@SI-ASIA.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'STERIAASIA' where (companyname like 'STERIA ASIA PTE%' or email like '%@STERIA.COM.SG');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'EQUARIATECH' where (companyname like 'ECQUARIA TECHNOLOGIES PTE%' or email like '%@ECQUARIA.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'CXRUS' where (companyname like 'CXRUS SOLUTIONS PTE%' or email like '%@CXRUS.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'FOSSTECH' where (companyname like 'FOSSTECH SOLUTIONS PTE%' or email like '%@FOSSTECH.BIZ');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'FUJITSUASIA' where (companyname like 'FUJITSU ASIA PTE%' or email like '%@SG.FUJITSU.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'NOVARETECH' where (companyname like 'NOVARE TECH%' or email like '%@NOVARE.COM.HK');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'REVOSOFTWARE' where (companyname like 'REVO SOFTWARE SOL%' or email like '%@MORPHLABS.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ITGROUPINC' where (companyname like 'IT GROUP INC%' or email like '%@ITGROUPINC.ASIA');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'NEXUSTECH' where (companyname like 'NEXUS TECHNOLOGIES%' or email like '%@NEXUSTECH.COM.PH');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'AUTOMATEDTECH' where (companyname like 'AUTOMATED TECHNOLOGIES%' or email like '%@ATIPH.NET');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ALBATROSSCONSULTING' where (companyname like 'ALBATROSS CONSULTING%' or email like '%@ALBATROSS-CONSULTING.NET');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'G-ABLECOMPANY' where (companyname like 'G-ABLE companyname LIM%' or email like '%@G-ABLE.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'NEUSOFTCORP' where (companyname like 'NEUSOFT CORP%' or email like '%@NEUSOFT.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SINODATA' where (companyname like 'SINODATA CO%' or email like '%@SINODATA.NET.CN');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SINOSOFT' where (companyname like 'SINOSOFT CO%' or email like '%@SINOSOFT.COM.CN');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'NSTRENDINFO' where (companyname like 'NSTREND INFORMATION TECH%' or email like '%@NSTREND.COM.CN');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BEIJINGUNITEDELEC' where (companyname like 'BEIJING UNITED ELECTRONICS CO%' or email like '%@RONGLIAN.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'CHINASMARTSOFT' where (companyname like 'CHINA SMART SOFTWARE CO%' or email like '%@GZCSS.COM.CN');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'FUJITSUCHINA' where (companyname like 'FUJITSU (CHINA) HOLDINGS CO%' or email like '%@CN.FUJITSU.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'YUANTONGTECH' where (companyname like 'YUANTONG TECHNOLOGIES CO%' or email like '%@YUANTONG.CN');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SHANGHAINEWTOUCH' where (companyname like 'SHANGHAI NEWTOUCH SOFTWARE CO%' or email like '%@NEWTOUCH.CN');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'LENOVOBEIJING' where (companyname like 'LENOVO (BEIJING) LIMIT%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'TCCCONSULTING' where (companyname like 'TCC CONSULTING LIMIT%' or email like '%@TCC-CONSULTING.COM.HK');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'MACROVIEWTELE' where (companyname like 'MACROVIEW TELECOM LIM%' or email like '%@MACROVIEW.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'INTEGRATEDGLOBALSOL' where (companyname like 'INTEGRATED GLOBAL SOLUTIONS SDN%' or email like '%@IGSB.COM.MY');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ITCHANNELASIA' where (companyname like 'IT CHANNEL (ASIA) LIM%' or email like '%@ITCHANNEL.ASIA');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'FUJITSUHONGKONG' where (companyname like 'FUJITSU HONG KONG LIM%' or email like '%@HK.FUJITSU.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'THINKPOWERINFO' where (companyname like 'THINKPOWER INFORMATION CORP%' or email like '%@THINKPOWER.COM.TW');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'INWINSTACK' where (companyname like 'INWINSTACK%' or email like '%@INWINSTACK.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ZECHSTERINFO' where (companyname like 'ZECHSTER INFO%' or email like '%@ZECHSTER.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'WIPROTECH' where (companyname like 'WIPRO TECH%' or email like '%@WIPRO.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'INFOSYS' where (companyname like 'INFOSYS LIM%' or email like '%@INFOSYS.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'TAASHEELINUX' where (companyname like 'TAASHEE LINUX SER%' or email like '%@TAASHEE.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'MINDTREE' where (companyname like 'MINDTREE LTD%' or email like '%@MINDTREE.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'FULTROO' where (companyname like 'FULTROO%' or email like '%@FULTROO.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'DELLASIAPACIFIC' where (companyname like 'DELL ASIA PACIFIC%' or (email like '%@DELL.COM' and region = 'APAC'));
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'LENOVOINDIA' where (companyname like 'LENOVO%INDIA%' or (email like '%@LENOVO.COM' and country = 'IN'));
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SCSKCORP' where (companyname like 'SCSK CORP%' or email like '%@SCSK.JP');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'OGIS-RI' where (companyname like 'OGIS-RI CO%' or email like '%@OGIS-RI.CO.JP');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'FUJITSUJAPAN' where (companyname like 'FUJITSU%' and email like '%@JP.FUJITSU.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'HITACHIJAPAN' where (companyname like 'HITACHI%' or email like '%@hitachi.com') and country = 'JP';
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'NIHONUNISYS' where (companyname like 'NIHON UNISYS%' or email like '%@UNISYS.CO.JP');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'OKIJAPAN' where (companyname like 'OKI%' and email like '%@oki.com');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'HPJAPAN' where (companyname like 'HEWLETT-PACKARD JAPAN%');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'NECJAPAN' where (companyname like 'NEC CORP%' and country = 'JP') or (email like '%@%JP.NEC.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'NETONESYS' where (companyname like 'NET ONE SYS%' or email like '%@NETONE.CO.JP');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'UNIADEX' where (companyname like 'UNIADEX%' or email like '%@UNIADEX.CO.JP');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'QUALICA' where (companyname like 'QUALICA%' or email like '%@QUALICA.CO.JP');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SIOSJAPAN' where (companyname like 'SIOS TECH%' or email like '%@SIOS.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SOGOMSOFT' where (companyname like 'SOGOM SOFT%' or email like '%@SOGOMSOFT.CO.KR');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'BARUNMO' where (companyname like 'BARUNMO%' or email like '%@BARUNMO.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'MIRAEING' where (companyname like 'MIRAE ING%' or email like '%@MIRAEING.CO.KR');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'MANTECHNO' where (companyname like 'MAN TECHNOLOGY%' or email like '%@MANTECH.CO.KR');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ROCKPLACE' where (companyname like 'ROCK PLACE%' or email like '%@ROCKPLACE.CO.KR');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'ADFONTESCLOUD' where (companyname like 'ADFONTES CLOUD SYS%' or email like '%@ADCLOUDSYSTEM.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'TECHMAHINDRA' where (companyname like 'TECH MAHINDRA%' or email like '%@TECHMAHINDRA.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'COGNIZANT' where (companyname like 'COGNIZANT TECH%' or email like '%@COGNIZANT.COM');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'HCL' where (companyname like 'HCL %' or email like '%@HCL.COM');

update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'SOPRASTERIA' where (companyname like 'Sopra Steria%' or email like '%@steria-mummbert.de');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'INDRASISTEMAS' where (companyname like 'INDRA SISTEMAS%' or email like '%@indra.es' or email like '%@indracompany.com');
update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set companycode = 'TIETO' where (companyname like 'TIETO%' or email like '%@tieto.com');

update 
	lms_reporting.Students s 
	join lms_reporting.FocusGroups f 
		on f.companycode = s.companycode 
        and f.region = s.region 
set 
	s.focusgroup = f.region
where
	length(ifnull(f.companycode,'')) > 0;

update lms_reporting.Students s join lms_transactional.Companies c on s.companyid = c.companyid set focusgroup = 'EMEA SIs' where partnertype = 'Systems Integrator' and companycode in ('ACCENTURE','ATOS','HP','TCSENTREPRISES','WIPROTECH','CGI','CSC','CAPGEMINI','SOPRASTERIA','DELOITTE','INFOSYS','INDRASISTEMAS','TIETO','COGNIZANT','TECHMAHINDRA','HCL') and region = 'EMEA' and focusgroup is null;

update lms_transactional.Companies set companyname = 'Red Hat' where companyname = 'red hat';
update lms_reporting.Students set companyid = (select companyid from lms_transactional.Companies where companyname = 'Red Hat') where email like '%@redhat.com';

drop table if exists lms_reporting.StudentAccreditationDetailsProcessing;

create table lms_reporting.StudentAccreditationDetailsProcessing as
select
	s.email, concat(s.lastname ,', ', s.firstname) as fullname, s.firstname, s.lastname, c.companyname as company, 
    s.companycode, 
    ifnull(s.region,'Unknown') as region, s.subregion, s.country, 
    c.partnertype, 
    s.SalesForceContactID as sfdcid, 
    s.sumtotalid, 
    c.partnertier as tier,
    case when s.email like '%@redhat.com' or ifnull(c.companyname,'') like '%red hat%' then 'Yes' else 'No' end as redhat,
    ad.accreditationname accreditation, sa.accreditationdate as accreditationdate
from 
	lms_transactional.StudentAccreditations sa 
	join lms_reporting.Students s 
		on sa.studentid = s.studentid
        and accreditationtype = 'Active'
	join lms_transactional.Companies c
		on s.companyid = c.companyid
	join lms_transactional.AccreditationDefinitions ad
		on sa.accreditationid = ad.accreditationid;

create index idx_email on lms_reporting.StudentAccreditationDetailsProcessing (email);
create index idx_fullname on lms_reporting.StudentAccreditationDetailsProcessing (fullname);
create index idx_firstname on lms_reporting.StudentAccreditationDetailsProcessing (firstname);
create index idx_lastname on lms_reporting.StudentAccreditationDetailsProcessing (lastname);
create index idx_company on lms_reporting.StudentAccreditationDetailsProcessing (company);
create index idx_companycode on lms_reporting.StudentAccreditationDetailsProcessing (companycode);
create index idx_region on lms_reporting.StudentAccreditationDetailsProcessing (region);
create index idx_subregion on lms_reporting.StudentAccreditationDetailsProcessing (subregion);
create index idx_country on lms_reporting.StudentAccreditationDetailsProcessing (country);
create index idx_partnertype on lms_reporting.StudentAccreditationDetailsProcessing (partnertype);
create index idx_sfdcid on lms_reporting.StudentAccreditationDetailsProcessing (sfdcid);
create index idx_sumtotalid on lms_reporting.StudentAccreditationDetailsProcessing (sumtotalid);
create index idx_tier on lms_reporting.StudentAccreditationDetailsProcessing (tier);
create index idx_redhat on lms_reporting.StudentAccreditationDetailsProcessing (redhat);
create index idx_accreditation on lms_reporting.StudentAccreditationDetailsProcessing (accreditation);
create index idx_accreditationdate on lms_reporting.StudentAccreditationDetailsProcessing (accreditationdate);


drop table if exists lms_reporting.StudentCourseCompletionDetailsProcessing;

create table lms_reporting.StudentCourseCompletionDetailsProcessing as
select
	s.email, concat(s.lastname, ', ', s.firstname) as fullname, s.firstname, s.lastname, c.companyname company, s.companycode, ifnull(s.region,'Unknown') region, s.subregion, s.country, c.partnertype, s.salesforcecontactid as sfdcid, s.sumtotalid, c.partnertier tier,
    case when s.email like '%@redhat.com' or ifnull(c.companyname,'') like '%red hat%' then 'Yes' else 'No' end as redhat,
    c2.coursename, null as coursetype, cc.assessmentdate as coursedate
from 
	lms_transactional.StudentCourses cc 
	join lms_reporting.Students s 
		on cc.studentid = s.studentid
	join lms_transactional.Companies c
		on s.companyid = c.companyid
	join lms_transactional.Courses c2
		on cc.courseid = c2.courseid;

create index idx_email on lms_reporting.StudentCourseCompletionDetailsProcessing (email);
create index idx_fullname on lms_reporting.StudentCourseCompletionDetailsProcessing (fullname);
create index idx_firstname on lms_reporting.StudentCourseCompletionDetailsProcessing (firstname);
create index idx_lastname on lms_reporting.StudentCourseCompletionDetailsProcessing (lastname);
create index idx_company on lms_reporting.StudentCourseCompletionDetailsProcessing (company);
create index idx_companycode on lms_reporting.StudentCourseCompletionDetailsProcessing (companycode);
create index idx_region on lms_reporting.StudentCourseCompletionDetailsProcessing (region);
create index idx_subregion on lms_reporting.StudentCourseCompletionDetailsProcessing (subregion);
create index idx_country on lms_reporting.StudentCourseCompletionDetailsProcessing (country);
create index idx_partnertype on lms_reporting.StudentCourseCompletionDetailsProcessing (partnertype);
create index idx_sfdcid on lms_reporting.StudentCourseCompletionDetailsProcessing (sfdcid);
create index idx_sumtotalid on lms_reporting.StudentCourseCompletionDetailsProcessing (sumtotalid);
create index idx_tier on lms_reporting.StudentCourseCompletionDetailsProcessing (tier);
create index idx_redhat on lms_reporting.StudentCourseCompletionDetailsProcessing (redhat);
create index idx_coursename on lms_reporting.StudentCourseCompletionDetailsProcessing (coursename);
create index idx_coursetype on lms_reporting.StudentCourseCompletionDetailsProcessing (coursetype);
create index idx_coursedate on lms_reporting.StudentCourseCompletionDetailsProcessing (coursedate);


drop table if exists lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing;

create table lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing as 
select distinct
	s.email, concat(s.lastname, ', ', s.firstname) as fullname, s.firstname, s.lastname, c.companyname company, s.companycode, ifnull(s.region,'Unknown') as region, s.subregion, s.country, c.partnertype, s.salesforcecontactid sfdcid, s.sumtotalid, c.partnertier as tier, 
    case when s.email like '%@redhat.com' or ifnull(c.companyname,'') like '%red hat%' then 'Yes' else 'No' end as redhat, case when s.alliancecode is not null then 'Yes' else 'No' end as alliance, s.focusgroup, s.sumtotalactive,
    c2.coursename as coursename, null as coursetype, cast(cc.assessmentdate as date) as coursedate, cc.studentcourseid as completedcourseid,     
    a.accreditationname accreditation, a.specialization, a.role, a.track, cast(sa.accreditationdate as date) as accreditationdate,  
    case when a.accreditationid is not null then 1 else 0 end as AccreditationComplete,     
    concat(s.email, '-', a.accreditationid) accreditationid,
    case s.roles when 'consultant' then 'Consultant' when 'other' then 'Other' when 'sa' then 'SA' end as org
from   
	(select max(studentcourseid) studentcourseid, studentid, courseid, languageid, max(assessmentdate) assessmentdate, assessmentresult, max(assessmentscore) assessmentscore, processed from lms_transactional.StudentCourses  group by studentid, courseid, languageid, assessmentresult, processed) cc   
    join lms_transactional.Courses c2
		on cc.courseid = c2.courseid
    join lms_reporting.Students s    
		on cc.studentid = s.studentid
	join lms_transactional.Companies c
		on s.companyid = c.companyid
	left join lms_transactional.StudentAccreditations sa          
		on cc.studentid = sa.studentid
		and cc.courseid = sa.courseid 
		and accreditationtype = 'Active'
	left join lms_transactional.AccreditationDefinitions a         
		on sa.accreditationid = a.accreditationid;

alter table lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing add primary key (completedcourseid, accreditationid);

alter table lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing   
	add RoleCode char(3),  
    add SpecializationCode char(5),     
    add TrackCode char(30),     
    add SpecializationTrack char(50),  
    add AccreditationTotalFY15 int,      
    add AccreditationTotalFY16 int,     
    add AccreditationTotalFY17 int,   
    add AccreditationFiscalYear int,     
    add AccreditationFiscalQuarter int,     
    add AccreditationFiscalMonth char(7),     
    add AccreditationFiscalYearQuarter char(7),     
    add CourseCompletionFiscalYear int,     
    add CourseCompletionFiscalQuarter int,     
    add CourseCompletionFiscalMonth char(7),  
    add RegionRoleSpecializationTrack varchar(250);

update lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing set   
    AccreditationTotalFY15 = case when accreditationdate >= '2014-03-01' and accreditationdate < '2015-03-01' then 1 else 0 end,  
    AccreditationTotalFY16 = case when accreditationdate >= '2015-03-01' and accreditationdate < '2016-03-01' then 1 else 0 end,
	AccreditationTotalFY17 = case when accreditationdate >= '2016-03-01' and accreditationdate < '2017-03-01' then 1 else 0 end,  
    AccreditationFiscalYear = case when accreditationdate >= '2013-03-01' and accreditationdate < '2014-03-01' then 2014 when accreditationdate >= '2014-03-01' and accreditationdate < '2015-03-01' then 2015 when accreditationdate >= '2015-03-01' and accreditationdate < '2016-03-01' then 2016 when accreditationdate >= '2016-03-01' and accreditationdate < '2017-03-01' then 2017 end,     
    AccreditationFiscalQuarter = case when month(accreditationdate) in (3,4,5) then 1 when month(accreditationdate) in (6,7,8) then 2 when month(accreditationdate) in (9,10,11) then 3 when month(accreditationdate) in (12,1,2) then 4 end,     
    AccreditationFiscalMonth = case when accreditationdate is not null then concat(year(accreditationdate),'-',right(concat('0',cast(month(accreditationdate) as char)),2)) else null end, 
    CourseCompletionFiscalQuarter = case when month(coursedate) in (3,4,5) then 1 when month(coursedate) in (6,7,8) then 2 when month(coursedate) in (9,10,11) then 3 when month(coursedate) in (12,1,2) then 4 end,     
    CourseCompletionFiscalMonth = case when coursedate is not null then concat(year(coursedate),'-',right(concat('0',cast(month(coursedate) as char)),2)) else null end,  RoleCode = case role when 'Sales' then 'S' when 'Delivery' then 'D' when 'Sales Engineer' then 'SE' when 'Advanced Training' then 'AT' end,  
    SpecializationCode = case specialization when 'Data Center Infrastructure' then 'DCI' when 'Middleware Solutions' then 'MW' when 'Cloud Infrastructure' then 'CI' when 'Advanced Training' then 'AT' else specialization end,  
    TrackCode = case track    when 'Business Process Automation' then 'BPA'         when 'Cloud Management' then 'Cloud Mgmt'         when 'Enterprise Messaging with jBoss A-MQ' then 'Ent Messaging'         when 'IAAS' then 'IaaS'         when 'Middleware Application Development' then 'MW App Dev'         when 'Middleware Integration Services' then 'MW Int Services'         when 'Middleware Migration' then 'MW Migration'         when 'Mobile Development' then 'Mobile Dev'         when 'PAAS' then 'PaaS'         when 'PAAS Development' then 'Paas Dev'         when 'Platform' then 'Platform'         when 'Platform Migration' then 'Platform Migration'         when 'RHEL for SAP Hana' then 'SAP Hana'         when 'RHEL Atomic Host and Containers' then 'RHEL Atomic'                 when 'Storage' then 'Storage'         when 'Virtualization' then 'Virtualization'  when 'ELECTIVE' then 'Elective' when 'MOBILITY' then 'Mobility' when 'ADVANCED CLOUD MANAGEMENT' then 'Adv Cloud Mgmt' end,  
    RegionRoleSpecializationTrack = concat(Region, ' - ', role, ' - ', specialization, ' - ', track);
    
update lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing set partnertype = 'Unknown' where partnertype not in ('Corporate Reseller','Distributor','DMR','ISV','OEM','Reseller','Service Provider','SI','Solution Provider','Systems Integrator','Training','VAR') or partnertype is null;
update lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing set tier = 'Unknown' where tier is null;
update lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing set specializationtrack = concat(specializationcode, ' - ', trackcode);
update lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing set AccreditationFiscalYearQuarter = concat(AccreditationFiscalYear,'-',AccreditationFiscalQuarter);

create index idx_email on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (email);
create index idx_fullname on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (fullname);
create index idx_firstname on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (firstname);
create index idx_lastname on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (lastname);
create index idx_company on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (company);
create index idx_companycode on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (companycode);
create index idx_region on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (region);
create index idx_subregion on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (subregion);
create index idx_country on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (country);
create index idx_regionsubregioncountrycompany on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (region, subregion, country, company);
create index idx_partnertype on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (partnertype);
create index idx_sfdcid on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (sfdcid);
create index idx_sumtotalid on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (sumtotalid);
create index idx_tier on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (tier);
create index idx_redhat on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (redhat);
create index idx_alliance on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (alliance);
create index idx_focusgroup on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (focusgroup);
create index idx_sumtotalactive on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (sumtotalactive);
create index idx_coursename on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (coursename);
create index idx_coursetype on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (coursetype);
create index idx_coursedate on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (coursedate);
create index idx_completedcourseid on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (completedcourseid);
create index idx_accreditation on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (accreditation);
create index idx_role on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (role);
create index idx_specialization on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (specialization);
create index idx_track on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (track);
create index idx_specializationtrack on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (specializationtrack);
create index idx_rolespecializationtrack on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (role, specialization, track);
create index idx_accreditationdate on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (accreditationdate);
create index idx_accreditationcomplete on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (accreditationcomplete);
create index idx_accreditationid on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (accreditationid);
create index idx_AccreditationFiscalYear on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (AccreditationFiscalYear);
create index idx_AccreditationFiscalQuarter on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (AccreditationFiscalQuarter);
create index idx_AccreditationFiscalMonth on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (AccreditationFiscalMonth);
create index idx_AccreditationFiscalYQM on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (AccreditationFiscalYear,AccreditationFiscalQuarter,AccreditationFiscalMonth);
create index idx_CourseCompletionFiscalYear on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (CourseCompletionFiscalYear);
create index idx_CourseCompletionFiscalQuarter on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (CourseCompletionFiscalQuarter);
create index idx_CourseCompletionFiscalMonth on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (CourseCompletionFiscalMonth);
create index idx_CourseCompletionFiscalYQM on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (CourseCompletionFiscalYear,CourseCompletionFiscalQuarter,CourseCompletionFiscalMonth);
create index idx_Org on lms_reporting.StudentCourseCompletionAccreditationDetailsProcessing (org);

drop table if exists lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing;

create table lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing as 
select distinct
	s.email, concat(s.lastname, ', ', s.firstname) as fullname, s.firstname, s.lastname, c.companyname company, s.companycode, ifnull(s.region,'Unknown') as region, s.subregion, s.country, c.partnertype, s.salesforcecontactid sfdcid, s.sumtotalid, c.partnertier as tier, 
    case when s.email like '%@redhat.com' or ifnull(c.companyname,'') like '%red hat%' then 'Yes' else 'No' end as redhat, case when s.alliancecode is not null then 'Yes' else 'No' end as alliance, s.focusgroup, s.sumtotalactive,
    c2.coursename as coursename, null as coursetype, cast(cc.assessmentdate as date) as coursedate, cc.studentcourseid as completedcourseid,     
    a.accreditationname accreditation, a.specialization, a.role, a.track, cast(sa.accreditationdate as date) as accreditationdate,  
    case when a.accreditationid is not null then 1 else 0 end as AccreditationComplete,     
    concat(s.email, '-', a.accreditationid) accreditationid,
    case s.roles when 'consultant' then 'Consultant' when 'other' then 'Other' when 'sa' then 'SA' end as org
from   
	(select max(studentcourseid) studentcourseid, studentid, courseid, languageid, max(assessmentdate) assessmentdate, assessmentresult, max(assessmentscore) assessmentscore, processed from lms_transactional.StudentCourses  group by studentid, courseid, languageid, assessmentresult, processed) cc   
    join lms_transactional.Courses c2
		on cc.courseid = c2.courseid
    join lms_reporting.Students s    
		on cc.studentid = s.studentid
	join lms_transactional.Companies c
		on s.companyid = c.companyid
	left join lms_transactional.StudentAccreditations sa          
		on cc.studentid = sa.studentid
		and cc.courseid = sa.courseid 
		and accreditationtype = 'Expired'
	left join lms_transactional.AccreditationDefinitions a         
		on sa.accreditationid = a.accreditationid;

alter table lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing add primary key (completedcourseid, accreditationid);

alter table lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing   
	add RoleCode char(3),  
    add SpecializationCode char(5),     
    add TrackCode char(30),     
    add SpecializationTrack char(50),  
    add AccreditationTotalFY15 int,      
    add AccreditationTotalFY16 int,     
    add AccreditationTotalFY17 int,   
    add AccreditationFiscalYear int,     
    add AccreditationFiscalQuarter int,     
    add AccreditationFiscalMonth char(7),     
    add AccreditationFiscalYearQuarter char(7),     
    add CourseCompletionFiscalYear int,     
    add CourseCompletionFiscalQuarter int,     
    add CourseCompletionFiscalMonth char(7),  
    add RegionRoleSpecializationTrack varchar(250);

update lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing set   
    AccreditationTotalFY15 = case when accreditationdate >= '2014-03-01' and accreditationdate < '2015-03-01' then 1 else 0 end,  
    AccreditationTotalFY16 = case when accreditationdate >= '2015-03-01' and accreditationdate < '2016-03-01' then 1 else 0 end,
	AccreditationTotalFY17 = case when accreditationdate >= '2016-03-01' and accreditationdate < '2017-03-01' then 1 else 0 end,  
    AccreditationFiscalYear = case when accreditationdate >= '2013-03-01' and accreditationdate < '2014-03-01' then 2014 when accreditationdate >= '2014-03-01' and accreditationdate < '2015-03-01' then 2015 when accreditationdate >= '2015-03-01' and accreditationdate < '2016-03-01' then 2016 when accreditationdate >= '2016-03-01' and accreditationdate < '2017-03-01' then 2017 end,     
    AccreditationFiscalQuarter = case when month(accreditationdate) in (3,4,5) then 1 when month(accreditationdate) in (6,7,8) then 2 when month(accreditationdate) in (9,10,11) then 3 when month(accreditationdate) in (12,1,2) then 4 end,     
    AccreditationFiscalMonth = case when accreditationdate is not null then concat(year(accreditationdate),'-',right(concat('0',cast(month(accreditationdate) as char)),2)) else null end, 
    CourseCompletionFiscalQuarter = case when month(coursedate) in (3,4,5) then 1 when month(coursedate) in (6,7,8) then 2 when month(coursedate) in (9,10,11) then 3 when month(coursedate) in (12,1,2) then 4 end,     
    CourseCompletionFiscalMonth = case when coursedate is not null then concat(year(coursedate),'-',right(concat('0',cast(month(coursedate) as char)),2)) else null end,  RoleCode = case role when 'Sales' then 'S' when 'Delivery' then 'D' when 'Sales Engineer' then 'SE' when 'Advanced Training' then 'AT' end,  
    SpecializationCode = case specialization when 'Data Center Infrastructure' then 'DCI' when 'Middleware Solutions' then 'MW' when 'Cloud Infrastructure' then 'CI' when 'Advanced Training' then 'AT' else specialization end,  
    TrackCode = case track    when 'Business Process Automation' then 'BPA'         when 'Cloud Management' then 'Cloud Mgmt'         when 'Enterprise Messaging with jBoss A-MQ' then 'Ent Messaging'         when 'IAAS' then 'IaaS'         when 'Middleware Application Development' then 'MW App Dev'         when 'Middleware Integration Services' then 'MW Int Services'         when 'Middleware Migration' then 'MW Migration'         when 'Mobile Development' then 'Mobile Dev'         when 'PAAS' then 'PaaS'         when 'PAAS Development' then 'Paas Dev'         when 'Platform' then 'Platform'         when 'Platform Migration' then 'Platform Migration'         when 'RHEL for SAP Hana' then 'SAP Hana'         when 'RHEL Atomic Host and Containers' then 'RHEL Atomic'                 when 'Storage' then 'Storage'         when 'Virtualization' then 'Virtualization' end,  
    RegionRoleSpecializationTrack = concat(Region, ' - ', role, ' - ', specialization, ' - ', track);
    
update lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing set partnertype = 'Unknown' where partnertype not in ('Corporate Reseller','Distributor','DMR','ISV','OEM','Reseller','Service Provider','SI','Solution Provider','Systems Integrator','Training','VAR') or partnertype is null;
update lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing set tier = 'Unknown' where tier is null;
update lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing set specializationtrack = concat(specializationcode, ' - ', trackcode);
update lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing set AccreditationFiscalYearQuarter = concat(AccreditationFiscalYear,'-',AccreditationFiscalQuarter);

create index idx_email on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (email);
create index idx_fullname on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (fullname);
create index idx_firstname on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (firstname);
create index idx_lastname on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (lastname);
create index idx_company on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (company);
create index idx_companycode on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (companycode);
create index idx_region on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (region);
create index idx_subregion on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (subregion);
create index idx_country on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (country);
create index idx_regionsubregioncountrycompany on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (region, subregion, country, company);
create index idx_partnertype on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (partnertype);
create index idx_sfdcid on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (sfdcid);
create index idx_sumtotalid on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (sumtotalid);
create index idx_tier on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (tier);
create index idx_redhat on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (redhat);
create index idx_alliance on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (alliance);
create index idx_focusgroup on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (focusgroup);
create index idx_sumtotalactive on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (sumtotalactive);
create index idx_coursename on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (coursename);
create index idx_coursetype on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (coursetype);
create index idx_coursedate on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (coursedate);
create index idx_completedcourseid on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (completedcourseid);
create index idx_accreditation on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (accreditation);
create index idx_role on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (role);
create index idx_specialization on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (specialization);
create index idx_track on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (track);
create index idx_specializationtrack on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (specializationtrack);
create index idx_rolespecializationtrack on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (role, specialization, track);
create index idx_accreditationdate on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (accreditationdate);
create index idx_accreditationcomplete on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (accreditationcomplete);
create index idx_accreditationid on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (accreditationid);
create index idx_AccreditationFiscalYear on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (AccreditationFiscalYear);
create index idx_AccreditationFiscalQuarter on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (AccreditationFiscalQuarter);
create index idx_AccreditationFiscalMonth on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (AccreditationFiscalMonth);
create index idx_AccreditationFiscalYQM on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (AccreditationFiscalYear,AccreditationFiscalQuarter,AccreditationFiscalMonth);
create index idx_CourseCompletionFiscalYear on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (CourseCompletionFiscalYear);
create index idx_CourseCompletionFiscalQuarter on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (CourseCompletionFiscalQuarter);
create index idx_CourseCompletionFiscalMonth on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (CourseCompletionFiscalMonth);
create index idx_CourseCompletionFiscalYQM on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (CourseCompletionFiscalYear,CourseCompletionFiscalQuarter,CourseCompletionFiscalMonth);
create index idx_Org on lms_reporting.StudentCourseCompletionAccreditationDetailsExpiredProcessing (org);

drop table if exists lms_reporting.LastUpdate;
create table lms_reporting.LastUpdate as select cast(now() as char) as LastUpdate;

drop table if exists lms_reporting.SummaryProcessing;
create table lms_reporting.SummaryProcessing as select * from lms_reporting.Summary;

drop table if exists `lms_reporting`.`StudentAccreditationDetailsTemp`;
ALTER TABLE `lms_reporting`.`StudentAccreditationDetails` 
RENAME TO  `lms_reporting`.`StudentAccreditationDetailsTemp`;
ALTER TABLE `lms_reporting`.`StudentAccreditationDetailsProcessing` 
RENAME TO  `lms_reporting`.`StudentAccreditationDetails`;
drop table if exists `lms_reporting`.`StudentAccreditationDetailsTemp`;

drop table if exists `lms_reporting`.`StudentCourseCompletionDetailsTemp`;
ALTER TABLE `lms_reporting`.`StudentCourseCompletionDetails` 
RENAME TO  `lms_reporting`.`StudentCourseCompletionDetailsTemp`;
ALTER TABLE `lms_reporting`.`StudentCourseCompletionDetailsProcessing` 
RENAME TO  `lms_reporting`.`StudentCourseCompletionDetails`;
drop table if exists `lms_reporting`.`StudentCourseCompletionDetailsTemp`;

drop table if exists `lms_reporting`.`StudentCourseCompletionAccreditationDetailsTemp`;
ALTER TABLE `lms_reporting`.`StudentCourseCompletionAccreditationDetails` 
RENAME TO  `lms_reporting`.`StudentCourseCompletionAccreditationDetailsTemp`;
ALTER TABLE `lms_reporting`.`StudentCourseCompletionAccreditationDetailsProcessing` 
RENAME TO  `lms_reporting`.`StudentCourseCompletionAccreditationDetails`;
drop table if exists `lms_reporting`.`StudentCourseCompletionAccreditationDetailsTemp`;

drop table if exists `lms_reporting`.`StudentCourseCompletionAccreditationDetailsExpiredTemp`;
ALTER TABLE `lms_reporting`.`StudentCourseCompletionAccreditationDetailsExpired` 
RENAME TO  `lms_reporting`.`StudentCourseCompletionAccreditationDetailsExpiredTemp`;
ALTER TABLE `lms_reporting`.`StudentCourseCompletionAccreditationDetailsExpiredProcessing` 
RENAME TO  `lms_reporting`.`StudentCourseCompletionAccreditationDetailsExpired`;
drop table if exists `lms_reporting`.`StudentCourseCompletionAccreditationDetailsExpiredTemp`;

-- update StudentAccreditations set accreditationtype = 'Active' where accreditationdate >= '2014-12-01' and accreditationtype = 'Expired';
update StudentAccreditations set accreditationtype = 'Active' where accreditationdate >= '2015-01-01' and accreditationtype = 'Expired';

end