/**
 * This class represents TLifeTime in seconds as a long.
 *
 * @author  Ezio Corso - Magnoni Luca
 * @author  EGRID ICTP Trieste / CNAF INFN Bologna
 * @date    Avril, 2005
 * @version 1.0
*/

package it.grid.storm.srm.types;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Date;
import java.util.Map;

import it.grid.storm.common.types.*;

public class TLifeTimeInSeconds implements Serializable {

    private long time = -1;
    private TimeUnit u = TimeUnit.EMPTY;
    private boolean empty = true;
    private static TLifeTimeInSeconds emptyTime = null;
    private boolean infinite = false;
    private static TLifeTimeInSeconds infiniteTime = null;
    
    public static String PNAME_LIFETIMEASSIGNED = "lifetimeAssigned";
    public static String PNAME_LIFETIMELEFT = "lifetimeLeft";
    public static String PNAME_DESIREDLIFETIMEOFRESERVEDSPACE = "desiredLifetimeOfReservedSpace";
    public static String PNAME_LIFETIMEOFRESERVEDSPACE = "lifetimeOfReservedSpace";
    public static String PNAME_FILELIFETIME = "fileLifetime";
    public static String PNAME_PINLIFETIME = "pinLifetime";
 
    /**
     * This constructor requires a long time representing the time in TimeUnit u.
     */
    private TLifeTimeInSeconds(long time, TimeUnit u,boolean empty,boolean infinite) {
        this.time = time;
        this.u = u;
        this.empty = empty;
        this.infinite = infinite;
    }

    /**
     * Method that requires a long time representing the time in TimeUnit u; it throws an
     * InvalidTLifeTimeAttributeException if u is null. A negative value for time, automatically
     * results in an Infinite TLifeTimeInSeconds.
     */
    public static TLifeTimeInSeconds make(long time, TimeUnit u) throws InvalidTLifeTimeAttributeException {
        if (u==null) throw new InvalidTLifeTimeAttributeException(u);
        if (time<0) return makeInfinite();
        return new TLifeTimeInSeconds(time,u,false,false);
    }

    /**
     * Method that returns an Empty TLifeTimeInSeconds.
     */
    public static TLifeTimeInSeconds makeEmpty() {
        if (emptyTime==null) emptyTime = new TLifeTimeInSeconds(0,TimeUnit.EMPTY,true,false);
        return emptyTime;
    }

    /**
     * Method that returns an Infinite TLifeTimeInSeconds.
     */
    public static TLifeTimeInSeconds makeInfinite() {
        if (infiniteTime==null) infiniteTime = new TLifeTimeInSeconds(-1,TimeUnit.EMPTY,false,true);
        return infiniteTime;
    }
    
    /**
     * Method that returns true if this is an Empty TLifeTimeInSeconds.
     */
    public boolean isEmpty() {
        return empty;
    }

    /**
     * Method that returns true if This is an Infinite TLifeTimeInSeconds.
     */
    public boolean isInfinite() {
        return infinite;
    }

    /**
     * Method that returns a long value for this LifeTime. It returns -1 if This is
     * an Empty or Infinite TLifeTimeInSeconds.
     */
    public long value() {
        return time;
    }

    /**
     * Method that returns the TimeUnit for this LifeTime. It returns TimeUnit.EMPTY
     * if This is an Empty or Infintie TLifeTimeInSeconds.
     */
    public TimeUnit unit() {
        return u;
    }

    /**
     * Public static method that return this LifeTime value converted into the
     * specified TimeUnit. It returns -1 if a null TimeUnit is passed, or if This
     * is an Empty or Infinite TLifeTimeInSeconds.
     */
    public double  getTimeIn(TimeUnit u) {
        if ((!empty) && (!infinite) && (u!=null)) {
            Long l_time = new Long(time);
            double result = l_time.doubleValue() * (this.u.conversionFactor()/u.conversionFactor());
            return result;
        } else return -1;
    }

    /**
     * Returns the number of seconds remaining to reach startTimeInSeconds plus the value of
     * this instance.
     * @param startTimeInSeconds The starting time in seconds.
     * @return Seconds remaining, zero otherwise.
     */
    public TLifeTimeInSeconds timeLeft(long startTimeInSeconds) {
        
        if (empty)
            return emptyTime;
        long secondsLeft = this.time + startTimeInSeconds;
        
        Date currentDate = new Date();
        long currentTime = currentDate.getTime();  // current time in milliseconds
        currentTime /= 1000; // current time in seconds
        secondsLeft -= currentTime;
        if (secondsLeft < 0) secondsLeft = 0;
        
        TLifeTimeInSeconds timeLeft = null;
        try {
            timeLeft = TLifeTimeInSeconds.make(secondsLeft, TimeUnit.SECONDS);
        } catch (InvalidTLifeTimeAttributeException e) {
            timeLeft = TLifeTimeInSeconds.makeEmpty();
        }
        
        return timeLeft;
    }

