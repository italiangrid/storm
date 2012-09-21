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

package it.grid.storm.synchcall.data.directory;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.synchcall.data.AbstractInputData;
import it.grid.storm.synchcall.data.exception.InvalidLSInputDataAttributeException;

/**
 * This class is part of the StoRM project.
 * Copyright: Copyright (c) 2008 
 * Company: INFN-CNAF and ICTP/EGRID project
 *
 * This class represents the LS Input Data associated with the SRM request, that is
 * it contains info about: ...,ecc.
 * 
 * @author lucamag
 * @date May 28, 2008
 *
 */

public class LSInputData extends AbstractInputData
{
    private GridUserInterface auth = null;
    private ArrayOfSURLs surlArray = null;
    private Boolean fullDetailedList;
    private Boolean allLevelRecursive;
    private Integer numOfLevels;
    private Integer offset;
    private Integer count;
    private final boolean storageTypeSpecified;

    public LSInputData(GridUserInterface auth, ArrayOfSURLs surlArray, TFileStorageType fileStorageType, Boolean fullDetList, //TSURLInfo[] surlArray,
                    Boolean allLev, Integer numOfLev, Integer offset, Integer count) throws InvalidLSInputDataAttributeException
    {
        boolean ok = (!(surlArray == null));

        if (!ok) throw new InvalidLSInputDataAttributeException(surlArray);

        this.auth = auth;
        this.surlArray = surlArray;
        this.storageTypeSpecified = (fileStorageType != null && !fileStorageType.equals(TFileStorageType.EMPTY));
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
     * Get method for TFileStorageType
     * @return
     */
    public boolean getStorageTypeSpecified()
    {
        return this.storageTypeSpecified;
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
    
    @Override
    public Boolean hasPrincipal()
    {
        return Boolean.TRUE;
    }

    @Override
    public String getPrincipal()
    {
        return this.auth.getDn();
    }
}
