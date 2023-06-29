/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.jna.lcmaps;

/**
 * @author dibenedetto_m
 * 
 */

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public interface LcmapsPoolindexInterface extends Library {

	public static final java.lang.String JNA_LIBRARY_NAME = "lcmaps_return_poolindex_without_gsi";
	public static final LcmapsPoolindexInterface INSTANCE = (LcmapsPoolindexInterface) Native
		.loadLibrary(
			it.grid.storm.jna.lcmaps.LcmapsPoolindexInterface.JNA_LIBRARY_NAME,
			LcmapsPoolindexInterface.class);

	/**
	 * Original signature :
	 * <code>int lcmaps_return_account_without_gsi(char*, char**, int, int, lcmaps_account_info_t*)</code>
	 * <br>
	 * <i>native declaration : line 30</i><br>
	 * 
	 * @deprecated use the safer methods
	 *             {@link #lcmaps_return_account_without_gsi(java.nio.ByteBuffer, com.sun.jna.ptr.PointerByReference, int, int, test.lcmaps_account_info_t)}
	 *             and
	 *             {@link #lcmaps_return_account_without_gsi(com.sun.jna.Pointer, com.sun.jna.ptr.PointerByReference, int, int, test.lcmaps_account_info_t)}
	 *             instead
	 */
	@java.lang.Deprecated
	int lcmaps_return_account_without_gsi(Pointer user_dn,
		PointerByReference fqan_list, int nfqan, int mapcounter,
		it.grid.storm.jna.lcmaps.lcmaps_account_info_t plcmaps_account);

	/**
	 * Original signature :
	 * <code>int lcmaps_return_account_without_gsi(char*, char**, int, int, lcmaps_account_info_t*)</code>
	 * <br>
	 * <i>native declaration : line 30</i>
	 */
	int lcmaps_return_account_without_gsi(String user_dn, String[] fqan_list,
		int nfqan, int mapcounter,
		it.grid.storm.jna.lcmaps.lcmaps_account_info_t plcmaps_account);
}