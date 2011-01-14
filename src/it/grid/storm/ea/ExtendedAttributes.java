/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
