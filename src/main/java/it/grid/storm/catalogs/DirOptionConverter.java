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

/**
 * Package private class that translates between DPM flag for TDirOption and
 * StoRM TDirOption proper.
 * 
 * In particular DPM uses the int 1 to denote a recursive call, yet it fails to
 * distinguish between a chosen recursion level; in other words there is no way
 * that DPM specifies the number of levels to recurse: so either you recurse
 * till the end or nothing.
 * 
 * @author EGRID - ICTP Trieste
 * @version 1.0
 * @date August, 2005
 */
class DirOptionConverter {

	static private DirOptionConverter converter = null;

	private DirOptionConverter() {

	}

	static public DirOptionConverter getInstance() {

		if (converter == null)
			converter = new DirOptionConverter();
		return converter;
	}

	/**
	 * Method that translates the int used by DPM as flag for TDirOption, into a
	 * boolean for isDirOption.
	 * 
	 * 1 causes true to be returned; any other value returns 0.
	 */
	public boolean toSTORM(int n) {

		return (n == 1);
	}

	/**
	 * Method used to translate the boolean isDirOption into an int used by DPM to
	 * express the same thing.
	 * 
	 * true gets translated into 1; false into 0.
	 */
	public int toDPM(boolean isDirOption) {

		if (isDirOption)
			return 1;
		return 0;
	}
}
