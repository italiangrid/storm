USE storm_db;
REPLACE INTO db_version (major,minor,revision,description) VALUES (1,5,00,'1 Oct 2009');
USE storm_be_ISAM;
CREATE TABLE IF NOT EXISTS tape_recall (
  taskId MEDIUMINT NOT NULL AUTO_INCREMENT,
  requestToken VARCHAR(255) BINARY,
  requestType char(3),
  fileName text not null,
  pinLifetime int,
  status int,
  voName VARCHAR(255) BINARY,
  userID VARCHAR(255) BINARY,
  retryAttempt int,
  timeStamp datetime not null,
  deferredStartTime datetime not null,
  primary key (taskId)) type=InnoDB;

ALTER TABLE tape_recall ADD INDEX deferredStartTime (deferredStartTime);
