package it.grid.storm.balancer.ftp;

import it.grid.storm.balancer.Node;

public interface HostNode extends Node {
    
    //nell'impl definire Map di string, transvalue
    
    public String getHostname();
    
    public int getPort();
    
    boolean isAvailable();
    
    Object getTransientValue(String name);
    
    void setTransientValue(String name, TransientProperties<Object> ciccio);

}
