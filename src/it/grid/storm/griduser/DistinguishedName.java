/*
 * You may copy, distribute and modify this file under the terms of
 * the INFN GRID licence.
 * For a copy of the licence please visit
 *
 *    http://www.cnaf.infn.it/license.html
 *
 * Original design made by Riccardo Zappi <riccardo.zappi@cnaf.infn.it.it>, 2007
 *
 * $Id: DistinguishedName.java,v 1.5 2007/05/22 19:54:54 rzappi Exp $
 *
 */

package it.grid.storm.griduser;

import javax.security.auth.x500.X500Principal;
import java.util.ArrayList;
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
 * @version 1.0
 */
public class DistinguishedName {

    /**
        C       Country Name
        ST      State Or ProvinceName
        O       Organization Name
        OU      Organizational Unit Name
        L       Locality Name
        CN      Common Name
        EMail   EMail address
        DC      Domain Component

        "/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Luca Magnoni/Email=luca.magnoni@cnaf.infn.it"
        "/DC=ch/DC=cern/OU=Organic Units/OU=Users/CN=elanciot/CN=576215/CN=Elisa Lanciotti"
        
        New in StoRM 1.4: service certificate support (please note the '/' char into the CN value...)
        "/DC=org/DC=doegrids/OU=Services/CN=sam/cdfsam1.cr.cnaf.infn.it"
     */

    private String countryName=null;
    private String provinceName=null;
    private String organizationName=null;
    private String localityName=null;
    private String canonizedProxyDN = null;
    private ArrayList<String> organizationalUnitNames= new ArrayList<String>();
    private ArrayList<String> commonNames= new ArrayList<String>();
    private ArrayList<String> domainComponents = new ArrayList<String>();

    private String eMailAddress=null;
    private String distinguishedName=null;

    private X500Principal x500DN = null;

    public DistinguishedName(String proxyDn) {
        if (proxyDn!=null) {
            this.distinguishedName = proxyDn;
            //Check the format of DN
            int slashIndex = distinguishedName.indexOf('/');
            int commaIndex = distinguishedName.indexOf(',');
            if (slashIndex>-1) parseDNslahed();
            if (commaIndex>-1) parseDNcommed();
            buildX500DN();
            
        } else {
            this.distinguishedName = "empty";
        }
    }

    private void assignAttributes(String[] dnChunk){
        if (dnChunk != null) {
            int length = dnChunk.length;
            for (int i = 0; i < length; i++) {
                if (dnChunk[i].startsWith("C=")) {
                    countryName = dnChunk[i].substring(2, dnChunk[i].length());
                }
                if (dnChunk[i].startsWith("ST=")) {
                    provinceName = dnChunk[i].substring(3, dnChunk[i].length());
                }
                if (dnChunk[i].startsWith("O=")) {
                    organizationName = dnChunk[i].substring(2, dnChunk[i].length());
                }
                if (dnChunk[i].startsWith("OU=")) {
                    organizationalUnitNames.add(dnChunk[i].substring(3, dnChunk[i].length()));
                }
                if (dnChunk[i].startsWith("L=")) {
                    localityName = dnChunk[i].substring(2, dnChunk[i].length());
                }
                if (dnChunk[i].startsWith("CN=")) {
                    commonNames.add(dnChunk[i].substring(3, dnChunk[i].length()));
                }
                if (dnChunk[i].startsWith("DC=")) {
                    domainComponents.add(dnChunk[i].substring(3, dnChunk[i].length()));
                }

                /**
                 * @todo : Implement case insentive for Attribute email
                 */
                if (dnChunk[i].startsWith("Email=")) {
                    eMailAddress = dnChunk[i].substring(6, dnChunk[i].length());
                }
                if (dnChunk[i].startsWith("E=")) {
                    eMailAddress = dnChunk[i].substring(2, dnChunk[i].length());
                }
                if (dnChunk[i].startsWith("EMailAddress=")) {
                    eMailAddress = dnChunk[i].substring(13, dnChunk[i].length());
                }
            }
        }
    }

