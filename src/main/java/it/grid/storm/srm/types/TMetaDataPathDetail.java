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

/**
 * 
 * This class represents the TMetaDataPathDetail,return structure for ls
 * request.
 * 
 * 
 * 
 * @author Magnoni Luca
 * 
 * @author Cnaf -INFN Bologna
 * 
 * @date
 * 
 * @version 1.0
 */

package it.grid.storm.srm.types;

import it.grid.storm.common.types.StFN;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TMetaDataPathDetail {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
		"yyyy-MM-dd'T'HH:mm:ss");

	private TSURL surl = null;
	// Change in new srm 2.2
	private StFN stfn = null;
	private TReturnStatus retStatus = null;
	private TSizeInBytes size = null;
	private Date createdAtTime = null;
	private Date lastModificationAtTime = null;
	private TFileStorageType fileStorageType = null;
	private TRetentionPolicyInfo retentionPolicyInfo = null;
	private TFileLocality fileLocality = null;
	private ArrayOfTSpaceToken tokenArray = null;
	private TFileType type = null;
	private TLifeTimeInSeconds lifetimeAssigned = null;
	private TLifeTimeInSeconds lifetimeLeft = null;
	private TUserPermission ownerPermission = null;
	private TGroupPermission groupPermission = null;
	private TPermissionMode otherPermission = null;
	private TCheckSumType checkSumType = null;
	private TCheckSumValue checkSumValue = null;
	private ArrayOfTMetaDataPathDetail arrayOfSubPaths = null;

	public TMetaDataPathDetail() {

	}

	/**
	 * Method that return Surl
	 */
	public TSURL getSurl() {

		return surl;
	}

	/**
	 * Method that set Surl
	 */
	public void setSurl(TSURL surl) {

		this.surl = surl;
	}

	/**
	 * Method that return StFN
	 */
	public StFN getStFN() {

		return stfn;
	}

	/**
	 * Method that set StFN
	 */
	public void setStFN(StFN stfn) {

		this.stfn = stfn;
	}

	/**
	 * Method that return Status
	 */
	public TReturnStatus getStatus() {

		return retStatus;
	}

	/**
	 * Method that set Status.
	 */
	public void setStatus(TReturnStatus status) {

		this.retStatus = status;
	}

	/**
	 * Method that Return Size
	 */
	public TSizeInBytes getSize() {

		return size;
	}

	/**
	 * Method that set Size
	 */
	public void setSize(TSizeInBytes size) {

		this.size = size;
	}

	/**
	 * Method that get LastModificationAtTime Value;
	 */
	public Date getModificationTime() {

		return lastModificationAtTime;
	}

	/**
	 * Method that set CreatedAtTime Value
	 */
	public void setModificationTime(Date lastModificationAtTime) {

		this.lastModificationAtTime = lastModificationAtTime;
	}

	/**
	 * Method that get CreatedAtTime Value;
	 */
	public Date getCreationTime() {

		return createdAtTime;
	}

	/**
	 * Method that set CreatedAtTime Value
	 */
	public void setCreationTime(Date createdAtTime) {

		this.createdAtTime = createdAtTime;
	}

	/**
	 * Method that return TFileStorageType;
	 */
	public TFileStorageType getFileStorageType() {

		return fileStorageType;
	}

	/**
	 * Method that set TFileStorageType
	 */
	public void setTFileStorageType(TFileStorageType type) {

		this.fileStorageType = type;
	}

	/**
	 * Method that return TRetentionPolicyInfo;
	 */
	public TRetentionPolicyInfo getTRetentionPolicyInfo() {

		return this.retentionPolicyInfo;
	}

	/**
	 * Method that set TRetentionPolicyInfo
	 */
	public void setTRetentionPolicyInfo(TRetentionPolicyInfo info) {

		this.retentionPolicyInfo = info;
	}

	/**
	 * Method that return TFileLocality;
	 */
	public TFileLocality getTFileLocality() {

		return this.fileLocality;
	}

	/**
	 * Method that set TFileLocality
	 */
	public void setTFileLocality(TFileLocality loc) {

		this.fileLocality = loc;
	}

	/**
	 * Method that return array of TSpaceToken;
	 */
	public ArrayOfTSpaceToken getArrayOfTSpaceToken() {

		return this.tokenArray;
	}

	/**
	 * Method that set array of TSpaceToken
	 */
	public void setArrayOfTSpaceToken(ArrayOfTSpaceToken tokenArray) {

		this.tokenArray = tokenArray;
	}

	/**
	 * Method that return TFileType;
	 */
	public TFileType getFileType() {

		return type;
	}

	/**
	 * Method that set TFileType
	 */
	public void setFileType(TFileType type) {

		this.type = type;
	}

	/**
	 * Method that GET lifetime assigned;
	 */
	public TLifeTimeInSeconds getLifetimeAssigned() {

		return lifetimeAssigned;
	}

	/**
	 * Method that set lifetime assigned
	 */
	public void setLifeTimeAssigned(TLifeTimeInSeconds lifetime) {

		this.lifetimeAssigned = lifetime;
	}

	/**
	 * Method that GET lifetime LEFT;
	 */

	public TLifeTimeInSeconds getLifetimeLeft() {

		return lifetimeLeft;
	}

	/**
	 * Method that set lifetime Left
	 */
	public void setLifetimeLeft(TLifeTimeInSeconds lifetime) {

		this.lifetimeLeft = lifetime;
	}

	/**
	 * Method that set OwnerPermission
	 */
	public void setOwnerPermission(TUserPermission ownerPermission) {

		this.ownerPermission = ownerPermission;
	}

	/**
	 * Method that return OwnerPermission;
	 */
	public TUserPermission getOwnerPermission() {

		return ownerPermission;
	}

	/**
	 * Method that set TGroupPermissionArray
	 */
	public void setGroupPermission(TGroupPermission groupP) {

		this.groupPermission = groupP;
	}

	/**
	 * Method that get TGroupPermissionArray
	 */
	public TGroupPermission getGroupPermission() {

		return groupPermission;
	}

	/**
	 * Method that set otherPermission
	 */
	public void setOtherPermission(TPermissionMode otherP) {

		this.otherPermission = otherP;
	}

	/**
	 * Method that get otherPermission
	 */
	public TPermissionMode getUserPermissionArray() {

		return otherPermission;
	}

	/**
	 * 
	 * @param checkSumType
	 */
	public void setCheckSumType(TCheckSumType checkSumType) {

		this.checkSumType = checkSumType;
	}

	/**
	 * Method that get CHECKSUMTYPE
	 */
	public TCheckSumType getCheckSumType() {

		return checkSumType;
	}

	/**
	 * 
	 * @param checkSumValue
	 */
	public void setCheckSumValue(TCheckSumValue checkSumValue) {

		this.checkSumValue = checkSumValue;
	}

	/**
	 * Method that get CHECKSUMVALUE
	 */
	public TCheckSumValue getCheckSumValue() {

		return checkSumValue;
	}

	/**
	 * Method that get TMetaDataPathDetails
	 * 
	 * @TODO
	 */
	public ArrayOfTMetaDataPathDetail getArrayOfSubPaths() {

		return arrayOfSubPaths;
	}

	public void setArrayOfSubPaths(ArrayOfTMetaDataPathDetail array) {

		arrayOfSubPaths = array;
	}

	/**
	 * Encode method, used to encode a TMetaDataPathDetail object into a
	 * structured paramter (Hashtable), used for communicate to the FE component
	 * thourgh xmlrpc.
	 * 
	 * @param param
	 *          Hashtable that must contain structures results
	 * @param name
	 *          name for the TMetaData field
	 */
	public void encode(List list) {

		Map param = new HashMap();

		/* (1) StFN */
		if (this.stfn != null) {
			this.stfn.encode(param, StFN.PNAME_PATH);
		}
		/* (2) TReturnStatus */
		if (this.retStatus != null) {
			this.retStatus.encode(param, TReturnStatus.PNAME_STATUS);
		}
		/* (3) Size */
		if (this.size != null) {
			this.size.encode(param, TSizeInBytes.PNAME_SIZE);
		}
		/* (4) createdAtTime */
		if (this.createdAtTime != null) {
			param.put("createdAtTime", dateFormat.format(createdAtTime));
		}
		/* (5) lastModificationTime */
		if (this.lastModificationAtTime != null) {
			// param.put("lastModificationTime", lastModificationAtTime.toString());
			param.put("lastModificationTime",
				dateFormat.format(lastModificationAtTime));
		}
		/* (6) fileStorageType */
		if (this.fileStorageType != null) {
			this.fileStorageType
				.encode(param, TFileStorageType.PNAME_FILESTORAGETYPE);
		}
		/* (7) TRetentionPolicyInfo */
		if (this.retentionPolicyInfo != null) {
			this.retentionPolicyInfo.encode(param,
				TRetentionPolicyInfo.PNAME_retentionPolicyInfo);
		}
		/* (8) fileLocality */
		if (this.fileLocality != null) {
			this.fileLocality.encode(param, TFileLocality.PNAME_FILELOCALITY);
		}
		/* (9) ArrayOfTSpaceToken */
		if (this.tokenArray != null) {
			this.tokenArray
				.encode(param, ArrayOfTSpaceToken.PNAME_ARRAYOFSPACETOKENS);
		}
		/* (10) TFileType */
		if (this.type != null) {
			this.type.encode(param, TFileType.PNAME_TYPE);
		}
		/* (11) lifeTimeAssigned */
		if (this.lifetimeAssigned != null) {
			this.lifetimeAssigned.encode(param,
				TLifeTimeInSeconds.PNAME_LIFETIMEASSIGNED);
		}
		/* (12) lifeTimeLeft */
		if (this.lifetimeLeft != null) {
			this.lifetimeLeft.encode(param, TLifeTimeInSeconds.PNAME_LIFETIMELEFT);
		}
		/* (13) TUserPermission ownerPermission */
		if (this.ownerPermission != null) {
			this.ownerPermission.encode(param, TUserPermission.PNAME_OWNERPERMISSION);
		}
		/* (14) TGroupPermission groupPermission */
		if (this.groupPermission != null) {
			this.groupPermission
				.encode(param, TGroupPermission.PNAME_GROUPPERMISSION);
		}
		/* (15) TPermissionMode otherPermission */
		if (this.otherPermission != null) {
			this.otherPermission.encode(param, TPermissionMode.PNAME_OTHERPERMISSION);
		}
		/* (16) TCheckSumType */
		if (this.checkSumType != null) {
			this.checkSumType.encode(param, TCheckSumType.PNAME_CHECKSUMTYPE);
		}
		/* (17) TCheckSumValue */
		if (this.checkSumValue != null) {
			this.checkSumValue.encode(param, TCheckSumValue.PNAME_CHECKSUMVALUE);
		}
		/* (18) ArrayOfTMetaDataPathDetails arrayOfSubPaths */
		if (this.arrayOfSubPaths != null) {
			this.arrayOfSubPaths.encode(param,
				ArrayOfTMetaDataPathDetail.PNAME_ARRAYOFSUBPATHS);
		}

		// Add Hastable to global vector
		list.add(param);
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append(stfn.toString() + "\n");
		return sb.toString();
	}
}
