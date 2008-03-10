/**
 * This class represents the ArrayOfTSizeInBytes SRM type.
 *
 * @author  Alberto Forti
 * @author  CNAF - INFN  Bologna
 * @date    Luglio, 2006
 * @version 1.0
 */
package it.grid.storm.srm.types;

import it.grid.storm.common.types.SizeUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ArrayOfTSizeInBytes {
    public static String PNAME_arrayOfExpectedFileSizes = "arrayOfExpectedFileSizes";
    
    private ArrayList  sizeInBytesList;
    
    public ArrayOfTSizeInBytes() {
        sizeInBytesList = new ArrayList();
    }
    
    public static ArrayOfTSizeInBytes decode(Map inputParam, String fieldName) {
        List inputList = null;
        try {
        	 inputList = Arrays.asList((Object[]) inputParam.get(fieldName));
        } catch (NullPointerException e) {
        	//log.warn("Empty SURL array found!");
        }
       
        if (inputList == null) return null;
        
        ArrayOfTSizeInBytes list = new ArrayOfTSizeInBytes();
        for (int i=0; i<inputList.size(); i++) {
            TSizeInBytes size = null;
            String strLong = (String) inputList.get(i);
            try {
                size = TSizeInBytes.make(Long.parseLong(strLong), SizeUnit.BYTES);
            }
            catch (InvalidTSizeAttributesException e) {
                return null;
            }
            list.addTSizeInBytes(size);
        }
        return list;
    }

    public Object[] getArray()
    {
      return sizeInBytesList.toArray();
    }
    
    public TSizeInBytes getTSizeInBytes(int i) {
        return (TSizeInBytes) sizeInBytesList.get(i);
    }
    
    public void setTSizeInBytes(int index, TSizeInBytes size) {
        sizeInBytesList.set(index, size);
    }
    
    public void addTSizeInBytes(TSizeInBytes size) {
        sizeInBytesList.add(size);
    }
    
    public int size() {
        return sizeInBytesList.size();
    }
}
