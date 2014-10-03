package it.grid.storm.catalogs.surl;


public class SURLStatusManagerFactory {

  public static SURLStatusManager newSURLStatusManager(){
    
    SURLStatusManager delegate = new SURLStatusManagerImpl(); 
    return new InMemorySURLStatusManager(delegate);
  }
  
}
