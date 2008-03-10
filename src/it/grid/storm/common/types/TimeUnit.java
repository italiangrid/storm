/**
 * This class represents a unit of measure for EstimatedTime; it contains a conversion factor
 * for changing from one to the other units.
 *
 * @author  Ezio Corso
 * @author  EGRID - ICTP Trieste
 * @date    March 23rd, 2005
 * @version 1.0
 */

package it.grid.storm.common.types;

import java.io.Serializable;


public abstract class TimeUnit implements Serializable {

  public static TimeUnit createTimeUnit(String unit) {
    String input = unit.toLowerCase();
    if (input.equals("seconds") || input.equals("sec")) return TimeUnit.SECONDS;
    if (input.equals("minutes") || input.equals("min")) return TimeUnit.MINUTES;
    if (input.equals("hours") || input.equals("h")) return TimeUnit.HOURS;
    if (input.equals("days") || input.equals("d")) return TimeUnit.DAYS;
    if (input.equals("weeks") || input.equals("week")) return TimeUnit.WEEKS;
    return TimeUnit.EMPTY;
    }

    public static TimeUnit EMPTY = new TimeUnit() {
        public double conversionFactor() {
            return -1.0;
        }

        public String toString() {
            return "none";
        }

        public int hashCode() {
            return -1;
        }
    };

    public static TimeUnit SECONDS = new TimeUnit() {
        public double conversionFactor() {
            return 1.0;
        }

        public String toString() {
            return "seconds";
        }

        public int hashCode() {
            return 1;
        }
    };

    public static TimeUnit MINUTES = new TimeUnit() {
        public double conversionFactor() {
            return 60.0;
        }

        public String toString() {
            return "minutes";
        }

        public int hashCode() {
            return 2;
        }
    };

    public static TimeUnit HOURS = new TimeUnit() {
        public double conversionFactor() {
            return 3600.0;
        }

        public String toString() {
            return "hours";
        }

        public int hashCode() {
            return 3;
        }
    };


    public static TimeUnit DAYS = new TimeUnit() {
        public double conversionFactor() {
            return 86400.0;
        }

        public String toString() {
            return "days";
        }

        public int hashCode() {
            return 4;
        }
    };


    public static TimeUnit WEEKS = new TimeUnit() {
        public double conversionFactor() {
            return 604800.0;
        }

        public String toString() {
            return "weeks";
        }

        public int hashCode() {
            return 5;
        }
    };

    private TimeUnit() {}

    /**
     * This method returns a converson factor: the amout of seconds present in
     * 1 unit of this.
     */
    public abstract double conversionFactor();

/*    public static void main(String[] args) {
        System.out.println("Time Unit SECONDS:"+TimeUnit.SECONDS+"; hashCode:"+TimeUnit.SECONDS.hashCode()+"; conversion factor:"+TimeUnit.SECONDS.conversionFactor());
        System.out.println("Time Unit MINUTES:"+TimeUnit.MINUTES+"; hashCode:"+TimeUnit.MINUTES.hashCode()+"; conversion factor:"+TimeUnit.MINUTES.conversionFactor());
        System.out.println("Time Unit HOURS:"+TimeUnit.HOURS+"; hashCode:"+TimeUnit.HOURS.hashCode()+"; conversion factor:"+TimeUnit.HOURS.conversionFactor());
    }*/
}
