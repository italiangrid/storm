---
layout: release_note_component
title: "YAIM StoRM v.4.3.2 release notes"
release_date: "03.06.2013"
release_description: "This was the first update for StoRM in EMI-3."
release_title: "StoRM v.1.11.1"
release_version: "StoRM-v1.11.1"
component:
      name: "YAIM StoRM"
      package: "yaim-storm"
      version: "4.3.2"
      rfcs:
        - id: STOR-113
          type: bug
          title: The StoRM YAIM module does not try to configure permissions on existing configured storage areas. It is assumed (and documented) that the correct permissions are set by the system administrator before running YAIM.
        - id: STOR-130
          type: bug
          title: StoRM GridHTTPs server is now correctly registered to start at system boot.
---