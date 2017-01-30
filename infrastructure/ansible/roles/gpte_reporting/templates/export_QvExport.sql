DELIMITER $$
CREATE DEFINER=`lms`@`%` PROCEDURE `export_QvExport`()
begin

select * from lms_transactional.QvExport 
INTO OUTFILE '/opt/shared/qvexport.csv' 
FIELDS ENCLOSED BY '' 
TERMINATED BY '\t' 
ESCAPED BY '' 
LINES TERMINATED BY '\R\N';

end$$
DELIMITER ;
