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