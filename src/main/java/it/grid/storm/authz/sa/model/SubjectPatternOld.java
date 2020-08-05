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

package it.grid.storm.authz.sa.model;

import it.grid.storm.authz.sa.conf.AuthzDBReaderException;
import it.grid.storm.griduser.DNMatchingRule;

public class SubjectPatternOld {

	private String dnPatternStr = null;
	private String fqanPatternStr = null;
	private DNMatchingRule dnMR = null;
	private EGEEFQANPattern fqanMR = null;

	private static DNMatchingRule DEFAULT_DN_PATTERN = DNMatchingRule
		.buildMatchAllDNMatchingRule();

	public SubjectPatternOld(String dnPattern, String fqanPattern)
		throws AuthzDBReaderException {

		this.dnPatternStr = dnPattern;
		this.dnMR = new DNMatchingRule(dnPattern);
		this.fqanPatternStr = fqanPattern;
		this.fqanMR = new EGEEFQANPattern(fqanPattern);
	}

	public SubjectPatternOld(String fqanPattern) {

		this.dnPatternStr = ".*";
		this.dnMR = SubjectPatternOld.DEFAULT_DN_PATTERN;
		this.fqanPatternStr = fqanPattern;
	}

	public String getDNPatternStr() {

		return this.dnPatternStr;
	}

	public String getFQANPatternStr() {

		return this.fqanPatternStr;
	}

	public DNMatchingRule getDNPattern() {

		return this.dnMR;
	}

	public EGEEFQANPattern getFQANPattern() {

		return this.fqanMR;
	}

}
