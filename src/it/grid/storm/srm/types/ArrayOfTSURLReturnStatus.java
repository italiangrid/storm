/**
 * This class represents a TExtraInfoArray
 *
 * @author  EGRID ICTP Trieste / CNAF Bologna
 * @date    March 23rd, 2005
 * @version 2.0
 */

package it.grid.storm.srm.types;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class ArrayOfTSURLReturnStatus  {

    public static String PNAME_ARRAYOFFILESTATUSES = "arrayOfFileStatuses";
    
    ArrayList  surlRetList;

    /**
     * Construct an ArrayOfTSURLReturnStatus of numItems empty elements.
     */
    public ArrayOfTSURLReturnStatus(int numItems)
    {
        surlRetList = new ArrayList(numItems);
    }
    
    /**
     * Constructor that requires a String. If it is null, then an
     * InvalidArrayOfTExtraInfoAttributeException is thrown.
     */
    public ArrayOfTSURLReturnStatus(TSURLReturnStatus[] surlArray) throws InvalidArrayOfTSURLReturnStatusAttributeException {

        if (surlArray == null) throw new InvalidArrayOfTSURLReturnStatusAttributeException(surlArray);
        //FIXME this.tokenArray = tokenArray;
    } 
	
    public ArrayOfTSURLReturnStatus() {
	    surlRetList = new ArrayList();
    }
   
    
    public ArrayList getArray()
    {
      return surlRetList;
    }

    public TSURLReturnStatus getTSURLReturnStatus(int i) {
        return (TSURLReturnStatus) surlRetList.get(i);
	}

    public void setTSURLReturnStatus(int index, TSURLReturnStatus surl) {
	    surlRetList.set(index, surl);
    }

    public void addTSurlReturnStatus(TSURLReturnStatus surl) {
	    surlRetList.add(surl);
    }

    public int size(){
        return surlRetList.size();
    }

    public void encode(Map outputParam, String name){ 
        List list = new ArrayList();
        for(int i=0;i<surlRetList.size();i++) {
           ((TSURLReturnStatus)surlRetList.get(i)).encode(list);
        }
        
        outputParam.put(name, list);
    }
}
