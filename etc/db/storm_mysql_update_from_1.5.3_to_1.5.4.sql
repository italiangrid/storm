-- 
-- Update StoRM database from 1.5.3 to 1.5.4
--

DELETE FROM storm_db.db_version;
INSERT INTO storm_db.db_version (major,minor,revision,description) VALUES (1,5,4,'1 July 2010');

ALTER TABLE storm_db.request_Get 
	ADD normalized_sourceSURL_StFN text,
  	ADD sourceSURL_uniqueID int,
	ADD INDEX index_sourceSURL_uniqueID (sourceSURL_uniqueID);
  
ALTER TABLE storm_db.request_Put 
	ADD normalized_targetSURL_StFN text,
	ADD targetSURL_uniqueID int,
	ADD INDEX index_targetSURL_uniqueID (targetSURL_uniqueID);
	
ALTER TABLE storm_db.request_BoL 
	ADD normalized_sourceSURL_StFN text,
    ADD sourceSURL_uniqueID int,
	ADD INDEX index_sourceSURL_uniqueID (sourceSURL_uniqueID);
	
ALTER TABLE storm_db.request_Copy
	ADD normalized_sourceSURL_StFN text,
    ADD sourceSURL_uniqueID int,
    ADD normalized_targetSURL_StFN text,
    ADD targetSURL_uniqueID int,
	ADD INDEX index_sourceSURL_uniqueID (sourceSURL_uniqueID),
	ADD INDEX index_targetSURL_uniqueID (targetSURL_uniqueID);
