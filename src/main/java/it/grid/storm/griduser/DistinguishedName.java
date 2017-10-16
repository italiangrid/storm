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

/*
 * You may copy, distribute and modify this file under the terms of the INFN
 * GRID licence. For a copy of the licence please visit
 * 
 * http://www.cnaf.infn.it/license.html
 * 
 * Original design made by Riccardo Zappi <riccardo.zappi@cnaf.infn.it.it>, 2007
 * 
 * $Id: DistinguishedName.java,v 1.5 2007/05/22 19:54:54 rzappi Exp $
 */

package it.grid.storm.griduser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.x500.X500Principal;

import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistinguishedName implements SubjectAttribute {

	private static final Logger log = LoggerFactory
		.getLogger(DistinguishedName.class);

	private String countryName = null;
	private String provinceName = null;
	private String organizationName = null;
	private String localityName = null;
	private String canonizedProxyDN = null;
	private final ArrayList<String> organizationalUnitNames = new ArrayList<String>();
	private final ArrayList<String> commonNames = new ArrayList<String>();
	private final ArrayList<String> domainComponents = new ArrayList<String>();

	private String eMailAddress = null;
	private String distinguishedName = null;

	private X500Principal x500DN = null;

	public DistinguishedName(String stringDN) {

		if (stringDN != null) {
			distinguishedName = stringDN;
			// Check the format of DN
			int slashIndex = distinguishedName.indexOf('/');
			int commaIndex = distinguishedName.indexOf(',');
			if (slashIndex > -1) {
				parseDNslahed();
				buildX500DN();
			}
			if (commaIndex > -1) {
				parseDNcommed();
				builderWithMap(stringDN);
			}

		} else {
			distinguishedName = "empty";
		}
	}

	/**
	 * @param stringDN
	 */
	private void builderWithMap(String stringDN) {

		String[] couples = stringDN.split(",");
		Map<String, String> pairs = new HashMap<String, String>();
		for (String couple : couples) {
			if (couple.contains("=")) {
				String key = couple.split("=")[0];
				String value = couple.split("=")[1];
				pairs.put(key, value);
			}
		}
		if (pairs.size() > 0) {
			log
				.error("To use this functionality (DN rfc 2253) you have to recompile with Java 1.6");
		}

	}

	private void assignAttributes(String[] dnChunk) {

		if (dnChunk != null) {
			int length = dnChunk.length;
			for (int i = 0; i < length; i++) {
				if (dnChunk[i].startsWith("C=")) {
					countryName = dnChunk[i].substring(2, dnChunk[i].length());
				}
				if (dnChunk[i].startsWith("ST=")) {
					provinceName = dnChunk[i].substring(3, dnChunk[i].length());
				}
				if (dnChunk[i].startsWith("O=")) {
					organizationName = dnChunk[i].substring(2, dnChunk[i].length());
				}
				if (dnChunk[i].startsWith("OU=")) {
					organizationalUnitNames.add(dnChunk[i].substring(3,
						dnChunk[i].length()));
				}
				if (dnChunk[i].startsWith("L=")) {
					localityName = dnChunk[i].substring(2, dnChunk[i].length());
				}
				if (dnChunk[i].startsWith("CN=")) {
					commonNames.add(dnChunk[i].substring(3, dnChunk[i].length()));
				}
				if (dnChunk[i].startsWith("DC=")) {
					domainComponents.add(dnChunk[i].substring(3, dnChunk[i].length()));
				}

				/**
				 * @todo : Implement case insentive for Attribute email
				 */
				if (dnChunk[i].startsWith("Email=")) {
					eMailAddress = dnChunk[i].substring(6, dnChunk[i].length());
				}
				if (dnChunk[i].startsWith("E=")) {
					eMailAddress = dnChunk[i].substring(2, dnChunk[i].length());
				}
				if (dnChunk[i].startsWith("EMailAddress=")) {
					eMailAddress = dnChunk[i].substring(13, dnChunk[i].length());
				}
			}
		}
	}

	private void parseDNslahed() {

		List<String> list = Lists.newArrayList();
		String dn = distinguishedName;
		boolean stop = false;

		while (!stop) {
			// Get index of lat '='
			int indexOfEq = dn.lastIndexOf('=');
			// Exit if it does not exists
			if (indexOfEq == -1) {
				stop = true;
				continue;
			}

			String tmpDN = dn.substring(0, indexOfEq);
			// Get index of the first '/' char on the left of the '='
			int indexOfAttr = tmpDN.lastIndexOf('/');

			// the substring from the indexOfAttr obtained to end of the String
			// is a attr-value pair!
			// Add it to the results List.
			list.add(dn.substring(indexOfAttr + 1, dn.length()));

			// Cut the result from the working DN string, and iterate.
			dn = dn.substring(0, indexOfAttr);
		}

		StringBuilder sb = new StringBuilder();
		String[] attributes = new String[list.size()];

		// Create a string representation of the DN.
		// Note that the result List contains attribute-value pair Strings in
		// reverse order!

		for (int i = 0; i < list.size(); i++) {
			if (i == list.size() - 1) {
				sb.append(list.get(list.size() - 1 - i));
			} else {
				sb.append(list.get(list.size() - 1 - i) + ",");
			}

			// Prepare the array for attributes evaluation
			attributes[i] = list.get((list.size() - 1 - i));
		}

		canonizedProxyDN = sb.toString();
		assignAttributes(attributes);

	}

	private void parseDNcommed() {

		String[] attributes = distinguishedName.split(",");
		assignAttributes(attributes);
	}

	private void buildX500DN() {

		x500DN = new X500Principal(canonizedProxyDN);
	}

	public String getX500DNString(String format) {

		return (x500DN != null ? x500DN.getName(format) : "");
	}

	public X500Principal getX500DN() {

		return x500DN;
	}

	public String getX500DN_rfc1779() {

		return (x500DN != null ? x500DN.getName(X500Principal.RFC1779) : "");
	}

	public String getX500DN_canonical() {

		return (x500DN != null ? x500DN.getName(X500Principal.CANONICAL) : "");
	}

	public String getX500DN_rfc2253() {

		return (x500DN != null ? x500DN.getName(X500Principal.RFC2253) : "");
	}

	public String getCountryName() {

		return countryName;
	}

	public String getProvinceName() {

		return provinceName;
	}

	public String getOrganizationName() {

		return organizationName;
	}

	public ArrayList<String> getOrganizationalUnitNames() {

		return organizationalUnitNames;
	}

	public ArrayList<String> getDomainComponents() {

		return domainComponents;
	}

	public String getLocalityName() {

		return localityName;
	}

	public ArrayList<String> getCommonNames() {

		return commonNames;
	}

	public String getEMail() {

		return eMailAddress;
	}

	public String getDN() {

		return distinguishedName;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		if (!(o instanceof DistinguishedName)) {
			return false;
		}

		final DistinguishedName dn = (DistinguishedName) o;

		if (!x500DN.equals(dn.getX500DN())) {
			return false;
		}

		return true;
	}

	public int hashCode() {

		int result = 17;
		if (x500DN != null) {
			result += 31 * x500DN.hashCode();
		}
		return result;
	}

	@Override
	public String toString() {

		StringBuilder result = new StringBuilder();
		if (countryName != null) {
			result.append("C=" + countryName + "\n");
		}
		if (provinceName != null) {
			result.append("ST=" + provinceName + "\n");
		}
		if (organizationName != null) {
			result.append("O=" + organizationName + "\n");
		}
		if (organizationalUnitNames != null) {
			for (String string : organizationalUnitNames) {
				result.append("OU=" + string + "\n");
			}
		}

		if (localityName != null) {
			result.append("L=" + localityName + "\n");
		}
		if (commonNames != null) {
			for (String string : commonNames) {
				result.append("CN=" + string + "\n");
			}
		}
		if (domainComponents != null) {
			for (String string : domainComponents) {
				result.append("DC=" + string + "\n");
			}
		}
		if (eMailAddress != null) {
			result.append("EMail=" + eMailAddress + "\n");
		}
		return result.toString();
	}
}
