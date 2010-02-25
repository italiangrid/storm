/**
 * 
 */
package component.namespace.config;

import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.config.NamespaceLoader;
import it.grid.storm.namespace.config.xml.XMLNamespaceLoader;
import it.grid.storm.namespace.config.xml.XMLNamespaceParser;
import it.grid.storm.namespace.config.xml.XMLParserUtil;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ritz
 */
public class DefaultACLTest {

    private static XMLNamespaceParser parser;
    private static XMLParserUtil parserUtil;

    private static final Logger log = LoggerFactory.getLogger(DefaultACLTest.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        DefaultACLTest tester = new DefaultACLTest();

        String path = System.getProperty("user.dir") + File.separator + "etc";
        String filename = "namespace_1.4.0_test.xml";
        int refresh = 3;

        // PersistenceDirector p;

        NamespaceLoader loader = new XMLNamespaceLoader(path, filename, refresh, false);
        parser = new XMLNamespaceParser(loader, false, true);
        parserUtil = new XMLParserUtil(loader.getConfiguration());

        tester.readDefaultACL();

    }

    /**
     * 
     */
    private void readDefaultACL() {

        int numFS = 0;
        int numDefaultACL = 0;
        try {
            numFS = parserUtil.getNumberOfFS();

            log.debug("Number of FS = " + numFS);

            // numDefaultACL = parserUtil.getDefaultACLDefined();
        } catch (NamespaceException ex) {
            log.error("Number of Mapping Rules error", ex);
        }
        log.debug("Number of Mapping Rule = " + numDefaultACL);
    }

}
