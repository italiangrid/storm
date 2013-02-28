package unitTests;

import static org.junit.Assert.*;
import it.grid.storm.authz.AuthzException;
import it.grid.storm.authz.path.model.PathACE;
import org.junit.Before;
import org.junit.Test;

public class PathACEtest
{

    private static final String stormDefaultLine = "  @ALL@     /                        WRFDLMN          permit";

    @Before
    public void setUp() throws Exception
    {
    }

    @Test
    public final void testBuildFromString() throws AuthzException
    {
        PathACE.buildFromString(stormDefaultLine);
    }

}
