package it.grid.storm.namespace.admin.common;

public class CommandException
    extends Exception {

    public static final CommandException GENERIC_ERR = builtBy(0, "Generic command error.");
    public static final CommandException UNKNOWN_CMD = builtBy(1, "Unknown command.");
    public static final CommandException UNASPECTED_EOT = builtBy(2, "Unaspected end of trasmission.");
    public static final CommandException CMD_LINE_EMPTY = builtBy(3, "Command line empty.");
    public static final CommandException CMD_LINE_UNKNOWN = builtBy(4, "Command unknown.");
    public static final CommandException MISSING_ARGS = builtBy(5, "Missing argument. Please see help.");
    public static final CommandException TOOMUCH_ARGS = builtBy(6, "Too much arguments. Please see help.");
    public static final CommandException WRONG_ARG_VALUE = builtBy(7, "Wrong argument value.");
    public static final CommandException WRONG_ARG_NAME = builtBy(8, "Wrong argument name.");
    public static final CommandException ARG_VALUE_NOT_PERMITTED = builtBy(9, "Argument value is not permitted.");
    public static final CommandException ARG_VALUE_NOT_SPECIFIED = builtBy(10, "Argument value is not specified.");


    private CmdDescription cmdErr;

    public CommandException() {
        super(CommandException.GENERIC_ERR.cmdErr.description);
        this.cmdErr = CommandException.GENERIC_ERR.cmdErr;
    }

    public CommandException(String message) {
        super(message);
        this.cmdErr = new CmdDescription(message);
    }

    public CommandException(CmdDescription cmdErr) {
        this.cmdErr = cmdErr;
    }

    public static CommandException builtBy(int code, String description) {

        CmdDescription cmdErr = new CmdDescription(code, description);
        CommandException cmdExpt = new CommandException(cmdErr);

        return cmdExpt;
    }

    public static CommandException getCmdException(byte errorCode) {
        CommandException result = new CommandException();
        return result;
    }

    public String toString() {

        return this.cmdErr.description;
    }

    /**
     *
     * <p>Title: </p>
     *
     * <p>Description: </p>
     *
     * <p>Copyright: Copyright (c) 2007</p>
     *
     * <p>Company: </p>
     *
     * @author not attributable
     * @version 1.0
     */
    static class CmdDescription {

        private int code;
        private String description;
        private static final int UNDEF_CODE = -1;

        CmdDescription(int code, String description) {
            this.code = code;
            this.description = description;
        }

        CmdDescription(String description) {
            this.code = UNDEF_CODE;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

}
