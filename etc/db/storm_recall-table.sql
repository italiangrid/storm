
USE storm_be_isam;
CREATE TABLE IF NOT EXISTS tape_recall (
  taskId VARCHAR(255) BINARY NOT NULL,
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