package it.grid.storm.authz.sa.model;

import java.util.ArrayList;
import java.util.List;


public class SpaceACE {

    public static final String ACE_PREFIX = "ace.";
    
    private int aceNumber;
    private SubjectType subjectType;
    private SubjectPattern subjectPattern;
    private List<SpaceAccessMask> spacePermission = new ArrayList<SpaceAccessMask>();
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
        this.subjectPattern = subject;
    }

    public void addSpacePermission(SpaceAccessMask spacePermission) {
        this.spacePermission.add(spacePermission);
    }

    public void setAceType(AceType aceType) {
        this.aceType = aceType;
    }

    public SubjectType getSubjectType() {
        return this.subjectType;
    }

    public SubjectPattern getSubjectPattern() {
        return this.subjectPattern;
    }

    /**
     * @return the spacePermission
     */
    public List<SpaceAccessMask> getSpacePermission() {
        return spacePermission;
    }
    
    public AceType getAceType() {
        return this.aceType;
    }

    @Override
	public String toString() {
        String spacePermissionStr = "";
        
        for (Object element : spacePermission) {
            SpaceAccessMask item = (SpaceAccessMask) element;
            spacePermissionStr+=item.toString();
        }
        return "SpaceACE ("+this.getAceNumber()+"): "+this.getSubjectType()+":"+this.getSubjectPattern()+":"+spacePermissionStr+":"+this.aceType;
    }


    /**
     * @param spacePermission the spacePermission to set
     */
    public void setSpacePermission(List<SpaceAccessMask> spacePermission) {
        this.spacePermission = spacePermission;
    }

}
