package it.grid.storm.asynch;


public interface RequestChunk extends Request
{

    public String getRequestToken();

    public void updateGlobalStatus();
}
