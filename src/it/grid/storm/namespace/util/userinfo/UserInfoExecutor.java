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

package it.grid.storm.namespace.util.userinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserInfoExecutor {

    private static final Logger log = LoggerFactory.getLogger(UserInfoExecutor.class);

    public UserInfoExecutor() {
        super();
    }

    
    public static int retrieveGroupID_ETC(String groupName) throws UserInfoException {
        int groupId = 0;

        //Retrieve Device
        String param1 = "-r";
        String param2 = "-g";
        String param3 = groupName + "001"; //Be Carefull!


        UserInfoCommand userInfoCommand = new UserInfoCommand();
        ArrayList<String> params = new ArrayList<String> ();

        params.add(0, param1);
        params.add(1, param2);
        params.add(2, param3);
        UserInfoParameters userInfoParameters = new UserInfoParameters(params);

        groupId = userInfoCommand.retrieveGroupID(userInfoParameters);

        return groupId;
    }

    
    public static int retrieveGroupID(String groupName) throws UserInfoException {

        int groupId = 0;

        //Retrieve Device
        String param1 = groupName;

        UserInfoCommand userInfoCommand = new UserInfoCommand();
        ArrayList<String> params = new ArrayList<String> ();

        params.add(0, param1);

        UserInfoParameters userInfoParameters = new UserInfoParameters(params);

        groupId = userInfoCommand.retrieveGroupID(userInfoParameters);
      
        return groupId;
    }

    public static Map<String,Integer> digestGroupDatabase() throws UserInfoException {
        Map<String,Integer> groupsDb = new HashMap<String, Integer>();
        UserInfoCommand userInfoCommand = new UserInfoCommand();
        groupsDb = userInfoCommand.retrieveGroupDb();
        return groupsDb;
    }
    

}
