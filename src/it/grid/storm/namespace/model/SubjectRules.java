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
