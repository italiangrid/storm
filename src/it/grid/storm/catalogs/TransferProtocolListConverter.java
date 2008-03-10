package it.grid.storm.catalogs;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.common.types.TransferProtocol;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * Package private auxiliary class used to convert between the DB raw data
 * representation and StoRM s Object model list of transfer protocols.
 *
 * @author:  EGRID ICTP
 * @version: 3.0
 * @date:    June 2005
 */
class TransferProtocolListConverter {

    private Map DBtoSTORM = new HashMap();
    private Map STORMtoDB = new HashMap();
    private static TransferProtocolListConverter c = new TransferProtocolListConverter();

    private TransferProtocolListConverter() {
        DBtoSTORM.put("gsiftp",TransferProtocol.GSIFTP);
        DBtoSTORM.put("file",TransferProtocol.FILE);
        DBtoSTORM.put("rfio",TransferProtocol.RFIO);
        DBtoSTORM.put("root",TransferProtocol.ROOT);
        
        Object aux;
        for (Iterator i = DBtoSTORM.keySet().iterator(); i.hasNext(); ) {
            aux = i.next();
            STORMtoDB.put(DBtoSTORM.get(aux),aux);
        }
    }

    /**
     * Method that returns the only instance of TransferProtocolListConverter.
     */
    public static TransferProtocolListConverter getInstance() {
        return c;
    }

    /**
     * Method that returns a List of Uppercase Strings used in the DB to
     * represent the given TURLPrefix. An empty List is returned in case
     * the conversion does not succeed, a null TURLPrefix is supplied, or
     * its size is 0.
     */
    public List toDB(TURLPrefix p) {
        List aux = new ArrayList();
        String str = null;
        if ((p==null) || (p.size()==0)) return aux;
        for (int i=0; i<p.size(); i++) {
            str = ((String)STORMtoDB.get(p.getTransferProtocol(i))).toUpperCase();
            aux.add(i,str);
        }
        return aux;
    }

    /**
     * Method that returns a TURLPrefix of transfer protocol. If the translation
     * cannot take place, a TURLPrefix of size 0 is returned. Likewise if a null
     * List is supplied.
     */
    public TURLPrefix toSTORM(List l) {
        TURLPrefix aux = new TURLPrefix();
        String str = null;
        TransferProtocol tp = null;
        if (l!=null) for (Iterator i = l.iterator(); i.hasNext(); ) {
            str = ((String)i.next()).toLowerCase();
            tp = (TransferProtocol) DBtoSTORM.get(str);
            if (tp!=null) aux.addTransferProtocol(tp);
        }
        return aux;
    }
}
