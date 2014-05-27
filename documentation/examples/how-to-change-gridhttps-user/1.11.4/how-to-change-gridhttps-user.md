---
layout: default
title: StoRM Storage Resource Manager - Example of how to change storm-gridhttps-server service user
version: 1.11.4
---

#### [StoRM Examples]({{site.baseurl}}/documentation/examples/)

### Example of how to change storm-gridhttps-server service user.

From [StoRM v1.11.4][storm_1_11_4_release_notes], using a single user for both StoRM Backend and StoRM GridHTTPs is encouraged. The best solution is using ```storm``` user (the default Backend user) also for GridHTTPs service.
To configure GridHTTPs to use a different user and, in particular, the ```storm``` user, you have to change your YAIM site variables as follow:

	STORM_GRIDHTTPS_SERVER_USER_UID=`id -u storm`
	STORM_GRIDHTTPS_SERVER_GROUP_UID=`id -g storm`

These variables are used by StoRM Backend. You can also manually set the user id and group values.
Then we have to tell StoRM GridHTTPs its new user:

	STORM_GRIDHTTPS_USER='storm'

and specify where it can find the host certificate and key to encript the communication:

	STORM_GRIDHTTPS_CERT_DIR="/etc/grid-security/storm"

Using a single user for both services makes the management simplier.
But the main advantage is that we can skip the dynamic addition of gridhttps' user acls by setting:

	STORM_PTG_SKIP_ACL_SETUP="true"

Relaunch YAIM and your storm-gridhttps-server service will be started by ```storm``` user.


[storm_1_11_4_release_notes]: {{ site.baseurl }}/release-notes/StoRM-v1.11.4.html