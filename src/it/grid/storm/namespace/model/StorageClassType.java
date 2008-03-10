package it.grid.storm.namespace.model;

public class StorageClassType {

  private String storageClassType;
  private String stringSchema;

  public final static StorageClassType T0D0 = new StorageClassType("T0D0", "T0D0");
  public final static StorageClassType T0D1 = new StorageClassType("T0D1", "T0D1");
  public final static StorageClassType T1D0 = new StorageClassType("T1D0", "T1D0");
  public final static StorageClassType T1D1 = new StorageClassType("T1D1", "T1D1");
  public final static StorageClassType UNKNOWN = new StorageClassType("UNKNOWN", "Storage Class Type UNKNOWN!");

  public StorageClassType(String storageClassType, String stringSchema) {
    this.storageClassType = storageClassType;
    this.stringSchema = stringSchema;
  }

  //Only get method for Name
  public String getStorageClassType() {
    return storageClassType;
  }

  //Only get method for Schema
  public String toString() {
    return this.stringSchema;
  }

  /**
   *
   * @param storageClassType String
   * @return StorageClassType
   */
  public static StorageClassType getStorageClassType(String storageClassType) {
    if (storageClassType.equals(T0D0.toString()))
      return StorageClassType.T0D0;
    if (storageClassType.equals(T0D1.toString()))
      return StorageClassType.T0D1;
    if (storageClassType.equals(T1D0.toString()))
      return StorageClassType.T1D0;
    if (storageClassType.equals(T1D1.toString()))
      return StorageClassType.T1D1;
    return StorageClassType.UNKNOWN;
  }


}
