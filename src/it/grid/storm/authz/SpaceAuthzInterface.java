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

package it.grid.storm.authz;

import it.grid.storm.authz.sa.AuthzDBInterface;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.griduser.GridUserInterface;

public interface SpaceAuthzInterface {

    public boolean authorize(GridUserInterface guser, SRMSpaceRequest srmSpaceOp);

    public boolean authorizeAnonymous(SRMSpaceRequest srmSpaceOp);
    
    void setAuthzDB(AuthzDBInterface authzDB);

    void refresh();

    public String getSpaceAuthzID();

}
