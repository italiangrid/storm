package it.grid.storm.namespace.model;

import it.grid.storm.griduser.DNMatchingRule;
import it.grid.storm.griduser.VONameMatchingRule;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF</p>
 *
 * @author R.Zappi
 * @version 1.0
 */
public class SubjectRules {

    private String dnRule = null;
    private String voNameRule = null;

    private DNMatchingRule dnMatchingRule = null;
    private VONameMatchingRule voNameMatchingRule = null;
    private boolean vomsCertRequired = false;

    public SubjectRules(String dn) {
        this.dnRule = dn;
        this.dnMatchingRule = new DNMatchingRule(dn);
        this.voNameRule = "*";
        this.voNameMatchingRule = new VONameMatchingRule(voNameRule);
        this.vomsCertRequired = false;
    }

    public SubjectRules(String dn, String voName) {
        this.dnRule = dn;
        this.dnMatchingRule = new DNMatchingRule(dn);
        this.voNameRule = voName;
        this.voNameMatchingRule = new VONameMatchingRule(voName);
        if (voName!=null) {
            this.vomsCertRequired = true;
        } else {
            this.vomsCertRequired = false;
        }
    }

    public String getDNRule() {
        return this.dnRule;
    }

    public String getVONameRule() {
        return this.voNameRule;
    }

    public DNMatchingRule getDNMatchingRule() {
        return this.dnMatchingRule;
    }

    public VONameMatchingRule getVONameMatchingRule() {
        return this.voNameMatchingRule;
    }

    public boolean isVomsCertRequired() {
        return this.vomsCertRequired;
    }

}
