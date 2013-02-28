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
 * $Id: AbstractGridUser.java 3604 2007-05-22 11:16:27Z rzappi $
 *
 */


package it.grid.storm.griduser;

import it.grid.storm.common.types.VO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public abstract class AbstractGridUser implements GridUserInterface {

    protected static final Logger log = LoggerFactory.getLogger(AbstractGridUser.class);
    protected DistinguishedName subjectDN = null;
    protected String proxyString = null;
    protected MapperInterface userMapperClass = null;
    protected LocalUser localUser = null;

    /**
     * @param mapperClass
     * @param distinguishedName
     */
    protected AbstractGridUser(MapperInterface mapperClass, String distinguishedName)
    {
        if (mapperClass == null || distinguishedName == null)
        {
            log.error("Provided null parameter: mapperClass=\'" + mapperClass + "\' distinguishedName=\'"
                    + distinguishedName + "\'");
            throw new IllegalArgumentException("Provided null parameter: mapperClass=\'" + mapperClass
                    + "\' distinguishedName=\'" + distinguishedName + "\'");
        }
        this.userMapperClass = mapperClass;
        this.setDistinguishedName(distinguishedName);
    }

    /**
     * @param mapperClass
     * @param distinguishedName
     * @param proxy
     */
    protected AbstractGridUser(MapperInterface mapperClass, String distinguishedName, String proxy)
    {
        this(mapperClass, distinguishedName);
        this.setProxyString(proxy);
    }
    
    /**
     * used by the GridUserFactory to set User Mapper Instance.
     * This method has package visibility.
     *
     * @param mapper MapperInterface
     */
    void setUserMapper(MapperInterface mapperClass)
    {
        if (mapperClass == null)
        {
            throw new IllegalArgumentException("Provided null MapperInterface!");
        }
        this.userMapperClass = mapperClass;
    }

    /**
     * used by the GridUserFactory to set Distinguished Name.
     * This method has package visibility.
     *
     * @param dnString String
     */
    void setDistinguishedName(String dnString)
    {
        if (dnString == null)
        {
            throw new IllegalArgumentException("Provided null DistinguishedName!");
        }
        this.subjectDN = new DistinguishedName(dnString);
    }
    
    /**
     * Get GridUser Distinguish Name.
     *
     * @return String
     */
    public String getDn()
    {
        String dn = this.subjectDN.getDN();
        return dn;
    }

    /**
     * Get GridUser Domain Name.
     * Used for metadada purpose.
     *
     * @return DistinguishedName
     */
    public DistinguishedName getDistinguishedName()
    {
        return subjectDN;
    }
    
    /**
     * used by the GridUserFactory to set Proxy String.
     * This method has package visibility.
     *
     * @param proxy String
     */
    void setProxyString(String proxy)
    {
        this.proxyString = proxy;
    }

    /**
     * Get Proxy certificate if there.
     * Else return null.
     *
     *
     * @return String
     */
    public String getProxyString()
    {
        return this.proxyString;
    }
    
    /**
     * Get Proxy certificate if there.
     * Else return null.
     *
     *
     * @return String
     */
    public String getUserCredentials()
    {
        return this.proxyString;
    }

    /**
     * Return the local user on wich the GridUser is mapped.
     * This method is abstract.
     *
     * @throws CannotMapUserException
     * @return LocalUser
     */
    public LocalUser getLocalUser() throws CannotMapUserException
    {
        if (localUser == null)
        {
            try
            {
                if(this.hasVoms())
                {
                    localUser = userMapperClass.map(getDn(), this.getFQANsAsString());
                }
                else
                {
                    localUser = userMapperClass.map(getDn(), null);
                }
            }
            catch (CannotMapUserException ex)
            {
                // log the operation that failed
                log.error("Error in mapping '" + subjectDN.getX500DN_rfc1779() + "' to a local user: "
                        + ex.getMessage());
                // re-throw same exception
                throw ex;
            }
        }
        return localUser;
    }

    /**
     * @return
     */
    public abstract String[] getFQANsAsString();
    
    /**
     * @return
     */
    public abstract FQAN[] getFQANs();

    /**
     * @return
     */
    public abstract boolean hasVoms();

    /**
     * Return the main Virtual Organization of the User.
     * This method is abstract.
     *
     * @return VO
     */
    public abstract VO getVO();

}
