package it.grid.storm.namespace;

import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.namespace.model.RetentionPolicy;
import it.grid.storm.namespace.model.ExpirationMode;
import it.grid.storm.namespace.model.AccessLatency;

public interface PropertyInterface {
  /**
  <properties>
    <RetentionPolicy>custodial</RetentionPolicy>
    <AccessLatency>online</AccessLatency>
    <ExpirationMode>neverExpire</ExpirationMode>
    <TotalOnlineSize unit="GB">500</TotalOnlineSize>
    <TotalNearlineSize unit="GB">0</TotalNearlineSize>
  </properties>
  **/

  //public Collection getManagedSpaceTypes();

  //public Collection getManagedFileTypes();

  public TSizeInBytes getTotalOnlineSize();

  public TSizeInBytes getTotalNearlineSize();

  public RetentionPolicy getRetentionPolicy();

  public ExpirationMode getExpirationMode();

  public AccessLatency getAccessLatency();


  //public TLifeTimeInSeconds getSpaceLifeTimeMAX();

  //public TSizeInBytes getTotalSpaceSizeMAX();

  //public TLifeTimeInSeconds getFileLifeTimeMAX();

  //public boolean isAllowedFileType(TFileStorageType fileType);

  //public boolean isAllowedSpaceType(TSpaceType spaceType);

}
