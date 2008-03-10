package it.grid.storm.wrapper;

/**
 *This class represents the possible ACL value.
 *
 * @author  Magnoni Luca
 * @author  Cnaf INFN Bologna
 * @date    Avril, 2005
 * @version 1.0
 */
public class ACL {
	//FIXME
	//INSERT TRUE ENUM PATTERN SCHEME
	//
	public static ACL R = new ACL() {
		public String toString() {
			return "r--";
		}

		public int hashCode() {
			return 1;
		}
	};
	
	public static ACL W = new ACL() {
        	public String toString() {
        		return "-w-";
		}

     		public int hashCode() {
           		return 2;
       		}
   	};

	public static ACL X = new ACL() {
		public String toString() {
        		return "--x";
		}
		public int hashCode() {
            		return 3;
		}
	};
	
	public static ACL RW = new ACL() {
		public String toString() {
        		return "rw-";
		}
		public int hashCode() {
            		return 4;
		}
	};
	
	public static ACL WX = new ACL() {
		public String toString() {
        		return "-wx";
		}
		public int hashCode() {
            		return 5;
		}
	};
	
	public static ACL RX = new ACL() {
		public String toString() {
        		return "r-x";
		}
		public int hashCode() {
            		return 6;
		}
	};
	
	public static ACL RWX = new ACL() {
		public String toString() {
        		return "rwx";
		}
		public int hashCode() {
            		return 7;
		}
	};

    public static ACL EMPTY = new ACL() {
        public String toString() {
            return "Empty ACL";
        }

        public int hashCode() {
            return 0;
        }
    };
	
	private ACL() {}

/*
    public static void main(String[] args) {
        System.out.println("r: "+ACL.R+ "; hash="+ACL.R.hashCode());
        System.out.println("w: "+ACL.W+ "; hash="+ACL.W.hashCode());
        System.out.println("x: "+ACL.X+ "; hash="+ACL.X.hashCode());
        System.out.println("rw: "+ACL.RW+ "; hash="+ACL.RW.hashCode());
        System.out.println("rx: "+ACL.RX+ "; hash="+ACL.RX.hashCode());
        System.out.println("wx: "+ACL.WX+ "; hash="+ACL.WX.hashCode());
        System.out.println("rwx: "+ACL.RWX+ "; hash="+ACL.RWX.hashCode());
    }*/
}
