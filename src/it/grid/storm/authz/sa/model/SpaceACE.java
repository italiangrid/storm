/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.authz.sa.model;



public class SpaceACE {

    public static final String ACE_PREFIX = "ace.";
    
    private int aceNumber;
    private SubjectType subjectType;
    private SubjectPattern subjectPattern;
    private SpaceAccessMask spaceAccessMask;
    private AceType aceType;

    public SpaceACE() {
    }
     

    /**
     * @return the aceNumber
     */
    public int getAceNumber() {
        return aceNumber;
    }

    /**
     * @param aceNumber the aceNumber to set
     */
    public void setAceNumber(int aceNumber) {
        this.aceNumber = aceNumber;
    }
    
	public void setSubjectType(SubjectType subjectType) {
        this.subjectType = subjectType;
    }

    public void setSubjectPattern(SubjectPattern subject) {
        subjectPattern = subject;
    }

    public void setSpaceAccessMask(SpaceAccessMask spAccessMask) {
        spaceAccessMask = spAccessMask;
    }

    public void setAceType(AceType aceType) {
        this.aceType = aceType;
    }

    public SubjectType getSubjectType() {
        return subjectType;
    }

    public SubjectPattern getSubjectPattern() {
        return subjectPattern;
    }

    /**
     * @return the spacePermission
     */
    public SpaceAccessMask getSpaceAccessMask() {
        return spaceAccessMask;
    }
    
    public AceType getAceType() {
        return aceType;
    }

    @Override
	public String toString() {
        String spacePermissionStr = spaceAccessMask.toString();
        return "SpaceACE ("+getAceNumber()+"): "+getSubjectType()+":"+getSubjectPattern()+":"+spacePermissionStr+":"+aceType;
    }


}
