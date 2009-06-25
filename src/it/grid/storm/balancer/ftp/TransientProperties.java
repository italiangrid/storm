package it.grid.storm.balancer.ftp;

/**
 * This class is part of the StoRM project.
 * Copyright (c) 2008 INFN-CNAF.
 * <p>
 * 
 * This class implements a concrete strategy to provide a Random policy.
 * The next Element is chosen in a random way. 
 *
 * Authors:
 * 		@author lucamag
 *
 * @param <T>
 */

public interface TransientProperties<T> {
  
  /**
   * The value. Null is returned after the expiration time.
   * 
   * @return - The value or null.
   */
  T getValue();
  
  /**
   * The time that the value expires.
   * 
   * @return - The number of milliseconds since January 1, 1970, 00:00:00 GMT
   */
  long getExpirationTime();
}
