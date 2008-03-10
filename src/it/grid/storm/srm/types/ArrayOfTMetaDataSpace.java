/**
 * This class represents a TTSpace Token
 *
 * @author  EGRID ICTP Trieste / CNAF Bologna
 * @date    March 23rd, 2005
 * @version 2.0
 */

package it.grid.storm.srm.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import it.grid.storm.srm.types.TMetaDataSpace;
import java.io.Serializable;

public class ArrayOfTMetaDataSpace implements Serializable {
    public static String PNAME_ARRAYOFSPACEDETAILS = "arrayOfSpaceDetails";
    
//    private static String 
//    private static String 
//    private static String 
//    private static String 

    ArrayList  metaDataList;

    /**
     * Constructor that requires a String. If it is null, then an
     * InvalidArrayOfTTMetaDataSpaceAttributeException is thrown.
     */
    public ArrayOfTMetaDataSpace(TMetaDataSpace[] tokenArray) throws InvalidArrayOfTMetaDataSpaceAttributeException {

        if (tokenArray==null) throw new InvalidArrayOfTMetaDataSpaceAttributeException(tokenArray);
        //FIXME this.tokenArray = tokenArray;
    } 
	
    public ArrayOfTMetaDataSpace() {
	    metaDataList = new ArrayList();
    }
   
    
    public TMetaDataSpace[] getArray()
    {
      return (TMetaDataSpace[]) metaDataList.toArray();
    }

    public TMetaDataSpace getTMetaDataSpace(int i) {
        return (TMetaDataSpace) metaDataList.get(i);
	}

    public void setTMetaDataSpace(int index, TMetaDataSpace data) {
	    metaDataList.set(index, data);
    }

    public void addTMetaDataSpace(TMetaDataSpace data) {
	    metaDataList.add(data);
    }

    public int size(){
        return metaDataList.size();
    }
    
    public void encode(Map outputParam, String fieldName) {
        ArrayList metaDataSpaceList = new ArrayList();
        int arraySize = this.size();
        
        for (int i=0; i<arraySize; i++) {
            Map metaDataSpace = new HashMap();
            TMetaDataSpace metaDataElement = this.getTMetaDataSpace(i);
            metaDataElement.encode(metaDataSpace);
            
            metaDataSpaceList.add(metaDataSpace);
        }
        
        outputParam.put(fieldName, metaDataSpaceList);
    }
}
