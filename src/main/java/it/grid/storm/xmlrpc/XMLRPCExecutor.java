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

package it.grid.storm.xmlrpc;

import it.grid.storm.common.OperationType;
import it.grid.storm.health.BookKeeper;
import it.grid.storm.health.HealthDirector;
import it.grid.storm.health.LogEvent;
import it.grid.storm.synchcall.SynchcallDispatcher;
import it.grid.storm.synchcall.SynchcallDispatcherFactory;
import it.grid.storm.synchcall.command.datatransfer.CommandException;
import it.grid.storm.synchcall.data.DataHelper;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.xmlrpc.converter.Converter;
import it.grid.storm.xmlrpc.converter.ConveterFactory;

import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is part of the StoRM project.
 * 
 * @author lucamag
 * @date May 27, 2008
 */

public class XMLRPCExecutor {

	private static ArrayList<BookKeeper> bookKeepers = HealthDirector
		.getHealthMonitor().getBookKeepers();

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory
		.getLogger(XMLRPCExecutor.class);

	/**
	 * @param type
	 * @param inputParam
	 * @return
	 */

	public Map execute(OperationType type, Map inputParam)
		throws StoRMXmlRpcException {

		long startTime = System.currentTimeMillis();
		long duration = System.nanoTime();
		log.debug("Executing a \'" + type.toString() + "\'" + "Call");
		log.debug("  Structure size  : " + inputParam.size());
		Converter converter = ConveterFactory.getConverter(type);
		SynchcallDispatcher dispatcher = SynchcallDispatcherFactory.getDispatcher();

		log.debug("Converting input data with Converter "
			+ converter.getClass().getName());
		InputData inputData = converter.convertToInputData(inputParam);

		log.debug("Dispatching request using SynchcallDispatcher "
			+ dispatcher.getClass().getName());
		OutputData outputData;
		try {
			outputData = dispatcher.processRequest(type, inputData);
		} catch (IllegalArgumentException e) {
			log
				.error("Unable to process the request. Error from the SynchcallDispatcher. IllegalArgumentException: "
					+ e.getMessage());
			throw new StoRMXmlRpcException(
				"Unable to process the request. IllegalArgumentException: "
					+ e.getMessage());
		} catch (CommandException e) {
			log
				.error("Unable to execute the request. Error from the SynchcallDispatcher. CommandException: "
					+ e.getMessage());
			throw new StoRMXmlRpcException(
				"Unable to process the request. CommandException: " + e.getMessage());
		}
		Map outputParam = converter.convertFromOutputData(outputData);
		duration = System.nanoTime() - duration;

		logExecution(convertOperationType(type),
			DataHelper.getRequestor(inputData), startTime, duration,
			outputData.isSuccess());
		// TODO rewrite the display method
		// log.debug("Output Map: " + ParameterDisplayHelper.display(outputParam));
		return outputParam;
	}

	/**
	 * Method used to book the execution of SYNCH operation
	 */
	private void logExecution(it.grid.storm.health.OperationType opType,
		String dn, long startTime, long duration, boolean successResult) {

		LogEvent event = new LogEvent(opType, dn, startTime, duration,
			successResult);
		if (!(bookKeepers.isEmpty())) {
			log.debug("Found # " + bookKeepers.size() + "bookeepers.");
			for (int i = 0; i < bookKeepers.size(); i++) {
				bookKeepers.get(i).addLogEvent(event);
			}
		}
	}

	/**
	 * TOREMOVE! this is a temporary code since two different class of
	 * OperationTYpe are defined. This is to convert the two kind of operation
	 * type, from the onw used here, enum based, to the one requested by the
	 * hearthbeat.
	 */
	private it.grid.storm.health.OperationType convertOperationType(
		OperationType type) {

		switch (type) {
		case PTG:
			return it.grid.storm.health.OperationType.PTG;
		case SPTG:
			return it.grid.storm.health.OperationType.SPTG;
		case PTP:
			return it.grid.storm.health.OperationType.PTP;
		case SPTP:
			return it.grid.storm.health.OperationType.SPTP;
		case COPY:
			return it.grid.storm.health.OperationType.COPY;
		case BOL:
			return it.grid.storm.health.OperationType.BOL;
		case AF:
			return it.grid.storm.health.OperationType.AF;
		case AR:
			return it.grid.storm.health.OperationType.AR;
		case EFL:
			return it.grid.storm.health.OperationType.EFL;
		case GSM:
			return it.grid.storm.health.OperationType.GSM;
		case GST:
			return it.grid.storm.health.OperationType.GST;
		case LS:
			return it.grid.storm.health.OperationType.LS;
		case MKD:
			return it.grid.storm.health.OperationType.MKD;
		case MV:
			return it.grid.storm.health.OperationType.MV;
		case PNG:
			return it.grid.storm.health.OperationType.PNG;
		case PD:
			return it.grid.storm.health.OperationType.PD;
		case RF:
			return it.grid.storm.health.OperationType.RF;
		case RESSP:
			return it.grid.storm.health.OperationType.RS;
		case RELSP:
			return it.grid.storm.health.OperationType.RSP;
		case RM:
			return it.grid.storm.health.OperationType.RM;
		case RMD:
			return it.grid.storm.health.OperationType.RMD;
		default:
			return it.grid.storm.health.OperationType.UNDEF;
		}
	}

}
