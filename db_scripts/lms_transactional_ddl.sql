-- MySQL dump 10.14  Distrib 5.5.52-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: lms_transactional
-- ------------------------------------------------------
-- Server version	5.5.52-MariaDB

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
-- Table structure for table `AD`
--

DROP TABLE IF EXISTS `AD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AD` (
  `accreditationname` varchar(150) NOT NULL,
  `role` varchar(20) DEFAULT NULL,
  `specialization` varchar(32) DEFAULT NULL,
  `track` varchar(42) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `AccreditationDefinitions`
--

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
  `CreateDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`AccreditationID`)
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=latin1;
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
  `SfdcId` varchar(18) DEFAULT NULL,
  `CreateDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`CompanyID`),
  UNIQUE KEY `IDX_CompanyName` (`CompanyName`),
  KEY `IDX_PartnerType` (`PartnerType`),
  KEY `IDX_PartnerTier` (`PartnerTier`)
) ENGINE=InnoDB AUTO_INCREMENT=30280 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Countries`
--

DROP TABLE IF EXISTS `Countries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Countries` (
  `CountryID` varchar(2) NOT NULL,
  `CountryName` varchar(100) DEFAULT NULL,
  `CreateDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`CountryID`),
  KEY `IDX_CountryName` (`CountryName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CourseIdMappings`
--

DROP TABLE IF EXISTS `CourseIdMappings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CourseIdMappings` (
  `OldCourseId` varchar(50) NOT NULL DEFAULT '',
  `NewCourseId` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`OldCourseId`),
  KEY `IDX_NewCourseId` (`NewCourseId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CourseIds_20160901`
--

DROP TABLE IF EXISTS `CourseIds_20160901`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CourseIds_20160901` (
  `CourseName` varchar(200) DEFAULT NULL,
  `OldCourseID` varchar(50) NOT NULL DEFAULT '',
  `NewCourseId` varchar(50) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CourseMappings`
--

DROP TABLE IF EXISTS `CourseMappings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CourseMappings` (
  `PrunedCourseID` varchar(100) NOT NULL,
  `CourseID` varchar(50) NOT NULL,
  `CreateDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY `IDX_NewCourseCode` (`PrunedCourseID`),
  KEY `FK_CourseMappings_Courses` (`CourseID`),
  CONSTRAINT `FK_CourseMappings_Courses` FOREIGN KEY (`CourseID`) REFERENCES `Courses` (`CourseID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CourseMappingsTemp`
--

DROP TABLE IF EXISTS `CourseMappingsTemp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CourseMappingsTemp` (
  `courseid` varchar(50) NOT NULL DEFAULT '',
  `coursetype` varchar(4) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `activitycode` varchar(150) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CourseMappings_20160622`
--

DROP TABLE IF EXISTS `CourseMappings_20160622`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CourseMappings_20160622` (
  `OldCourseCode` varchar(50) NOT NULL,
  `NewCourseCode` varchar(50) DEFAULT NULL,
  `CourseID` varchar(50) NOT NULL,
  `Source` varchar(20) DEFAULT NULL,
  `CreateDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CourseMappings_20160901`
--

DROP TABLE IF EXISTS `CourseMappings_20160901`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CourseMappings_20160901` (
  `OldCourseCode` varchar(100) NOT NULL,
  `OldCourseId` varchar(100) DEFAULT NULL,
  `CourseID` varchar(50) NOT NULL,
  `Source` varchar(20) DEFAULT NULL,
  `CreateDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'
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
  `CreateDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`CourseID`),
  UNIQUE KEY `CourseName` (`CourseName`),
  KEY `IDX_CourseName` (`CourseName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CoursesBak`
--

DROP TABLE IF EXISTS `CoursesBak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CoursesBak` (
  `CourseID` varchar(50) NOT NULL DEFAULT '',
  `CourseName` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Courses_20160513`
--

DROP TABLE IF EXISTS `Courses_20160513`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Courses_20160513` (
  `CourseID` varchar(50) NOT NULL DEFAULT '',
  `CourseName` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Courses_20160901`
--

DROP TABLE IF EXISTS `Courses_20160901`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Courses_20160901` (
  `CourseID` varchar(50) NOT NULL DEFAULT '',
  `CourseName` varchar(200) DEFAULT NULL,
  `CreateDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'
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
  `CreateDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`LanguageID`),
  UNIQUE KEY `IDX_LanguageName` (`LanguageName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Lookup`
--

DROP TABLE IF EXISTS `Lookup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Lookup` (
  `studentid` tinyint(4) NOT NULL,
  `email` tinyint(4) NOT NULL,
  `accreditationid` tinyint(4) NOT NULL,
  `accreditationname` tinyint(4) NOT NULL,
  `accreditationdate` tinyint(4) NOT NULL,
  `accreditationtype` tinyint(4) NOT NULL,
  `courseid` tinyint(4) NOT NULL,
  `coursename` tinyint(4) NOT NULL,
  `assessmentdate` tinyint(4) NOT NULL,
  `lastname` tinyint(4) NOT NULL,
  `firstname` tinyint(4) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NewMappings_20161201`
--

DROP TABLE IF EXISTS `NewMappings_20161201`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NewMappings_20161201` (
  `OldCourseCode` varchar(30) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `CourseId` varchar(27) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `OldCourseId` varchar(50) DEFAULT NULL,
  `flag` varchar(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QvExport`
--

DROP TABLE IF EXISTS `QvExport`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QvExport` (
  `Month` varchar(8) CHARACTER SET utf8 DEFAULT NULL,
  `Year` int(4) DEFAULT NULL,
  `FY` varchar(5) CHARACTER SET utf8 DEFAULT NULL,
  `Full Name` varchar(102) DEFAULT NULL,
  `companyname` varchar(100),
  `User Primary Job` varchar(40) DEFAULT NULL,
  `User Primary Organization` varchar(50) DEFAULT NULL,
  `User Primary Domain` varchar(8) DEFAULT NULL,
  `Country Code` char(2) DEFAULT NULL,
  `Email` varchar(100) NOT NULL,
  `Code` varchar(50) NOT NULL DEFAULT '',
  `Start Date` datetime,
  `End Date` datetime,
  `RedHat` varchar(3) CHARACTER SET utf8 DEFAULT NULL,
  `Is Certification` varchar(3) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `In Progress` varchar(3) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `Role` varchar(20) DEFAULT NULL,
  `Skills Track` varchar(42) DEFAULT NULL,
  `Specialization` varchar(32) DEFAULT NULL,
  `AccreditationName` varchar(150),
  `Geo` varchar(16) DEFAULT NULL,
  `Channel Mix` varchar(6) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `Split Business Unit` varchar(4) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `tier` varchar(50) DEFAULT NULL,
  `alliancecode` varchar(15) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QvExportMarch`
--

DROP TABLE IF EXISTS `QvExportMarch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QvExportMarch` (
  `Month` varchar(8) CHARACTER SET utf8 DEFAULT NULL,
  `Year` int(4) DEFAULT NULL,
  `FY` varchar(5) CHARACTER SET utf8 DEFAULT NULL,
  `Full Name` varchar(102) DEFAULT NULL,
  `companyname` varchar(100),
  `User Primary Job` varchar(40) DEFAULT NULL,
  `User Primary Organization` varchar(50) DEFAULT NULL,
  `User Primary Domain` varchar(8) DEFAULT NULL,
  `Country Code` char(2) DEFAULT NULL,
  `Email` varchar(100) NOT NULL,
  `Code` varchar(50) NOT NULL DEFAULT '',
  `Start Date` datetime,
  `End Date` datetime,
  `RedHat` varchar(3) CHARACTER SET utf8 DEFAULT NULL,
  `Is Certification` varchar(3) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `In Progress` varchar(3) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `Role` varchar(20) DEFAULT NULL,
  `Skills Track` varchar(42) DEFAULT NULL,
  `Specialization` varchar(32) DEFAULT NULL,
  `AccreditationName` varchar(150),
  `Geo` varchar(16) DEFAULT NULL,
  `Channel Mix` varchar(6) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `Split Business Unit` varchar(4) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `tier` varchar(50) DEFAULT NULL,
  `alliancecode` varchar(15) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
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
  `RuleFired` varchar(200) DEFAULT NULL,
  `CreateDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`StudentID`,`AccreditationID`),
  KEY `IDX_StudentAccreditations_Students` (`StudentID`),
  KEY `IDX_StudentAccreditations_AccreditationDefinitions` (`AccreditationID`),
  KEY `IDX_StudentAccreditations_AccreditationDate` (`AccreditationDate`),
  KEY `IDX_StudentAccreditations_StudentID_CourseID` (`StudentID`,`CourseID`),
  KEY `IDX_StudentAccreditations_Processed` (`Processed`),
  KEY `IDX_StudentAccreditations_RuleFired` (`RuleFired`),
  CONSTRAINT `FK_StudentAccreditations_AccreditationDefinitions` FOREIGN KEY (`AccreditationID`) REFERENCES `AccreditationDefinitions` (`AccreditationID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_StudentAccreditations_StudentCourses` FOREIGN KEY (`StudentID`, `CourseID`) REFERENCES `StudentCourses` (`StudentID`, `CourseID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_StudentAccreditations_Students` FOREIGN KEY (`StudentID`) REFERENCES `Students` (`StudentID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StudentAccreditationsDrools`
--

DROP TABLE IF EXISTS `StudentAccreditationsDrools`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `StudentAccreditationsDrools` (
  `StudentID` int(11) NOT NULL,
  `AccreditationID` int(11) NOT NULL,
  `AccreditationDate` datetime NOT NULL,
  `AccreditationType` varchar(20) NOT NULL,
  `CourseID` varchar(50) NOT NULL,
  `Processed` tinyint(1) NOT NULL DEFAULT '0',
  `RuleFired` varchar(100) NOT NULL,
  `CreateDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`StudentID`,`AccreditationID`),
  KEY `IDX_StudentAccreditations_Students` (`StudentID`),
  KEY `IDX_StudentAccreditations_AccreditationDefinitions` (`AccreditationID`),
  KEY `IDX_StudentAccreditations_AccreditationDate` (`AccreditationDate`),
  KEY `IDX_StudentAccreditations_StudentID_CourseID` (`StudentID`,`CourseID`),
  KEY `IDX_StudentAccreditations_Processed` (`Processed`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StudentAccreditationsScott`
--

DROP TABLE IF EXISTS `StudentAccreditationsScott`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `StudentAccreditationsScott` (
  `StudentID` int(11) NOT NULL,
  `AccreditationID` int(11) NOT NULL,
  `AccreditationDate` datetime NOT NULL,
  `AccreditationType` varchar(20) NOT NULL,
  `CourseID` varchar(50) NOT NULL,
  `Processed` tinyint(1) NOT NULL DEFAULT '0',
  `RuleFired` varchar(100) NOT NULL,
  `CreateDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`StudentID`,`AccreditationID`),
  KEY `IDX_StudentAccreditations_Students` (`StudentID`),
  KEY `IDX_StudentAccreditations_AccreditationDefinitions` (`AccreditationID`),
  KEY `IDX_StudentAccreditations_AccreditationDate` (`AccreditationDate`),
  KEY `IDX_StudentAccreditations_StudentID_CourseID` (`StudentID`,`CourseID`),
  KEY `IDX_StudentAccreditations_Processed` (`Processed`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StudentAccreditationsScottBak`
--

DROP TABLE IF EXISTS `StudentAccreditationsScottBak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `StudentAccreditationsScottBak` (
  `StudentID` int(11) NOT NULL,
  `AccreditationID` int(11) NOT NULL,
  `AccreditationDate` datetime NOT NULL,
  `AccreditationType` varchar(20) NOT NULL,
  `CourseID` varchar(50) NOT NULL,
  `Processed` tinyint(1) NOT NULL DEFAULT '0',
  `RuleFired` varchar(100) NOT NULL,
  `CreateDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StudentAccreditations_20160901`
--

DROP TABLE IF EXISTS `StudentAccreditations_20160901`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `StudentAccreditations_20160901` (
  `StudentID` int(11) NOT NULL,
  `AccreditationID` int(11) NOT NULL,
  `AccreditationDate` datetime NOT NULL,
  `AccreditationType` varchar(20) NOT NULL,
  `CourseID` varchar(50) NOT NULL,
  `Processed` tinyint(1) NOT NULL DEFAULT '0',
  `RuleFired` varchar(200) DEFAULT NULL,
  `CreateDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'
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
  `CreateDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `totaraCourseCompletionId` int(11) DEFAULT '0',
  PRIMARY KEY (`StudentCourseID`),
  KEY `IDX_StudentCourses_Students` (`StudentID`),
  KEY `IDX_StudentCourses_Courses` (`CourseID`),
  KEY `IDX_StudentCourses_Languages` (`LanguageID`),
  KEY `IDX_StudentCourses_StudentID_CourseID` (`StudentID`,`CourseID`),
  KEY `IDX_StudentCourses_Processed` (`Processed`),
  CONSTRAINT `FK_StudentCourses_Courses` FOREIGN KEY (`CourseID`) REFERENCES `Courses` (`CourseID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_StudentCourses_Languages` FOREIGN KEY (`LanguageID`) REFERENCES `Languages` (`LanguageID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_StudentCourses_Students` FOREIGN KEY (`StudentID`) REFERENCES `Students` (`StudentID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=178317 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StudentCoursesBefore37`
--

DROP TABLE IF EXISTS `StudentCoursesBefore37`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `StudentCoursesBefore37` (
  `StudentCourseID` int(11) NOT NULL DEFAULT '0',
  `StudentID` int(11) NOT NULL,
  `CourseID` varchar(50) NOT NULL,
  `LanguageID` varchar(5) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `AssessmentDate` datetime NOT NULL,
  `AssessmentResult` varchar(5) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `AssessmentScore` tinyint(4) NOT NULL DEFAULT '100',
  `Processed` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StudentCourses_20160901`
--

DROP TABLE IF EXISTS `StudentCourses_20160901`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `StudentCourses_20160901` (
  `StudentCourseID` int(11) NOT NULL DEFAULT '0',
  `StudentID` int(11) NOT NULL,
  `CourseID` varchar(50) NOT NULL,
  `LanguageID` varchar(5) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `AssessmentDate` datetime NOT NULL,
  `AssessmentResult` varchar(5) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `AssessmentScore` tinyint(4) NOT NULL DEFAULT '100',
  `Processed` tinyint(1) NOT NULL DEFAULT '0',
  `CreateDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
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
  `CreateDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
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
  `SalesForceContactID` varchar(18) DEFAULT NULL,
  `SalesForceActive` char(3) DEFAULT NULL,
  `SumTotalID` varchar(18) DEFAULT NULL,
  `SumTotalActive` char(3) DEFAULT NULL,
  `SkillsbaseStatus` tinyint(1) NOT NULL DEFAULT '0',
  `IpaStatus` tinyint(1) NOT NULL DEFAULT '0',
  `ActivationDate` datetime DEFAULT NULL,
  `DeActivationDate` datetime DEFAULT NULL,
  `CreateDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `SalesForceUserName` varchar(80) DEFAULT NULL,
  `SalesForceManagerID` varchar(18) DEFAULT NULL,
  `SalesForceAccountName` varchar(255) DEFAULT NULL,
  `SalesForceJobFunctions` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`StudentID`),
  UNIQUE KEY `Email` (`Email`),
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
) ENGINE=InnoDB AUTO_INCREMENT=62608 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Summary`
--

DROP TABLE IF EXISTS `Summary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Summary` (
  `region` varchar(8) DEFAULT NULL,
  `sort1` int(1) DEFAULT NULL,
  `sort2` int(1) DEFAULT NULL,
  `role` varchar(20) DEFAULT NULL,
  `specialization` varchar(32) DEFAULT NULL,
  `track` varchar(42) DEFAULT NULL,
  `accreditationname` varchar(150) NOT NULL,
  `FY15` bigint(21) NOT NULL DEFAULT '0',
  `FY16` bigint(21) NOT NULL DEFAULT '0',
  `FY17` bigint(21) NOT NULL DEFAULT '0',
  `total` bigint(21) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SummaryRedHat`
--

DROP TABLE IF EXISTS `SummaryRedHat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SummaryRedHat` (
  `region` varchar(8) DEFAULT NULL,
  `sort1` int(1) DEFAULT NULL,
  `sort2` int(1) DEFAULT NULL,
  `role` varchar(20) DEFAULT NULL,
  `specialization` varchar(32) DEFAULT NULL,
  `track` varchar(42) DEFAULT NULL,
  `accreditationname` varchar(150) NOT NULL,
  `FY15` bigint(21) NOT NULL DEFAULT '0',
  `FY16` bigint(21) NOT NULL DEFAULT '0',
  `FY17` bigint(21) NOT NULL DEFAULT '0',
  `total` bigint(21) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cm`
--

DROP TABLE IF EXISTS `cm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cm` (
  `oldcoursecode` varchar(50) NOT NULL DEFAULT '',
  `newcoursecode` varchar(50) DEFAULT NULL,
  `courseid` varchar(50) DEFAULT NULL,
  `source` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fedsled_20170117`
--

DROP TABLE IF EXISTS `fedsled_20170117`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fedsled_20170117` (
  `email` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `usernumber` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `active` varchar(100) CHARACTER SET utf8 DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `newcourses`
--

DROP TABLE IF EXISTS `newcourses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `newcourses` (
  `assessment` varchar(200) NOT NULL DEFAULT '',
  `code` varchar(200) DEFAULT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `newcode` varchar(200) DEFAULT NULL,
  `comment` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sample`
--

DROP TABLE IF EXISTS `sample`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sample` (
  `LearnerName` varchar(150) NOT NULL,
  `Email` varchar(150) NOT NULL,
  `CourseName` varchar(150) NOT NULL,
  `CourseCode` varchar(150) NOT NULL,
  `CompletionStatus` varchar(150) NOT NULL,
  `CompletionDate` varchar(150) NOT NULL,
  `JobTitle` varchar(150) NOT NULL,
  `Region` varchar(150) NOT NULL,
  `SubRegion` varchar(150) NOT NULL,
  `Country` varchar(150) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-04-12 22:46:17
