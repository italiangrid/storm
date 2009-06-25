/**
 * 
 */
package it.grid.storm.health.external;

import it.grid.storm.health.HealthDirector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;



/**
 * @author zappi
 *
 */
public class FSMetadataStatus {

    private String pulseNumberStr = "";
    private long lifetime = -1L;
    private String lifetimeStr = "";
    private final int benchmarkCount = -1;
    private final Hashtable<String, Long> pathName = new Hashtable<String, Long>();

    
    /**
     * 
     */
    public FSMetadataStatus(ArrayList<String> storageAreasName) {
        super();
        pathName.put("Local", -1L);
        for (Object element : storageAreasName) {
            pathName.put((String) element, -1L);
        }
    }

    

    /**
    *
    * @param number long
    */
   public void setPulseNumber(long number) {
     this.pulseNumberStr = number + "";
     String prefix = "";
     for (int i = 0; i < (6 - pulseNumberStr.length()); i++) {
        prefix+=".";
     }
      this.pulseNumberStr = prefix + this.pulseNumberStr;
   }
    
   
   public void calculateLifeTime() {
       long bornTime = HealthDirector.getBornInstant();
       long now = System.currentTimeMillis();
       this.lifetime = now - bornTime;

       Date date = new Date(this.lifetime);
       SimpleDateFormat formatter = new SimpleDateFormat("mm.ss");
       String minsec = formatter.format(date);
       long hours = this.lifetime / 3600000;
       this.lifetimeStr = hours + ":" + minsec;
     }
   
   /**
   *
   * @return String
   */
  @Override
public String toString() {
    StringBuffer result = new StringBuffer();
    result.append(" [#"+this.pulseNumberStr+" lifetime="+this.lifetimeStr+"]");
    Enumeration<String> sas = pathName.keys();
    while( sas.hasMoreElements() ) {
      String sa = sas.nextElement();
      Long average = pathName.get(sa);
      result.append("SA('"+sa+"')="+average);
    }
    return result.toString();
  }
   
}
