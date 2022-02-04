package it.grid.storm.space;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NullSpaceUpdaterHelper implements SpaceUpdaterHelperInterface {

  private static final Logger log = LoggerFactory.getLogger(NullSpaceUpdaterHelper.class);

  @Override
  public boolean increaseUsedSpace(long size) {

    log.debug("NullSpaceUpdaterHelper doesn't increase used size!");
    return true;
  }

  @Override
  public boolean decreaseUsedSpace(long size) {

    log.debug("NullSpaceUpdaterHelper doesn't decrease used size!");
    return true;
  }

}
