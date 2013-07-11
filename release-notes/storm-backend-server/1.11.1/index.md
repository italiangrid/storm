---
layout: release_note_component
title: "StoRM BackEnd v. 1.11.1 release notes"
release_date: "03.06.2013"
release_description: "This was the first update for StoRM in EMI-3."
release_title: "StoRM v.1.11.1"
release_version: "StoRM-v1.11.1"
component:
      name: "StoRM BackEnd"
      package: "storm-backend-server"
      version: "1.11.1"
      rfcs:
        - id: STOR-172
          type: bug
          title: StoRM now correctly publishes information about storage area sizes in the information system.
        - id: STOR-148
          type: bug
          title: StoRM now leverages quota limits information gathered from the underlying GPFS file-system to compute a storage area size
        - id: STOR-10
          type: bug
          title: StoRM now gets quota information directly from GPFS file-system
        - id: STOR-117
          type: bug
          title: Duplicate prepare-to-get calls on a SURL are now correctly handled
        - id: STOR-109
          type: bug
          title: The Java JDK dependency has been fixed so that all StoRM packages explicitly requires OpenJDK.
---