/**
 * This class represents an ArrayOfTSURLLifetimeReturnStatus.
 * @author  Alberto Forti
 * @author  CNAF Bologna
 * @date    Dec 2006
 * @version 1.0
 */
package it.grid.storm.srm.types;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

public class ArrayOfTSURLLifetimeReturnStatus
{
    public static String PNAME_ARRAYOFFILESTATUSES = "arrayOfFileStatuses";

    ArrayList            array;

    /**
     * Constructs an ArrayOfTSURLLifetimeReturnStatus of 'numItems' empty elements.
     * @param numItems
     */
    public ArrayOfTSURLLifetimeReturnStatus(int numItems) {
        array = new ArrayList(numItems);
    }

    //    /**
    //     * Constructor that requires a String. If it is null, then an
    //     * InvalidArrayOfTExtraInfoAttributeException is thrown.
    //     */
    //    public ArrayOfTSURLLifetimeReturnStatus(TSURLReturnStatus[] surlArray) throws InvalidArrayOfTSURLReturnStatusAttributeException {
    //
    //        if (surlArray == null) throw new InvalidArrayOfTSURLReturnStatusAttributeException(surlArray);
    //        //FIXME this.tokenArray = tokenArray;
    //    } 
    
    /**
     * Constructs an empty ArrayOfTSURLLifetimeReturnStatus.
     */
    public ArrayOfTSURLLifetimeReturnStatus() {
        array = new ArrayList();
    }
    
    /**
     * Get the array list.
     * @return ArrayList
     */
    public ArrayList getArray()
    {
        return array;
    }
    
    /**
     * Get the i-th element of the array.
     * @param i int
     * @return TSURLLifetimeReturnStatus
     */
    public TSURLLifetimeReturnStatus getTSURLLifetimeReturnStatus(int i)
    {
        return (TSURLLifetimeReturnStatus) array.get(i);
    }
    
    /**
     * Set the i-th element of the array.
     * @param index int
     * @param item TSURLLifetimeReturnStatus
     */
    public void setTSURLReturnStatus(int index, TSURLLifetimeReturnStatus item)
    {
        array.set(index, item);
    }
    
    /**
     * Add an element to the array.
     * @param item TSURLLifetimeReturnStatus
     */
    public void addTSurlReturnStatus(TSURLLifetimeReturnStatus item)
    {
        array.add(item);
    }
    
    /** 
     * Returns the size of the array.
     * @return int
     */
    public int size()
    {
        return array.size();
    }
    
    /**
     * Encodes the array to a Hashtable structure.
     * @param outputParam Hashtable
     * @param name String
     */
    public void encode(Map outputParam, String name)
    {
        ArrayList list = new ArrayList();
        for (int i = 0; i < array.size(); i++) {
            ((TSURLLifetimeReturnStatus) array.get(i)).encode(list);
        }
        outputParam.put(name, list);
    }
}
