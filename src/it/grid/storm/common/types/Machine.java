package it.grid.storm.common.types;

/**
 * This class represents the name of a machine in a SFN.
 *
 * @author  EGRID - ICTP Trieste; CNAF - Bologna
 * @date    March 25th, 2005
 * @version 2.0
 */
public class Machine {
	private String name = ""; //name of the machine in the SFN
	private boolean empty = true; //boolean true if this object is the empty object
	
	private Machine(String name, boolean empty){
		this.name = name.replaceAll(" ","" );
		this.empty = empty;
	}

    /**
     * Static method that returns an empty Machine.
     */
	public static Machine makeEmpty() {
		return new Machine("",true);
	}
	
	/**
	 * Static method requiring the name of the machine: it cannot be null or the empty
     * String, otherwise an InvalidMachineAttributeException is thrown. Beware that
     * any empty space is removed.
	 */
	public static Machine make(String s)  throws InvalidMachineAttributeException {
		if ((s == null) || (s=="")) throw new InvalidMachineAttributeException(s);
		return new Machine(s,false);
	} 

	/**
	 * Return true if Empty instance of machine object
	 */
	public boolean isEmpty() {
		return empty;
	}
	

	public String getValue() {
		return name;
	}


    
	public String toString() {
		if (empty) return "Empty Machine";
		return name;
	}

	public boolean equals(Object o) {	
		if (o==this) return true;
		if (!(o instanceof Machine)) return false;
		Machine mo = (Machine) o;
		if(mo.empty && empty) return true; 
		return (!mo.empty && !empty && mo.getValue().equals(name));
	}
	
	public int hashCode() {
		if (empty) return 0;
		int hash = 17;
		return 37*hash + name.hashCode();
	}


/*
    public static void main(String[] args) {
        //
        //Testing empty Machine
        System.out.println("Testing empty Machine objects...");
        Machine me1 = Machine.makeEmpty(); System.out.println("me1 is an empty Machine; should see Empty Machine:"+me1+"; should see hashCode 0:"+me1.hashCode()+"; it is empty so should see true:"+me1.isEmpty()+"; getValue():"+me1.getValue());
        Machine me2 = Machine.makeEmpty(); System.out.println("me2 is an empty Machine; should see Empty Machine:"+me2+"; should see hashCode 0:"+me2.hashCode()+"; it is empty so should see true:"+me2.isEmpty()+"; getValue():"+me2.getValue());
        System.out.println("me1.equals(me2) should see true:"+ me1.equals(me2)+"; me2.equals(me1) should see true:"+me2.equals(me1));
        //
        //Testing correct port creation
        System.out.println("\n\nTesting correct creation of Machine objects...");
        try {
            Machine m1 = Machine.make("m1"); System.out.println("Machine m1 - should see m1: "+m1+"; hashCode:"+m1.hashCode()+"; isEmpty should be false:"+m1.isEmpty()+"; getValue():"+m1.getValue());
            Machine m2 = Machine.make("m2"); System.out.println("Machine m2 - should see m2: "+m2+"; hashCode:"+m2.hashCode()+"; isEmpty should be false:"+m2.isEmpty()+"; getValue():"+m2.getValue());
            System.out.println("m1.equals(m2) false: "+m1.equals(m2));
            System.out.println("m2.equals(m1) false: "+m2.equals(m1));
            System.out.println("m1.equals(m1) true: "+m1.equals(m1));
            System.out.println("m1.equals(null) false: "+m1.equals(null));
            System.out.println("m1.equals(Object) false: "+m1.equals(new Object()));
            System.out.println("m1.equals(empty) false: "+m1.equals(Machine.makeEmpty()));
            System.out.println("empty.equals(m1) false: "+Machine.makeEmpty().equals(m1));
        } catch (Exception e) {
            System.out.println("Should not see this!");
        }
        //
        //TEsting Exception handling
        System.out.println("\n\nTesting object creation with invalid attribute...");
        System.out.print("Attempting creation with null: ");
        try {
            Machine errm = Machine.make(null);
            System.out.println("Should not see this!");
        } catch (InvalidMachineAttributeException e) {
            System.out.println(" creation failed as expected. " + e);
        }
        System.out.print("Attempting creation with empty String: ");
        try {
            Machine errm = Machine.make("");
            System.out.println("Should not see this!");
        } catch (InvalidMachineAttributeException e) {
            System.out.println(" creation failed as expected. " + e);
        }
        //
        //Testing empty space removal
        String spstr1 = " No Empty Space ";
        String spstr2 = "NoEmptySpace";
        System.out.println("\n\nTesting empty space removal:");
        try {
            Machine m1 = Machine.make(spstr1); System.out.println("Created Machine with "+spstr1+"; should see "+spstr2+": "+m1);
            Machine m2 = Machine.make(spstr2); System.out.println("Created Machine with "+spstr2+"; should see "+spstr2+": "+m2);
            System.out.println("Both should be equal - m1.equals(m2) true:" + m1.equals(m2));
        } catch (Exception e) {
            System.out.println("Should not see this!"+e);
        }
    }*/
}

