/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.grid.storm.info.remote;

/**
 * @author Michele Dibenedetto
 *
 */
public class Constants
{
    public static final String ENCODING_SCHEME = "UTF-8";
    
    public static final String RESOURCE = "info/status";
    
//    public static final String VERSION = "1.0";
    
    public static final String UPDATE_OPERATION = "update";
    
    public static final String TOTAL_SPACE_KEY = "total";

    public static final String USED_SPACE_KEY = "used";

    public static final String RESERVED_SPACE_KEY = "reserved";

    public static final String UNAVALILABLE_SPACE_KEY = "unavailable";
    
    /* get: /RESOURCE/alias
     * put: /RESOURCE/alias/UPDATE_OPERATION?TOTAL_SPACE_KEY=total&USED_SPACE_KEY=used&RESERVED_SPACE_KEY=reserved&UNAVALILABLE_SPACE_KEY=unavailable
     */
}

