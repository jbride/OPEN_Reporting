use lms;

DROP TABLE IF EXISTS `Regions`;
CREATE TABLE `Regions` (
  `RegionCode` varchar(10) NOT NULL,
  `RegionName` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`RegionCode`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `Regions` VALUES ('APAC','Asia Pacific'),('EMEA','Europe, Middle East and Africa'),('Fed/Sled','Fed/Sled'),('LATAM','Latin America'),('NA','North America');
