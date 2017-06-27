# Metadata interface

This interface allows authorized users/clients to retrieve metadata info from files and directories.

The metadata endpoint path is `/metadata`. The endpoint should be followed by the `stfnPath` of the requested resource:

    GET /metadata/{stfnPath}


## The metadata object

The list of attributes in common between FILE and FOLDER types:

| Metadata name            | Value 
|--------------------------|---------------------
| `absolutePath`           | The absolute path of the resource
| `type`                   | The resource type. Values: `FILE`, `FOLDER`
| `status`                 | The 'latency' status. Values: `ONLINE`, `NEARLINE`
| `filesystem.name`        | The VFS name.
| `filesystem.root`        | The VFS root path.
| `lastModified`           | Timestamp last change to the resource.

The additional list of attributes when resource type is `FILE`: 

| Metadata name            | Value 
|--------------------------|---------------------
| `attributes.pinned`      | If type is FILE, the value of its extended attribute 'pinned'.
| `attributes.migrated`    | If type is FILE, the value of its extended attribute 'migrated'
| `attributes.premigrated` | If type is FILE, the value of its extended attribute 'premigrated'
| `attributes.tsmRecT`     | If type is FILE, the value of its extended attribute 'tsmRecT', the list of recall task ids.
| `attributes.tsmRecR`     | If type is FILE, the value of its extended attribute 'tsmRecR', the number or retry.
| `attributes.tsmRecD`     | If type is FILE, the value of its extended attribute 'tsmRecD', the recall in progress timestamp.

The additional list of attributes when resource type is `FOLDER`: 

| Metadata name            | Value 
|--------------------------|---------------------
| `children`               | If type is FOLDER, the array of the names of the its children resources.


## Examples

To get the metadata related to a resource identified by a valid `stfnPath` you have to provide the `Token` header. The value must be equal to the XMLRPC token used between Frontend and Backend.

### GET metadata of an online file

CURL command:

    curl -H "Token:********" http://storm.test.infn.it:9998/metadata/tape/test_metadata/diskonly.txt

Output:

```JSON
{
    "absolutePath": "/storage/tape/test_metadata/diskonly.txt",
    "type":"FILE",
    "status":"ONLINE",
    "filesystem": {
        "name": "TAPE-FS",
        "root": "/storage/tape"
    },
    "attributes": {
        "pinned": false,
        "migrated":false,
        "premigrated":false
    },
    "lastModified": "2017-06-26 16:50 PM UTC"
}
```

### GET metadata of an online file

CURL command:

    curl -H "Token:********" http://storm.test.infn.it:9998/metadata/tape/test_metadata/diskonly.txt

Output:

```JSON
{
    "absolutePath": "/storage/tape/test_metadata/diskonly.txt",
    "type":"FILE",
    "status":"ONLINE",
    "filesystem": {
        "name": "TAPE-FS",
        "root": "/storage/tape"
    },
    "attributes": {
        "pinned": false,
        "migrated":false,
        "premigrated":false
    },
    "lastModified": "2017-06-26 16:50 PM UTC"
}
```

### GET metadata of a migrated file

CURL command:

    curl -H "Token:********" http://storm.test.infn.it:9998/metadata/tape/test_metadata/diskandtape.txt

Output:


```JSON
{
	"absolutePath": "/storage/tape/test_metadata/diskandtape.txt",
	"type": "FILE",
	"status": "ONLINE",
	"filesystem": {
		"name": "TAPE-FS",
		"root": "/storage/tape"
	},
	"attributes": {
		"pinned": false,
		"migrated": true,
		"premigrated": false
	},
	"lastModified": "2017-06-26 16:50 PM UTC"
}
```

### GET metadata of a migrated and stubbed file

CURL command:

    curl -H "Token:********" http://storm.test.infn.it:9998/metadata/tape/test_metadata/tapeonly.txt

Output:


```JSON
{
	"absolutePath": "/storage/tape/test_metadata/tapeonly.txt",
	"type": "FILE",
	"status": "NEARLINE",
	"filesystem": {
		"name": "TAPE-FS",
		"root": "/storage/tape"
	},
	"attributes": {
		"pinned": false,
		"migrated": true,
		"premigrated": false
	},
	"lastModified": "2017-06-26 16:50 PM UTC"
}
```

### GET metadata of file with a recall in progress

CURL command:

    curl -H "Token:********" http://storm.test.infn.it:9998/metadata/tape/test_metadata/recallinprogress.txt

Output:


```JSON
{
	"absolutePath": "/storage/tape/test_metadata/recallinprogress.txt",
	"type": "FILE",
	"status": "NEARLINE",
	"filesystem": {
		"name": "TAPE-FS",
		"root": "/storage/tape"
	},
	"attributes": {
		"pinned": false,
		"migrated": true,
		"premigrated": false,
		"tsmRecD": 1495721014482,
		"tsmRecR": 0,
		"tsmRecT": "5b44eee4-de80-4d9c-b4ac-d4a205b4a9d4"
	},
	"lastModified": "2017-06-26 16:50 PM UTC"
}
```