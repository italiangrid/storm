package it.grid.storm.dmapi;

public interface DMAPI {

    /**
     * Retrieve a data management attribute.
     *  
     * @param filename name of the to retrieve the attribute for.
     * @param attributeName name of the attribute to retrieve.
     * @return the value of the requested attribute name, <code>null</code> in case of error.
     */
    byte[] getDMAttr(String filename, String attributeName) throws DMAPIException, FileNotFoundException, AttributeNotFoundException;
    
    public void setDMAttr(String filename, String attributeName, String attributeValue) throws DMAPIException, FileNotFoundException;
    
    public void rmDMAttr(String filename, String attributeName) throws DMAPIException, FileNotFoundException, AttributeNotFoundException;
}
