
# Recall tasks interface

This interface expose operations on recall tasks.

All methods consume or produce text/plain, so issuing a request with an incompatible Accept or Content-Type will result in an error.

## Get recall tasks that are in progress

GET /recalltable/task

This method can be used to get the recall tasks that are currently in progress. It takes a query parameter named maxResults that sets the maximum number of result that will be returned in the response. The method returns a list of recall tasks.

A sample request is

	GET /recalltable/task?maxResults=2 HTTP/1.1
	Accept: */*

and the corresponding response would be

	HTTP/1.1 200 OK
	Content-Type: text/plain
	Content-Length: 440
	{16f73913-719c-4cbd-bc7a-123be47444fc	19-04-2013 00.00.00	bol	/gpfs_omni/testbed_1.11_sl6/storage/tape/adir/afile	testers.eu-emi.eu	null	0	in-progress	19-04-2013 00.00.00	259200	14ab0086-cd62-4722-9b5d-e6665432a6aa # f656310a-ecc2-4c08-89c4-b026193a3c8d	19-04-2013 00.00.00	bol	/gpfs_omni/testbed_1.11_sl6/storage/tape/adir/anotherfile	testers.eu-emi.eu	null	0	in-progress	19-04-2013 00.00.00	259200	606385d4-16a9-4704-8720-091946cf4a5d # }

## Create a new recall task

POST /recalltable/task

Is this used?

## Check whether a recall task has been completed, and eventually set its status accordingly 

PUT /recalltable/task

This method takes a request token and a surl in the body of the PUT request as follows

	requestToken=abc
	surl=srm://example.org/etc

and check whether a corresponding recall tasks exists and is completed by checking the file on the filesystem. If the file has been recalled, the task status is set to completed. The method returns either true (the task was completed) or false in the body of the response.

A sample request is 

	PUT /recalltable/task HTTP/1.1
	Accept: */*
	Content-Type:text/plain
	Content-Length: 7

	first=2

and the corresponding response would be

	HTTP/1.1 200 OK
	Content-Type: text/plain
	Content-Length: 4

	true

## Update a recall task status

PUT  /recalltable/task/{groupTaskId}

This method updates a recall task status. The status is passed in the boyd of the PUT request encoded as follows

 status=0

A sample request is 

	PUT /recalltable/task/abc HTTP/1.1
	Accept: */*
	Content-Type:text/plain
	Content-Length: 8

	status=0

and the corresponding response would be

	HTTP/1.1 200 OK

## Get the number of tasks that are queued

GET /recalltable/cardinality/tasks/queued

This method returns the number of tasks that are queued. 

A sample request is 

	GET /recalltable/cardinality/tasks/queued HTTP/1.1
	Accept: */*

and the corresponding response would be
 
	HTTP/1.1 200 OK
	Content-Type: text/plain

	0

## Get the number of tasks that are ready for being taken over

GET /recalltable/cardinality/tasks/readyTakeOver

This method returns the number of tasks that are ready for being taken over.

A sample request is 

	GET /recalltable/cardinality/tasks/readyTakeOver HTTP/1.1
	Accept: */*

and the corresponding response would be
 
	HTTP/1.1 200 OK
	Content-Type: text/plain

	0

## Get recall tasks that are ready for being taken over, and set their status to in progress

This method can be used to get the recall tasks that are ready for being taken over. The status of the tasks returned is set to in progress. The method needs to be passed the maximum number of result that are to be returned. This have to be passed in the body of the request, as in

	first=1

The method consumes a text/plain content, so issuing the request with an incompatible Content-Type will result in an error. The method returns a list of recall tasks.

A sample request is 

	PUT /recalltable/task HTTP/1.1
	Accept: */*
	Content-Type:text/plain
	Content-Length: 7

	first=2

and the corresponding response would be

	HTTP/1.1 200 OK
	Content-Type: text/plain
	Content-Length: 440
	{16f73913-719c-4cbd-bc7a-123be47444fc	19-04-2013 00.00.00	bol	/gpfs_omni/testbed_1.11_sl6/storage/tape/adir/afile	testers.eu-emi.eu	null	0	in-progress	19-04-2013 00.00.00	259200	14ab0086-cd62-4722-9b5d-e6665432a6aa # f656310a-ecc2-4c08-89c4-b026193a3c8d	19-04-2013 00.00.00	bol	/gpfs_omni/testbed_1.11_sl6/storage/tape/adir/anotherfile	testers.eu-emi.eu	null	0	in-progress	19-04-2013 00.00.00	259200	606385d4-16a9-4704-8720-091946cf4a5d # }
