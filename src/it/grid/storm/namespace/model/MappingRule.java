package it.grid.storm.namespace.model;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */
public class MappingRule {

    private String ruleName = null;
    private String stfn_root = null;
    private String mapped_fs = null;

    /**
     * Constructor
     *
     * @param ruleName String
     * @param stfn_root String
     * @param mapped_fs String
     */
    public MappingRule(String ruleName, String stfn_root, String mapped_fs) {
        this.ruleName = ruleName;
        this.stfn_root = stfn_root;
        this.mapped_fs = mapped_fs;
    }

    public String getRuleName() {
        return this.ruleName;
    }

    public String getStFNRoot() {
        return this.stfn_root;
    }

    public String getMappedFS() {
        return this.mapped_fs;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        String sep = System.getProperty("line.separator");
        sb.append(sep + "   Mapping rule name       : " + this.ruleName + sep);
        sb.append("      StFN-Root            : " + this.stfn_root + sep);
        sb.append("      VFS-Name             : " + this.mapped_fs + sep);
        return sb.toString();
    }

}
