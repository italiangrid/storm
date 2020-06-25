--###################################################
--#
--# Copyright (c) 2008 on behalf of the INFN CNAF 
--# The Italian National Institute for Nuclear Physics (INFN), 
--# All rights reserved.
--#
--# create StoRM databases
--#
--# author: luca.magnoni@cnaf.infn.it
--# changelog: Add "ON DELETE CASCADE" for requestDirOption.
--#
--###################################################

CREATE DATABASE IF NOT EXISTS storm_db;

USE storm_db;

CREATE TABLE IF NOT EXISTS db_version (
  ID int NOT NULL auto_increment,
  major int,
  minor int,
  revision int,
  description VARCHAR(100),
  primary key (ID)
) engine=InnoDB;

DELETE FROM storm_db.db_version;
INSERT INTO storm_db.db_version (major,minor,revision,description) VALUES (1,7,2,'10 Mar 2015');
   
CREATE TABLE IF NOT EXISTS request_queue (
  ID int not null auto_increment,
  config_FileStorageTypeID CHAR(1),
  config_AccessPatternID CHAR(1),
  config_ConnectionTypeID CHAR(1),
  config_OverwriteID CHAR(1),
  config_RequestTypeID VARCHAR(3) not null,
  client_dn VARCHAR(255) BINARY,
  u_token VARCHAR(255) BINARY,
  retrytime int,
  pinLifetime int,
  s_token VARCHAR(255) BINARY,
  status int not null,
  errstring VARCHAR(255),
  r_token VARCHAR(255) BINARY,
  remainingTotalTime int NOT NULL DEFAULT -1,
  fileLifetime int,
  nbreqfiles int,
  numOfCompleted int,
  numOfWaiting int,
  numOfFailed int,
  timeStamp datetime not null,
  proxy blob,
  deferredStartTime int,
  remainingDeferredStartTime int,
  primary key (ID)) engine=InnoDB;
  
CREATE TABLE IF NOT EXISTS request_Get (
  ID int not null auto_increment,
  request_DirOptionID int,
  request_queueID int,
  sourceSURL text not null,
  normalized_sourceSURL_StFN text,
  sourceSURL_uniqueID int,
  primary key (ID)) engine=InnoDB;
  
CREATE TABLE IF NOT EXISTS status_Get (
  ID int not null auto_increment,
  statusCode int not null,
  explanation VARCHAR(255),
  fileSize bigint,
  estimatedWaitTime int,
  remainingPinTime int,
  transferURL text,
  request_GetID int not null,
  primary key (ID)) engine=InnoDB;
  
CREATE TABLE IF NOT EXISTS request_Put (
  ID int not null auto_increment,
  request_queueID int not null,
  targetSURL text not null,
  expectedFileSize bigint,
  normalized_targetSURL_StFN text,
  targetSURL_uniqueID int,
  primary key (ID)) engine=InnoDB;

CREATE TABLE IF NOT EXISTS status_Put (
  ID int not null auto_increment,  
  statusCode int not null,
  explanation VARCHAR(255),
  fileSize bigint,
  estimatedWaitTime int,
  remainingPinTime int,
  remainingFileTime int,
  transferURL text,
  request_PutID int not null,
  primary key (ID)) engine=InnoDB;
  
CREATE TABLE IF NOT EXISTS request_BoL (
  ID int not null auto_increment,
  sourceSURL text not null,
  request_DirOptionID int,
  request_queueID int,
  normalized_sourceSURL_StFN text,
  sourceSURL_uniqueID int,
  primary key (ID)) engine=InnoDB;

CREATE TABLE IF NOT EXISTS status_BoL (
  ID int not null auto_increment,
  request_BoLID int,
  statusCode int not null,
  explanation VARCHAR(255),
  fileSize bigint,
  estimatedWaitTime int,
  remainingPinTime int,
  primary key (ID)) engine=InnoDB;

