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

package it.grid.storm.srm.types;

import java.util.Hashtable;
import java.util.Map;

import it.grid.storm.filesystem.FilesystemPermission;

/**
 * This class represents the TPermissionMode of a File or Space Area managed by Srm. 
 *
 * @author  Magnoni Luca
 * @author  CNAF - INFN  Bologna
 * @date    Avril, 2005
 * @version 1.0
 */

public class TPermissionMode
{

    public static String                PNAME_OTHERPERMISSION = "otherPermission";
    public static String                PNAME_MODE            = "mode";
    
    private String                      mode                 = null;

    public static final TPermissionMode NONE                 = new TPermissionMode("None");
    public static final TPermissionMode X                    = new TPermissionMode("X");
    public static final TPermissionMode W                    = new TPermissionMode("W");
    public static final TPermissionMode WX                   = new TPermissionMode("WX");
    public static final TPermissionMode R                    = new TPermissionMode("R");
    public static final TPermissionMode RX                   = new TPermissionMode("RX");
    public static final TPermissionMode RW                   = new TPermissionMode("RW");
    public static final TPermissionMode RWX                  = new TPermissionMode("RWX");

    private TPermissionMode(String mode) {
        this.mode = mode;
    }

    public String toString()
    {
        return mode;
    }

    public String getValue()
    {
        return mode;
    }

    public static TPermissionMode getTPermissionMode(String type)
    {
        if (type.equals(NONE.getValue()))
            return NONE;
        if (type.equals(X.getValue()))
            return X;
        if (type.equals(W.getValue()))
            return W;
        if (type.equals(WX.getValue()))
            return WX;
        if (type.equals(R.getValue()))
            return R;
        if (type.equals(RX.getValue()))
            return RX;
        if (type.equals(RW.getValue()))
            return RW;
        if (type.equals(RWX.getValue()))
            return RWX;
        return null;
    }

    public static TPermissionMode getTPermissionMode(int type)
    {
        switch (type) {
        case 0:
            return TPermissionMode.NONE;
        case 1:
            return TPermissionMode.X;
        case 2:
            return TPermissionMode.W;
        case 3:
            return TPermissionMode.WX;
        case 4:
            return TPermissionMode.R;
        case 5:
            return TPermissionMode.RX;
        case 6:
            return TPermissionMode.RW;
        case 7:
            return TPermissionMode.RWX;
        default:
            return NONE;
        }
    }
    
    public static TPermissionMode getTPermissionMode(FilesystemPermission type)
    {
        String perm = "";

        if (type.canReadFile() || type.canListDirectory())
            perm += "R";
        if (type.canWriteFile())
            perm += "W";
        if (type.canTraverseDirectory())
           perm += "X";
        if (perm.length() == 0)
            perm = "None";
        return getTPermissionMode(perm);
    }

    /**
     * This method is used to encode Permission mode from BE to FE commonucation.
     * @param param Hashtable that will contains output xmlrpc structure.
     * @param name The name of the field to be added.
     */
    public void encode(Map param, String name)
    {
        Integer permissionInt = null;
        if (this.equals(TPermissionMode.NONE))
            permissionInt = new Integer(0);
        if (this.equals(TPermissionMode.X))
            permissionInt = new Integer(1);
        if (this.equals(TPermissionMode.W))
            permissionInt = new Integer(2);
        if (this.equals(TPermissionMode.WX))
            permissionInt = new Integer(3);
        if (this.equals(TPermissionMode.R))
            permissionInt = new Integer(4);
        if (this.equals(TPermissionMode.RX))
            permissionInt = new Integer(5);
        if (this.equals(TPermissionMode.RW))
            permissionInt = new Integer(6);
        if (this.equals(TPermissionMode.RWX))
            permissionInt = new Integer(7);

        param.put(name, permissionInt);
    }
}
