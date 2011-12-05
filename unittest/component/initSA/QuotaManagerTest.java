package component.initSA;

import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.space.quota.QuotaManager;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.utiltest.SetUpTest;

public class QuotaManagerTest {

    private NamespaceInterface namespace;
    private static Logger log = LoggerFactory.getLogger(QuotaManagerTest.class);

    private void init() {

        //Logging Set Up
        System.out.println("Using SLF4J and LogBack");
        String unitTestPath = System.getProperty("user.dir") + File.separator + "unittest" + File.separator;
        String logConfigFile = unitTestPath + "logging-test.xml";
        SetUpTest.init(logConfigFile);
        NamespaceDirector.initializeDirector(false,true);
        namespace = NamespaceDirector.getNamespace();
    }

    
    @Test
    public void testUpdateSAwithQuota() {
        init();
        QuotaManager qm = QuotaManager.getInstance();
        qm.updateSAwithQuotaSynch(true);
        
        
    }

}
