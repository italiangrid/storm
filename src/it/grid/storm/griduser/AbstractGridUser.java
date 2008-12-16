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

import it.grid.storm.common.types.*;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

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

    protected static Log log = LogFactory.getLog("griduser");
    protected DistinguishedName subjectDN = null;
    protected String proxyString = null;
    protected Class userMapperClass = null;
    protected LocalUser localUser = null;


    protected AbstractGridUser(Class mapperClass) {
        this.userMapperClass = mapperClass;
    }


    /**
     * used by the GridUserFactory to set User Mapper Instance.
     * This method has package visibility.
     *
     * @param mapper MapperInterface
     */
    void setUserMapper(Class mapperClass) {
        this.userMapperClass = mapperClass;
    }

    /**
     * used by the GridUserFactory to set Proxy String.
     * This method has package visibility.
     *
     * @param proxy String
     */
    void setProxyString(String proxy) {
        this.proxyString = proxy;
    }

    /**
     * Get Proxy certificate if there.
     * Else return null.
     *
     *
     * @return String
     */
    public String getProxyString() {
        return this.proxyString;
    }

    /**
     * used by the GridUserFactory to set Distinguished Name.
     * This method has package visibility.
     *
     * @param dnString String
     */
    void setDistinguishedName(String dnString) {
        this.subjectDN = new DistinguishedName(dnString);
    }



    /**
     * Get Proxy certificate if there.
     * Else return null.
     *
     *
     * @return String
     */
    public String getUserCredentials() {
        return this.proxyString;
    }


    /**
     * Get GridUser Distinguish Name.
     *
     * @return String
     */
    public String getDn() {
        String dn = this.subjectDN.getDN();
        return dn;
    }


    /**
     * Get GridUser Domain Name.
     * Used for metadada purpose.
     *
     * @return DistinguishedName
     */
    public DistinguishedName getDistinguishedName() {
        return subjectDN;
    }


    /**
     * Return the local user on wich the GridUser is mapped.
     * This method is abstract.
     *
     * @throws CannotMapUserException
     * @return LocalUser
     */
    public abstract LocalUser getLocalUser() throws CannotMapUserException;



    /**
     * Return the main Virtual Organization of the User.
     * This method is abstract.
     *
     * @return VO
     */
    public abstract VO getVO();

}
