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

/*
 * You may copy, distribute and modify this file under the terms of
 * the INFN GRID licence.
 * For a copy of the licence please visit
 *
 *    http://www.cnaf.infn.it/license.html
 *
 * Original design made by Riccardo Zappi <riccardo.zappi@cnaf.infn.it.it>, 2007
 *
 * $Id: DNMatchingRule.java,v 1.4 2007/05/22 19:54:54 rzappi Exp $
 *
 */

package it.grid.storm.griduser;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Iterator;

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
 * @version 1.1
 */
public class DNMatchingRule {

    private enum DNFields
    {
        COUNTRY("C"),
        ORGANIZATION("O"),
        ORGANIZATIONALUNIT("OU"),
        LOCALITY("L"),
        COMMONNAME("CN"),
        DOMAINCOMPONENT("DC"),
        UNKNOWN("");
        private final String code;

        private DNFields(String code) throws IllegalArgumentException
        {
            if(code == null)
            {
                throw new IllegalArgumentException("Received null code argument");
            }
            this.code = code;
        }
        
        public static DNFields fromString(String code)
        {
            for(DNFields field : DNFields.values())
            {
                if(field.code.equals(code))
                {
                    return field;
                }
            }
            return UNKNOWN;
        }
    }
    
    private static final String ADMIT_ALL = ".*";

    private String countryPatternString;
    private String organizationPatternString;
    private String organizationalUnitPatternString;
    private String localityPatternString;
    private String commonNamePatternString;
    private String domainComponentPatternString;

    private Pattern countryPattern = null;
    private Pattern organizationPattern = null;
    private Pattern organizationalUnitPattern = null;
    private Pattern localityPattern = null;
    private Pattern commonNamePattern = null;
    private Pattern domainComponentPattern = null;

    public static DNMatchingRule buildMatchAllDNMatchingRule()
    {
        return new DNMatchingRule(ADMIT_ALL, ADMIT_ALL, ADMIT_ALL, ADMIT_ALL, ADMIT_ALL, ADMIT_ALL);
    }

    /**
     * Constructor with implicit Pattern String
     *
     * @param regularExpressionRule String
     */
    public DNMatchingRule(String regularExpressionRule)
    {
        if ((regularExpressionRule == null) || regularExpressionRule.trim().equals("*")
                || (regularExpressionRule.trim().equals(".*")))
        {
            this.countryPatternString = ADMIT_ALL;
            this.organizationPatternString = ADMIT_ALL;
            this.organizationalUnitPatternString = ADMIT_ALL;
            this.localityPatternString = ADMIT_ALL;
            this.commonNamePatternString = ADMIT_ALL;
            this.domainComponentPatternString = ADMIT_ALL;
        }
        else
        {
            // Split the rule into the attribute rules
            String[] rules = regularExpressionRule.split("/");
            if (rules != null)
            {
                for (int i = 0; i < rules.length; i++)
                {
                    if(rules[i].indexOf('=') < 0)
                    {
                        continue;
                    }
                    String[] elementCoupple = rules[i].split("=");
                    if(elementCoupple.length != 2)
                    {
                        continue;
                    }
                    switch (DNFields.fromString(elementCoupple[0]))
                    {
                        case COUNTRY:
                            countryPatternString = elementCoupple[1];
                            break;
                        case ORGANIZATION:
                            organizationPatternString = elementCoupple[1];
                            break;
                        case ORGANIZATIONALUNIT:
                            organizationalUnitPatternString = elementCoupple[1];
                            break;
                        case LOCALITY:
                            localityPatternString = elementCoupple[1];
                            break;
                        case COMMONNAME:
                            commonNamePatternString = elementCoupple[1];
                            break;
                        case DOMAINCOMPONENT:
                            domainComponentPatternString = elementCoupple[1];
                            break;
                        default:
                            break;
                    }
                }
            }
            else
            {
                countryPatternString = ADMIT_ALL;
                organizationPatternString = ADMIT_ALL;
                organizationalUnitPatternString = ADMIT_ALL;
                localityPatternString = ADMIT_ALL;
                commonNamePatternString = ADMIT_ALL;
                domainComponentPatternString = ADMIT_ALL;
            }
        }
        initPatterns();
    }

