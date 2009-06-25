package it.grid.storm.namespace.util.userinfo;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserInfoExecutor {

    private static final Logger log = LoggerFactory.getLogger(UserInfoExecutor.class);

    public UserInfoExecutor() {
        super();
    }

    public static int retrieveGroupID(String groupName) throws UserInfoException {
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

    public static int retrieveGroupID_ENT(String groupName) throws UserInfoException {
        // getent group storm  | awk -F"," '{print $1}'| awk -F":" '{print $3}'

        int groupId = 0;

        //Retrieve Device
        String param1 = groupName;
        String param2 = " | awk -F\",\" '{print $1}'| awk -F\":\" '{print $3}'";

        UserInfoCommand userInfoCommand = new UserInfoCommand();
        ArrayList<String> params = new ArrayList<String> ();

        params.add(0, param1);
        params.add(1, param2);
        //    params.add(2, param3);
        UserInfoParameters userInfoParameters = new UserInfoParameters(params);

        groupId = userInfoCommand.retrieveGroupID(userInfoParameters);

        return groupId;
    }


}
