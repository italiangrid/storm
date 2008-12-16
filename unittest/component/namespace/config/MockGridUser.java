package component.namespace.config;


import it.grid.storm.common.types.VO;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.griduser.DistinguishedName;

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
public class MockGridUser implements GridUserInterface {


    DistinguishedName dn = null;

    public MockGridUser() {
        this.dn = new DistinguishedName("CN=Riccardo Zappi,OU=Cnaf");
    }

  /**
   * Get GridUser Domain Name.
   *
   * @return String
   * @todo Implement this it.grid.storm.griduser.GridUserInterface method
   */
  public String getDn() {
    return "CN=Riccardo Zappi,OU=Cnaf";
  }


  /**
   * Return the local user on wich the GridUser is mapped.
   *
   * @throws CannotMapUserException
   * @return LocalUser
   * @todo Implement this it.grid.storm.griduser.GridUserInterface method
   */
  public LocalUser getLocalUser() throws CannotMapUserException {
    LocalUser localUser = new LocalUser(1001,2222);
    return localUser;
  }


  /**
   * Return the LocalUser Name String on wich the GridUser is mapped.
   *
   * @return String
   * @todo Implement this it.grid.storm.griduser.GridUserInterface method
   */
  public String getLocalUserName() {
    return "ritz";
  }


  /**
   * Return the main Virtual Organization of the User.
   *
   * @return VO
   * @todo Implement this it.grid.storm.griduser.GridUserInterface method
   */
  public VO getVO() {
    return VO.make("stormdev");
  }

    public DistinguishedName getDistinguishedName() {
        return null;
    }

    public String toString() {
        return "";
    }
}