    private void parseDNslahed() {
        
        /**
         * New parser.
         * Split by / doesn't work since DN could contain attributes with a '/' char as valid value.
         * 
         * The idea is start from the end of the DN string:
         * 
         *  - get last index of '=' char
         *  - get last index of '\' char on the left of the '='
         *  - Substring from the index obtained.
         *  - Add the resulting substring as an attribute-value pair of the DN
         *  - Cycle on each pair
         *  
         * In this way, at the end of the cycle the List will contains the whole set of attribute-value 
         * pairs composing the DN, in the reverse order. 
         * Then, a reverse add to a string buffer, separated with ',' , compose the 
         * final DN representation in comma separated String.
         * 
         */
        
        ArrayList<String> list = new ArrayList<String>();
        String DN = this.distinguishedName;
        boolean stop = false;

        while(!stop) {
            //Get index of lat '='
            int indexOfEq = DN.lastIndexOf('=');
            //Exit if it does not exists
            if(indexOfEq == -1) {
                stop = true;
                continue;
            }
            
            String tmpDN = DN.substring(0,indexOfEq);
            //Get index of the first '/' char on the left of the '='
            int indexOfAttr = tmpDN.lastIndexOf('/');

            //the substring from the indexOfAttr obtained to end of the String 
            //is a attr-value pair!
            // Add it to the results List.
            list.add(DN.substring(indexOfAttr+1, DN.length()));
            
            //Cut the result from the working DN string, and iterate. 
            DN = DN.substring(0,indexOfAttr);
        }

        StringBuffer sb = new StringBuffer();
        String[] attributes =  new String[list.size()];
        
        //Create a string representation of the DN.
        //Note that the result List contains attribute-value pair Strings in 
        //reverse order!
        
        for (int i=0;i<list.size();i++) {
            if(i==list.size()-1) 
                sb.append(list.get(list.size()-1-i));
            else        
                sb.append(list.get(list.size()-1-i)+",");
            
            //Prepare the array for attributes evaluation
            attributes[i] = list.get((list.size()-1-i));
        }
       
            
        canonizedProxyDN =  sb.toString();
        assignAttributes(attributes);
        
    }

    private void parseDNcommed() {
        String[] attributes = distinguishedName.split(",");
        //TODO set properly the canonizedDN string
        assignAttributes(attributes);
    }

    private void buildX500DN() {
        x500DN = new X500Principal(canonizedProxyDN);
    }

    public String getX500DNString(String format) {
        return (x500DN!=null?x500DN.getName(format):"");
    }

    public X500Principal getX500DN() {
        return this.x500DN;
    }


    public String getX500DN_rfc1779(){
        return (x500DN!=null?x500DN.getName(X500Principal.RFC1779):"");
    }

    public String getX500DN_canonical(){
        return (x500DN!=null?x500DN.getName(X500Principal.CANONICAL):"");
    }

    public String getX500DN_rfc2253(){
        return (x500DN!=null?x500DN.getName(X500Principal.RFC2253):"");
    }


    public String getCountryName() {
        return this.countryName;
    }

    public String getProvinceName() {
        return this.provinceName;
    }

    public String getOrganizationName() {
        return this.organizationName;
    }

    public ArrayList<String> getOrganizationalUnitNames() {
        return this.organizationalUnitNames;
    }

    public ArrayList<String> getDomainComponents() {
        return this.domainComponents;
    }

    public String getLocalityName() {
        return this.localityName;
    }

    public ArrayList<String> getCommonNames() {
        return this.commonNames;
    }

    public String getEMail() {
        return this.eMailAddress;
    }




    public String getDN() {
        return this.distinguishedName;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
         if (!(o instanceof DistinguishedName)) return false;

         final DistinguishedName dn = (DistinguishedName) o;

         if (!x500DN.equals(dn.getX500DN())) return false;

         return true;
    }


    public String toString() {
        StringBuffer result = new StringBuffer();
        if (countryName != null)
            result.append("C=" + countryName + "\n");
        if (provinceName != null)
            result.append("ST=" + provinceName + "\n");
        if (organizationName != null)
            result.append("O=" + organizationName + "\n");
        if (organizationalUnitNames != null) {
            for (Iterator<String> i = organizationalUnitNames.iterator(); i.hasNext(); )
              result.append("OU=" + i.next() + "\n");
        }

        if (localityName != null)
            result.append("L=" + localityName + "\n");
        if (commonNames != null) {
            for (Iterator<String> i = commonNames.iterator(); i.hasNext(); )
              result.append("CN=" + i.next() + "\n");
        }
        if (domainComponents != null) {
            for (Iterator<String> i = domainComponents.iterator(); i.hasNext(); )
              result.append("DC=" + i.next() + "\n");
        }
        if (eMailAddress != null)
            result.append("EMail=" + eMailAddress + "\n");
        return result.toString();
    }
}
