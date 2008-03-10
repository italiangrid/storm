package it.grid.storm.synchcall.discovery;

import org.apache.commons.logging.Log;

public interface DiscoveryManager
{

    public static final int PING_Id = 0;
    public static final Functionality PING = new Functionality("PING", PING_Id);

    public PingOutputData ping(PingInputData inputData);



    public static class Functionality
    {
        private String description;
        private int    funcId;

        private Functionality(String description, int funcId) {
            this.description = description;
            this.funcId = funcId;
        }

        public int getFuncId()
        {
            return funcId;
        }

        public String toString()
        {
            return description;
        }

        public boolean equals(Object obj)
        {
            if (obj == null)
                return false;
            if (obj instanceof Functionality) {
                Functionality func = (Functionality) obj;
                if (func.funcId == this.funcId)
                    return true;
            }
            return false;
        }
    }
}
