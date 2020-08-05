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

/**
 * 
 */
package it.grid.storm.authz.sa.model;

import it.grid.storm.authz.sa.AuthzDBReaderException;
import it.grid.storm.griduser.FQAN;
import it.grid.storm.griduser.SubjectAttribute;

/**
 * @author zappi
 * 
 */
public class FQANEveryonePattern extends FQANPattern implements Everyone {

  /*
   * Return always true because the pattern is built programmatically, and it is supposed to be
   * valid.
   * 
   * @see it.grid.storm.authz.sa.model.SubjectPattern#isValidPattern()
   */
  @Override
  public boolean isValidPattern() throws AuthzDBReaderException {

    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.grid.storm.authz.sa.model.SubjectPattern#match(it.grid.storm.griduser .FQAN)
   */
  @Override
  public boolean match(SubjectAttribute sa) {

    boolean result = false;
    if (sa instanceof FQAN) {
      result = true;
    }
    return result;
  }

  @Override
  public String toString() {

    return Everyone.EVERYONE;
  }

}
