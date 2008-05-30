package it.grid.storm.xmlrpc.converter;

import org.apache.log4j.Logger;

import it.grid.storm.common.OperationType;
import it.grid.storm.xmlrpc.converter.directory.LsConverter;
import it.grid.storm.xmlrpc.converter.directory.MkdirConverter;
import it.grid.storm.xmlrpc.converter.directory.MvConverter;
import it.grid.storm.xmlrpc.converter.directory.RmdirConverter;
import it.grid.storm.xmlrpc.converter.directory.RmConverter;
import it.grid.storm.xmlrpc.converter.discovery.PingConverter;
import it.grid.storm.xmlrpc.converter.space.GetSpaceMetaDataConverter;
import it.grid.storm.xmlrpc.converter.space.GetSpaceTokensConverter;
import it.grid.storm.xmlrpc.converter.space.ReleaseSpaceConverter;
import it.grid.storm.xmlrpc.converter.space.ReserveSpaceConverter;

/**
 * This class is part of the StoRM project.
 * Copyright: Copyright (c) 2008 
 * Company: INFN-CNAF and ICTP/EGRID project
 *
 * @author lucamag
 * @date May 28, 2008
 *
 */


public class ConveterFactory {
    
    protected Logger log = Logger.getLogger("synch");


    /**
     * @param type
     * @return
     */
    public static Converter getConverter(OperationType type) {
       
        switch(type) {
        case RM: return new RmConverter();
        case RMD: return new RmdirConverter();
        case MKD:  return new MkdirConverter();
        case MV: return new MvConverter();
        case LS: return new LsConverter();
        
        case PNG:return new PingConverter();
        
        case GSM: return new GetSpaceMetaDataConverter();
        case GST: return new GetSpaceTokensConverter();
        case RESSP: return new ReserveSpaceConverter();
        case RELSP: return new ReleaseSpaceConverter();
        
        }
        throw new AssertionError("ConverterFactory: Unknown op: ");
    }

}
