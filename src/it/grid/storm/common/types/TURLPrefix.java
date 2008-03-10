package it.grid.storm.common.types;

import java.util.*;

/**
 * This class represent the Transport Protocol available to get file from a certain Storage Element.
 * This Trasnport Protocol prefix will be used to match with user specifed prefix  to TTURL Creation.
 */
public class TURLPrefix  {
	
	private ArrayList prefix;

	public TURLPrefix() {
		this.prefix = new ArrayList();
	}

    /**
     * Method used to add a TransferProtocol to this holding structure.
     * Null may also be added. A boolean true is returned if the holding
     * structure changed as a result of the add. If this holding structure
     * does not change, then false is returned.
     */
	public boolean addTransferProtocol(TransferProtocol tp) {
		return this.prefix.add(tp);
	}

    /**
     * Method used to retrieve a TransferProtocol from this holding structure.
     * An int is needed as index to the TransferProtocol to retrieve. Elements
     * are not removed!
     */
	public TransferProtocol getTransferProtocol(int index ) {
		return (TransferProtocol) prefix.get(index);
	}

	public int size() {
		return prefix.size();
	}
		
	public void print() {
	}

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("TURLPrefix: ");
        for (Iterator i = prefix.iterator(); i.hasNext(); ) {
            sb.append(i.next());
            sb.append(" ");
        }
        return sb.toString();
    }
	

//	public TransferProtocol[] getArray() {
//		return prefix.toArray();
//	}

/*    public static void main(String[] args) {
        //Testing adding, getting and writing to st.out
        System.out.println("Empty TURLPrefix: "+new TURLPrefix());
        TURLPrefix p = new TURLPrefix();
        p.addTransferProtocol(TransferProtocol.FILE);
        p.addTransferProtocol(TransferProtocol.GSIFTP);
        p.addTransferProtocol(TransferProtocol.EMPTY);
        System.out.println("\nTURLPrefix with FILE, GSIFTP, EMPTY; "+p);
        p.addTransferProtocol(TransferProtocol.FILE);
        p.addTransferProtocol(TransferProtocol.GSIFTP);
        p.addTransferProtocol(TransferProtocol.EMPTY);
        System.out.println("\nTURLPrefix with FILE, GSIFTP, EMPTY, FILE, GSIFTP, EMPTY; "+p);
        System.out.println("Size:"+p.size());
        System.out.print("Protocols: ");
        for (int i=0; i<p.size(); i++) System.out.print(p.getTransferProtocol(i)+" ");
        System.out.println("\n"+p+"\nSize:"+p.size());
        TURLPrefix n = new TURLPrefix(); n.addTransferProtocol(null);
        System.out.println("\nTURLPrefix with null: "+n);
    }*/
}