CREATE TABLE IF NOT EXISTS request_Copy (
  ID int not null auto_increment,
  request_queueID int,
  request_DirOptionID int,
  sourceSURL text not null,
  targetSURL text not null,
  normalized_sourceSURL_StFN text,
  sourceSURL_uniqueID int,
  normalized_targetSURL_StFN text,
  targetSURL_uniqueID int,
  primary key (ID)) engine=InnoDB;

CREATE TABLE IF NOT EXISTS status_Copy (
  ID int not null auto_increment,
  statusCode int not null,
  explanation VARCHAR(255),
  fileSize bigint,
  estimatedWaitTime int,  
  remainingFileTime int,
  request_CopyID int not null,
  primary key (ID)) engine=InnoDB;
  
CREATE TABLE IF NOT EXISTS request_ExtraInfo (
  ID int not null auto_increment,
  request_queueID int,
  status_GetID int,
  request_queueID2 int,
  status_PutID int,
  ei_key VARCHAR(255) not null,
  ei_value VARCHAR(255),
  primary key (ID)) engine=InnoDB;
  
CREATE TABLE IF NOT EXISTS request_RetentionPolicyInfo (
  ID int not null auto_increment,
  request_queueID int not null,
  config_RetentionPolicyID CHAR(1),
  config_AccessLatencyID CHAR(1),
  primary key (ID)) engine=InnoDB;
  
CREATE TABLE IF NOT EXISTS request_ClientNetworks (
  ID int not null auto_increment,
  network VARCHAR(255) not null,
  request_queueID int, primary key (ID)) engine=InnoDB;
  
CREATE TABLE IF NOT EXISTS request_TransferProtocols (
  ID int not null auto_increment,
  request_queueID int,
  config_ProtocolsID VARCHAR(30),
  primary key (ID)) engine=InnoDB;
 
CREATE TABLE IF NOT EXISTS request_DirOption (
  ID int not null auto_increment,
  isSourceADirectory tinyint(1) default 0 not null,
  allLevelRecursive tinyint(1) default 0,
  numOfLevels int default 1,
  primary key (ID)) engine=InnoDB;

CREATE TABLE IF NOT EXISTS request_VOMSAttributes (
  ID int not null auto_increment,  
  request_queueID int,
  vo VARCHAR(255) not null,
  voms_group text,
  voms_role text,
  voms_capability text,
  primary key (ID)) engine=InnoDB;
  
CREATE TABLE IF NOT EXISTS volatile (
  ID int not null auto_increment,
  file text not null,
  start datetime not null,
  fileLifetime int not null,
  primary key (ID)) engine=InnoDB;

CREATE TABLE IF NOT EXISTS jit (
  ID int not null auto_increment,
  file text not null,
  acl int not null,
  uid int not null,
  start datetime not null,
  pinLifetime int not null,
  gid int not null,
  primary key (ID)) engine=InnoDB;

CREATE TABLE IF NOT EXISTS config_Protocols (
  ID VARCHAR(30) not null,
  primary key (ID)) engine=InnoDB;

CREATE TABLE IF NOT EXISTS config_RetentionPolicy (
  ID CHAR(1) not null,
  primary key (ID)) engine=InnoDB;

CREATE TABLE IF NOT EXISTS config_AccessLatency (
  ID CHAR(1) not null,
  primary key (ID)) engine=InnoDB;

CREATE TABLE IF NOT EXISTS config_FileStorageType (
  ID CHAR(1) not null,
  primary key (ID)) engine=InnoDB;

CREATE TABLE IF NOT EXISTS config_AccessPattern (
  ID CHAR(1) not null,
  primary key (ID)) engine=InnoDB;

CREATE TABLE IF NOT EXISTS config_ConnectionType (
  ID CHAR(1) not null,
  primary key (ID)) engine=InnoDB;

CREATE TABLE IF NOT EXISTS config_Overwrite (
  ID CHAR(1) not null,
  primary key (ID)) engine=InnoDB;

CREATE TABLE IF NOT EXISTS config_RequestType (
  ID VARCHAR(3) not null,
  primary key (ID)) engine=InnoDB;



