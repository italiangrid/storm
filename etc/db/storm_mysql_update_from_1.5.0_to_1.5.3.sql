-- 
-- Update StoRM database from 1.5.0 to 1.5.3
--

DELETE FROM storm_db.db_version;
INSERT INTO storm_db.db_version (major,minor,revision,description) VALUES (1,5,3,'25 June 2010');

ALTER TABLE storm_be_ISAM.tape_recall 
  MODIFY taskId CHAR(36) NOT NULL,
  MODIFY requestToken VARCHAR(255) BINARY NOT NULL,
  MODIFY requestType CHAR(4),
  DROP PRIMARY KEY, 
  ADD CONSTRAINT pk_RecallTask PRIMARY KEY (taskId , requestToken);
  
ALTER TABLE storm_db.request_queue
  MODIFY remainingTotalTime int NOT NULL DEFAULT -1;
