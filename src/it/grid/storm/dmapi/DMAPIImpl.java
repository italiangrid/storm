package it.grid.storm.dmapi;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class DMAPIImpl implements DMAPI {

    public interface DMAPILibrary extends Library {
        DMAPILibrary INSTANCE = (DMAPILibrary) Native.loadLibrary(("storm_dmutil"), DMAPILibrary.class);

        String error_string(int err);

        int dmutil_get_dmattr(String filename, String attributeName, PointerByReference bufp,
                IntByReference bufpSize);

        int dmutil_set_dmattr(String filename, String attributeName, String attributeValue);

        int dmutil_rm_dmattr(String filename, String attributeName);
    }

    public static final int FILE_NOT_FOUND = 1000;
    public static final int ATTRIBUTE_NOT_FOUND = 1001;

    public byte[] getDMAttr(String filename, String attributeName) throws DMAPIException {

        PointerByReference bufp = new PointerByReference();
        IntByReference bufpSize = new IntByReference();

        int ret = DMAPILibrary.INSTANCE.dmutil_get_dmattr(filename, attributeName, bufp, bufpSize);

        if (ret != 0) {

            if (ret == FILE_NOT_FOUND) {
                throw new FileNotFoundException(filename);
            }

            if (ret == ATTRIBUTE_NOT_FOUND) {
                throw new AttributeNotFoundException(attributeName);
            }

            String errMsg = DMAPILibrary.INSTANCE.error_string(ret);
            throw new DMAPIException(errMsg);
        }

        byte[] byteArray = bufp.getValue().getByteArray(0, bufpSize.getValue());

        return byteArray;
    }

    public void setDMAttr(String filename, String attributeName, String attributeVale) throws DMAPIException {

        int ret = DMAPILibrary.INSTANCE.dmutil_set_dmattr(filename, attributeName, attributeVale);

        if (ret != 0) {
            if (ret == FILE_NOT_FOUND) {
                throw new FileNotFoundException(filename);
            }

            String errMsg = DMAPILibrary.INSTANCE.error_string(ret);
            throw new DMAPIException(errMsg);
        }
    }
    
    public void rmDMAttr(String filename, String attributeName) throws DMAPIException {
        
        int ret = DMAPILibrary.INSTANCE.dmutil_rm_dmattr(filename, attributeName);
        
        if (ret != 0) {

            if (ret == FILE_NOT_FOUND) {
                throw new FileNotFoundException(filename);
            }

            if (ret == ATTRIBUTE_NOT_FOUND) {
                throw new AttributeNotFoundException(attributeName);
            }

            String errMsg = DMAPILibrary.INSTANCE.error_string(ret);
            throw new DMAPIException(errMsg);
        }
    }
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("usage: <command>\ncommands: get set rm");
            System.exit(1);
        }
     
        DMAPI dmapi = new DMAPIImpl();
        
        if ("get".equals(args[0].toLowerCase())) {
            
            if (args.length != 3) {
                System.out.println("usage: get <filename> <attributename>");
                System.exit(1);
            }
            
            String filename = args[1];
            String attributeName = args[2];
            
            try {
                
                byte[] byteArray = dmapi.getDMAttr(filename, attributeName);
                
                System.out.println("Result: " + new String(byteArray));
                
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + filename);
                return; 
                
            } catch (AttributeNotFoundException e) {
                System.out.println("Attribute not found: " + attributeName);
                return;
                
            } catch (DMAPIException e) {
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
                dmapi.setDMAttr(filename, attributeName, attributeValue);
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + filename);
                return;
                
            } catch (DMAPIException e) {
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
                dmapi.rmDMAttr(filename, attributeName);
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + filename);
                return; 
                
            } catch (AttributeNotFoundException e) {
                System.out.println("Attribute not found: " + attributeName);
                return;
                
            } catch (DMAPIException e) {
                System.out.println("Error: " + e.getMessage());
                return;
            }
            
        } else {
            System.out.println("Unrecognized command.");
        }
    }
}