ALTER TABLE request_queue 
  add index FK_request_qu_2651 (config_FileStorageTypeID), 
  add constraint FK_request_qu_2651 foreign key (config_FileStorageTypeID) references config_FileStorageType (ID);
  
ALTER TABLE request_queue 
  add index FK_request_qu_4029 (config_AccessPatternID), 
  add constraint FK_request_qu_4029 foreign key (config_AccessPatternID) references config_AccessPattern (ID);
  
ALTER TABLE request_queue 
  add index FK_request_qu_8833 (config_ConnectionTypeID), 
  add constraint FK_request_qu_8833 foreign key (config_ConnectionTypeID) references config_ConnectionType (ID);
  
ALTER TABLE request_queue 
  add index FK_request_qu_8815 (config_OverwriteID), 
  add constraint FK_request_qu_8815 foreign key (config_OverwriteID) references config_Overwrite (ID);
  
ALTER TABLE request_queue 
  add index FK_request_qu_375 (config_RequestTypeID), 
  add constraint FK_request_qu_375 foreign key (config_RequestTypeID) references config_RequestType (ID);
  
CREATE INDEX r_token_index ON request_queue (r_token(8));
CREATE INDEX status_index on request_queue (status);

ALTER TABLE request_Get 
  add index FK_request_Ge_9630 (request_DirOptionID), 
  add constraint FK_request_Ge_9630 foreign key (request_DirOptionID) references request_DirOption (ID) ON DELETE CASCADE;
  
ALTER TABLE request_Get 
  add index FK_request_Ge_3811 (request_queueID), 
  add constraint FK_request_Ge_3811 foreign key (request_queueID) references request_queue (ID) ON DELETE CASCADE;
  
CREATE INDEX index_sourceSURL_uniqueID on request_Get (sourceSURL_uniqueID);
  
ALTER TABLE status_Get 
  add index FK_status_Get_4853 (request_GetID), 
  add constraint FK_status_Get_4853 foreign key (request_GetID) references request_Get (ID) ON DELETE CASCADE;  

ALTER TABLE request_Put 
  add index FK_request_Pu_4665 (request_queueID), 
  add constraint FK_request_Pu_4665 foreign key (request_queueID) references request_queue (ID) ON DELETE CASCADE;

CREATE INDEX index_targetSURL on request_Put (targetSURL(255));
CREATE INDEX index_targetSURL_uniqueID on request_Put (targetSURL_uniqueID);

ALTER TABLE status_Put 
  add index FK_status_Put_3223 (request_PutID), 
  add constraint FK_status_Put_3223 foreign key (request_PutID) references request_Put (ID) ON DELETE CASCADE;
  
CREATE INDEX statusCode_index on status_Put (statusCode);
CREATE INDEX statusCodeGet_index on status_Get (statusCode);
CREATE INDEX transferURL_index ON status_Put (transferURL(255));

ALTER TABLE request_BoL 
  add index FK_request_Bo_4166 (request_DirOptionID), 
  add constraint FK_request_Bo_4166 foreign key (request_DirOptionID) references request_DirOption (ID) ON DELETE CASCADE;
  
ALTER TABLE request_BoL 
  add index FK_request_Bo_8346 (request_queueID), 
  add constraint FK_request_Bo_8346 foreign key (request_queueID) references request_queue (ID) ON DELETE CASCADE;

CREATE INDEX index_sourceSURL_uniqueID on request_BoL (sourceSURL_uniqueID);

ALTER TABLE status_BoL 
  add index FK_status_BoL_1747 (request_BoLID), 
  add constraint FK_status_BoL_1747 foreign key (request_BoLID) references request_BoL (ID) ON DELETE CASCADE;  

ALTER TABLE request_Copy 
  add index FK_request_Co_6810 (request_queueID), 
  add constraint FK_request_Co_6810 foreign key (request_queueID) references request_queue (ID) ON DELETE CASCADE;
  
ALTER TABLE request_Copy 
  add index FK_request_Co_2630 (request_DirOptionID), 
  add constraint FK_request_Co_2630 foreign key (request_DirOptionID) references request_DirOption (ID) ON DELETE CASCADE;
  
