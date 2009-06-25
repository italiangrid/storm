package it.grid.storm.namespace;

import it.grid.storm.config.Configuration;

import java.io.File;

import org.apache.xerces.parsers.SAXParser;
import org.slf4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */
public class NamespaceValidator {

    private static String schemaURL;
    private static String namespaceFile;
    private Logger log = NamespaceDirector.getLogger();

    public NamespaceValidator() {
        //String configPath = Configuration.getInstance().getNamespaceConfigPath() + File.separator;
        String configPath = System.getProperty("user.dir") + File.separator + "etc" + File.separator;
        schemaURL = configPath + "namespace.xsd";
        namespaceFile = configPath + Configuration.getInstance().getNamespaceConfigFilename();
        /**
             log.debug("CONFIG PATH        : "+configPath);
             log.debug("SCHEMA URL         : "+schemaURL);
             log.debug("NAMESPACE FILENAME : "+namespaceFile);
         **/
    }

    public boolean validateSchema(String SchemaUrl, String XmlDocumentUrl) {
        boolean valid = false;
        SAXParser parser = new SAXParser();
        try {
            parser.setFeature("http://xml.org/sax/features/validation", true);
            parser.setFeature("http://apache.org/xml/features/validation/schema", true);
            parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
            parser.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", SchemaUrl);
            Validator handler = new Validator();
            parser.setErrorHandler(handler);
            parser.parse(XmlDocumentUrl);
            if (handler.validationError == true) {
                manageErrorWithinNamespace(handler);
            }
            else {
                log.info("Namespace Document is valid with Schema");
                valid = true;
            }
        }
        catch (java.io.IOException ioe) {
            log.error("IOException" + ioe.getMessage());
        }
        catch (SAXException e) {
            log.error("SAXException" + e.getMessage());
        }
        return valid;
    }

    /**
     * manageErrorWithinNamespace
     *
     * @param handler Validator
     */
    private void manageErrorWithinNamespace(it.grid.storm.namespace.NamespaceValidator.Validator handler) {
        StringBuffer sb = new StringBuffer();
        sb.append("##############################################" + "\n");
        System.out.println();
        sb.append("###   WARNING :  namespace.xml   INVALID   ###" + "\n");
        sb.append("##############################################" + "\n");
        sb.append("# Please check it. " + "\n");
        sb.append("# The error is : " + handler.saxParseException.getMessage() + "\n");
        sb.append("#   at line : " + handler.saxParseException.getLineNumber() +
                ", column " + handler.saxParseException.getColumnNumber() + "\n");
        sb.append("#   in entity : " + handler.saxParseException.getSystemId() + "\n");
        sb.append("##############################################" + "\n");
        System.out.println(sb.toString());
        log.error(sb.toString());

    }

    private class Validator
    extends DefaultHandler {
        public boolean validationError = false;
        public SAXParseException saxParseException = null;

        @Override
        public void error(SAXParseException exception) throws SAXException {

            log.error("ERROR : " + exception.getMessage());
            log.error(" at line " + exception.getLineNumber() + ", column " + exception.getColumnNumber());
            log.error(" in entity " + exception.getSystemId());

            validationError = true;
            saxParseException = exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {

            log.error("FATAL ERROR: " + exception.getMessage());
            log.error(" at line " + exception.getLineNumber() + ", column " + exception.getColumnNumber());
            log.error(" in entity " + exception.getSystemId());

            validationError = true;
            saxParseException = exception;
        }

        @Override
        public void warning(SAXParseException exception) throws SAXException {

            log.error("Warning: " + exception.getMessage());
            log.error(" at line " + exception.getLineNumber() + ", column " + exception.getColumnNumber());
            log.error(" in entity " + exception.getSystemId());

        }
    }
}
