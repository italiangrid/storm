/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.jna.lcmaps;

/**
 * @author dibenedetto_m
 *
 */

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface LcmapsAccountInterface extends Library
{

	public static final java.lang.String JNA_LIBRARY_NAME = "lcmaps_without_gsi";
	public static final LcmapsAccountInterface INSTANCE = (LcmapsAccountInterface) Native.loadLibrary(
		it.grid.storm.jna.lcmaps.LcmapsAccountInterface.JNA_LIBRARY_NAME,
		LcmapsAccountInterface.class);

	/**
	 * Original signature : <code>int lcmaps_account_info_init(lcmaps_account_info_t*)</code><br>
	 * <i>native declaration : line 20</i>
	 */
	int lcmaps_account_info_init(it.grid.storm.jna.lcmaps.lcmaps_account_info_t plcmaps_account);
	/**
	 * Original signature : <code>int lcmaps_account_info_clean(lcmaps_account_info_t*)</code><br>
	 * <i>native declaration : line 25</i>
	 */
	int lcmaps_account_info_clean(it.grid.storm.jna.lcmaps.lcmaps_account_info_t plcmaps_account);
}