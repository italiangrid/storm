package it.grid.storm.balancer.ftp;

import it.grid.storm.balancer.AbstractNode;
import it.infn.bo.FMC.CurrentProcs;

public class FTPNode extends AbstractNode {
    
    private String hostname;
    private int port;
    private int weight = 0;;
    
   
    
    
    public FTPNode() {
       
    }

    public FTPNode(String hostname, int port, int weight) {
        this.hostname = hostname;
        this.port = port;
        this.weight = weight;
    }


    /**
     * @return String hostname
     */
    public String getHostName() {
        return hostname;
    }

    public int getWeight() {
        return weight;
    }


    public void setHostname(String hostname) {
        this.hostname = hostname;
    }



    public int getPort() {
        return port;
    }



    public void setPort(int port) {
        this.port = port;
    }



    
    public void setWeight(int w) {
        //Weight between 1 and 100
        weight = w%100;        
    }
    
    
  //TODO
    //Gestire la validita' dell'informazione 
    //facendo una cache 
    //Vedere se si puo' generalizzare il concetto di metrica 
    //con Map<String, Obj metric>
    
    public int getFTPProc() {
        int nprocs =  0 ;
        nprocs = new CurrentProcs().getN(this.getHostName());
        nprocs = (nprocs<0) ? -1 : nprocs;
        return nprocs;
    }

}
