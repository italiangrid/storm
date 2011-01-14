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

package it.grid.storm.catalogs;

/**
 * Class that represents data associated to JiT entries. It contains a String
 * representing the file, an int representing the ACL, an int representing the
 * user UID, an int representing the user GID.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    November 2006
 */
public class JiTData {

    private String file = "";
    private int uid = -1;
    private int gid = -1;
    private int acl = -1;

    /**
     * Constructor requiring the complete name of the file as String, the acl as
     * int, the uid and primary gid of the LocalUser bith as int.
     */
    public JiTData(String file, int acl, int uid, int gid) {
        this.file = file;
        this.acl = acl;
        this.uid = uid;
        this.gid = gid;
    }

    public String pfn() {
        return file;
    }

    public int acl() {
        return acl;
    }

    public int uid() {
        return uid;
    }

    public int gid() {
        return gid;
    }

    public String toString() {
        return "file="+file+" acl="+acl+" uid="+uid+" gid="+gid;
    }
}
