package it.grid.storm.checksum;

public class ChecksumServerStatus {
    
    public static final String STATUS_STRING_KEY = "statusString";
    public static final String REQUEST_QUEUE_KEY = "requestQueue";
    public static final String IDLE_THREADS_KEY = "idleThreads";

    private boolean running = false;
    private String statusString = "";
    private int requestQueue = -1;
    private int idleThreads = -1;

    public ChecksumServerStatus(boolean isRunning) {
        this.running = isRunning;
    }
    
    public ChecksumServerStatus(boolean isRunning, String statusString, int requestQueue, int idleThreads) {
        this.running = isRunning;
        this.statusString = statusString;
        this.requestQueue = requestQueue;
        this.idleThreads = idleThreads;
    }

    public int getIdleThreads() {
        return idleThreads;
    }

    public int getRequestQueue() {
        return requestQueue;
    }

    public String getStatusString() {
        return statusString;
    }

    public boolean isRunning() {
        return running;
    }

    public void setIdleThreads(int idleThreads) {
        this.idleThreads = idleThreads;
    }

    public void setRequestQueue(int requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setStatusString(String statusString) {
        this.statusString = statusString;
    }

}
