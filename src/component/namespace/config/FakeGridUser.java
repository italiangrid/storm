package component.namespace.config;

import it.grid.storm.common.types.VO;
import it.grid.storm.griduser.*;

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
public class FakeGridUser implements GridUserInterface {

    private String dn;
    private VO vo;

    public FakeGridUser(String dn, String mainVoName) {
        this.dn = dn;
        this.vo = VO.make(mainVoName);
    }

    /**
     * Get GridUser Domain Name.
     *
     * @return String
     * @todo Implement this it.grid.storm.griduser.GridUserInterface method
     */
    public String getDn() {
        return dn;
    }

    /**
     * Return the local user on wich the GridUser is mapped.
     *
     * @throws CannotMapUserException
     * @return LocalUser
     * @todo Implement this it.grid.storm.griduser.GridUserInterface method
     */
    public LocalUser getLocalUser() throws CannotMapUserException {
        return new LocalUser(0,0);
    }

    /**
     * Return the LocalUser Name String on wich the GridUser is mapped.
     *
     * @return String
     * @todo Implement this it.grid.storm.griduser.GridUserInterface method
     */
    public String getLocalUserName() {
        return "no-name";
    }

    /**
     * Return the main Virtual Organization of the User.
     *
     * @return VO
     * @todo Implement this it.grid.storm.griduser.GridUserInterface method
     */
    public VO getMainVo() {
        return null;
    }
}
