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

public interface LcmapsInterface extends Library {

	public static final java.lang.String JNA_LIBRARY_NAME = "lcmaps";
	public static final LcmapsInterface INSTANCE = (LcmapsInterface) Native
		.loadLibrary(it.grid.storm.jna.lcmaps.LcmapsInterface.JNA_LIBRARY_NAME,
			LcmapsInterface.class);

	/**
	 * \fn lcmaps_init_and_logfile(<br>
	 * char * logfile,<br>
	 * FILE* fp,<br>
	 * unsigned short logtype<br>
	 * )<br>
	 * \brief Initialize the LCMAPS module, select logging type and set logfile<br>
	 * The function does the following:<br>
	 * - initialize LCMAPS module.<br>
	 * - Setup logging by providing a file handle or file name, error handling
	 * (not yet).<br>
	 * - start PluginManager<br>
	 * \param logfile name of logfile <br>
	 * \param fp file handle for logging (from gatekeeper or other previously
	 * opened file handle)<br>
	 * If the file handle is zero, assume that only syslogging is requested<br>
	 * \param logtype type of logging (usrlog and/or syslog)<br>
	 * \retval 0 initialization succeeded.<br>
	 * \retval 1 initialization failed.<br>
	 * Original signature :
	 * <code>int lcmaps_init_and_logfile(char*, FILE*, unsigned short)</code><br>
	 * <i>native declaration : line 217</i>
	 */
	int lcmaps_init_and_logfile(String logfile, com.sun.jna.Pointer fp,
		short logtype);
}