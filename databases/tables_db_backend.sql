---
-- Create schema storm_be
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ storm_be_ISAM;
USE storm_be_ISAM;

--
-- Table structure for table `storm_be_isam`.`storage_file`
--

DROP TABLE IF EXISTS `storage_file`;
CREATE TABLE `storage_file` (
  `SF_ID` bigint(20) NOT NULL auto_increment,
  `USERDN` varchar(150) NOT NULL default '',
  `VOGROUP` varchar(100) default NULL,
  `NAME` varchar(255) NOT NULL default '',
  `JIT` tinyint(1) NOT NULL default '0',
  `CREATED` timestamp(14) NOT NULL,
  `SIZE` bigint(20) default NULL,
  `LIFETIME` bigint(20) default NULL,
  `FK_SS_ID` bigint(20) default NULL,
  PRIMARY KEY  (`SF_ID`),
  KEY `name` (`NAME`),
  KEY `FK_SS_ID` (`FK_SS_ID`),
  CONSTRAINT `storage_file_ibfk_1` FOREIGN KEY (`FK_SS_ID`) REFERENCES `storage_space` (`SS_ID`)
) ENGINE=MyISAM;



--
-- Table structure for table `storm_be_isam`.`storage_space`
--

DROP TABLE IF EXISTS `storage_space`;
CREATE TABLE `storage_space` (
  `SS_ID` bigint(20) NOT NULL auto_increment,
  `USERDN` varchar(150) NOT NULL default '',
  `VOGROUP` varchar(20) NOT NULL default '',
  `ALIAS` varchar(100) default NULL,
  `SPACE_TOKEN` varchar(100) NOT NULL default '',
  `CREATED` timestamp(14) NOT NULL,
  `TOTAL_SIZE` bigint(20) NOT NULL default '0',
  `GUAR_SIZE` bigint(20) NOT NULL default '0',
  `FREE_SIZE` bigint(20) default NULL,
  `SPACE_FILE` varchar(145) NOT NULL default '',
  `STORAGE_INFO` varchar(255) default NULL,
  `LIFETIME` bigint(20) default NULL,
  `SPACE_TYPE` varchar(10) NOT NULL default '',
  PRIMARY KEY  (`SS_ID`),
  KEY `SPACE_NAME` (`SPACE_TOKEN`)
) ENGINE=MyISAM;

