package it.grid.storm.persistence.model;


import it.grid.storm.common.types.VO;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.srm.types.TFileStorageType;

import java.io.Serializable;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StorageFileTO implements Serializable, Comparable {

    /**
     *Logger.
     */
    private static final Logger log = LoggerFactory.getLogger("persistence");

    //----- PRIMARY KEY ----//
    private Long storageFileId = null; //Persistence Object IDentifier

    //----- FIELDS ----//
    private String ownerName = null;
    private String voName = null;
    private String name = null; //This field must be no null.
    private boolean justInTime = true;
    private TFileStorageType fileType = null;
    private Date created = new Date(); //This field could be changed
    private long currentSize = 0L; //This attribute could be no persistent.
    private long currentLifeTime = 0L; //This attribute could be no persistent.

    //----- ASSOCIATIONS ----//
    //Relationship with StorageSpace [many-to-one (not-null = false)]
    private StorageSpaceTO storageSpace = null;
    //Relationship with Pin [one-to-many (not-null = false)]
    // private Set pins = new HashSet();

    //Component splitted into ownerName e voName
    private VomsGridUser owner = null; //The maker

    // ********************** Constructor methods ********************** //

    /**
     * No-arg constructor for JavaBean tools.
     */
    public StorageFileTO()
    {
        super();
    }

    /**
     * Full constructor.
     *
     * @param owner GridUser
     * @param surl TSURL
     * @param jit boolean
     * @param currentSize long
     * @param storageSpace StorageSpaceData
     */
    public StorageFileTO(String name, VomsGridUser owner, boolean jit, long currentSize, StorageSpaceTO storageSpace)
    {
        this.owner = owner;
        this.ownerName = owner.getDn();
        boolean voms = owner.hasVoms();
        if (voms) {
            this.voName = owner.getVO().getValue();
        }
        else {
            this.voName = VO.makeNoVo().getValue();
        }

        this.name = name;
        this.justInTime = jit;
        this.currentSize = currentSize;
        this.storageSpace = storageSpace; //SS can be NULL.
        //this.pins = null; //Pins can be NULL.
    }

    // ********************** Accessor Methods ********************** //

    /**
     * @hibernate.id
     *  generator-class="native"
     */
    public Long getStorageFileId()
    {
        return this.storageFileId;
    }

    public void setStorageFileId(Long id)
    {
        this.storageFileId = id;
    }

    //-------------------------------------

    public VomsGridUser getOwner()
    {
        return this.owner;
    }

    public void setOwner(VomsGridUser owner)
    {
        this.owner = owner;
        this.ownerName = owner.getDn();
        boolean voms = owner.hasVoms();
        if (voms) {
            this.voName = owner.getVO().getValue();
        }
        else {
            this.voName = VO.makeNoVo().getValue();
        }
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

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    //-------------------------------------

    public boolean isJustInTime()
    {
        return this.justInTime;
    }

    public void setJustInTime(boolean jit)
    {
        this.justInTime = jit;
    }

    //-------------------------------------

    public TFileStorageType getFileType()
    {
        return this.fileType;
    }

    public void setFileType(TFileStorageType fileType)
    {
        this.fileType = fileType;
    }

    //-------------------------------------

    /**
     * @hibernate.property
     */
    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date date)
    {
        this.created = date;
    }

    //-------------------------------------

    /**
     * @hibernate.property
     */
    public long getCurrentSize()
    {
        return this.currentSize;
    }

    public void setCurrentSize(long size)
    {
        this.currentSize = size;
    }

    //-------------------------------------

    /**
     * @hibernate.property
     */
    public long getCurrentLifeTime()
    {
        return this.currentLifeTime;
    }

    public void setCurrentLifeTime(long lifeTime)
    {
        this.currentLifeTime = lifeTime;
    }

    //-------------------------------------

    public void setStorageSpace(StorageSpaceTO ss)
    {
        this.storageSpace = ss;
    }

    public StorageSpaceTO getStorageSpace()
    {
        return this.storageSpace;
    }

    //-------------------------------------

    /**
  public void setPins(Set pins)
  {
    this.pins = pins;
  }

  public Set getPins()
  {
    return this.pins;
  }

  public void addPin(PinData pin)
  {
    if (pin==null)
    {
      throw new IllegalArgumentException("Can't add a null Pin action!");
    }
    //This setting is for bi-directional association
    pin.setStorageFile(this);
    this.pins.add(pin);
  }
     **/
    // ********************** Common Methods ********************** //

    @Override
    public boolean equals(Object o)
    {
        if (this==o)
        {
            return true;
        }
        if (!(o instanceof StorageFileTO))
        {
            return false;
        }
        final StorageFileTO storageFile = (StorageFileTO)o;
        if (!name.equals(storageFile.getName()))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public String toString()
    {
        return "SURL ('"+getName()+"'), "+"size: '"+getCurrentSize()+"'";
    }

    public int compareTo(Object o)
    {
        if (o instanceof StorageFileTO)
        {
            return this.getCreated().compareTo(((StorageFileTO)o).getCreated());
        }
        return 0;
    }
    // ********************** Business Methods ********************** //
}
