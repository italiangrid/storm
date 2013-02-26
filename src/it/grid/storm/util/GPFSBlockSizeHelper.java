package it.grid.storm.util;

/**
 * @author baltico
 *
 */
public class GPFSBlockSizeHelper
{

    private GPFSBlockSizeHelper()
    {
        
    }
    
    public static final long BLOCK_SIZE = 512;
    
    public static long getBytesFromBlocks(long blocks)
    {
        if(blocks < 0)
        {
            throw new IllegalArgumentException("Invalid block number: " + blocks);
        }
        return blocks * BLOCK_SIZE;
    }

}
