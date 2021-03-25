package it.grid.storm.asynch;

public interface Request {

  public String getUserDN();

  public String getSURL();

  /**
   * @return boolean
   */
  public boolean isResultSuccess();

}
