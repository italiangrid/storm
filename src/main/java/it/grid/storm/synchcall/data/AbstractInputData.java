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

package it.grid.storm.synchcall.data;

import java.util.Map;

public abstract class AbstractInputData implements InputData {

	private static final String sepBegin = "(";
	private static final String sepEnd = ")";
	private static final String arrow = "->";

	@Override
	public String display(Map<?, ?> map) {

		StringBuilder sb = new StringBuilder("[");
		for (Object object : map.keySet()) {
			sb.append(sepBegin).append(object.toString()).append(arrow)
				.append(map.get(object).toString()).append(sepEnd);
		}
		return sb.append("]").toString();
	}
}
