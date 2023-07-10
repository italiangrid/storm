/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents a unit of measure for FileSize; it contains a conversion factor for
 * changing among units.
 *
 * @author Ezio Corso
 * @author EGRID - ICTP Trieste
 * @date March 23rd, 2005
 * @version 1.0
 */
package it.grid.storm.common.types;

import java.io.Serializable;

public abstract class SizeUnit implements Serializable {

  /** */
  private static final long serialVersionUID = -3885767398054889628L;

  public static SizeUnit createSizeUnit(String unit) {

    String input = unit.toLowerCase();
    if (input.toLowerCase().equals("byte")) return SizeUnit.BYTES;
    if (input.toLowerCase().equals("kb")) return SizeUnit.KILOBYTES;
    if (input.toLowerCase().equals("mb")) return SizeUnit.MEGABYTES;
    if (input.toLowerCase().equals("gb")) return SizeUnit.GIGABYTES;
    if (input.toLowerCase().equals("tb")) return SizeUnit.TERABYTES;
    return SizeUnit.EMPTY;
  }

  public static final SizeUnit BYTES =
      new SizeUnit() {

        /** */
        private static final long serialVersionUID = 4181134075585414373L;

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

  public static final SizeUnit KILOBYTES =
      new SizeUnit() {

        /** */
        private static final long serialVersionUID = 9095939098314802303L;

        public double conversionFactor() {

          return 1000.0;
        }

        public String toString() {

          return "KB";
        }

        public int hashCode() {

          return 2;
        }
      };

  public static final SizeUnit MEGABYTES =
      new SizeUnit() {

        /** */
        private static final long serialVersionUID = -4371556318373779599L;

        public double conversionFactor() {

          return 1000000.0;
        }

        public String toString() {

          return "MB";
        }

        public int hashCode() {

          return 3;
        }
      };

  public static final SizeUnit GIGABYTES =
      new SizeUnit() {

        /** */
        private static final long serialVersionUID = -7917622928734775939L;

        public double conversionFactor() {

          return SizeUnit.MEGABYTES.conversionFactor() * 1000;
        }

        public String toString() {

          return "GB";
        }

        public int hashCode() {

          return 4;
        }
      };

  public static final SizeUnit TERABYTES =
      new SizeUnit() {

        /** */
        private static final long serialVersionUID = -8093974088166886328L;

        public double conversionFactor() {

          return SizeUnit.GIGABYTES.conversionFactor() * 1000;
        }

        public String toString() {

          return "TB";
        }

        public int hashCode() {

          return 5;
        }
      };

  public static final SizeUnit EMPTY =
      new SizeUnit() {

        /** */
        private static final long serialVersionUID = 5609696668282214567L;

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

  /** This method returns a conversion factor: the amount of bytes present in 1 unit of this. */
  public abstract double conversionFactor();
}
