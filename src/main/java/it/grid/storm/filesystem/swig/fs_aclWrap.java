/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2011.
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
package it.grid.storm.filesystem.swig;

import it.grid.storm.filesystem.swig.fs_acl;

/**
 * @author Michele Dibenedetto
 *
 */
public class fs_aclWrap extends fs_acl
{

    /**
     * @param cPtr
     * @param cMemoryOwn
     */
    protected fs_aclWrap(long cPtr, boolean cMemoryOwn)
    {
        super(cPtr, cMemoryOwn);
    }
    
    /**
     * @param obj
     * @return
     */
    public static long getPointer(fs_acl obj)
    {
        return getCPtr(obj) ;
    }

    /**
     * @param swigCPtr
     */
    public static void deleteAcl(long swigCPtr)
    {
        posixapi_interfaceJNI.delete_fs_acl(swigCPtr);
    }
}
