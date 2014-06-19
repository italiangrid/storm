package it.grid.storm.catalogs.surl;


public class SURLStatusManagerFactory {

  public static SURLStatusManager newSURLStatusManager(){
    return new SURLStatusManagerImpl();
  }
  
}
