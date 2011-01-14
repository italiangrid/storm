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

package it.grid.storm.griduser;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


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
public class VONameMatchingRule {

    private static final String ADMIT_ALL = ".*";

    private String voNameString = null;
    private Pattern voNamePattern = null;

    /**
     * Default Constructor
     *
     * @param regularExpressionRule String
     */
    public VONameMatchingRule(String regularExpressionRule) {
        if ( (regularExpressionRule == null) || (regularExpressionRule.equals("*"))) {
            init("*");
        } else {
            init(regularExpressionRule);
        }
    }

    /**
     * init
     *
     * @param regularExpressionRule String
     */
    private void init(String voNameString) {
        this.voNameString = voNameString;

        //VOName
        if (voNameString != null) {
            if (voNameString.equals("*")) {
                this.voNameString = ".*";
            }
            voNamePattern = Pattern.compile(this.voNameString);
        }
        else {
            voNameString = ADMIT_ALL;
            voNamePattern = Pattern.compile(ADMIT_ALL);
        }
    }

    /**
     * Match voName with this MatchingRule
     *
     * @param voName String
     * @return boolean
     */
    public boolean match(String voName) {
        boolean result = false;
        CharSequence voNameSequence = voName.subSequence(0, voName.length());
        Matcher voNameMatcher = voNamePattern.matcher(voNameSequence);
        result = voNameMatcher.find();
        return result;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(" VONAME=" + voNameString);
        return result.toString();
    }


}
