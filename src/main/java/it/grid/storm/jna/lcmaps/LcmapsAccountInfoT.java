/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.jna.lcmaps;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

@FieldOrder({"uid", "pgid_list", "npgid", "sgid_list", "nsgid", "poolindex"})
public class LcmapsAccountInfoT extends Structure {

	public int uid;
	public int[] pgid_list;
	public int npgid;
	public int[] sgid_list;
	public int nsgid;
	public String poolindex;

	public LcmapsAccountInfoT() {

		super();
	}

	/**
	 * @param uid
	 *          < the uid of the local account<br>
	 * @param pgid_list
	 *          < the list of primary gids<br>
	 *          C type : int*<br>
	 * @param npgid
	 *          < the number of primary gids found<br>
	 * @param sgid_list
	 *          < the list of secondary gids<br>
	 *          C type : int*<br>
	 * @param nsgid
	 *          < the number of secondary gids found<br>
	 * @param poolindex
	 *          < the pool index<br>
	 *          C type : char*
	 */
	public LcmapsAccountInfoT(int uid, int[] pgid_list, int npgid,
	    int[] sgid_list, int nsgid, String poolindex) {

		super();
		this.uid = uid;
		this.pgid_list = pgid_list;
		this.npgid = npgid;
		this.sgid_list = sgid_list;
		this.nsgid = nsgid;
		this.poolindex = poolindex;
	}

	public static class ByReference extends LcmapsAccountInfoT implements
		Structure.ByReference {

	};

	public static class ByValue extends LcmapsAccountInfoT implements
		Structure.ByValue {

	};
}