CREATE INDEX index_sourceSURL_uniqueID on request_Copy (sourceSURL_uniqueID);
CREATE INDEX index_targetSURL_uniqueID on request_Copy (targetSURL_uniqueID);
  
ALTER TABLE status_Copy 
  add index FK_status_Cop_447 (request_CopyID), 
  add constraint FK_status_Cop_447 foreign key (request_CopyID) references request_Copy (ID) ON DELETE CASCADE;
  
ALTER TABLE request_ExtraInfo 
  add index FK_request_Ex_2570 (request_queueID),
  add constraint FK_request_Ex_2570 foreign key (request_queueID) references request_queue (ID) ON DELETE CASCADE;

ALTER TABLE request_ExtraInfo 
  add index FK_request_Ex_9422 (status_GetID), 
  add constraint FK_request_Ex_9422 foreign key (status_GetID) references status_Get (ID) ON DELETE CASCADE;
  
ALTER TABLE request_ExtraInfo 
  add index FK_request_Ex_9425 (request_queueID2), 
  add constraint FK_request_Ex_9425 foreign key (request_queueID2) references request_queue (ID) ON DELETE CASCADE;
  
ALTER TABLE request_ExtraInfo 
  add index FK_request_Ex_8646 (status_PutID), 
  add constraint FK_request_Ex_8646 foreign key (status_PutID) references status_Put (ID) ON DELETE CASCADE;
  
ALTER TABLE request_RetentionPolicyInfo 
  add index FK_request_Re_5291 (request_queueID),
  add constraint FK_request_Re_5291 foreign key (request_queueID) references request_queue (ID) ON DELETE CASCADE;
  
ALTER TABLE request_RetentionPolicyInfo 
  add index FK_request_Re_503 (config_RetentionPolicyID), 
  add constraint FK_request_Re_503 foreign key (config_RetentionPolicyID) references config_RetentionPolicy (ID);
  
ALTER TABLE request_RetentionPolicyInfo 
  add index FK_request_Re_2860 (config_AccessLatencyID), 
  add constraint FK_request_Re_2860 foreign key (config_AccessLatencyID) references config_AccessLatency (ID);  

ALTER TABLE request_ClientNetworks 
  add index FK_request_Cl_4686 (request_queueID),
  add constraint FK_request_Cl_4686 foreign key (request_queueID) references request_queue (ID) ON DELETE CASCADE;
  
ALTER TABLE request_TransferProtocols 
  add index FK_request_Tr_6848 (request_queueID),
  add constraint FK_request_Tr_6848 foreign key (request_queueID) references request_queue (ID) ON DELETE CASCADE;
  
ALTER TABLE request_TransferProtocols 
  add index FK_request_Tr_8127 (config_ProtocolsID), 
  add constraint FK_request_Tr_8127 foreign key (config_ProtocolsID) references config_Protocols (ID);
  
ALTER TABLE request_VOMSAttributes 
  add index FK_request_VO_5290 (request_queueID), 
  add constraint FK_request_VO_5290 foreign key (request_queueID) references request_queue (ID) ON DELETE CASCADE;

CREATE INDEX file_index ON volatile (file(255));

REPLACE INTO config_Protocols (ID) VALUES ('file');
REPLACE INTO config_Protocols (ID) VALUES ('gsiftp');
REPLACE INTO config_Protocols (ID) VALUES ('rfio');
REPLACE INTO config_Protocols (ID) VALUES ('root');
REPLACE INTO config_Protocols (ID) VALUES ('http');
REPLACE INTO config_Protocols (ID) VALUES ('https');
REPLACE INTO config_Protocols (ID) VALUES ('xroot');

REPLACE INTO config_Overwrite (ID) VALUES ('N');
REPLACE INTO config_Overwrite (ID) VALUES ('A');
REPLACE INTO config_Overwrite (ID) VALUES ('D');

