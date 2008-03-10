/**
 * This class represents the LS Input Data associated with the SRM request, that is
 * it contains info about: ...,ecc.
 * * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.synchcall.directory;

import java.util.Vector;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.TFileStorageType;

public class LSInputData
{
    private GridUserInterface auth = null;
    private ArrayOfSURLs surlArray = null;
    private ArrayOfTExtraInfo storageSystemInfo = null;
    private TFileStorageType fileStorageType = null;
    private Boolean fullDetailedList;
    private Boolean allLevelRecursive;
    private Integer numOfLevels;
    private Integer offset;
    private Integer count;

    public LSInputData()
    {

    }

    public LSInputData(GridUserInterface auth, ArrayOfSURLs surlArray, ArrayOfTExtraInfo storageSystemInfo, TFileStorageType fileStorageType, Boolean fullDetList, //TSURLInfo[] surlArray,
                    Boolean allLev, Integer numOfLev, Integer offset, Integer count) throws InvalidLSInputDataAttributeException
    {
        boolean ok = (!(surlArray == null));

        if (!ok) throw new InvalidLSInputDataAttributeException(surlArray);

        this.auth = auth;
        this.surlArray = surlArray;
        this.storageSystemInfo = storageSystemInfo;
        this.fileStorageType = fileStorageType;
        this.fullDetailedList = fullDetList;
        this.allLevelRecursive = allLev;
        this.numOfLevels = numOfLev;
        this.offset = offset;
        this.count = count;

    }

    /**
     * Set User
     */
    public void setUser(GridUserInterface user)
    {
        this.auth = user;
    }

    /**
     * Get User
     */
    public GridUserInterface getUser()
    {
        return this.auth;
    }

    /**
     * Method that returns ArrayOfSurls specify in SRM request.
     */

    public ArrayOfSURLs getSurlArray()
    {
        return surlArray;
    }

    /**
     * Setter method for SURL array
     * @param s_array
     */

    public void setSurlArray(ArrayOfSURLs s_array)
    {
        this.surlArray = s_array;
    }

    /**
     * Method that returns ArrayOfTExtraInfo specify in SRM request.
     */
    public ArrayOfTExtraInfo getStorageSystemInfo()
    {
        return storageSystemInfo;
    }

    /**
     * Set method for TExtraInfo array
     * @param i_array
     */
    public void setStorageSystemInfo(ArrayOfTExtraInfo i_array)
    {
        this.storageSystemInfo = i_array;
    }

    /**
     * Get method for TFileStorageType
     * @return
     */
    public TFileStorageType getTFileStorageType()
    {
        return this.fileStorageType;
    }

    /**
     * Set method for TFileStorageType
     * @param ftype
     */
    public void setTFileStorageType(TFileStorageType ftype)
    {
        this.fileStorageType = ftype;
    }

    /**
     * Get Full Detailed List
     */
    public Boolean getFullDetailedList()
    {
        return this.fullDetailedList;
    }

    /**
     * Set Full Detailed List
     */
    public void setFullDetailedList(Boolean fdl)
    {
        this.fullDetailedList = fdl;
    }

    /**
     * Set AllLevelRecursive
     */
    public void setAllLevelRecursive(Boolean alr)
    {
        this.allLevelRecursive = alr;
    }

    /**
     * get AllLevelRecurisve
     */
    public Boolean getAllLevelRecursive()
    {
        return this.allLevelRecursive;
    }

    /**
     * Set NumOfLevels
     */
    public void setNumOfLevels(Integer nol)
    {
        this.numOfLevels = nol;
    }

    /**
     * get NumOfLevels
     */
    public Integer getNumOfLevels()
    {
        return this.numOfLevels;
    }

    /**
     * Get offset
     */
    public Integer getOffset()
    {
        return this.offset;
    }

    /**
     * Set offset
     */
    public void setOffset(Integer offset)
    {
        this.offset = offset;
    }

    /**
     * Get count
     */
    public Integer getCount()
    {
        return this.count;
    }

    /**
     * Set count
     */
    public void setCount(Integer count)
    {
        this.count = count;
    }
}
