package it.grid.storm.ea;

public interface ExtendedAttributes {

    /**
     * Retrieve the value of a give extended attribute of a given file.
     * 
     * @param fileName complete path filename of the file to retrieve the attribute for.
     * @param attributeName name of the attribute to retrieve.
     * @return the value of the requested attribute.
     * 
     * @throws FileNotFoundException the given file does not exists.
     * @throws AttributeNotFoundException if <code>attributeName</code> does not exists.
     * @throws NotSupportedException if extended attributes are not supported by the filesystem.
     * @throws ExtendedAttributesException if an unrecognized error occurred (exception message is set).
     */
    byte[] getXAttr(String fileName, String attributeName) throws ExtendedAttributesException;

    /**
     * Sets the value of the extended attribute identified by <code>fileName</code> and associated
     * with the given file in the filesystem.
     * 
     * @param filename complete path filename of the file to set the attribute for.
     * @param attributeName name of the attribute.
     * @param attributeValue value of the attribute.
     * 
     * @throws FileNotFoundException the given file does not exists.
     * @throws NotSupportedException  if extended attributes are not supported by the filesystem.
     * @throws ExtendedAttributesException if an unrecognized error occurred (exception message is set).
     */
    public void setXAttr(String filename, String attributeName, byte[] attributeValue)
            throws ExtendedAttributesException;

    /**
     * Removes the extended attribute identified by <code>attributeName</code> and associated with
     * the given file in the filesystem.
     * 
     * @param filename
     * @param attributeName
     * 
     * @throws FileNotFoundException the given file does not exists.
     * @throws AttributeNotFoundException if <code>attributeName</code> does not exists.
     * @throws NotSupportedException  if extended attributes are not supported by the filesystem.
     * @throws ExtendedAttributesException if an unrecognized error occurred (exception message is set).
     */
    public void rmXAttr(String filename, String attributeName) throws ExtendedAttributesException;
}
