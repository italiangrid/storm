package it.grid.storm.checksum;

public class ChecksumClientFactory {
    
    public static ChecksumClient getChecksumClient() {
        return new ChecksumClientImpl();
    }
}
