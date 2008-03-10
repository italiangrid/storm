package it.grid.storm.srm.types;

import java.util.Hashtable;
import java.util.Map;


/**
 * This class represents the TFileStorageType of an Srm request.
 *
 * @author  Magnoni Luca
 * @author  CNAF - INFN  Bologna
 * @date    Avril, 2005
 * @version 1.0
 */
public class TFileStorageType {

  private String fileType = null;
  public static String PNAME_FILESTORAGETYPE = "fileStorageType";
  
  public static final TFileStorageType VOLATILE = new TFileStorageType("Volatile");
  public static final TFileStorageType DURABLE = new TFileStorageType("Durable");
  public static final TFileStorageType PERMANENT = new TFileStorageType("Permanent");
  public static final TFileStorageType EMPTY = new TFileStorageType("Empty");

  private TFileStorageType(String fileType) {
    this.fileType = fileType;
  }


  public String toString() {
    return fileType;
  }


  public String getValue() {
    return fileType;
  }


  /**
   * Facility method to obtain a TFileStorageType object given its
   * String representation. If an invalid String is supplied, then an
   * EMPTY TFileStorageType is returned.
   */
  public static TFileStorageType getTFileStorageType(String type) {
    if (type.toLowerCase().replaceAll(" ", "").equals(VOLATILE.getValue().toLowerCase())) {
      return VOLATILE;
    }
    if (type.toLowerCase().replaceAll(" ", "").equals(PERMANENT.getValue().toLowerCase())) {
      return PERMANENT;
    }
    if (type.toLowerCase().replaceAll(" ", "").equals(DURABLE.getValue().toLowerCase())) {
      return DURABLE;
    }
    else {
      return EMPTY;
    }
  }
  
  /**
   * Facility method to obtain a TFileStorageType object given its
   * String representation. If an invalid String is supplied, then an
   * EMPTY TFileStorageType is returned.
   */
  public static TFileStorageType getTFileStorageType(int type) {
      switch (type) {
          case 0: return VOLATILE;
          case 1: return DURABLE; 
          case 2: return PERMANENT;
          default: return EMPTY;
      }
  }
  /**
   * Decode method use to create a TFileStorageType object from
   * the inforation containde into structured paramter receivec from FE. 
   * @param inputParam
   * @param name
   * @return
   */
 
  public static TFileStorageType decode(Map inputParam, String name) {
      Integer fileType = (Integer)inputParam.get(name);
      if(fileType!=null)
          return TFileStorageType.getTFileStorageType(fileType.intValue());
      else
          return TFileStorageType.EMPTY;
  }
  
  /**
   * Encode method use to Create a structured paramter that rapresents 
   * this object, used for pass information to FE.
   * @param param
   * @param name
   */
  public void encode(Map param, String name ) {
      Integer value = null;
      if(this.equals(TFileStorageType.VOLATILE))
          value = new Integer(0);
      if(this.equals(TFileStorageType.DURABLE))
          value = new Integer(1);
      if(this.equals(TFileStorageType.PERMANENT))
          value = new Integer(2);
      param.put(name, value);
  }

  /*    public static void main(String[] args) {
          //Testing all types
          System.out.println("Testing all types...");
          System.out.println("VOLATILE: "+TFileStorageType.VOLATILE+"; getTFileStorageType from String:"+TFileStorageType.getTFileStorageType(TFileStorageType.VOLATILE.toString()));
          System.out.println("DURABLE: "+TFileStorageType.DURABLE+"; getTFileStorageType from String:"+TFileStorageType.getTFileStorageType(TFileStorageType.DURABLE.toString()));
          System.out.println("PERMANENT: "+TFileStorageType.PERMANENT+"; getTFileStorageType from String:"+TFileStorageType.getTFileStorageType(TFileStorageType.PERMANENT.toString()));
          System.out.println("EMPTY: "+TFileStorageType.EMPTY+"; getTFileStorageType from String:"+TFileStorageType.getTFileStorageType(TFileStorageType.EMPTY.toString()));
      }*/

}
