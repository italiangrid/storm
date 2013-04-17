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

package it.grid.storm.synchcall.common;

import it.grid.storm.namespace.StoRI;

/**
 * Interface for the variuos Plugin that can be used to manage the T1D1
 * migration (different user, hidden file etc)
 * 
 * @author lucamag
 * 
 */
public interface T1D1PluginInterface {

	public int startMigration(StoRI stori, String prefix);

}
