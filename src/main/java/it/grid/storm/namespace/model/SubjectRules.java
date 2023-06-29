/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.model;

import it.grid.storm.griduser.DNMatchingRule;
import it.grid.storm.griduser.VONameMatchingRule;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company: INFN-CNAF
 * </p>
 * 
 * @author R.Zappi
 * @version 1.0
 */
public class SubjectRules {

	private DNMatchingRule dnMatchingRule = null;
	private VONameMatchingRule voNameMatchingRule = null;

	public SubjectRules() {

		this.dnMatchingRule = DNMatchingRule.buildMatchAllDNMatchingRule();
		this.voNameMatchingRule = VONameMatchingRule
			.buildMatchAllVONameMatchingRule();
	}

	public SubjectRules(String dn) {

		this.dnMatchingRule = new DNMatchingRule(dn);
		this.voNameMatchingRule = VONameMatchingRule
			.buildMatchAllVONameMatchingRule();
	}

	public SubjectRules(String dn, String voName) {

		this.dnMatchingRule = new DNMatchingRule(dn);
		this.voNameMatchingRule = new VONameMatchingRule(voName);
	}

	public DNMatchingRule getDNMatchingRule() {

		return this.dnMatchingRule;
	}

	public VONameMatchingRule getVONameMatchingRule() {

		return this.voNameMatchingRule;
	}

}
