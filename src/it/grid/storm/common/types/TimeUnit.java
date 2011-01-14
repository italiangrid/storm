/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
