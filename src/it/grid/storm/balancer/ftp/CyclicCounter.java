package it.grid.storm.balancer.ftp;


import java.util.concurrent.atomic.AtomicInteger;

public class CyclicCounter
{

    private int maxVal;
    private final AtomicInteger counter = new AtomicInteger(0);

    public CyclicCounter(int maxVal) throws IllegalArgumentException
    {
      if(maxVal < 0)
      {
          throw new IllegalArgumentException("Maximum counter value should be >= 0");
      }
        this.maxVal = maxVal;
    }
    
    /**
     * @return
     */
    public int next()
    {
        /*
         * 
         * This method can generate thread starvation
         * */
        int newVal,curVal;
        do
        {
            curVal = this.counter.get();
            newVal = (curVal + 1) % (this.maxVal + 1);
        } while (!this.counter.compareAndSet(curVal, newVal));
        return newVal;
    }

}
