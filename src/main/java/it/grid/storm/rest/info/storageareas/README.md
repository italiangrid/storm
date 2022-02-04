# Storage Areas Resource

This interface allows to retrieve info about the defined storage areas.

The endpoint path is `/info/storage-areas`:

    GET /info/storage-areas


## The Storage Area object

The list of attributes returned for each storage area are:

| Attribute            | Value 
|:---------------------|:---------------------
| `name` | The name of the storage area.
| `token` | The space token value.
| `vos` | The list of VO names allowed to access this storage area.
| `rootPath` | The absolute path of storage area's root directory.
| `storageClass` | The Storage Area class type. One of the values: "T0D1", "T1D0", "T1D1".
| `accessPoints` | The list of access points.
| `retentionPolicy` | The retention policy.
| `accessLatency` | The access latency.
| `protocols` | The list of the supported transfer protocols.
| `anonymous` | Permissions for anonymous users.
| `availableNearlineSpace` | Total nearline space size.
| `approachableRules` | List of approachable rules.


## Examples

CURL command:

    curl http://storm.test.infn.it:9998/info/storage-areas

Output:

```JSON
  {
    "TESTVO-FS": {
      "name": "TESTVO-FS",
      "token": "TESTVO_TOKEN",
      "vos": [
        "test.vo"
      ],
      "rootPath": "/storage/test.vo",
      "storageClass": "T0D1",
      "accessPoints": [
        "/test.vo"
      ],
      "retentionPolicy": "replica",
      "accessLatency": "online",
      "protocols": [
        "xroot",
        "https",
        "http",
        "root",
        "gsiftp",
        "file"
      ],
      "anonymous": "NOREAD",
      "availableNearlineSpace": 0,
      "approachableRules": [
        "vo:test.vo"
      ]
    }
  }
```
