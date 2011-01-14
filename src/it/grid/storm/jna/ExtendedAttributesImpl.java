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

package it.grid.storm.jna;

import it.grid.storm.ea.AttributeNotFoundException;
import it.grid.storm.ea.ExtendedAttributes;
import it.grid.storm.ea.ExtendedAttributesException;
import it.grid.storm.ea.FileNotFoundException;
import it.grid.storm.ea.NotSupportedException;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class ExtendedAttributesImpl implements ExtendedAttributes {

    /* Functions implemented by libattr */
    private interface DMAPILibrary extends Library {
        
        DMAPILibrary INSTANCE = (DMAPILibrary) Native.loadLibrary(("attr"), DMAPILibrary.class);

        int getxattr(String fileName, String attributeName, byte[] bufp, int bufpsize);
        int removexattr(String fileName, String attributeName);
        int setxattr(String fileName, String attributeName, byte[] bufp, int bufpSize, int flags);

    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("usage: <command>\ncommands: get set rm check");
            System.exit(1);
        }

        ExtendedAttributes dmapi = new ExtendedAttributesImpl();

        if ("check".equals(args[0].toLowerCase())) {

            System.out.println("System last error is preserved: " + Native.getPreserveLastError());

        } else if ("get".equals(args[0].toLowerCase())) {

            if (args.length != 3) {
                System.out.println("usage: get <filename> <attributename>");
                System.exit(1);
            }

            String filename = args[1];
            String attributeName = args[2];

            try {

                byte[] byteArray = dmapi.getXAttr(filename, attributeName);

                System.out.println("Result: " + new String(byteArray));

            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + filename);
                return;

            } catch (AttributeNotFoundException e) {
                System.out.println("Attribute not found: " + attributeName);
                return;

            } catch (ExtendedAttributesException e) {
                System.out.println("Error: " + e.getMessage());
                return;
            }

        } else if ("set".equals(args[0].toLowerCase())) {

            if (args.length != 4) {
                System.out.println("usage: set <filename> <attribute_name> <attribute_value>");
                System.exit(1);
            }

            String filename = args[1];
            String attributeName = args[2];
            String attributeValue = args[3];

            try {
                dmapi.setXAttr(filename, attributeName, attributeValue.getBytes());
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + filename);
                return;

            } catch (ExtendedAttributesException e) {
                System.out.println("Error: " + e.getMessage());
                return;
            }

        } else if ("rm".equals(args[0].toLowerCase())) {

            if (args.length != 3) {
                System.out.println("usage: rm <filename> <attributename>");
                System.exit(1);
            }

            String filename = args[1];
            String attributeName = args[2];

            try {
                dmapi.rmXAttr(filename, attributeName);
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + filename);
                return;

            } catch (AttributeNotFoundException e) {
                System.out.println("Attribute not found: " + attributeName);
                return;

            } catch (ExtendedAttributesException e) {
                System.out.println("Error: " + e.getMessage());
                return;
            }

        } else {
            System.out.println("Unrecognized command.");
        }
    }

    /* (non-Javadoc)
     * @see it.grid.storm.ea.ExtendedAttributes#getXAttr(java.lang.String, java.lang.String)
     */
    public byte[] getXAttr(String fileName, String attributeName) throws ExtendedAttributesException {

        int bufpSize = 100;
        byte[] bufp = new byte[bufpSize];

        int attributeValueSize = DMAPILibrary.INSTANCE.getxattr(fileName, attributeName, bufp, bufpSize);

        if (attributeValueSize == -1) {

            int errno = Native.getLastError();

            if (errno == Errno.ERANGE) {

                attributeValueSize = DMAPILibrary.INSTANCE.getxattr(fileName, attributeName, bufp, 0);
                bufp = new byte[attributeValueSize];
                attributeValueSize = DMAPILibrary.INSTANCE.getxattr(fileName, attributeName, bufp, attributeValueSize);

                throw new ExtendedAttributesException("Error, errno value: " + errno);

            } else if (errno == Errno.ENOATTR) {

                throw new AttributeNotFoundException(attributeName);

            } else if ((errno == Errno.ENOENT) || (errno == Errno.ENOTDIR)) {

                throw new FileNotFoundException(fileName);

            } else if (errno == Errno.ENOTSUP) {

                throw new NotSupportedException();

            } else {
                throw new ExtendedAttributesException("Error, errno value: " + errno);
            }
        }

        byte[] result = new byte[attributeValueSize];

        for (int i = 0; i < attributeValueSize; i++) {
            result[i] = bufp[i];
        }

        return result;
    }
    
    public void rmXAttr(String fileName, String attributeName) throws ExtendedAttributesException {

        int ret = DMAPILibrary.INSTANCE.removexattr(fileName, attributeName);

        if (ret == -1) {
            
            int errno = Native.getLastError();

            if ((errno == Errno.ENOENT) || (errno == Errno.ENOTDIR)) {

                throw new FileNotFoundException(fileName);

            } else if (errno == Errno.ENOATTR) {

                throw new AttributeNotFoundException(attributeName);

            } else if (errno == Errno.ENOTSUP) {

                throw new NotSupportedException();

            } else {
                throw new ExtendedAttributesException("Error, errno value: " + errno);
            }
        }
    }

    public void setXAttr(String fileName, String attributeName, byte[] attributeValue)
            throws ExtendedAttributesException {

        if ((fileName == null) || (attributeName == null)) {
            throw new ExtendedAttributesException("Null input");
        }

        int bufpSize = 0;
        
        if (attributeValue != null) {
            bufpSize = attributeValue.length;
        }

        int ret = DMAPILibrary.INSTANCE.setxattr(fileName, attributeName, attributeValue, bufpSize, 0);

        if (ret == -1) {

            int errno = Native.getLastError();

            if ((errno == Errno.ENOENT) || (errno == Errno.ENOTDIR)) {

                throw new FileNotFoundException(fileName);

            } else if (errno == Errno.ENOSPC) {
                
                throw new ExtendedAttributesException("Not enough space on disk to write the extended attribute");
                
            } else if (errno == Errno.EDQUOT) {
                
                throw new ExtendedAttributesException("Not enough space on disk (due to quota enforcement) to write the extended attribute");
                
            } else if (errno == Errno.ENOTSUP) {

                throw new NotSupportedException();

            } else {
                throw new ExtendedAttributesException("Error, errno value: " + errno);
            }
        }
    }
}