    /**
     * private method used to initialize everything
     *
     * @param countryPatternString String
     * @param organizationPatternString String
     * @param organizationalUnitPatternString String
     * @param localityPatternString String
     * @param commonNameString String
     */
    private void initPatterns() {

        //C country
        if (isMatchAll(countryPatternString))
        {
            countryPattern = Pattern.compile(ADMIT_ALL);
        }
        else
        {
            countryPattern = Pattern.compile(this.countryPatternString);
        }

        //O organization
        if (isMatchAll(organizationPatternString))
        {
            organizationPattern = Pattern.compile(ADMIT_ALL);
        }
        else
        {
            organizationPattern = Pattern.compile(this.organizationPatternString);
        }

        //OU organizationalUnit
        if (isMatchAll(organizationalUnitPatternString))
        {
            organizationalUnitPattern = Pattern.compile(ADMIT_ALL);
        }
        else
        {
            organizationalUnitPattern = Pattern.compile(this.organizationalUnitPatternString);
        }

        //L locality
        if (isMatchAll(localityPatternString))
        {
            localityPattern = Pattern.compile(ADMIT_ALL);
        }
        else
        {
            localityPattern = Pattern.compile(this.localityPatternString);
        }

        // CN Common Name
        if (isMatchAll(commonNamePatternString))
        {
            commonNamePattern = Pattern.compile(ADMIT_ALL);
        }
        else
        {
            commonNamePattern = Pattern.compile(this.commonNamePatternString);
        }

        // DC Domain Component
        if (isMatchAll(domainComponentPatternString))
        {
            domainComponentPattern = Pattern.compile(ADMIT_ALL);
        }
        else
        {
            domainComponentPattern = Pattern.compile(this.domainComponentPatternString);
        }
    }

    private static boolean isMatchAll(String pattern)
    {
        return pattern == null || pattern.trim().equals("*") || pattern.trim().equals(".*");
    }

    /**
     * Constructor with explicited Pattern String
     *
     * @param countryPatternString String
     * @param organizationPatternString String
     * @param organizationalUnitPatternString String
     * @param localityPatternString String
     * @param commonNameString String
     */
    public DNMatchingRule(String countryPatternString,
                          String organizationPatternString,
                          String organizationalUnitPatternString,
                          String localityPatternString,
                          String commonNamePatternString,
                          String domainComponentPatternString) {
        this.countryPatternString = countryPatternString;
        this.organizationPatternString = organizationPatternString;
        this.organizationalUnitPatternString = organizationalUnitPatternString;
        this.localityPatternString = localityPatternString;
        this.commonNamePatternString = commonNamePatternString;
        this.domainComponentPatternString = domainComponentPatternString;
        initPatterns();
    }

    public boolean isMatchAll()
    {
        return isMatchAll(countryPatternString) && isMatchAll(organizationPatternString)
                && isMatchAll(organizationalUnitPatternString) && isMatchAll(localityPatternString)
                && isMatchAll(commonNamePatternString) && isMatchAll(domainComponentPatternString);
    }


