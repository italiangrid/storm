-- 
-- Update StoRM database from 1.5.0 to 1.5.2
--

REPLACE INTO storm_db.db_version (major,minor,revision,description) VALUES (1,5,2,'15 May 2010');

ALTER TABLE storm_be_ISAM.tape_recall modify taskId CHAR(36) NOT NULL; 
