/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.griduser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalUser {

	private static final Logger log = LoggerFactory.getLogger(LocalUser.class);

	private int uid;
	private int[] gids;

	public LocalUser(int uid, int[] gids, long ngids) {

		this.uid = uid;

		this.gids = new int[(int) ngids];

		for (int i = 0; i < ngids; i++)
			this.gids[i] = gids[i];
	}

	public LocalUser(int uid, int gid, int[] supplementaryGids) {

		this.uid = uid;

		this.gids = new int[1 + supplementaryGids.length];
		this.gids[0] = gid;

		for (int i = 1; i <= supplementaryGids.length; i++)
			this.gids[i] = supplementaryGids[i - 1];
	}

	public LocalUser(int uid, int gid) {

		this(uid, gid, new int[0]);
	}

	public LocalUser(String uidgids) {

		this.uid = 501;
		this.gids = new int[1];
		this.gids[0] = 501;
		if (uidgids != null) {
			String[] aux = uidgids.split(",");
			// try parsing the chunks provided there are at least two!
			if (aux.length >= 2) {
				try {
					int auxuid = Integer.parseInt(aux[0]);
					int[] auxgid = new int[aux.length - 1];
					for (int i = 0; i < aux.length - 1; i++)
						auxgid[i] = Integer.parseInt(aux[i + 1]);
					this.uid = auxuid;
					this.gids = auxgid;
				} catch (NumberFormatException e) {
					log.error("LocalUser: Error while setting uid/gid. NFE:" + e);
				}
			}
		}
	}

	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append(uid);
		for (int i = 0; i < gids.length; i++) {
			sb.append(",");
			sb.append(gids[i]);
		}
		return sb.toString();
	}

	public String getLocalUserName() {
		return Integer.toString(getUid());
	}

	public int getUid() {
		return uid;
	}

	public int[] getGids() {
		return gids;
	}

	public int getPrimaryGid() {
		return gids[0];
	}
}
