/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.model;

import it.grid.storm.namespace.NamespaceDirector;

import org.slf4j.Logger;

public class Quota {

	private final Logger log = NamespaceDirector.getLogger();

	private boolean defined = false;
	private boolean enabled = false;
	private String device = null;
	private QuotaType quotaType = null;

	public Quota() {

		super();
	}

	public Quota(boolean enabled, String device, QuotaType quotaType) {

		defined = true;
		this.enabled = enabled;
		this.device = device;
		this.quotaType = quotaType;
	}

	/**
	 * Read only attribute
	 * 
	 * @return boolean
	 */
	public boolean getDefined() {

		return defined;
	}

	public boolean getEnabled() {

		return enabled;
	}

	public void setEnabled(boolean enabled) {

		this.enabled = enabled;
	}

	public String getDevice() {

		return device;
	}

	public void setDevice(String device) {

		this.device = device;
	}

	public QuotaType getQuotaType() {

		return quotaType;
	}

	public void setQuotaType(QuotaType quotaType) {

		this.quotaType = quotaType;
	}

	/**
	 * Return the value of UserName or GroupName or FileSetName. The meaning of
	 * the value depends on QuotaType.
	 * 
	 * @return the quotaElementName
	 */
	public String getQuotaElementName() {

		return quotaType.getValue();
	}

	@Override
	public String toString() {

		StringBuilder result = new StringBuilder();
		result.append("Quota : [ Defined:'" + defined + "' ");
		result.append("Enabled:'" + enabled + "' ");
		result.append("device:'" + device + "', ");
		result.append("quotaType:'" + quotaType + " ");
		result.append("]");
		return result.toString();
	}

}
