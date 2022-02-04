/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.scheduler;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: Project 'Grid.it' for INFN-CNAF, Bologna, Italy
 * </p>
 * 
 * @author Zappi Riccardo <mailto://riccardo.zappi@cnaf.infn.it>
 * @version 1.0
 * @date
 * 
 */
public class SchedulerException extends Exception {

  /**
  * 
  */
  private static final long serialVersionUID = 1L;

  private String whichScheduler;

  public SchedulerException(String whichSched) {

    super();
    whichScheduler = whichSched;
  }

  public SchedulerException(String whichSched, String message) {

    super(message);
  }

  public SchedulerException(Throwable cause) {

    super(cause);
  }

  public SchedulerException(String message, Throwable cause) {

    super(message, cause);
  }

  public String toString() {

    return "Exception occurred within scheduler type = " + whichScheduler;
  }

}
