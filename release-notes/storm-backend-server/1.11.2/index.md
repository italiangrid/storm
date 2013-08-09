---
layout: release_note_component
title: "StoRM BackEnd v. 1.11.2 release notes"
release_date: "20.07.2013"
release_description: "This was the second update for StoRM in EMI-3."
release_title: "StoRM v.1.11.2"
release_version: "StoRM-v1.11.2"
component:
      name: "StoRM BackEnd"
      package: "storm-backend-server"
      version: "1.11.2"
      rfcs:
        - id: STOR-257
          type: bug
          title: Unable to change STORM_USER via yaim setup of StoRM
        - id: STOR-303
          type: bug
          title: StoRM creates too many threads
        - id: STOR-304
          type: bug
          title: Slow db queries makes transfer operations latency increase
        - id: STOR-305
          type: bug
          title: srmReleaseFiles doesn't release multiple files at once
        - id: STOR-317
          type: feature
          title: Remove checksum.algorithm configuration variable
        - id: STOR-308
          type: bug
          title: srmMv returns SRM_SUCCESS instead of SRM_FILE_BUSY
        - id: STOR-314
          type: bug
          title: PutDone on multiple files fails all the SURLs after the first specified
---


