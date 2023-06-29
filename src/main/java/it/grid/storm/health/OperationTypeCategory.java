/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * 
 */
package it.grid.storm.health;

import java.util.ArrayList;

/**
 * @author zappi
 * 
 */
public enum OperationTypeCategory {
	ASYNCH("ASYNCH", new ArrayList<OperationType>() {

		private static final long serialVersionUID = 3641598296676643733L;
		{
			add(OperationType.BOL);
			add(OperationType.COPY);
			add(OperationType.PTG);
			add(OperationType.PTP);
		}
	}), PURESYNCH("PURESYNCH", new ArrayList<OperationType>() {

		private static final long serialVersionUID = -6608417863091343037L;

		{
			add(OperationType.PNG);
		}
	}), SYNCH_DB("SYNCH_DB", new ArrayList<OperationType>() {

		private static final long serialVersionUID = 5028836664777062718L;
		{
			add(OperationType.EFL);
			add(OperationType.GST);
			add(OperationType.RSP);
			add(OperationType.SPTG);
			add(OperationType.SPTP);
		}
	}), SYNCH_FS("SYNCH_FS", new ArrayList<OperationType>() {

		private static final long serialVersionUID = -5750075706467406539L;
		{
			add(OperationType.MKD);
			add(OperationType.MV);
			add(OperationType.RM);
			add(OperationType.RMD);
		}
	}), SYNCH_FS_DB("SYNCH_FS_DB", new ArrayList<OperationType>() {

		private static final long serialVersionUID = 6155834878615823037L;
		{
			add(OperationType.AF);
			add(OperationType.AR);
			add(OperationType.GSM);
			add(OperationType.LS);
			add(OperationType.PD);
			add(OperationType.RF);
			add(OperationType.RS);
		}
	}), UNKNOWN("UNKNOWN", new ArrayList<OperationType>() {

		private static final long serialVersionUID = -3529992869598284560L;
		{
			add(OperationType.UNDEF);
		}
	});

	private ArrayList<OperationType> opTypeList;
	private String acronym;

	private OperationTypeCategory(String acronym,
		ArrayList<OperationType> opTypeList) {

		this.opTypeList = opTypeList;
		this.acronym = acronym;
	}

	public boolean contains(OperationType op) {

		boolean result = false;
		if (this.opTypeList.contains(op)) {
			result = true;
		}
		return result;
	}

	public OperationTypeCategory getCategory(OperationType opType) {

		if (ASYNCH.contains(opType)) {
			return ASYNCH;
		}
		if (PURESYNCH.contains(opType)) {
			return PURESYNCH;
		}
		if (SYNCH_DB.contains(opType)) {
			return SYNCH_DB;
		}
		if (SYNCH_FS.contains(opType)) {
			return SYNCH_FS;
		}
		if (SYNCH_FS_DB.contains(opType)) {
			return SYNCH_FS_DB;
		}
		return UNKNOWN;
	}

	@Override
	public String toString() {

		return this.acronym;
	}
}