DELETE FROM storm_db.db_version;
INSERT INTO storm_db.db_version (major,minor,revision,description) VALUES (1,7,2,'10 Mar 2015');

CREATE INDEX statusCodeGet_index on storm_db.status_Get (statusCode);

