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

import it.grid.storm.authz.path.model.PathOperation;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.common.types.StFN;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.StoRI;

public interface PathAuthzInterface {

    public AuthzDecision authorize(GridUserInterface guser, PathOperation pathOperation, StFN fileStFN);
    
    public AuthzDecision authorize(GridUserInterface guser, SRMFileRequest srmPathOp, StoRI stori);
    
    public AuthzDecision authorize(GridUserInterface guser, SRMFileRequest srmPathOp, StFN fileStFN);

    public AuthzDecision authorize(GridUserInterface guser, SRMFileRequest srmPathOp, StoRI storiSource, StoRI storiDest);

    public AuthzDecision authorizeAnonymous(PathOperation pathOperation, StFN fileStFN);
    
    public AuthzDecision authorizeAnonymous(SRMFileRequest srmPathOp, StFN fileStFN);

    public AuthzDecision authorizeAnonymous(SRMFileRequest mvSource, StoRI fromStori, StoRI toStori);

}
