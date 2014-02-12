/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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

		StringBuffer sb = new StringBuffer();
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
