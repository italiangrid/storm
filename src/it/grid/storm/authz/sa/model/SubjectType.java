package it.grid.storm.authz.sa.model;

public class SubjectType {

    public final static SubjectType DN = new SubjectType("DN");
    public final static SubjectType FQAN = new SubjectType("FQAN");
    public final static SubjectType UNKNOWN = new SubjectType("UNKNOWN");

    private final String subjectType;

    private SubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public static SubjectType getSubjectType(String subjectTp) {
        if (subjectTp.toUpperCase().equals(DN.toString())) {
            return SubjectType.DN;
        }
        if (subjectTp.toUpperCase().equals(FQAN.toString())) {
            return SubjectType.FQAN;
        }
        return SubjectType.UNKNOWN;
    }

    @Override
    public String toString() {
        return this.subjectType;
    }
}
