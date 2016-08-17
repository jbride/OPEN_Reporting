-- MySQL dump 10.13  Distrib 5.6.24, for Win64 (x86_64)
--
-- Host: docker1.ose.opentlc.com    Database: lms_transactional
-- ------------------------------------------------------
-- Server version	5.5.47-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `AccreditationDefinitions`
--

use lms_transactional;

DROP TABLE IF EXISTS `AccreditationDefinitions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AccreditationDefinitions` (
  `AccreditationID` int(11) NOT NULL AUTO_INCREMENT,
  `AccreditationName` varchar(150) NOT NULL,
  `Role` varchar(20) DEFAULT NULL,
  `Specialization` varchar(32) DEFAULT NULL,
  `Track` varchar(42) DEFAULT NULL,
  `Proficiency` varchar(30) DEFAULT NULL,
  `AccreditationExportID` varchar(30) DEFAULT NULL,
  `CreateDate` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`AccreditationID`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Companies`
--

DROP TABLE IF EXISTS `Companies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Companies` (
  `CompanyID` int(11) NOT NULL AUTO_INCREMENT,
  `CompanyName` varchar(100) NOT NULL,
  `PartnerType` varchar(50) DEFAULT NULL,
  `PartnerTier` varchar(50) DEFAULT NULL,
  `LdapID` varchar(100) DEFAULT NULL,
  `SfdcID` varchar(18) DEFAULT NULL,
  `CreateDate` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`CompanyID`),
  UNIQUE KEY `IDX_CompanyName` (`CompanyName`),
  KEY `IDX_PartnerType` (`PartnerType`),
  KEY `IDX_PartnerTier` (`PartnerTier`),
  KEY `IDX_LdapID` (`LdapID`)
) ENGINE=InnoDB AUTO_INCREMENT=26383 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CourseMappings`
--

DROP TABLE IF EXISTS `CourseMappings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CourseMappings` (
  `OldCourseCode` varchar(50) NOT NULL,
  `OldCourseId` varchar(50) DEFAULT NULL,
  `CourseID` varchar(50) NOT NULL,
  `Source` varchar(20) DEFAULT NULL,
  `CreateDate` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`OldCourseCode`),
  KEY `IDX_OldCourseId` (`OldCourseId`),
  KEY `IDX_Source` (`Source`),
  KEY `FK_CourseMappings_Courses` (`CourseID`),
  CONSTRAINT `FK_CourseMappings_Courses` FOREIGN KEY (`CourseID`) REFERENCES `Courses` (`CourseID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Courses`
--

DROP TABLE IF EXISTS `Courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Courses` (
  `CourseID` varchar(50) NOT NULL DEFAULT '',
  `CourseName` varchar(200) DEFAULT NULL,
  `CreateDate` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`CourseID`),
  KEY `IDX_CourseName` (`CourseName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Languages`
--

DROP TABLE IF EXISTS `Languages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Languages` (
  `LanguageID` varchar(5) NOT NULL,
  `LanguageName` varchar(100) DEFAULT NULL,
  `CreateDate` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`LanguageID`),
  UNIQUE KEY `IDX_LanguageName` (`LanguageName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StudentAccreditations`
--

DROP TABLE IF EXISTS `StudentAccreditations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `StudentAccreditations` (
  `StudentID` int(11) NOT NULL,
  `AccreditationID` int(11) NOT NULL,
  `AccreditationDate` datetime NOT NULL,
  `AccreditationType` varchar(20) NOT NULL,
  `CourseID` varchar(50) NOT NULL,
  `Processed` tinyint(1) NOT NULL DEFAULT '0',
  `RuleFired` varchar(100) NOT NULL,
  `CreateDate` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`StudentID`,`AccreditationID`),
  KEY `IDX_StudentAccreditations_Students` (`StudentID`),
  KEY `IDX_StudentAccreditations_AccreditationDefinitions` (`AccreditationID`),
  KEY `IDX_StudentAccreditations_AccreditationDate` (`AccreditationDate`),
  KEY `IDX_StudentAccreditations_StudentID_CourseID` (`StudentID`,`CourseID`),
  KEY `IDX_StudentAccreditations_Processed` (`Processed`),
  KEY `IDX_StudentAccreditations_RuleFired` (`RuleFired`),
  CONSTRAINT `FK_StudentAccreditations_AccreditationDefinitions` FOREIGN KEY (`AccreditationID`) REFERENCES `AccreditationDefinitions` (`AccreditationID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_StudentAccreditations_Students` FOREIGN KEY (`StudentID`) REFERENCES `Students` (`StudentID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_StudentAccreditations_StudentCourses` FOREIGN KEY (`StudentID`, `CourseID`) REFERENCES `StudentCourses` (`StudentID`, `CourseID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StudentCourses`
--

DROP TABLE IF EXISTS `StudentCourses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `StudentCourses` (
  `StudentCourseID` int(11) NOT NULL AUTO_INCREMENT,
  `StudentID` int(11) NOT NULL,
  `CourseID` varchar(50) NOT NULL,
  `LanguageID` varchar(5) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `AssessmentDate` datetime NOT NULL,
  `AssessmentResult` varchar(5) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `AssessmentScore` tinyint(4) NOT NULL DEFAULT '100',
  `Processed` tinyint(1) NOT NULL DEFAULT '0',
  `CreateDate` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`StudentCourseID`),
  KEY `IDX_StudentCourses_Students` (`StudentID`),
  KEY `IDX_StudentCourses_Courses` (`CourseID`),
  KEY `IDX_StudentCourses_Languages` (`LanguageID`),
  KEY `IDX_StudentCourses_StudentID_CourseID` (`StudentID`,`CourseID`),
  KEY `IDX_StudentCourses_Processed` (`Processed`),
  CONSTRAINT `FK_StudentCourses_Languages` FOREIGN KEY (`LanguageID`) REFERENCES `Languages` (`LanguageID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_StudentCourses_Students` FOREIGN KEY (`StudentID`) REFERENCES `Students` (`StudentID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_StudentCourses_Courses` FOREIGN KEY (`CourseID`) REFERENCES `Courses` (`CourseID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=141070 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StudentMappings`
--

DROP TABLE IF EXISTS `StudentMappings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `StudentMappings` (
  `OldEmail` varchar(100) NOT NULL DEFAULT '',
  `NewEmail` varchar(100) DEFAULT NULL,
  `StudentID` int(11) DEFAULT NULL,
  `CreateDate` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`OldEmail`),
  KEY `IDX_NewEmail` (`NewEmail`),
  KEY `IDX_StudentID` (`StudentID`),
  CONSTRAINT `FK_StudentMappings_Students` FOREIGN KEY (`StudentID`) REFERENCES `Students` (`StudentID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Students`
--

DROP TABLE IF EXISTS `Students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Students` (
  `StudentID` int(11) NOT NULL AUTO_INCREMENT,
  `Email` varchar(100) NOT NULL,
  `FirstName` varchar(50) DEFAULT NULL,
  `LastName` varchar(50) DEFAULT NULL,
  `CompanyID` int(11) NOT NULL,
  `Region` varchar(8) DEFAULT NULL,
  `SubRegion` varchar(20) DEFAULT NULL,
  `Country` char(2) DEFAULT NULL,
  `Roles` varchar(40) DEFAULT NULL,
  `SalesForceContactID` varchar(15) DEFAULT NULL,
  `SalesForceActive` char(3) DEFAULT NULL,
  `SumTotalID` varchar(18) DEFAULT NULL,
  `SumTotalActive` char(3) DEFAULT NULL,
  `SkillsbaseStatus` tinyint(1) NOT NULL DEFAULT '0',
  `IpaStatus` tinyint(1) NOT NULL DEFAULT '0',
  `ActivationDate` datetime DEFAULT NULL,
  `DeActivationDate` datetime DEFAULT NULL,
  `CreateDate` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`StudentID`),
  KEY `IDX_Email` (`Email`),
  KEY `IDX_FirstName` (`FirstName`),
  KEY `IDX_LastName` (`LastName`),
  KEY `IDX_CompanyID` (`CompanyID`),
  KEY `IDX_Region` (`Region`),
  KEY `IDX_SubRegion` (`SubRegion`),
  KEY `IDX_Country` (`Country`),
  KEY `IDX_Roles` (`Roles`),
  KEY `IDX_SalesForceContactID` (`SalesForceContactID`),
  KEY `IDX_SalesForceActive` (`SalesForceActive`),
  KEY `IDX_SumTotalID` (`SumTotalID`),
  KEY `IDX_SumTotalActive` (`SumTotalActive`),
  KEY `IDX_SkillsbaseStatus` (`SkillsbaseStatus`),
  KEY `IDX_IpaStatus` (`IpaStatus`),
  KEY `IDX_ActivationDate` (`ActivationDate`),
  KEY `IDX_DeActivationDate` (`DeActivationDate`),
  CONSTRAINT `FK_Students_Companies` FOREIGN KEY (`CompanyID`) REFERENCES `Companies` (`CompanyID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=42767 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-03-25 11:11:16
