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

package it.grid.storm.namespace.config;

import java.util.List;
import java.util.Map;

import it.grid.storm.namespace.model.ApproachableRule;
import it.grid.storm.namespace.model.MappingRule;
import it.grid.storm.namespace.model.VirtualFS;

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
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company: INFN-CNAF
 * </p>
 * 
 * @author Riccardo Zappi
 * @version 1.0
 */

public interface NamespaceParser {

	public String getNamespaceVersion();

	public Map<String, VirtualFS> getVFSs();

	public VirtualFS getVFS(String vfsName);

	public List<String> getAllVFS_Roots();

	public Map<String, VirtualFS> getMapVFS_Root();

	public List<String> getAllMappingRule_StFNRoots();

	public Map<String, MappingRule> getMappingRules();

	public Map<String, String> getMappingRuleMAP();

	public Map<String, ApproachableRule> getApproachableRules();

	public long getLastUpdateTime();

}
