package it.grid.storm.space.quota;

import it.grid.storm.namespace.model.Quota;

import java.util.ArrayList;


public abstract class GPFSQuotaCommand {

    protected long timeout;
    
    
    public enum ExitCode {
        SUCCESS(0), IO_ERROR(1), INTERRUPTED(2), TIMEOUT(3), EMPTY_OUTPUT(4), UNDEFINED(-1);

        private int code;

        ExitCode(int c) {
            code = c;
        }

        public int getCode() {
            return code;
        }

        static ExitCode getExitCode(int code) {
            ExitCode result = ExitCode.UNDEFINED;
            for (ExitCode ec : ExitCode.values()) {
                if (ec.getCode() == code) {
                    result = ec;
                    break;
                }
            }
            return result;
        }
    }
    
    
    public GPFSQuotaCommand(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Constructor without timeout. It'll use default timeout of 10 sec.
     */
    public GPFSQuotaCommand() {
        this.timeout = -1;
    }
    
    
    public abstract String getQuotaCommandString();
    
    public abstract ArrayList<GPFSQuotaInfo> executeGetQuotaInfo(boolean test) throws QuotaException;
    
    public abstract GPFSQuotaInfo executeGetQuotaInfo(Quota quotaElement, boolean test) throws QuotaException;
    
    
}
