package it.grid.storm.namespace.model;

public class StorageClassType {

    public final static StorageClassType T0D0 = new StorageClassType("T0D0", "T0D0");
    public final static StorageClassType T0D1 = new StorageClassType("T0D1", "T0D1");
    public final static StorageClassType T1D0 = new StorageClassType("T1D0", "T1D0");
    public final static StorageClassType T1D1 = new StorageClassType("T1D1", "T1D1");
    public final static StorageClassType UNKNOWN = new StorageClassType("UNKNOWN",
                                                                        "Storage Class Type UNKNOWN!");
    private String storageClassTypeString;
    private String stringSchema;
    private StorageClassType storageClassType;
    private boolean tapeEnabled = false;

    public StorageClassType(String storageClassTypeString, String stringSchema) {

        this.storageClassTypeString = storageClassTypeString;
        this.stringSchema = stringSchema;

        if (storageClassTypeString.equals(T0D0.toString())) {

            storageClassType = StorageClassType.T0D0;

        } else if (storageClassTypeString.equals(T0D1.toString())) {

            storageClassType = StorageClassType.T0D1;

        } else if (storageClassTypeString.equals(T1D0.toString())) {

            storageClassType = StorageClassType.T1D0;
            tapeEnabled = true;

        } else if (storageClassTypeString.equals(T1D1.toString())) {

            storageClassType = StorageClassType.T1D1;
            tapeEnabled = true;

        } else {

            storageClassType = StorageClassType.UNKNOWN;
        }
    }

    /**
     * 
     * @param storageClassTypeString String
     * @return StorageClassType
     */
    public static StorageClassType getStorageClassType(String storageClassTypeString) {

        if (storageClassTypeString.equals(T0D0.toString())) {
            return StorageClassType.T0D0;
        }
        
        if (storageClassTypeString.equals(T0D1.toString())) {
            return StorageClassType.T0D1;
        }
        
        if (storageClassTypeString.equals(T1D0.toString())) {
            return StorageClassType.T1D0;
        }
        
        if (storageClassTypeString.equals(T1D1.toString())) {
            return StorageClassType.T1D1;
        }

        return StorageClassType.UNKNOWN;
    }

    /**
     * Returns the storage class type identified by this instance.
     * 
     * @return the storage class type.
     */
    public StorageClassType getStorageClassType() {
        return storageClassType;
    }

    /**
     * Returns the String representation of this storage class type instance.
     * 
     * @return the String representation of this storage class type instance.
     */
    public String getStorageClassTypeString() {
        return storageClassTypeString;
    }

    public boolean isTapeEnabled() {
        return tapeEnabled;
    }

    //Only get method for Schema
    public String toString() {
        return this.stringSchema;
    }

}
