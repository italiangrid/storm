-- 
-- Update StoRM database from 1.4.00 to 1.5.00
--

REPLACE INTO storm_db.db_version (major,minor,revision,description) VALUES (1,5,00,'1 Feb 2010');

CREATE TABLE IF NOT EXISTS storm_be_ISAM.tape_recall (
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

ALTER TABLE storm_be_ISAM.tape_recall ADD INDEX deferredStartTime (deferredStartTime);

