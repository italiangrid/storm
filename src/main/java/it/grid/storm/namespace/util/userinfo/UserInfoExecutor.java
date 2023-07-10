/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.util.userinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserInfoExecutor {

  public UserInfoExecutor() {

    super();
  }

  public static int retrieveGroupID_ETC(String groupName) throws UserInfoException {

    int groupId = 0;

    // Retrieve Device
    String param1 = "-r";
    String param2 = "-g";
    String param3 = groupName + "001"; // Be Carefull!

    UserInfoCommand userInfoCommand = new UserInfoCommand();
    ArrayList<String> params = new ArrayList<String>();

    params.add(0, param1);
    params.add(1, param2);
    params.add(2, param3);
    UserInfoParameters userInfoParameters = new UserInfoParameters(params);

    groupId = userInfoCommand.retrieveGroupID(userInfoParameters);

    return groupId;
  }

  public static int retrieveGroupID(String groupName) throws UserInfoException {

    int groupId = 0;

    // Retrieve Device
    String param1 = groupName;

    UserInfoCommand userInfoCommand = new UserInfoCommand();
    ArrayList<String> params = new ArrayList<String>();

    params.add(0, param1);

    UserInfoParameters userInfoParameters = new UserInfoParameters(params);

    groupId = userInfoCommand.retrieveGroupID(userInfoParameters);

    return groupId;
  }

  public static Map<String, Integer> digestGroupDatabase() {

    Map<String, Integer> groupsDb = new HashMap<String, Integer>();
    UserInfoCommand userInfoCommand = new UserInfoCommand();
    groupsDb = userInfoCommand.retrieveGroupDb();
    return groupsDb;
  }
}
