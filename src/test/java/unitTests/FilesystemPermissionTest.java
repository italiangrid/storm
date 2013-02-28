package unitTests;

import static org.junit.Assert.*;
import it.grid.storm.filesystem.FilesystemPermission;
import org.junit.Before;
import org.junit.Test;

public class FilesystemPermissionTest
{

    @Before
    public void setUp() throws Exception
    {
    }

    @Test
    public final void testDeny()
    {
        FilesystemPermission f = FilesystemPermission.ListTraverseWrite;
        assertEquals("Expected a traverse permission" ,f.deny(FilesystemPermission.ReadWrite).getInt() ,FilesystemPermission.Traverse.getInt());
    }

}
