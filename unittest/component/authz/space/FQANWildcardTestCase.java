/**
 * 
 */
package component.authz.space;

import it.grid.storm.authz.sa.model.EGEEFQANPattern;

import java.io.File;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.utiltest.SetUpTest;

/**
 * @author zappi
 *
 */
public class FQANWildcardTestCase extends TestCase {

    private final String[] validPattern = {
            "/vo/subgroup",
            "/vo/subgroup/Role=role",
            "/vo/subgroup/*/Role=role",
            "/vo/*/Role=*",
            "/vo/subgroup/Role=*"
    };

    private final String[] invalidPattern = {
            "/*",
            "/vo/*/subgroup",
            "/vo/subgroup*",
            "/vo/*ubgroup",
            "/vo/subgroup/**",
            "/vo/*/*"
    };



    private static Logger log = LoggerFactory.getLogger(FQANWildcardTestCase.class);

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        System.out.println("Using SLF4J and LogBack");
        String unitTestPath = System.getProperty("user.dir") + File.separator + "unittest" + File.separator;
        String logConfigFile = unitTestPath + "logging-test.xml";
        SetUpTest.init(logConfigFile);

    }


    public void testValidFQANPatternSintax() {
        for (String element : validPattern) {
            log.debug(element);
            assertEquals(true, isPatternValid(element));
        }
    }

    public void testInvalidFQANPatternSintax() {
        for (String element : invalidPattern) {
            log.debug(element);
            assertEquals(false, isPatternValid(element));
        }
    }

    private boolean isPatternValid(String pattern) {
        boolean result = true;
        try {
            EGEEFQANPattern fp = new EGEEFQANPattern(pattern);
        } catch (Exception e) {
            result = false;
        }
        return result;
    }


}
