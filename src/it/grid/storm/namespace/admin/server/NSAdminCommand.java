package it.grid.storm.namespace.admin.server;

import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import it.grid.storm.namespace.admin.common.CommandException;
import java.util.logging.Logger;
import java.util.logging.LogManager;
import java.util.Collection;
import java.util.Iterator;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF</p>
 *
 * @author R.Zappi
 * @version 1.0
 */
public class NSAdminCommand {

    private static final Logger logger = Logger.getLogger("it.grid.storm.namespace.admin.client");
    static {
        System.setProperty("java.util.logging.config.file", "./etc/adminserver-log.properties");
        try {
            LogManager.getLogManager().readConfiguration();
        }
        catch (Exception e) {
            System.err.println("Could not read logging configuration: " + e.getMessage());
            System.err.println("Proceeding...");
        }
    }

    private int commandID = -1;
    private String commandName = null;
    private int minArgs = 0;
    private int maxArgs = 0;
    private Argument[] argsAllowed = null;
    private HashMap args = null;


    private static final Argument HELP_ARG_1 = new Argument("quit",false, false, "java.lang.String");
    private static final Argument HELP_ARG_2 = new Argument("help",false, false, "java.lang.String");
    private static final Argument HELP_ARG_3 = new Argument("status",false, false, "java.lang.String");
    private static final Argument HELP_ARG_4 = new Argument("reload",false, false, "java.lang.String");
    private static final Argument HELP_ARG_5 = new Argument("list",false, false, "java.lang.String");
    private static final Argument HELP_ARG_6 = new Argument("check",false, false, "java.lang.String");
    private static final Argument[] HELP_ARGS = {
        HELP_ARG_1, HELP_ARG_2, HELP_ARG_3,
        HELP_ARG_4, HELP_ARG_5, HELP_ARG_6};

    private static final Argument[] STATUS_ARGS = {new Argument("vfs",true, true, "java.lang.String")};

    private static final Argument LIST_ARG_1 = new Argument("vfs",false, true, "java.lang.String");
    private static final Argument LIST_ARG_2 = new Argument("map-rule",false, true, "java.lang.String");
    private static final Argument LIST_ARG_3 = new Argument("vo-view",false, true, "java.lang.String");
    private static final Argument LIST_ARG_4 = new Argument("detail",false, true, "java.lang.Integer");
    private static final Argument[] LIST_ARGS = {LIST_ARG_1, LIST_ARG_2, LIST_ARG_3, LIST_ARG_4};

    public static final NSAdminCommand QUIT =  new NSAdminCommand(1,"quit",0,0,null);
    public static final NSAdminCommand HELP =  new NSAdminCommand(2,"help",0,1,HELP_ARGS);
    public static final NSAdminCommand STATUS =  new NSAdminCommand(3,"status",0,1,STATUS_ARGS);
    public static final NSAdminCommand RELOAD =  new NSAdminCommand(4,"reload",0,0,null);
    public static final NSAdminCommand LIST =  new NSAdminCommand(5,"list",0,2,LIST_ARGS);
    public static final NSAdminCommand CHECK =  new NSAdminCommand(6,"check",0,0,null);

    private static final Map COMMANDS = new HashMap();

    static {
        COMMANDS.put(QUIT.getCommandName(), QUIT);
        COMMANDS.put(HELP.getCommandName(), HELP);
        COMMANDS.put(STATUS.getCommandName(), STATUS);
        COMMANDS.put(RELOAD.getCommandName(), RELOAD);
        COMMANDS.put(LIST.getCommandName(), LIST);
        COMMANDS.put(CHECK.getCommandName(), CHECK);
    }


    private NSAdminCommand(int commandID, String commandName, int minArgs, int maxArgs, Argument[] listArg) {
       this.commandID = commandID;
       this.commandName = commandName;
       this.minArgs = minArgs;
       this.maxArgs = maxArgs;
       this.argsAllowed = listArg;
    }

      // ********************** Common CLASS Methods ********************** //


      /**
       * Default constructor (Factory pattern)
       *
       * @param cmdline String
       * @return NSAdminCommand
       * @throws CommandException
       */
      public static NSAdminCommand getCommandByString(String cmdline) throws CommandException {
        NSAdminCommand adminCmd = null;
        //Retrieve the Command
        if (cmdline==null) throw CommandException.CMD_LINE_EMPTY;
        StringTokenizer parser = new StringTokenizer(cmdline);
        int cmdChunk = parser.countTokens();
        if (cmdChunk==0) throw CommandException.CMD_LINE_EMPTY;
        String command = parser.nextToken();
        boolean found = COMMANDS.containsKey(command);
        if (found) adminCmd = (NSAdminCommand) COMMANDS.get(command);
        else throw CommandException.CMD_LINE_UNKNOWN;
        //Check for arguments
        int nrArgs = parser.countTokens();
        if (nrArgs<adminCmd.minArgs) throw CommandException.MISSING_ARGS;
        if (nrArgs>adminCmd.maxArgs) throw CommandException.TOOMUCH_ARGS;
        //Scan the arguments, if there.
        if (nrArgs>0) {
            String argName = null;
            Argument arg = null;
            adminCmd.args = new HashMap();
            while (parser.hasMoreTokens()) {
                argName = parser.nextToken();
                //Search arg within argument allowed
                arg = adminCmd.getArg(argName);
                if (arg!=null)  { //Argument found is legal!
                    //Search for arg value, if there.
                    String argValue = null;
                    /** @todo REMOVE THIS if USE JDK 5.0 **/
                    /**
                    if (argName.contains("=")) { //Value present
                        //Check if the value is permitted.
                        if (arg.isValuePossible()) {
                            argValue = argName.split("=", 2)[1];
                            arg.setValue(argValue);
                        } else { //Value specified when it is not possible
                            throw CommandException.ARG_VALUE_NOT_PERMITTED;
                        }
                    } else { //Value is absent.
                        //Check if value is mandatory
                        if (arg.isValueMandatory()) { //absence of value!
                            throw CommandException.ARG_VALUE_NOT_SPECIFIED;
                        } else {
                            //set Empty Value (default, so do nothing)
                        }



                    }**/
                    adminCmd.args.put(argName, arg);
                } else {
                    throw CommandException.WRONG_ARG_NAME;
                }
            }
        }
        return adminCmd;
    }

