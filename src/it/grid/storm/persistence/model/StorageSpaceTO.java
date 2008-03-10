/*
* (c)2004 INFN / ICTP-eGrid
* This file can be distributed and/or modified under the terms of
* the INFN Software License. For a copy of the licence please visit
* http://www.cnaf.infn.it/license.html
*
*/

/**
 * StorageSpaceTO
 */
package it.grid.storm.persistence.model;


import java.io.Serializable;
import java.util.*;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.config.DefaultValue;
import it.grid.storm.common.*;
import org.apache.log4j.Logger;

/**
 *
 * @author Riccardo Zappi - riccardo.zappi AT cnaf.infn.it
 * @version $Id: StorageSpaceTO.java,v 1.13 2006/06/29 14:46:25 aforti Exp $
 */
public class StorageSpaceTO implements Serializable, Comparable {


    /**
     *Logger.
     */
    private static final Logger log = Logger.getLogger("persistence");


  //----- PRIMARY KEY ----//
  private Long storageSpaceId = null; //Persistence Object IDentifier
  //----- FIELDS ----//
  private String ownerName = null;
  private String voName = null;
  private String spaceType = null;
  private String alias = null;
  private String spaceToken = null;
  private String spaceFile = null;
  private Date created = new Date();
  private long totalSize = -1L;
  private long guaranteedSize = -1L;
  private long unusedSize = -1L;
  private long lifetime = -1L;
  private String storageInfo = null;

  //----- ASSOCIATIONS ----//
  //Relationship with StorageFile [one-to-many (not-null = false)]
  private Set storageFiles = new HashSet();
  //Component splitted into ownerName e voName
  private GridUserInterface owner = null; //The maker
  // ********************** Constructor methods ********************** //

  /**
   * No-arg constructor for JavaBean tools.
   */
  public StorageSpaceTO()
  {
    super();
  }

  /**
   * Minimal constructor.
   *
   * @param maker User
   */
  public StorageSpaceTO(GridUserInterface maker)
  {
    //Always exists a creator!
    this.owner = maker;
    this.ownerName = maker.getDn();
    this.voName = (maker.getMainVo()).getValue();
    //No Alias (or Token description) is setted
    this.alias = null;

    /**
     * The below parameters are filled with DEFAULT values for named user.
     */
    this.spaceType = DefaultValue.getNamedVO_SpaceType(voName);;
    this.guaranteedSize = DefaultValue.getNamedVO_GuaranteedSpaceSize(voName);
    this.totalSize = DefaultValue.getNamedVO_TotalSpaceSize(voName);
    this.lifetime = DefaultValue.getNamedVO_SpaceLifeTime(voName);

    /**
     *  The below parameters are filled with GENERATED values for named user.
     */
    //StoRM generates a space_token
    this.spaceToken = (new GUID()).toString();
    this.spaceFile = "sf-"+spaceToken;
  }

  public StorageSpaceTO(GridUserInterface maker, String alias)
  {
      //Always exists a creator!
  this.owner = maker;
  this.ownerName = maker.getDn();
  this.voName = (maker.getMainVo()).getValue();
  //No Alias (or Token description) is setted
  this.alias = alias;

  /**
   * The below parameters are filled with DEFAULT values for named user.
   */
  this.spaceType = DefaultValue.getNamedVO_SpaceType(voName);;
  this.guaranteedSize = DefaultValue.getNamedVO_GuaranteedSpaceSize(voName);
  this.totalSize = DefaultValue.getNamedVO_TotalSpaceSize(voName);
  this.lifetime = DefaultValue.getNamedVO_SpaceLifeTime(voName);

  /**
   *  The below parameters are filled with GENERATED values for named user.
   */
  //StoRM generates a space_token
  this.spaceToken = (new GUID()).toString();
  this.spaceFile = "/"+"sf-"+spaceToken;

  }

