package it.grid.storm.synchcall.data.directory;


import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.synchcall.data.InputData;

public interface LSInputData extends InputData 
{

    /**
     * Method that returns ArrayOfSurls specify in SRM request.
     */

    public ArrayOfSURLs getSurlArray();

    /**
     * Get method for TFileStorageType
     * @return
     */
    public boolean getStorageTypeSpecified();

    /**
     * Get Full Detailed List
     */
    public Boolean getFullDetailedList();

    /**
     * get AllLevelRecurisve
     */
    public Boolean getAllLevelRecursive();

    /**
     * get NumOfLevels
     */
    public Integer getNumOfLevels();

    /**
     * Get offset
     */
    public Integer getOffset();

    /**
     * Get count
     */
    public Integer getCount();

}
