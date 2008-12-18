package it.grid.storm.authz.sa.model;

public class AceType {

    public final static AceType ALLOW = new AceType("ALLOW");
    public final static AceType DENY = new AceType("DENY");
    public final static AceType UNKNOWN = new AceType("UNKNOWN");

    private String aceType;

    private AceType(String aceType) {
        this.aceType = aceType;
    }

    public static AceType getAceType(String aceTp) {
        if (aceTp.toUpperCase().equals(ALLOW.toString())) return AceType.ALLOW;
        if (aceTp.toUpperCase().equals(DENY.toString())) return AceType.DENY;
        return AceType.UNKNOWN;
    }

    public String toString() {
        return this.aceType;
    }

}
