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

package it.grid.storm.catalogs;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import it.grid.storm.srm.types.TOverwriteMode;

/**
 * Package private auxiliary class used to convert between DPM and StoRM
 * representation of Copy TOverwriteMode+RemoveSourceFiles global information
 * for the whole request, and Flags in storm_req.
 * 
 * @author: EGRID - ICTP Trieste
 * @version: 1.0
 * @date: September 2005
 */
class CopyGlobalFlagConverter {

	private Map DPMtoSTORM = new HashMap();
	private Map STORMtoDPM = new HashMap();

	private static CopyGlobalFlagConverter c = new CopyGlobalFlagConverter();

	/**
	 * Private constructor that fills in the conversion table; in particular, DPM
	 * uses int values to represent the pair of values:
	 * 
	 * 0 NEVER + DO NOT RemoveSourceFiles 1 ALWAYS + DO NOT RemoveSourceFiles 2
	 * WHENFILESAREDIFFERENT + DO NOT RemoveSourceFiles 4 NEVER +
	 * RemoveSourceFiles 5 ALWAYS + RemoveSourceFiles 6 WHENFILESAREDIFFERENT +
	 * RemoveSourceFiles
	 */
	private CopyGlobalFlagConverter() {

		DPMtoSTORM.put(new Integer(0), new Object[] { TOverwriteMode.NEVER,
			new Boolean(false) });
		DPMtoSTORM.put(new Integer(1), new Object[] { TOverwriteMode.ALWAYS,
			new Boolean(false) });
		DPMtoSTORM.put(new Integer(2), new Object[] {
			TOverwriteMode.WHENFILESAREDIFFERENT, new Boolean(false) });
		DPMtoSTORM.put(new Integer(4), new Object[] { TOverwriteMode.NEVER,
			new Boolean(true) });
		DPMtoSTORM.put(new Integer(5), new Object[] { TOverwriteMode.ALWAYS,
			new Boolean(true) });
		DPMtoSTORM.put(new Integer(6), new Object[] {
			TOverwriteMode.WHENFILESAREDIFFERENT, new Boolean(true) });
		Object aux;
		for (Iterator i = DPMtoSTORM.keySet().iterator(); i.hasNext();) {
			aux = i.next();
			STORMtoDPM.put(DPMtoSTORM.get(aux), aux);
		}
	}

	/**
	 * Method that returns the only instance of OverwriteModeConverter.
	 */
	public static CopyGlobalFlagConverter getInstance() {

		return c;
	}

	/**
	 * Method that returns the int used by DPM to represent the given
	 * TOverwriteMode and removeSourceFiles boolean. -1 is returned if no match is
	 * found.
	 */
	public int toDPM(TOverwriteMode om, boolean removeSourceFiles) {

		Integer aux = (Integer) STORMtoDPM.get(new Object[] { om,
			new Boolean(removeSourceFiles) });
		if (aux == null)
			return -1;
		return aux.intValue();
	}

	/**
	 * Method that returns an Object[] containing the TOverwriteMode and the
	 * boolean used by StoRM to represent the supplied int representation of DPM.
	 * An empty Object[] is returned if no StoRM type is found.
	 */
	public Object[] toSTORM(int n) {

		Object[] aux = (Object[]) DPMtoSTORM.get(new Integer(n));
		if (aux == null)
			return new Object[] {};
		return aux;
	}

	public String toString() {

		return "OverWriteModeConverter.\nDPMtoSTORM map:" + DPMtoSTORM
			+ "\nSTORMtoDPM map:" + STORMtoDPM;
	}
}