REPLACE INTO config_FileStorageType (ID) VALUES ('V');
REPLACE INTO config_FileStorageType (ID) VALUES ('P');
REPLACE INTO config_FileStorageType (ID) VALUES ('D');

REPLACE INTO config_RequestType (ID) VALUES ('BOL');
REPLACE INTO config_RequestType (ID) VALUES ('PTG');
REPLACE INTO config_RequestType (ID) VALUES ('PTP');
REPLACE INTO config_RequestType (ID) VALUES ('COP');

REPLACE INTO config_RetentionPolicy (ID) VALUES ('R');
REPLACE INTO config_RetentionPolicy (ID) VALUES ('C');
REPLACE INTO config_RetentionPolicy (ID) VALUES ('O');

REPLACE INTO config_AccessLatency (ID) VALUES ('O');
REPLACE INTO config_AccessLatency (ID) VALUES ('N');

--
-- StoRM Backend DATABASE
-- storm_be_ISAM
--

CREATE DATABASE IF NOT EXISTS storm_be_ISAM;
USE storm_be_ISAM;

CREATE TABLE IF NOT EXISTS db_version (
  ID int NOT NULL auto_increment,
  major int,
  minor int,
  revision int,
  description VARCHAR(100),
  primary key (ID)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

DELETE FROM storm_be_ISAM.db_version;
INSERT INTO storm_be_ISAM.db_version (major,minor,revision,description) VALUES (1,1,0,'27 May 2011');

--
-- Table structure for table `storage_space`
--
CREATE TABLE IF NOT EXISTS `storage_space` (
  `SS_ID` bigint(20) NOT NULL auto_increment,
  `USERDN` VARCHAR(150) NOT NULL default '',
  `VOGROUP` VARCHAR(20) NOT NULL default '',
  `ALIAS` VARCHAR(100) default NULL,
  `SPACE_TOKEN` VARCHAR(100) BINARY NOT NULL default '',
  `CREATED` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP,
  `TOTAL_SIZE` bigint(20) NOT NULL default '0',
  `GUAR_SIZE` bigint(20) NOT NULL default '0',
  `FREE_SIZE` bigint(20) default NULL default '-1',
  `SPACE_FILE` VARCHAR(145) NOT NULL default '',
  `STORAGE_INFO` VARCHAR(255) default NULL,
  `LIFETIME` bigint(20) default NULL,
  `SPACE_TYPE` VARCHAR(10) NOT NULL default '',
  `USED_SIZE` bigint(20) NOT NULL default '-1',
  `BUSY_SIZE` bigint(20) NOT NULL default '-1',
  `UNAVAILABLE_SIZE` bigint(20) NOT NULL default '-1',
  `AVAILABLE_SIZE` bigint(20) NOT NULL default '-1',
  `RESERVED_SIZE` bigint(20) NOT NULL default '-1',
  `UPDATE_TIME` TIMESTAMP NOT NULL default '1970-01-02 00:00:00', 
  PRIMARY KEY  (`SS_ID`),
  INDEX ALIAS_index (`ALIAS`),
  INDEX TOKEN_index (`SPACE_TOKEN`),
  KEY `SPACE_NAME` (`SPACE_TOKEN`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


--
-- Table structure for table `tape_recall`
--
CREATE TABLE IF NOT EXISTS tape_recall (
  taskId CHAR(36) NOT NULL,
  requestToken VARCHAR(255) BINARY NOT NULL,
  requestType CHAR(4),
  fileName text not null,
  pinLifetime int,
  status int,
  voName VARCHAR(255) BINARY,
  userID VARCHAR(255) BINARY,
  retryAttempt int,
  timeStamp datetime not null,
  deferredStartTime datetime not null,
  groupTaskId CHAR(36) NOT NULL,
  inProgressTime datetime,
  finalStatusTime datetime,
  primary key (taskId , requestToken)) ENGINE=InnoDB;

ALTER TABLE tape_recall 
  ADD INDEX deferredStartTime (deferredStartTime),
  ADD INDEX groupTaskId_index (groupTaskId);