    public Map getArgument() {
        return this.args;
    }

    private Argument[] getArgsAllowed() {
        return this.argsAllowed;
    }

    private Argument getArg(String name) {
        Argument result = null;
        if (this.argsAllowed!=null) {
            for (int i=0; i<argsAllowed.length; i++) {
                if (name.startsWith(argsAllowed[i].getArgName())) {
                    result = argsAllowed[i];
                    break;
                }
            }
        }
        return result;
    }

    public int getCommandID() {
        return this.commandID;
    }

    public String getCommandName() {
        return this.commandName;
    }

    public boolean hasArguments() {
        return (this.args!=null);
    }

    public int howmuchArgs() {
        if (hasArguments()) return this.args.size();
        else return 0;
    }


    public String toString() {
        String result = "";
        if (this.getCommandName()!= null ) result+= this.getCommandName();
        if (this.args!=null) {
            Iterator scan = this.args.values().iterator();
            String arg;
            while (scan.hasNext()) {
                result+=" ";
                arg = ((Argument) scan.next()).toString();
                result += arg;
            }
        }
        return result;
    }

   public String cmdDetails() {
       String result = "";
       result+="CMD Name : "+this.getCommandName() +"\n";
       result+="CMD ID   : "+this.getCommandID() +"\n";
       result+="CMD has args : "+this.hasArguments() +"\n";
       result+="CMD how much args : "+this.howmuchArgs() +"\n";
       return result;
   }

    /**
     *
     * <p>Title: </p>
     *
     * <p>Description: </p>
     *
     * <p>Copyright: Copyright (c) 2006</p>
     *
     * <p>Company: INFN-CNAF</p>
     *
     * @author R.Zappi
     * @version 1.0
     */
    private static class Argument {
        private String argName = null;
        private boolean valueMandatory;
        private boolean valuePossible;
        private Object value = null;
        private String valueType = null;
        private int valueInt = -1;
        private String valueStr = null;
        private boolean emptyValue = true;

        public Argument(String argName, boolean mandatory, boolean possible, String valueType) {
            this.argName = argName;
            this.valueMandatory = mandatory;
            this.valuePossible = possible;
            this.valueType = valueType;
        }

        public String getArgName() {
            return this.argName;
        }

        public String getValueType() {
            return this.valueType;
        }

        public String getStringValue() {
            return this.valueStr;
        }

        public int getIntValue() {
            return this.valueInt;
        }

        private boolean isStringValue() {
            /** @todo REMOVE THIS if USE JDK 5.0 **/
            /**
            if (valueType!=null) return valueType.contains("String");
            else return false;
}           **/
            return false;
        }

        private boolean isIntegerValue() {
            /** @todo REMOVE THIS if USE JDK 5.0 **/
            /**
            if (valueType!=null) return valueType.contains("Integer");
            else return false;
            **/
            return false;
        }

        public boolean isValueMandatory() {
            return this.valueMandatory;
        }

        public boolean isValuePossible() {
            return this.valuePossible;
        }

        public boolean isValuePresent() {
            return !this.emptyValue;
        }

        public void setValue(Object obj) throws CommandException {
            this.value = obj;
            if (isIntegerValue()) { this.valueInt = setIntValue(obj); this.emptyValue = false; }
            if (isStringValue()) { this.valueStr = setStringValue(obj); this.emptyValue = false; }
        }

        public Object getValue() {
            return this.value;
        }

        public String toString() {
            String result = this.argName;
            if (isValuePresent()) result+="="+getValue();
            return result;
        }

        private String setStringValue(Object obj) throws CommandException {
            String result = null;
            result = obj.toString();
            return result;
        }

        private int setIntValue(Object obj) throws CommandException {
            Integer result = null;
            try {
                result = Integer.valueOf((String)obj);
            } catch (NumberFormatException nfe) {
                logger.warning("Command error. Aspected INT value. Found others.");
                throw CommandException.WRONG_ARG_VALUE;
            }
            return result.intValue();
        }
    }

}
