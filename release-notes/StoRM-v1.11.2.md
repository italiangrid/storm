---
layout: release_note_release
title: "StoRM v.1.11.2 - release notes"
release_date: "20.07.2013"
release_description: "This release fixes v.1.11.1 known issues."
release_title: "StoRM v.1.11.2"
release_rfcs:
    - id: STOR-259
      type: bug
      title: StoRM native libs call to change_group_ownership now correctly forwards exceptions to the parent java process
    - id: STOR-250
      type: bug
      title: StoRM GPFS get_fileset_quota_info now doesn't leak more file descriptors
    - id: STOR-235
      type: bug
      title: YAIM StoRM does not provide a way to configure the XML-RPC service port
    - id: STOR-257
      type: bug
      title: Unable to change STORM_USER via yaim setup of StoRM
    - id: STOR-103
      type: bug
      title: StoRM publishes a wrong GLUE2EndpointServingState in one of the two GLUE2Endpoint
    - id: STOR-303
      type: bug
      title: StoRM creates too many threads
    - id: STOR-304
      type: bug
      title: Slow db queries makes transter operations latency increase
    - id: STOR-305
      type: bug
      title: srmReleaseFiles doesn't release multiple files at once
release_components:
    - name: StoRM Backend
      package: storm-backend-server
      version: 1.11.2
    - name: StoRM Frontend
      package: storm-frontend-server
      version: 1.8.2
    - name: StoRM native libs
      package: storm-native-libs
      version: 1.0.2
    - name: YAIM StoRM
      package: yaim-storm
      version: 4.3.3
---