---
layout: release_note_component
title: "StoRM native libs v.1.0.2 release notes"
release_date: "20.07.2013"
release_description: "This was the second update for StoRM in EMI-3."
release_title: "StoRM v.1.11.2"
release_version: "StoRM-v1.11.2"
component:
      name: "StoRM native libs"
      package: "storm-native-libs"
      version: "1.0.2"
      rfcs:
        - id: STOR-259
          type: bug
          title: StoRM native libs call to change_group_ownership now correctly forwards exceptions to the parent java process
        - id: STOR-250
          type: bug
          title: StoRM GPFS get_fileset_quota_info now doesn't leak more file descriptors
---