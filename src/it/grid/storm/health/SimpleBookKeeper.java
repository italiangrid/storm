package it.grid.storm.health;


public class SimpleBookKeeper extends BookKeeper {

    public static final String KEY = "BK";

    public SimpleBookKeeper() {
        super();
    }

    /* (non-Javadoc)
     * @see it.grid.storm.health.BookKeeper#addLogEvent(it.grid.storm.health.LogEvent)
     */
    @Override
    public void addLogEvent(LogEvent logEvent) {
        logbook.add(logEvent);
        logDebug("Event is added to Log Book (item #"+(logbook.size()-1)+"");
        logInfo(logEvent.toString());
    }


    /**
     *
     * @return int
     */
    public int getNumberOfAsynchRequest() {
        int result = 0;
        for (int i=0; i<logbook.size(); i++) {
            if (!(logbook.get(i).getOperationType().isSynchronousOperation())) {
                result++;
            }
        }
        return result;
    }


    /**
     *
     * @return int
     */
    public int getNumberOfSynchRequest() {
        int result = 0;
        for (int i=0; i<logbook.size(); i++) {
            if (logbook.get(i).getOperationType().isSynchronousOperation()) {
                result++;
            }
        }
        return result;
    }


    /**
     *
     * @param opType OperationType
     * @return int
     */
    public int getNumberOfRequest(OperationType opType) {
        int result = 0;
        for (int i=0; i<logbook.size(); i++) {
            if (logbook.get(i).getOperationType().equals(opType)) {
                result++;
            }
        }
        return result;
    }

    /**
     *
     * @param opType OperationType
     * @return long
     */
    public long getMeansDuration(OperationType opType) {
        long meanTime = 0L;
        long sumTime = 0L;
        int requestNumber = getNumberOfRequest(opType);
        if (requestNumber > 0) {
            for (int i = 0; i < logbook.size(); i++) {
                if (logbook.get(i).getOperationType().equals(opType)) {
                    sumTime += logbook.get(i).getDuration();
                }
            }
            meanTime = sumTime / requestNumber;
        }
        return meanTime;
    }

    /**
     *
     * @param opType OperationType
     * @return int
     */
    public int getNumberOfSuccess(OperationType opType) {
        int result = 0;
        int requestNumber = getNumberOfRequest(opType);
        if (requestNumber > 0) {
            for (int i = 0; i < logbook.size(); i++) {
                LogEvent logE = logbook.get(i);
                if (logE.getOperationType().equals(opType)) {
                    if (logE.isSuccess()) {
                        result++;
                    }
                }
            }
        }
        return result;
    }





}
