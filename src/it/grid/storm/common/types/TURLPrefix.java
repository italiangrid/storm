package it.grid.storm.common.types;

import java.util.*;
import it.grid.storm.namespace.model.Protocol;

/**
 * This class represent the Transport Protocol available to get file from a certain Storage Element.
 * This Trasnport Protocol prefix will be used to match with user specifed prefix  to TTURL Creation.
 */
public class TURLPrefix  {

	private ArrayList<Protocol> desiredProtocols;

	public TURLPrefix() {
		this.desiredProtocols = new ArrayList<Protocol>();
	}

    /**
     * Method used to add a TransferProtocol to this holding structure.
     * Null may also be added. A boolean true is returned if the holding
     * structure changed as a result of the add. If this holding structure
     * does not change, then false is returned.
     */
	public boolean addProtocol(Protocol protocol) {
		return this.desiredProtocols.add(protocol);
	}

    /**
     * Method used to retrieve a TransferProtocol from this holding structure.
     * An int is needed as index to the TransferProtocol to retrieve. Elements
     * are not removed!
     */
	public Protocol getProtocol(int index ) {
		return desiredProtocols.get(index);
	}

        public List<Protocol> getDesiredProtocols() {
          return this.desiredProtocols;
        }

	public int size() {
		return desiredProtocols.size();
	}

	public void print() {
	}

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("TURLPrefix: ");
        for (Iterator i = desiredProtocols.iterator(); i.hasNext(); ) {
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
