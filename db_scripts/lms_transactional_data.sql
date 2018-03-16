-- MySQL dump 10.16  Distrib 10.2.12-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: lms_transactional
-- ------------------------------------------------------
-- Server version	10.2.12-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `AccreditationDefinitions`
--

LOCK TABLES `AccreditationDefinitions` WRITE;
/*!40000 ALTER TABLE `AccreditationDefinitions` DISABLE KEYS */;
/*!40000 ALTER TABLE `AccreditationDefinitions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Companies`
--

LOCK TABLES `Companies` WRITE;
/*!40000 ALTER TABLE `Companies` DISABLE KEYS */;
/*!40000 ALTER TABLE `Companies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Countries`
--

LOCK TABLES `Countries` WRITE;
/*!40000 ALTER TABLE `Countries` DISABLE KEYS */;
/*!40000 ALTER TABLE `Countries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `CountryMappings`
--

LOCK TABLES `CountryMappings` WRITE;
/*!40000 ALTER TABLE `CountryMappings` DISABLE KEYS */;
/*!40000 ALTER TABLE `CountryMappings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `CourseIdMappings`
--

LOCK TABLES `CourseIdMappings` WRITE;
/*!40000 ALTER TABLE `CourseIdMappings` DISABLE KEYS */;
/*!40000 ALTER TABLE `CourseIdMappings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `CourseMappings`
--

LOCK TABLES `CourseMappings` WRITE;
/*!40000 ALTER TABLE `CourseMappings` DISABLE KEYS */;
/*!40000 ALTER TABLE `CourseMappings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Courses`
--

LOCK TABLES `Courses` WRITE;
/*!40000 ALTER TABLE `Courses` DISABLE KEYS */;
/*!40000 ALTER TABLE `Courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Languages`
--

LOCK TABLES `Languages` WRITE;
/*!40000 ALTER TABLE `Languages` DISABLE KEYS */;
/*!40000 ALTER TABLE `Languages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `StudentAccreditations`
--

LOCK TABLES `StudentAccreditations` WRITE;
/*!40000 ALTER TABLE `StudentAccreditations` DISABLE KEYS */;
/*!40000 ALTER TABLE `StudentAccreditations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `StudentCourses`
--

LOCK TABLES `StudentCourses` WRITE;
/*!40000 ALTER TABLE `StudentCourses` DISABLE KEYS */;
/*!40000 ALTER TABLE `StudentCourses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `StudentMappings`
--

LOCK TABLES `StudentMappings` WRITE;
/*!40000 ALTER TABLE `StudentMappings` DISABLE KEYS */;
/*!40000 ALTER TABLE `StudentMappings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Students`
--

LOCK TABLES `Students` WRITE;
/*!40000 ALTER TABLE `Students` DISABLE KEYS */;
/*!40000 ALTER TABLE `Students` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-03-16  8:51:24
