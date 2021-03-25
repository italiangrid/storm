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

package it.grid.storm.griduser;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import it.grid.storm.common.types.VO;

/**
 * Encapsulates user Grid credentials access, and maps those to a local user
 * account. Has methods to extract the permanent identifier (subject DN), VO and
 * VOMS group/role membership from the X.509 certificate / GSI proxy that the
 * user presented to StoRM. Will also invoke LCMAPS library to map the Grid
 * credentials to a local user account.
 * 
 * @todo implement a flyweight pattern, so that we don't have 1'000 different
 *       GridUser objects for 1'000 requests from the same user...
 * 
 * 
 */
class VomsGridUser extends GridUser implements Serializable {

	private static final long serialVersionUID = -117007717079470189L;
	private List<FQAN> fqans = new ArrayList<FQAN>();
	private List<String> fqansString = new ArrayList<String>();

	// --- public accessor methods --- //

	VomsGridUser(MapperInterface mapper, String distinguishedName, String proxy,
		FQAN[] fqansArray) throws IllegalArgumentException {

		super(mapper, distinguishedName, proxy);
		if (fqansArray == null || fqansArray.length == 0) {
			throw new IllegalArgumentException(
				"Unable to create VomsGridUser. Inavlid fqansArray argument: "
					+ fqansArray);
		}
		this.setFqans(fqansArray);
	}

	VomsGridUser(MapperInterface mapper, String distinguishedName,
		FQAN[] fqansArray) throws IllegalArgumentException {

		super(mapper, distinguishedName);
		if (fqansArray == null || fqansArray.length == 0) {
			throw new IllegalArgumentException(
				"Unable to create VomsGridUser. Inavlid fqansArray argument: "
					+ fqansArray);
		}
		this.setFqans(fqansArray);
	}

	private void setFqans(FQAN[] fqans) {

		this.fqans.clear();
		this.fqansString.clear();
		for (FQAN fqan : fqans) {
			this.fqans.add(fqan);
			this.fqansString.add(fqan.toString());
		}
	}

	public void addFqan(FQAN fqan) {

		this.fqans.add(fqan);
		this.fqansString.add(fqan.toString());

	}


	/**
	 * Return <code>true</code> if any VOMS attributes are stored in this object.
	 * 
	 * <p>
	 * If the explicit constructor {@link VomsGridUser(String, Fqan[], String)}
	 * was used, then this flag will be true if the <code>Fqan[]</code> parameter
	 * was not null in the constructor invocation.
	 * 
	 * @return <code>true</code> if any VOMS attributes are stored in this object.
	 */
	public boolean hasVoms() {

		return true;
	}

	@Override
	public FQAN[] getFQANs() {

		FQAN[] FQANs = null;
		if (fqans != null) {
			FQANs = fqans.toArray(new FQAN[fqans.size()]);
		}
		return FQANs;
	}

	@Override
	public String[] getFQANsAsString() {

		String[] FQANs = null;
		if (fqansString != null) {
			FQANs = fqansString.toArray(new String[fqansString.size()]);
		}
		return FQANs;
	}

	public VO getVO() {

		VO result = VO.makeNoVo();
		if ((fqans != null) && (fqans.size() > 0)) {
			FQAN firstFQAN = fqans.get(0);
			String voName = firstFQAN.getVo();
			result = VO.make(voName);
		}
		return result;
	}

	/**
	 * Print a string representation of this object, in the form
	 * <code>GridUser:"</code><i>subject DN</i><code>"</code>.
	 */
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("Grid User (VOMS) = ");
		sb.append(" DN:'" + getDistinguishedName().getX500DN_rfc1779() + "'");
		sb.append(" FQANS:" + fqans);
		return sb.toString();
	}

	public int hashCode() {

		int result = 17;
		result += 31 * this.subjectDN.hashCode();
		for (FQAN fqan : fqans) {
			result += 37 * fqan.hashCode();
		}
		return result;
	}

	/**
	 * Return true if other is a VomsGridUser with the same String representation,
	 * that is: - same DN, and - same FQANs
	 */
	@Override
	public boolean equals(Object obj) {

		boolean result = false;
		if (obj != null) {
			if (obj instanceof VomsGridUser) {
				VomsGridUser other = (VomsGridUser) obj;
				if (!(other.hasVoms())) {
					result = this.getDistinguishedName().equals(
						other.getDistinguishedName());
				} else {
					// Also the other is a VomsGridUser
					if (this.getDistinguishedName().equals(other.getDistinguishedName())) {
						// Equals if they have the same FQANs
						FQAN[] otherFQANs = other.getFQANs();
						FQAN[] thisFQANs = this.getFQANs();
						if (otherFQANs.length == thisFQANs.length) {
							result = true;
							for (int i = 0; i < otherFQANs.length; i++) {
								if (!(otherFQANs[i].equals(thisFQANs[i]))) {
									result = false;
									break; // Exit from the loop at first fail.
								}
							}
						}
					} else {
						result = false;
					}
				}
			}
		}
		return result;
	}
}