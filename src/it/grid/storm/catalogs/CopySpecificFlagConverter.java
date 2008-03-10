package it.grid.storm.catalogs;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import it.grid.storm.srm.types.TOverwriteMode;

/**
 * Package private auxiliary class used to convert between DPM and StoRM
 * representation of Copy TOverwriteMode+TDirOption request specific information,
 * and Flags in storm_copy_filereq.
 *
 * @author:  EGRID - ICTP Trieste
 * @version: 1.0
 * @date:    September 2005
 */
class CopySpecificFlagConverter {

    private Map DPMtoSTORM = new HashMap();
    private Map STORMtoDPM = new HashMap();

    private static CopySpecificFlagConverter c = new CopySpecificFlagConverter();

    /**
     * Private constructor that fills in the conversion table; in particular, DPM uses int
     * values to represent the pair of values:
     *
     * 0 NEVER + source NOT directory
     * 1 ALWAYS + source NOT directory
     * 2 WHENFILESAREDIFFERENT + source NOT directory
     * 4 NEVER + source is directory
     * 5 ALWAYS + source is directory
     * 6 WHENFILESAREDIFFERENT + source is directory
     */
    private CopySpecificFlagConverter() {
        DPMtoSTORM.put(new Integer(0),new Object[]{TOverwriteMode.NEVER,new Boolean(false)});
        DPMtoSTORM.put(new Integer(1),new Object[]{TOverwriteMode.ALWAYS,new Boolean(false)});
        DPMtoSTORM.put(new Integer(2),new Object[]{TOverwriteMode.WHENFILESAREDIFFERENT,new Boolean(false)});
        DPMtoSTORM.put(new Integer(4),new Object[]{TOverwriteMode.NEVER,new Boolean(true)});
        DPMtoSTORM.put(new Integer(5),new Object[]{TOverwriteMode.ALWAYS,new Boolean(true)});
        DPMtoSTORM.put(new Integer(6),new Object[]{TOverwriteMode.WHENFILESAREDIFFERENT,new Boolean(true)});
        Object aux;
        for (Iterator i = DPMtoSTORM.keySet().iterator(); i.hasNext(); ) {
            aux = i.next();
            STORMtoDPM.put(DPMtoSTORM.get(aux),aux);
        }
    }

    /**
     * Method that returns the only instance of CopySpecificFlagConverter.
     */
    public static CopySpecificFlagConverter getInstance() {
        return c;
    }

    /**
     * Method that returns the int used by DPM to represent the
     * given TOverwriteMode and isSourceADirectory boolean.
     * -1 is returned if no match is found.
     */
    public int toDPM(TOverwriteMode om, boolean isSourceADirectory) {
        Integer aux = (Integer) STORMtoDPM.get( new Object[]{om,new Boolean(isSourceADirectory)} );
        if (aux==null) return -1;
        return aux.intValue();
    }

    /**
     * Method that returns an Object[] containing the TOverwriteMode and the
     * Boolean used by StoRM to represent the supplied int representation of DPM.
     * An empty Object[] is returned if no StoRM type is found.
     */
    public Object[] toSTORM(int n) {
        Object[] aux =  (Object[]) DPMtoSTORM.get(new Integer(n));
        if (aux==null) return new Object[]{};
        return aux;
    }


    public String toString() {
        return "OverWriteModeConverter.\nDPMtoSTORM map:"+DPMtoSTORM+"\nSTORMtoDPM map:"+STORMtoDPM;
    }

}
