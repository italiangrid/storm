package it.grid.storm.namespace.util.userinfo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserInfoCommand {

    
    private static final String COMMAND_ID = "id";
    private static final String COMMAND_GETENT = "getent";
    private static final Logger log = LoggerFactory.getLogger(UserInfoCommand.class);

    public UserInfoCommand() {
        super();
    }
    
    
    /**
    *
    * @return String
    */
   public static String getCommandId() {
     return COMMAND_ID;
   }

   /**
    *
    * @return String
    */
   public static String getCommandGetENT() {
     return COMMAND_GETENT;
   }
    
    /**
     * 
     * @param parameters
     * @return
     * @throws UserInfoException
     */
    public int retrieveGroupID(UserInfoParameters parameters) throws UserInfoException {
        int groupId = -1;
		String[] command = buildCommandString(parameters);

		StringBuffer commandOutput = new StringBuffer();
        for (String element : command) {
			commandOutput.append(element).append(" ");

			log.debug("UserInfo Command INPUT String : " + commandOutput.toString());
		}

		String output = getOutput(command);
        if ((output!=null)&&(output.length()>0)){
            try {
                groupId = Integer.parseInt(output);
            } catch (NumberFormatException nfe) {
                log.error("Group named '"+parameters+"' return a result different from a integer");
                throw new UserInfoException("Group named '"+parameters+"' return a result different from a integer");
				}
        } else {
            throw new UserInfoException("Group named '"+parameters+"' return a result different from a integer");
				}
		return groupId;
	}
    
    
    public HashMap<String, Integer> retrieveGroupDb() {
        HashMap<String,Integer> groupsDb = new HashMap<String, Integer>();
        UserInfoParameters param = new UserInfoParameters(Arrays.asList("group"));
        String[] command = buildCommandString(param);
        String output = getOutput(command);
        if ((output != null) && (output.length() > 0)) {
            String lines[] = output.split("\\r?\\n");
            for (int i = 0; i < lines.length; i++) {
                int gid = getGroupId(lines[i]);
                String groupName = getGroupName(lines[i]);
                if (gid > -1) {
                    groupsDb.put(groupName, gid);
                } else {
                    log.warn("Error while parsing the line '" + lines[i] + "' in group DB");
                }
            }

        } else {
            throw new UserInfoException("Unable to digest group database.");
        }
        return groupsDb;
    }
    
    /**
     * Command "getent group <groupname>" if parameters contain a string representing the groupname
     * Command "getent group" if parameters is empty or null
     * @param 
     * @return String[]
     */
    private static String[] buildCommandString(UserInfoParameters parameters) {
        String[] command = null;
        List<String> param = null;
        if (parameters!=null) {
          param = parameters.getParameters();
          command = new String[1 + param.size()];
          command[0] = UserInfoCommand.getCommandGetENT();
          int cont = 1;
          // Adding parameters to the command
          for (Object element : param) {
              String p = (String) element;
              command[cont++] = p;
          }
        } else {
            command = new String[] {UserInfoCommand.getCommandGetENT()};
        }  
        return command;
    }
    

    /**
     *
     * @param command String[]
     * @return String
     */
    private String getOutput(String[] command) throws UserInfoException {
        String result = "";
        try {
            Process child = Runtime.getRuntime().exec(command);
            log.debug("Command executed: "+ArrayUtils.toString(command));
            BufferedReader stdInput = null;
            BufferedReader stdError = null;
            // Get the input stream and read from it
            if (child!=null) {
                stdInput = new BufferedReader(new InputStreamReader(child.getInputStream()));
                stdError = new BufferedReader(new InputStreamReader(child.getErrorStream()));    
            }
            
            
            if (stdInput!=null) {
                
                //process the Command Output (Input for StoRM ;) )
                String line;
                int row = 0;
                log.trace("UserInfo Command Output :");
                while ( (line = stdInput.readLine()) != null) {
                    log.trace(row + ": "+line);
                    boolean lineOk = processOutput(row,line);
                    if (lineOk) {
                        result = result + line + "\n";
                    }
                    row++;
                }

                // process the Errors
                String errLine;
                if (stdError != null) {
                    while ((errLine = stdError.readLine()) != null) {
                        log.warn("User Info Command Output contains an ERROR message " + errLine);
                        throw new UserInfoException(errLine);
                    }
                }    
            }
        }
        catch (IOException ex) {
            log.error("getUserInfo (id) I/O Exception: "+ex);
            throw new UserInfoException(ex);
        }
        return result;
    }


    private boolean processOutput(int row, String line) {
        boolean result = false;
        if (row>=0) {
            result = true;
        }
        return result;
    }

    private String getGroupName(String line) {
        String groupName = null;
        String[] fields = getElements(line);      
        if ((fields!=null) && (fields.length>1) && (fields[0]!=null) ){
            log.trace("field[0], GroupName ='"+fields[0]+"'");
            groupName = fields[0];
         }
        return groupName;       

        
    }
    
    private int getGroupId(String line) {
		int gidInt = -1;
		String[] fields = getElements(line);
        if ((fields!=null) && (fields.length>2) && (fields[2]!=null) ){  
			log.trace("field[2], GID ='" + fields[2] + "'");
            try {
                gidInt = Integer.parseInt(fields[2]);
              } catch (NumberFormatException nfe) {
                  log.error("Unable to retrieve the GID number of groupName '"+fields[0]+"'");
				}
				}
        return gidInt;       
	}
    
    
    /**
     * Split the line in atomic part
     * 
     * @param line
     * @return
     */
    private String[] getElements(String line) {

		String patternStr = ":";
		String[] fields = null;
        if (line!=null) {
			log.trace("LINE = " + line);
			fields = line.split(patternStr);
		}
		return fields;
	}
    

}