  /**
   * Full constructor.
   *
   * @param maker User
   * @param alias String
   * @param spaceToken String
   * @param path PathName
   */
  public StorageSpaceTO(GridUserInterface maker, String alias, String spaceToken, String spaceFile)
  {
    this.owner = maker;
    this.alias = alias;
    this.spaceToken = spaceToken;
    this.spaceFile = spaceFile;
  }



  public StorageSpaceTO(GridUserInterface maker, String type, String alias, String spaceToken, String spaceFile, long guaranteedSize, long totalSize)
  {
    this.owner = maker;
    this.ownerName = maker.getDn();
    this.voName = (maker.getMainVo()).getValue();
    this.spaceType = type;
    this.alias = alias;
    this.spaceToken = spaceToken;
    this.spaceFile = spaceFile;
    this.guaranteedSize = guaranteedSize;
    this.totalSize = totalSize;
  }

  /**
   * Constructor from Domain Object
   *
   * @param spaceData SpaceData
   */
  public StorageSpaceTO(it.grid.storm.common.StorageSpaceData spaceData)
  {
  if (spaceData!=null)
    {
      log.debug("Building StorageSpaceTO with "+spaceData);
      this.owner = spaceData.getUser();
      if (spaceData.getUser()!=null)
      {
        this.ownerName = (spaceData.getUser()).getDn();
        //@todo Manage case of non VOMS user.
        if(spaceData.getUser().getMainVo()!=null)
          this.voName = ( (spaceData.getUser()).getMainVo()).getValue();
      }
      if (spaceData.getSpaceType()!=null)
        this.spaceType = (spaceData.getSpaceType()).getValue();

      this.alias = spaceData.getSpaceTokenAlias();

      if (spaceData.getSpaceToken()!=null)
        this.spaceToken = spaceData.getSpaceToken().getValue();

      this.spaceFile = spaceData.getSpaceFileNameString();

      if (spaceData.getGuaranteedSize()!=null)
        this.guaranteedSize = spaceData.getGuaranteedSize().value();

      if (spaceData.getDesiredSize()!=null)
        this.totalSize = spaceData.getDesiredSize().value();

      if (spaceData.getUnusedSizes()!=null)
        this.unusedSize = spaceData.getUnusedSizes().value();

      if (spaceData.getLifeTime() != null)
        this.lifetime = spaceData.getLifeTime().value();
      
      if (spaceData.getDate() != null)
    	this.created = spaceData.getDate();
    }
  }


  // ********************** Accessor Methods ********************** //

  public Long getStorageSpaceId()
  {
    return this.storageSpaceId;
  }

  public void setStorageSpaceId(Long id)
  {
    this.storageSpaceId = id;
  }

  //-------------------------------------

  public GridUserInterface getOwner()
  {
    return this.owner;
  }

  public void setOwner(GridUserInterface owner)
  {
    this.owner = owner;
    // this.ownerName = owner.getDn();
    // this.voName = (owner.getMainVo()).getValue();
  }

  //-------------------------------------

  public String getOwnerName()
  {
    return this.ownerName;
  }

  public void setOwnerName(String ownerName)
  {
    this.ownerName = ownerName;
  }

  //-------------------------------------

  public String getVoName()
  {
    return this.voName;
  }

  public void setVoName(String voName)
  {
    this.voName = voName;
  }

  //-------------------------------------

  public String getSpaceType()
  {
    return this.spaceType;
  }

  public void setSpaceType(String spaceType)
  {
    this.spaceType = spaceType;
  }

  //-------------------------------------

  public long getGuaranteedSize()
  {
    return this.guaranteedSize;
  }

  public void setGuaranteedSize(long guaranteedSize)
  {
    this.guaranteedSize = guaranteedSize;
  }

  //-------------------------------------

  public long getTotalSize()
  {
    return this.totalSize;
  }

  public void setTotalSize(long totalSize)
  {
    this.totalSize = totalSize;
  }

  //-------------------------------------

