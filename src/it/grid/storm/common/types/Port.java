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

package it.grid.storm.common.types;

/**
 * This class represents a port in a SFN. An int between 0 and 65535 is required:
 * if the limits are exceeded then an InvalidPortAttributeException is thrown.
 *
 * @author  EGRID - ICTP Trieste; CNAF - Bologna
 * @date    March 25th, 2005
 * @version 2.0
 */
public class Port {

	private int port; //int representing the port number
	private boolean empty = true; //boolean true id this object refers to the empty port
	
	/**	
	 * Private constructor.
	 */
	private Port(int port, boolean empty){	
		this.port = port;
		this.empty = empty; 
	}

	/**
	 * Static method to make an empty port.
	 */
	public static  Port makeEmpty() {
		return new Port(-1,true);
	}

    /**
     * Static method used to make a non empty Port object. It requires an
     * int between 0 and 65535 representing the port: if the limits are
     * exceeded then an InvalidPortAttributeException is thrown.
     */
	public static Port make(int port)  throws InvalidPortAttributeException {
		if ((port<0) || (port>65535)) throw new InvalidPortAttributeException(port);
		return new Port(port, false);
	}

    /**
     * Method that returns whether this object refers to the empty port or not.
     */
	public boolean isEmpty() {
		return empty;
	}
	
	/**
	 * Method that returns an int representing this port. An empty
     * port will return -1.
	 */
	public int toInt() {
        if (empty) return -1;
		return port;
	}
	
	public int getValue() {
		return port;
	}



	public String toString() {
        if (empty) return "Empty Port";
		return ""+port;
	}

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Port)) return false;
        Port po = (Port) o;
      	if (po.empty && empty) return true;
	    return (!po.empty) && (!empty) && (port==po.port);
    }

    public int hashCode() {
        if (empty) return -1;
        int hash = 17;
        return 37*hash + port;
    }


/*
    public static void main(String[] args) {
        //
        //Testing empty Port creation
        System.out.println("Testing empty Port creation...");
        Port pe1 = Port.makeEmpty(); System.out.println("pe1 is an empty Port; should see empty port:"+pe1+"; should see hashCode -1:"+pe1.hashCode()+"; it is empty so should see true:"+pe1.isEmpty()+"; toInt should be -1:"+pe1.toInt()+"; getValue():"+pe1.getValue());
        Port pe2 = Port.makeEmpty(); System.out.println("pe2 is an empty Port; should see empty port:"+pe2+"; should see hashCode -1:"+pe2.hashCode()+"; it is empty so should see true:"+pe2.isEmpty()+"; toInt should be -1:"+pe2.toInt()+"; getValue():"+pe2.getValue());
        System.out.println("pe1.equals(pe2) should see true:"+ pe1.equals(pe2)+"; pe2.equals(pe1) should see true:"+pe2.equals(pe1));
        //
        //Testing correct port creation
        System.out.println("\n\nTesting correct creation of Port objects...");
        try {
            Port p0 = Port.make(0); System.out.println("Port 0 - should see 0: "+p0+"; hashCode:"+p0.hashCode()+"; isEmpty should be false:"+p0.isEmpty()+"; toInt should be 0:"+p0.toInt()+"; getValue() should be 0:"+p0.getValue());
            Port p65535 = Port.make(65535); System.out.println("Port 65535 - should see 65535: "+p65535+"; hashCode:"+p65535.hashCode()+"; isEmpty should be false:"+p65535.isEmpty()+"; toInt should be 65535:"+p65535.toInt()+"; getValue() should be 65535:"+p65535.getValue());
            System.out.println("p0.equals(p65535) false: "+p0.equals(p65535));
            System.out.println("p65535.equals(p0) false: "+p65535.equals(p0));
            System.out.println("p0.equals(p0) true: "+p0.equals(p0));
            System.out.println("p0.equals(null) false: "+p0.equals(null));
            System.out.println("p0.equals(Object) false: "+p0.equals(new Object()));
            System.out.println("p0.equals(empty) false: "+p0.equals(Port.makeEmpty()));
            System.out.println("empty.equals(p0) false: "+Port.makeEmpty().equals(p0));
        } catch (Exception e) {
            System.out.println("Should not see this!");
        }
        //
        //TEsting Exception handling
        System.out.println("\n\nTesting object creation with invalid attribute...");
        System.out.print("Attempting creation with -1: ");
        try {
            Port errp = Port.make(-1);
            System.out.println("Should not see this!");
        } catch (InvalidPortAttributeException e) {
            System.out.println(" creation failed as expected. " + e);
        }
        System.out.print("Attempting creation with 65536: ");
        try {
            Port errp = Port.make(65536);
            System.out.println("Should not see this!");
        } catch (InvalidPortAttributeException e) {
            System.out.println(" creation failed as expected. " + e);
        }
    }*/
}
