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

package it.grid.storm.xmlrpc.converter;

import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.xmlrpc.StoRMXmlRpcException;

import java.util.Map;

/**
 * 
 * This class is part of the StoRM project. Copyright (c) 2008 INFN-CNAF.
 * <p>
 * 
 * 
 * Authors:
 * 
 * @author=lucamag luca.magnoniATcnaf.infn.it
 * 
 * @date = Oct 9, 2008
 * 
 */

public interface Converter {

	/**
	 * This method return a RmInputData created from input Hashtable structure of
	 * an xmlrpc Rm v2.1 call. Rm Input Data can be used to invoke mkdir method of
	 * DirectoryFunctionsManager
	 */
	public abstract InputData convertToInputData(Map<String, Object> inputParam)
		throws IllegalArgumentException, StoRMXmlRpcException;

	public abstract Map<String, Object> convertFromOutputData(
		OutputData outputData) throws IllegalArgumentException;

}