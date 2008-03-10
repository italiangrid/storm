/**
 * This class represents a unit of measure for FileSize; it contains a conversion factor
 * for changing among units.
 *
 * @author  Ezio Corso
 * @author  EGRID - ICTP Trieste
 * @date    March 23rd, 2005
 * @version 1.0
 */
package it.grid.storm.common.types;

import java.io.Serializable;
public abstract class SizeUnit implements Serializable {

    public static SizeUnit createSizeUnit(String unit) {
      String input = unit.toLowerCase();
      if (input.equals("byte")) return SizeUnit.BYTES;
      if (input.equals("kb")) return SizeUnit.KILOBYTES;
      if (input.equals("mb")) return SizeUnit.MEGABYTES;
      return SizeUnit.EMPTY;
    }

    public static SizeUnit BYTES = new SizeUnit() {
        public double conversionFactor() {
            return 1.0;
        }

        public String toString() {
            return "Bytes";
        }

        public int hashCode() {
            return 1;
        }
    };

    public static SizeUnit KILOBYTES = new SizeUnit() {
        public double conversionFactor() {
            return 1024.0;
        }

        public String toString() {
            return "KB";
        }

        public int hashCode() {
            return 2;
        }
    };

    public static SizeUnit MEGABYTES = new SizeUnit() {
        public double conversionFactor() {
            return 1048576.0;
        }

        public String toString() {
            return "MB";
        }

        public int hashCode() {
            return 3;
        }
    };

    public static SizeUnit EMPTY = new SizeUnit() {
        public double conversionFactor() {
            return 0.0;
        }

        public String toString() {
            return "EMPTY";
        }

        public int hashCode() {
            return 0;
        }
    };

    private SizeUnit() {}

    /**
     * This method returns a converson factor: the amout of bytes present in
     * 1 unit of this.
     */
    public abstract double conversionFactor();
/*
    public static void main(String[] args) {
        System.out.println("bytes: "+SizeUnit.BYTES+"; hash="+SizeUnit.BYTES.hashCode()+"; conversion-factor="+SizeUnit.BYTES.conversionFactor());
        System.out.println("kilobytes: "+SizeUnit.KILOBYTES+"; hash="+SizeUnit.KILOBYTES.hashCode()+"; conversion-factor="+SizeUnit.KILOBYTES.conversionFactor());
        System.out.println("megabytes: "+SizeUnit.MEGABYTES+"; hash="+SizeUnit.MEGABYTES.hashCode()+"; conversion-factor="+SizeUnit.MEGABYTES.conversionFactor());
    }*/
}