  public long getUnusedSize()
  {
    return this.unusedSize;
  }

  public void setUnusedSize(long unusedSize)
  {
    this.unusedSize = unusedSize;
  }

  //-------------------------------------


  public void setSpaceToken(String spaceToken)
  {
    this.spaceToken = spaceToken;
  }

  public String getSpaceToken()
  {
    return this.spaceToken;
  }

  //-------------------------------------

  public void setAlias(String alias)
  {
    this.alias = alias;
  }

  public String getAlias()
  {
    return this.alias;
  }

  //-------------------------------------

  public void setSpaceFile(String spaceFile)
  {
    this.spaceFile = spaceFile;
  }

  public String getSpaceFile()
  {
    return this.spaceFile;
  }

  //-------------------------------------

  public long getLifetime()
  {
    return this.lifetime;
  }

  public void setLifetime(long lifetime)
  {
    this.lifetime = lifetime;
  }

  //-------------------------------------

  public String getStorageInfo()
  {
    return this.storageInfo;
  }

  public void setStorageInfo(String storageInfo)
  {
    this.storageInfo = storageInfo;
  }

  //-------------------------------------

  public void setStorageFiles(Set storageFiles)
  {
    this.storageFiles = storageFiles;
  }

  public Set getStorageFiles()
  {
    return this.storageFiles;
  }

  public void addStorageFile(StorageFileTO storageFile)
  {
    if (storageFile==null)
    {
      throw new IllegalArgumentException("Can't add a null actionsEntry!");
    }
    //This setting is for bi-directional association
    storageFile.setStorageSpace(this);
    this.getStorageFiles().add(storageFile);
  }

    public void removeFile(StorageFileTO storageFile) {
            /**
             * @todo To check the consistency of set..
             **/
            getStorageFiles().remove(storageFile);
    }

    //-------------------------------------

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date date) {
	this.created = date;
    }
    // ********************** Common Methods ********************** //

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StorageFileTO)) {
            return false;
        }
        final StorageSpaceTO storageSpace = (StorageSpaceTO) o;
        if (!spaceToken.equals(storageSpace.getSpaceToken())) {
            return false;
        }
        if (!spaceFile.equals(storageSpace.getSpaceFile())) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 17;
        hash = 37 * hash + spaceFile.hashCode();
        hash = 37 * hash + spaceToken.hashCode();
        return hash;

    }

    public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append(" ==== STORAGE SPACE (token="+spaceToken+") ==== \n");
      sb.append(" STORAGE SPACE ID = "+storageSpaceId); sb.append("\n");
      sb.append(" OWNER USER NAME  = "+ownerName); sb.append("\n");
      sb.append(" OWNER VO NAME    = "+voName); sb.append("\n");
      sb.append(" SPACE ALIAS NAME = "+alias); sb.append("\n");
      sb.append(" SPACE TYPE       = "+spaceType); sb.append("\n");
      sb.append(" SPACE TOKEN      = "+spaceToken); sb.append("\n");
      sb.append(" SPACE FILE       = "+spaceFile); sb.append("\n");
      sb.append(" CREATED          = "+created); sb.append("\n");
      sb.append(" TOTAL SIZE       = "+totalSize); sb.append("\n");
      sb.append(" GUARANTEED SIZE  = "+guaranteedSize); sb.append("\n");
      sb.append(" FREE SIZE        = "+unusedSize); sb.append("\n");
      sb.append(" LIFETIME (sec)   = "+lifetime); sb.append("\n");
      sb.append(" STORAGE INFO     = "+storageInfo); sb.append("\n");
      sb.append(" NR STOR_FILES    = <UNDEF for NOW..>"); sb.append("\n");
      return sb.toString();
    }

    public int compareTo(Object o) {
        if (o instanceof StorageFileTO) {
            return this.getCreated().compareTo(((StorageSpaceTO) o).getCreated());
        }
        return 0;
    }

    // ********************** Business Methods ********************** //
}
