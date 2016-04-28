DROP DATABASE IF EXISTS `ipa` ;
CREATE SCHEMA `ipa` ;

USE `ipa`;

CREATE TABLE `user_role` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `LMS_USER_PRIMARY_JOB` varchar(64) DEFAULT NULL,
  `LDAP_ROLE` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `ipa`.`user_role` (`LMS_USER_PRIMARY_JOB`, `LDAP_ROLE`) VALUES ('Sales / Data Center Infrastructure', 'sales');
INSERT INTO `ipa`.`user_role` (`LMS_USER_PRIMARY_JOB`, `LDAP_ROLE`) VALUES ('Sales Engineer / Data Center Infrastructure', 'sa');
INSERT INTO `ipa`.`user_role` (`LMS_USER_PRIMARY_JOB`, `LDAP_ROLE`) VALUES ('Delivery / Data Center Infrastructure', 'consultant');
INSERT INTO `ipa`.`user_role` (`LMS_USER_PRIMARY_JOB`, `LDAP_ROLE`) VALUES ('Sales / Cloud Infrastructure', 'sales');
INSERT INTO `ipa`.`user_role` (`LMS_USER_PRIMARY_JOB`, `LDAP_ROLE`) VALUES ('Sales Engineer / Cloud Infrastructure', 'sa');
INSERT INTO `ipa`.`user_role` (`LMS_USER_PRIMARY_JOB`, `LDAP_ROLE`) VALUES ('Delivery / Cloud Infrastructure', 'consultant');
INSERT INTO `ipa`.`user_role` (`LMS_USER_PRIMARY_JOB`, `LDAP_ROLE`) VALUES ('Sales / Middleware Solutions', 'sales');
INSERT INTO `ipa`.`user_role` (`LMS_USER_PRIMARY_JOB`, `LDAP_ROLE`) VALUES ('Sales Engineer / Middleware Solutions', 'sa');
INSERT INTO `ipa`.`user_role` (`LMS_USER_PRIMARY_JOB`, `LDAP_ROLE`) VALUES ('Delivery / Middleware Solutions', 'consultant');