    /**
     * Returns the number of seconds remaining to reach startingDate plus the value of
     * this instance.
     * @param startingDate The starting date.
     * @return Seconds remaining, zero otherwise.
     */
    public TLifeTimeInSeconds timeLeft(Date startingDate) {
        
        if (empty || (startingDate == null))
            return emptyTime;
        long startTimeInSeconds = startingDate.getTime() / 1000;
        return timeLeft(startTimeInSeconds);
    }

    /**
     * Method that returns a TSizeInBytes object retrieving its value by the Hashtable
     * used for comunicating with the FE
     */
    public static TLifeTimeInSeconds decode(Map inputParam, String fieldName) {
        String lifetime = (String) inputParam.get(fieldName);
        
        if (lifetime == null) return TLifeTimeInSeconds.makeEmpty();
        long lifetimeLong = Long.parseLong(lifetime);
        
        try {
            return TLifeTimeInSeconds.make(lifetimeLong, TimeUnit.SECONDS);
        } catch (InvalidTLifeTimeAttributeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return TLifeTimeInSeconds.makeEmpty();
        }
    }

    /**
     * Encode method, create a representation of this object into a 
     * structured paramter used for communication to FE component.
     * @param param
     * @param name
     */
    public void encode(Map param, String name) {
    	if (empty) return;
        String lifetimeString;
        lifetimeString = String.valueOf(this.time);
        param.put(name, lifetimeString);
    }





    public String toString() {
        if (empty) return "Empty TLifeTimeInSeconds!";
        if (infinite) return "Infinite TLifeTimeInSeconds";
        return ""+time+" "+u;
    }

    /**
     * Beware that this equality will _not_ return true for the same quantity
     * expressed in different units of measure!
     */
    public boolean equals(Object o) {
        if (o==this) return true;
        if (!(o instanceof TLifeTimeInSeconds)) return false;
        TLifeTimeInSeconds et = (TLifeTimeInSeconds) o;
        if ((this.empty) && (et.empty)) return true;
        if ((this.infinite) && (et.infinite)) return true;
        return ((this.time==et.time) && (this.u==et.u) && (this.empty==et.empty));
    }

    public int hashCode() {
        if (empty) return -1;
        if (infinite) return -2;
        int hash = 17;
        hash = 37*hash + new Long(time).hashCode();
        hash = 37*hash + u.hashCode();
        return hash;
    }
    

/*    public static void main(String[] args) {
        System.out.print("TEsting TLifeTimeInSeconds\n\ns10 is 10 seconds:");
        try {
            TLifeTimeInSeconds s10 = new TLifeTimeInSeconds(10,TimeUnit.SECONDS); System.out.println(s10+"; hashCode="+s10.hashCode());
            TLifeTimeInSeconds s20 = new TLifeTimeInSeconds(20,TimeUnit.SECONDS); System.out.println("s20 is 20 seconds: "+s20+"; hashCode="+s20.hashCode());
            TLifeTimeInSeconds a10 = new TLifeTimeInSeconds(10,TimeUnit.SECONDS); System.out.println("a10 is 10 seconds: "+a10+"; hashCode="+a10.hashCode());
            System.out.println("s10.equals(s20) false:"+s10.equals(s20)+"; s20.equals(s10) false:"+s20.equals(s10));
            System.out.println("s10.equals(a10) true:"+s10.equals(a10)+"; a10.equals(s10) true:"+a10.equals(s10));
            System.out.println("s10 Value: "+s10.value()+"; s10 Unit: "+s10.unit());
            System.out.println("Corresponding to minutes (0.167): "+s10.getTimeIn(TimeUnit.MINUTES)+"; and to hours (0.00278):"+s10.getTimeIn(TimeUnit.HOURS));
        } catch (Exception e) {
            System.out.println("Should not see this! "+e);
        }
        //Testing Exceptions
        System.out.println("Testing Exception throwing");
        try {
            TLifeTimeInSeconds s10 = new TLifeTimeInSeconds(10,TimeUnit.SECONDS);
            System.out.println("Successfully created: "+s10);
            System.out.print("Attempting creation with null TimeUnit: ");
            try {
                new TLifeTimeInSeconds(10,null);
                System.out.println("Should not see this!");
            } catch (InvalidTLifeTimeAttributeException e) {
                System.out.println(" creation failed as expected. "+e);
            }
        } catch (Exception e) {
            System.out.println("Should not see this!");
        }
    }*/
}

