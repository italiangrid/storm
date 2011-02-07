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


import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import it.grid.storm.common.types.VO;

/**
 * Encapsulates user Grid credentials access, and maps those to a local
 * user account.  Has methods to extract the permanent identifier
 * (subject DN), VO and VOMS group/role membership from the X.509
 * certificate / GSI proxy that the user presented to StoRM.  Will
 * also invoke LCMAPS library to map the Grid credentials to a local
 * user account.
 *
 * @todo implement a flyweight pattern, so that we don't have 1'000
 * different GridUser objects for 1'000 requests from the same user...
 *
 *
 */
public class VomsGridUser extends AbstractGridUser implements Serializable {


    private List<FQAN> fqans = new ArrayList<FQAN> ();
    private List<String> fqansString = new ArrayList<String>();

    // --- public accessor methods --- //

    VomsGridUser(MapperInterface mapper, String distinguishedName)
    {
        super(mapper, distinguishedName);
    }

    VomsGridUser(MapperInterface mapper, String distinguishedName, String proxy)
    {
        this(mapper, distinguishedName);
        this.setProxyString(proxy);
    }

    VomsGridUser(MapperInterface mapper, String distinguishedName, String proxy, FQAN[] fqansArray)
    {
        this(mapper, distinguishedName, proxy);
        this.setFqans(fqansArray);
    }

    // --- SETTER Methods --- //

    private void setFqans(FQAN[] fqans)
    {
        this.fqans.clear();
        this.fqansString.clear();
        for (FQAN fqan : fqans)
        {
            this.fqans.add(fqan);
            this.fqansString.add(fqan.toString());
        }
    }
    
    void setFqans(List<FQAN> fqans)
    {
        this.fqans.clear();
        this.fqansString.clear();
        for (FQAN fqan : fqans)
        {
            this.fqans.add(fqan);
            this.fqansString.add(fqan.toString());
        }
    }

    public void addFqan(FQAN fqan)
    {
        this.fqans.add(fqan);
        this.fqansString.add(fqan.toString());

    }
    
    // --- GETTER Methods --- //

    public List<FQAN> getFQANsList()
    {
        return this.fqans;
    }

    public List<String> getFQANsStringList()
    {
        return this.fqansString;
    }
    
    /**
     * Return <code>true</code> if any VOMS attributes are stored in
     * this object.
     *
     * <p> If the explicit constructor {@link VomsGridUser(String,
     * Fqan[], String)} was used, then this flag will be true if the
     * <code>Fqan[]</code> parameter was not null in the constructor
     * invocation.
     *
     * @return <code>true</code> if any VOMS attributes are stored in
     *         this object.
     */
    public boolean hasVoms()
    {
        if ((this.fqans != null) && (this.fqans.size() > 0))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Return the local user on wich the GridUser is mapped.
     *
     * @throws CannotMapUserException
     * @return LocalUser
     */
    public LocalUser getLocalUser() throws CannotMapUserException
    {
        if (null == localUser)
        {
            log.debug("VomsGridUser.getLocalUser");

            // call LCMAPS and do the mapping
            try
            {
                localUser = userMapperClass.map(getDn(), fqansString.toArray(new String[0]));
            }
            catch (CannotMapUserException ex)
            {
                // log the operation that failed
                log.error("Error in mapping '" + this + "' to a local user: " + ex.getMessage());
                // re-throw same exception
                throw ex;
            }
        }
        return localUser;
    }

    public VO getVO()
    {
        VO result = VO.makeNoVo();
        if ((fqans != null) && (fqans.size() > 0))
        {
            FQAN firstFQAN = fqans.get(0);
            String voName = firstFQAN.getVo();
            result = VO.make(voName);
        }
        return result;
    }

    /**
     * Print a string representation of this object, in the form
     * <code>GridUser:"</code><i>subject DN</i><code>"</code>.
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Grid User (VOMS) = ");
        sb.append(" DN:'"+getDistinguishedName().getX500DN_rfc1779()+"'");
        sb.append(" FQANS:"+fqans);
        return sb.toString();
    }
}
