---
layout: release_note_release
title: "EMI3 StoRM - 2nd Update release notes"
release_date: "20.07.2013"
release_description: "This was the second update for StoRM in EMI-3."
release_version: "EMI3-U2"
release_title: "EMI3 StoRM - 2nd Update"
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
release_components:
    - name: StoRM BackEnd
      package: storm-backend-server
      version: 1.11.2
    - name: StoRM FrontEnd
      package: storm-frontend-server
      version: 1.8.2
    - name: StoRM native libs
      package: storm-native-libs
      version: 1.0.2
    - name: YAIM StoRM
      package: yaim-storm
      version: 4.3.3
---