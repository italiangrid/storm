package it.grid.storm.catalogs.surl;


public class SURLStatusCheckerFactory {

  public static SURLStatusChecker createSURLStatusChecker(){
    return new SURLStatusCheckerImpl();
  }
  
}
