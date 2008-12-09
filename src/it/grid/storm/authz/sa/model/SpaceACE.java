package it.grid.storm.authz.sa.model;

import java.util.*;

public class SpaceACE {

    private SubjectType subjectType;
    private String subject;
    private List<SpacePermission> spacePermission = new ArrayList();
    private AceType aceType;

    public SpaceACE() {
    }

    public void setSubjectType(SubjectType subjectType) {
        this.subjectType = subjectType;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void addSpacePermission(SpacePermission spacePermission) {
        this.spacePermission.add(spacePermission);
    }

    public void setAceType(AceType aceType) {
        this.aceType = aceType;
    }

    public SubjectType getSubjectType() {
        return this.subjectType;
    }

    public String getSubject() {
        return this.subject;
    }

    public List<SpacePermission> getSpacePermissions() {
        return spacePermission;
    }

    public AceType getAceType() {
        return this.aceType;
    }

    public String toString() {
        String spacePermissionStr = "";
        for (Iterator iter = spacePermission.iterator(); iter.hasNext(); ) {
            SpacePermission item = (SpacePermission) iter.next();
            spacePermissionStr+=item.toString();
        }
        return "SpaceACE : "+this.getSubjectType()+":"+this.getSubject()+":"+spacePermissionStr+":"+this.aceType;
    }

}