    /**
     *
     * @param principalDN DistinguishedName
     * @return boolean
     *
     * @todo Implement performance. After first false exit with false!
     */
    public boolean match(DistinguishedName principalDN) throws IllegalArgumentException {
        if(principalDN == null)
        {
            throw new IllegalArgumentException("Unable to perform rule matching. Received null argument: principalDN=" + principalDN);
        }
        if (this.isMatchAll())
        {
            return true;            
        }
        boolean result = false;
        boolean countryMatch = false;
        boolean organizationMatch = false;
        boolean localityMatch = false;
        boolean organizationalUnitMatch = false;
        boolean commonNameMatch = false;
        boolean domainComponentMatch = false;


        //C
        String countryName = principalDN.getCountryName();
        if (countryName != null) {
	        CharSequence country = countryName.subSequence(0, countryName.length());
	        Matcher countryMatcher = countryPattern.matcher(country);
	        countryMatch = countryMatcher.find();
        } else {
        	countryMatch = countryPatternString.equals(ADMIT_ALL);
        }
        if (!(countryMatch)) return false;

        //O
        String organizationName = principalDN.getOrganizationName();
        if (organizationName != null) {
        	CharSequence organization = organizationName.subSequence(0, organizationName.length());
        	Matcher organizationMatcher = organizationPattern.matcher(organization);
        	organizationMatch = organizationMatcher.find();
        } else {
        	organizationMatch = organizationPatternString.equals(ADMIT_ALL);
        }
        if (!(organizationMatch)) return false;

        //L
        String localityName = principalDN.getLocalityName();
        if (localityName != null) {
            CharSequence locality = localityName.subSequence(0, localityName.length());
            Matcher localityMatcher = localityPattern.matcher(locality);
            localityMatch = localityMatcher.find();
        } else {
            localityMatch = localityPatternString.equals(ADMIT_ALL);
        }
        if (!(localityMatch)) return false;

        //OU ArrayList
        ArrayList<String> organizationalUnitNames = principalDN.getOrganizationalUnitNames();
        if ((organizationalUnitNames != null) && (!(organizationalUnitNames.isEmpty()))) {
            CharSequence organizationalUnit = null;
            String nameStr = null;
            Matcher organizationalUnitMatcher = null;
            for (Iterator<String> name = organizationalUnitNames.iterator(); name.hasNext(); ) {
                nameStr = name.next();
                organizationalUnit = nameStr.subSequence(0, nameStr.length());
                organizationalUnitMatcher = organizationalUnitPattern.matcher(organizationalUnit);
                organizationalUnitMatch = organizationalUnitMatcher.find();
                if (organizationalUnitMatch) break;
            }
        } else {
            organizationalUnitMatch = organizationalUnitPatternString.equals(ADMIT_ALL);
        }
        if (!(organizationalUnitMatch)) return false;

        //CN ArrayList
        ArrayList<String> commonNames = principalDN.getCommonNames();
        if ((commonNames != null) && (!(commonNames.isEmpty()))) {
            CharSequence commonName = null;
            String commonNameStr = null;
            Matcher commonNameMatcher = null;
            for (Iterator<String> scanCN = commonNames.iterator(); scanCN.hasNext(); ) {
                commonNameStr = scanCN.next();
                commonName = commonNameStr.subSequence(0, commonNameStr.length());
                commonNameMatcher = commonNamePattern.matcher(commonName);
                commonNameMatch = commonNameMatcher.find();
                if (commonNameMatch) break;
            }
        } else {
            commonNameMatch = commonNamePatternString.equals(ADMIT_ALL);
        }
        if (!(commonNameMatch)) return false;

        //DC ArrayList
        ArrayList<String> domainComponents = principalDN.getDomainComponents();
        if ((domainComponents != null) && (!(domainComponents.isEmpty()))) {
            CharSequence domainComponent = null;
            String domainComponentStr = null;
            Matcher domainComponentMatcher = null;
            for (Iterator<String> scanDC = domainComponents.iterator(); scanDC.hasNext(); ) {
                domainComponentStr = scanDC.next();
                domainComponent = domainComponentStr.subSequence(0, domainComponentStr.length());
                domainComponentMatcher = domainComponentPattern.matcher(domainComponent);
                domainComponentMatch = domainComponentMatcher.find();
                if (domainComponentMatch) break;
            }
        } else {
            domainComponentMatch = commonNamePatternString.equals(ADMIT_ALL);
        }
        if (!(domainComponentMatch)) return false;

        //Total Result
        // NOTE : At this point result should be always TRUE!
        result = countryMatch && organizationMatch && organizationalUnitMatch &&
                 localityMatch && commonNameMatch && domainComponentMatch;
        return result;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(" C=" + countryPatternString);
        result.append(" O=" + organizationPatternString);
        result.append(" OU=" + organizationalUnitPatternString);
        result.append(" L=" + localityPatternString);
        result.append(" CN=" + commonNamePatternString);
        return result.toString();
    }

}
