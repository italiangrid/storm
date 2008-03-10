package it.grid.storm.namespace.util.userinfo;

import java.util.List;
import java.util.Iterator;

public class UserInfoParameters {

  private static final String COMMAND_ID = "id";
  private static final String COMMAND_GETENT = "getent group";
  private List<String> parameters = null;

  public UserInfoParameters(List<String> parameters) {
    this.parameters = parameters;
  }

  /**
   *
   * @return String
   * @todo Implement this
   *   it.grid.storm.synchcall.space.quota.QuotaParametersInterface method
   */
  public String getCommandId() {
    return COMMAND_ID;
  }

  /**
   *
   * @return String
   */
  public String getCommandGetENT() {
    return COMMAND_GETENT;
  }


  /**
   *
   * @return List
   * @todo Implement this
   *   it.grid.storm.synchcall.space.quota.QuotaParametersInterface method
   */
  public List getParameters() {
    return this.parameters;
  }


  /**
   *
   * @return String
   */
  public String toString() {
    if (parameters==null) return "NULL parameters";
    if (parameters.isEmpty()) return "EMPTY parameters";
    StringBuffer result = new StringBuffer();
    Iterator<String> scan = parameters.iterator();
    while (scan.hasNext()) {
      result.append(scan.next());
      result.append(" ");
    }
    return result.toString();
  }

}
