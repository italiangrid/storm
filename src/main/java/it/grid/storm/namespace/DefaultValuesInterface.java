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

package it.grid.storm.namespace;

import it.grid.storm.srm.types.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public interface DefaultValuesInterface {

    public TLifeTimeInSeconds getDefaultSpaceLifetime();

    public TSpaceType getDefaultSpaceType();

    public TSizeInBytes getDefaultGuaranteedSpaceSize();

    public TSizeInBytes getDefaultTotalSpaceSize();

    public TLifeTimeInSeconds getDefaultFileLifeTime();

    public TFileStorageType getDefaultFileType();

    public final long   DEFAULT_SPACE_LT = 2147483647L;
    public final String DEFAULT_SPACE_TYPE = "permament";
    public final long   DEFAULT_SPACE_GUAR_SIZE = 2147483647L;
    public final long   DEFAULT_SPACE_TOT_SIZE = 2147483647L;
    public final long   DEFAULT_FILE_LT = 2147483647L;
    public final String DEFAULT_FILE_TYPE = "permament";

}
