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
