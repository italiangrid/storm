package it.grid.storm.synchcall.discovery;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class DiscoveryManagerImpl implements DiscoveryManager
{
    private static final Log LOG = LogFactory.getLog("synch");
    private Functionality functionality = null;
    private PingExecutor  ping          = null;

    public DiscoveryManagerImpl(Functionality func)
    {
        functionality = func;

        switch (func.getFuncId()) {
        case DiscoveryManager.PING_Id:
            ping = new PingExecutor();
            break;
        default:
            LOG.error("Unable to instanciate DiscoveryManager. Please select the supported functionality.");
        }
    }

    public PingOutputData ping(PingInputData inputData) {
        if (ping != null) {
          return ping.doIt(inputData);
        }
        else {
          LOG.error("Discovery Manager instanciate for " + functionality.toString());
          return null;
        }
      }


      public static Log getLogger() {
          return LOG;
      }
}
