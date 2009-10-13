package it.grid.storm.synchcall.command.space.quota;

import java.util.*;

public class GPFSQuotaParameters implements QuotaParametersInterface {

  private static final String COMMAND = "mmlsquota";
  private List<String> parameters = null;

  public GPFSQuotaParameters(List<String> parameters) {
    this.parameters = parameters;
  }

  /**
   *
   * @return String
   * @todo Implement this
   *   it.grid.storm.synchcall.space.quota.QuotaParametersInterface method
   */
  public String getCommand() {
    return COMMAND;
  }

  /**
   *
   * @return List
   * @todo Implement this
   *   it.grid.storm.synchcall.space.quota.QuotaParametersInterface method
   */
  public List<String> getParameters() {
    return this.parameters;
  }
}
