package it.grid.storm.authz.sa.model;

public class SubjectType {

    public final static SubjectType DN = new SubjectType("dn");
    public final static SubjectType FQAN = new SubjectType("fqan");
    public final static SubjectType UNKNOWN = new SubjectType("UNKNOWN");

    private String subjectType;

    private SubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public static SubjectType getSubjectType(String subjectTp) {
        if (subjectTp.toLowerCase().equals(DN.toString())) return SubjectType.DN;
        if (subjectTp.toLowerCase().equals(FQAN.toString())) return SubjectType.FQAN;
        return SubjectType.UNKNOWN;
    }

    public String toString() {
        return this.subjectType;
    }
}
