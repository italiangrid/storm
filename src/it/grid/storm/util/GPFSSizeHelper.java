package it.grid.storm.util;

/**
 * @author baltico
 *
 */
public class GPFSSizeHelper
{

    private GPFSSizeHelper()
    {
        
    }
    
    
    public static long getBytesFromKIB(long kibiBytes)
    {
        if(kibiBytes < 0)
        {
            throw new IllegalArgumentException("Invalid kibiBytes number: " + kibiBytes);
        }
        return kibiBytes * 1024;
    }

}
