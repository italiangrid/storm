package it.grid.storm;

public class Constants {

  public static Entry BE_VERSION = new Entry("BE-Version","1.3.19-02");

  public static class Entry
   {
     private String key;
     private String value;

     private Entry(String key, String value) {
       this.key = key;
       this.value = value;
     }

     public String getKey() {
       return this.key;
     }

     public String getValue() {
       return this.value;
     }
   }




}
