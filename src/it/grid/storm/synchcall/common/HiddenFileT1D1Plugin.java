package it.grid.storm.synchcall.common;

import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.naming.NamespaceUtil;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HiddenFileT1D1Plugin implements T1D1PluginInterface {

    private static final Logger log = LoggerFactory.getLogger(HiddenFileT1D1Plugin.class);

    public int startMigration(StoRI stori, String prefix) {
        if(stori!=null) {
            log.debug("HiddenFileT1D1PLugin: SURL filename"+stori.getFilename());
            String localPathWithoutFilename =  NamespaceUtil.consumeFileName(stori.getLocalFile().getPath());
            log.debug("HiddenFileT1D1PLugin: local path without filename "+ localPathWithoutFilename);
            String hiddenFileName = localPathWithoutFilename + "."+prefix+stori.getFilename();
            log.debug("HiddenFileT1D1Plugin: hidden file name "+hiddenFileName);

            File hiddenFile =   new File(hiddenFileName);
            try {
                hiddenFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch blo
                log.debug("HiddenFileT1D1Plugin: Error creating file "+e);
                return 1;
            }

            return 0;

        } else {
            return 1;
        }
    }

}
