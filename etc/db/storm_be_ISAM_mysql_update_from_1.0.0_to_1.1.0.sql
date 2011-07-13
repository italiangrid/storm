--
-- Update StoRM tape recall database from 1.0.0 to 1.1.0
--

DELETE FROM storm_be_ISAM.db_version;
INSERT INTO storm_be_ISAM.db_version (major,minor,revision,description) VALUES (1,1,0,'27 May 2011');

DROP TABLE IF EXISTS storm_be_ISAM.storage_file;

ALTER TABLE storm_be_ISAM.tape_recall 
	ADD `groupTaskId` CHAR(36) NOT NULL,
	ADD `inProgressTime` datetime,
    ADD `finalStatusTime` datetime,
    ADD INDEX groupTaskId_index (groupTaskId);

ALTER TABLE storm_be_ISAM.storage_space
    ADD `USED_SIZE` bigint(20) NOT NULL default '-1',
    ADD `BUSY_SIZE` bigint(20) NOT NULL default '-1',
    ADD `UNAVAILABLE_SIZE` bigint(20) NOT NULL default '-1',
    ADD `AVAILABLE_SIZE` bigint(20) NOT NULL default '-1',
    ADD `RESERVED_SIZE` bigint(20) NOT NULL default '-1',
    ADD `UPDATE_TIME` TIMESTAMP NOT NULL default '1970-01-02 00:00:00',
    MODIFY COLUMN `CREATED` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP,
    ADD INDEX ALIAS_index (ALIAS),
    ADD INDEX TOKEN_index (SPACE_TOKEN);
    
    
