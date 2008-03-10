package it.grid.storm.namespace.util.userinfo;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.List;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInfoCommand {

  //private Log log = NamespaceDirector.getLogger();
  private static final Logger log = Logger.getLogger("namespace");


  public UserInfoCommand() {
    super();
  }

  public int retrieveGroupID(UserInfoParameters parameters) throws UserInfoException {
    int groupId = -1;
    String[] command = buildCommandString(parameters);
    if (log.getEffectiveLevel().equals(Level.DEBUG)) {
      StringBuffer commandOutput = new StringBuffer();
      for (int i = 0; i < command.length; i++) {
        commandOutput.append(command[i]).append(" ");
      }
      log.debug("UserInfo Command INPUT String : " + commandOutput.toString());
    }

    String output = getOutput(command);
    try {
      groupId = Integer.parseInt(output);
    } catch (NumberFormatException nfe) {
      log.error("Group named '"+parameters+"' return a result different from a integer");
      throw new UserInfoException("Group named '"+parameters+"' return a result different from a integer");
    }
    return groupId;
  }

  /**
   *
   * @param gpfsParameters GPFSQuotaParameters
   * @return String[]
   */
  private static String[] buildCommandString(UserInfoParameters parameters) {
    String[] command = null;
    List<String> param = parameters.getParameters();
    if (param != null) {
      command = new String[1 + param.size()];
      //command[0] = parameters.getCommandGetENT();
      command[0] = parameters.getCommandId();
      int cont = 0;
      // Adding parameters to the command
      for (Iterator it = param.iterator(); it.hasNext(); ) {
        cont++;
        String p = (String) it.next();
        command[cont] = p;
      }
    }
    else {
      command = new String[] {
          parameters.getCommandId()};
          //parameters.getCommandGetENT()};
    }
    return command;
  }


  /**
   *
   * @param command String[]
   * @return String
   */
  private String getOutput(String[] command) throws UserInfoException {
    String result = null;
    try {
      Process child = Runtime.getRuntime().exec(command);
      // Get the input stream and read from it
      BufferedReader stdInput = new BufferedReader(new InputStreamReader(child.getInputStream()));
      BufferedReader stdError = new BufferedReader(new InputStreamReader(child.getErrorStream()));

      //process the Command Output (Input for StoRM ;) )
      String line;
      int row = 0;
      log.debug("UserInfo Command Output :");
      while ( (line = stdInput.readLine()) != null) {
        log.debug(row + ": "+line);
        boolean lineOk = processOutput(row,line);
        if (lineOk) {
          result = line;
          break;
        }
        row++;
      }

      //process the Errors
      String errLine;
      while ((errLine = stdError.readLine()) != null) {
        log.warn("User Info Command Output contains an ERROR message "+errLine);
        throw new UserInfoException(errLine);
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
      /**
       * @todo : Implement a more smart check to verify the right line
       */
      result = true;
    }
    return result;
  }

}
