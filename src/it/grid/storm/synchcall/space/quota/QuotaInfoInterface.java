package it.grid.storm.synchcall.space.quota;

import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.namespace.model.QuotaType;
import it.grid.storm.common.types.TimeUnit;

public interface QuotaInfoInterface {

  public void setFilesystemName(String fileSystemName) ;

  public String getFilesystemName();

  public void setQuotaType(QuotaType quotaType) ;

  public QuotaType getQuotaType() ;

  public void setBlockUsage(long blockUsage) ;

  public long getBlockUsage();

  public void setBlockHardLimit(long blockHardLimit) ;

  public long getBlockHardLimit();

  public void setBlockSoftLimit(long blockSoftLimit);

  public long getBlockSoftLimit() ;

  public void setBlockGraceTime(String blockGraceTime);

  public long getBlockGraceTime();

  public void setINodeUsage(long iNodeUsage) ;

  public long getINodeUsage() ;

  public void setINodeHardLimit(long iNodeHardLimit) ;

  public long getINodeHardLimit() ;

  public void setINodeSoftLimit(long iNodeSoftLimit);

  public long getINodeSoftLimit() ;

  public void setINodeGraceTime(String iNodeGraceTime) ;

  public long getINodeGraceTime();

  public void setSizeUnit(String sizeUnit) ;

  public SizeUnit getSizeUnit() ;

  public void setINodeTimeUnit(String timeUnit) ;

  public TimeUnit getINodeTimeUnit();

  public void setSizeTimeUnit(String timeUnit) ;

  public TimeUnit getSizeTimeUnit();


  //*** BUSINESS METHOD **//
  public void build(String output) throws QuotaException;


}
