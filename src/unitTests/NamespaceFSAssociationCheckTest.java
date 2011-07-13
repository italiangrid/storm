/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package unitTests;

import static org.junit.Assert.*;
import it.grid.storm.check.CheckResponse;
import it.grid.storm.check.GenericCheckException;
import it.grid.storm.check.sanity.filesystem.NamespaceFSAssociationCheck;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.VirtualFS;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.Test;



/**
 * @author Michele Dibenedetto
 *
 */
public class NamespaceFSAssociationCheckTest
{


    /**
     * Test method for {@link it.grid.storm.check.sanity.filesystem.NamespaceFSAssociationCheck#NamespaceFSAssociationCheck(java.util.Map, java.util.Collection)}.
     */
    @Test
    public void testNamespaceFSAssociationCheck()
    {

        NamespaceFSAssociationCheck check = null;
        HashMap<String, String> mountPoints = null;
        HashSet<VirtualFSInterface> vfsSet = null;
        try
        {
            //null parameters
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
        }
        assertNull(check);
        
        mountPoints = new HashMap<String, String>();
        try
        {
            //null vfsSet
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
        }
        assertNull(check);
        
        mountPoints = null;
        vfsSet = new HashSet<VirtualFSInterface>();
        try
        {
            //null mountPoints
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
        }
        assertNull(check);
        
        mountPoints = new HashMap<String, String>();
        mountPoints.put(null, "test");
        try
        {
            //null mountPoint key
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
            
        }
        assertNull(check);
        
        mountPoints.clear();
        mountPoints.put("key_test", null);
        try
        {
            //null mountPoint value
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
        }
        assertNull(check);
        
        mountPoints.clear();
        vfsSet.add(null);
        try
        {
            //null vfs element
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
        }
        assertNull(check);
        
        VirtualFS vfs = new VirtualFS(true);
        vfsSet.add(vfs);
        try
        {
            //null vfs element fields
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
        }
        assertNull(check);
        
        vfsSet.clear();
        vfs.setFSType("test_type");
        vfsSet.add(vfs);
        try
        {
            //null vfs element rootPath field
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
        }
        assertNull(check);
        
        vfsSet.clear();
        vfs = new VirtualFS(true);
        try
        {
            vfs.setRoot("test_root");
        }
        catch (NamespaceException e1)
        {
            //never thrown
        }
        vfsSet.add(vfs);
        try
        {
            //null vfs element FSType field
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
        }
        assertNull(check);
        
        vfsSet.clear();
        vfs.setFSType("test_type");
        vfsSet.add(vfs);
        try
        {
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
        }
        assertNotNull(check);
    }

    /**
     * Test method for {@link it.grid.storm.check.sanity.filesystem.NamespaceFSAssociationCheck#execute()}.
     */
    @Test
    public void testExecute()
    {
        NamespaceFSAssociationCheck check = null;
        HashMap<String, String> mountPoints = new HashMap<String, String>();
        HashSet<VirtualFSInterface> vfsSet = new HashSet<VirtualFSInterface>();
        mountPoints.put("/u", "W");
        mountPoints.put("/u/aa/b", "X");
        mountPoints.put("/u/aa/c", "W");
        mountPoints.put("/u/aa", "Y");
        mountPoints.put("/u/aa/ee", "U");
        mountPoints.put("/u/cc/dd", "Z");
        
        VirtualFS vfs1 = new VirtualFS(true);
        vfs1.setFSType("X");
        try
        {
            vfs1.setRoot("/u/aa/b");
        }
        catch (NamespaceException e1)
        {
            //never thrown
        }
        vfsSet.add(vfs1);
        VirtualFS vfs2 = new VirtualFS(true);
        vfs2.setFSType("W");
        try
        {
            vfs2.setRoot("/u/e");
        }
        catch (NamespaceException e1)
        {
            //never thrown
        }
        vfsSet.add(vfs2);
        VirtualFS vfs3 = new VirtualFS(true);
        vfs3.setFSType("X");
        try
        {
            vfs3.setRoot("/u/aa/b/c/u");
        }
        catch (NamespaceException e1)
        {
            //never thrown
        }
        vfsSet.add(vfs3);
        VirtualFS vfs4 = new VirtualFS(true);
        vfs4.setFSType("Y");
        try
        {
            vfs4.setRoot("/u/aa/cc/u/m");
        }
        catch (NamespaceException e1)
        {
            //never thrown
        }
        vfsSet.add(vfs4);
        VirtualFS vfs5 = new VirtualFS(true);
        vfs5.setFSType("Y");
        try
        {
            vfs5.setRoot("/u/aa/e");
        }
        catch (NamespaceException e1)
        {
            //never thrown
        }
        vfsSet.add(vfs5);
        VirtualFS vfs6 = new VirtualFS(true);
        vfs6.setFSType("W");
        try
        {
            vfs6.setRoot("/u/cc");
        }
        catch (NamespaceException e1)
        {
            //never thrown
        }
        vfsSet.add(vfs6);
        VirtualFS vfs7 = new VirtualFS(true);
        vfs7.setFSType("ext3");
        try
        {
            vfs7.setRoot("/notexistent");
        }
        catch (NamespaceException e1)
        {
            //never thrown
        }
        vfsSet.add(vfs7);
        
        try
        {
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
        }
        CheckResponse response = null;
        try
        {
            response = check.execute();
        }
        catch (GenericCheckException e)
        {
            fail("Generic exception : " + e);
        }
        assertTrue(response.isSuccessfull());
        
        mountPoints.clear();
        vfsSet.clear();
        mountPoints.put("/u", "W");
        mountPoints.put("/u/aa/b", "X");
        mountPoints.put("/u/aa/c", "W");
        mountPoints.put("/u/aa", "Y");
        mountPoints.put("/u/aa/ee", "U");
        mountPoints.put("/u/cc/dd", "Z");
        
        vfs1 = new VirtualFS(true);
        vfs1.setFSType("W");
        try
        {
            vfs1.setRoot("/u/aa/b");
        }
        catch (NamespaceException e1)
        {
            //never thrown
        }
        vfsSet.add(vfs1);
        try
        {
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
        }
        response = null;
        try
        {
            response = check.execute();
        }
        catch (GenericCheckException e)
        {
            fail("Generic exception : " + e);
        }
        assertFalse(response.isSuccessfull());
        
        vfsSet.clear();
        vfs2 = new VirtualFS(true);
        vfs2.setFSType("Y");
        try
        {
            vfs2.setRoot("/u/ee");
        }
        catch (NamespaceException e1)
        {
            //never thrown
        }
        vfsSet.add(vfs2);
        try
        {
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
        }
        response = null;
        try
        {
            response = check.execute();
        }
        catch (GenericCheckException e)
        {
            fail("Generic exception : " + e);
        }
        assertFalse(response.isSuccessfull());
        vfsSet.clear();
        
        vfs3 = new VirtualFS(true);
        vfs3.setFSType("U");
        try
        {
            vfs3.setRoot("/u/aa/b/c/u");
        }
        catch (NamespaceException e1)
        {
            //never thrown
        }
        vfsSet.add(vfs3);
        try
        {
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
        }
        response = null;
        try
        {
            response = check.execute();
        }
        catch (GenericCheckException e)
        {
            fail("Generic exception : " + e);
        }
        assertFalse(response.isSuccessfull());
        vfsSet.clear();
        
        vfs4 = new VirtualFS(true);
        vfs4.setFSType("W");
        try
        {
            vfs4.setRoot("/u/aa/cc/u/m");
        }
        catch (NamespaceException e1)
        {
            //never thrown
        }
        vfsSet.add(vfs4);
        try
        {
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
        }
        response = null;
        try
        {
            response = check.execute();
        }
        catch (GenericCheckException e)
        {
            fail("Generic exception : " + e);
        }
        assertFalse(response.isSuccessfull());
        vfsSet.clear();
        
        vfs5 = new VirtualFS(true);
        vfs5.setFSType("W");
        try
        {
            vfs5.setRoot("/u/aa/e");
        }
        catch (NamespaceException e1)
        {
            //never thrown
        }
        vfsSet.add(vfs5);
        try
        {
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
        }
        response = null;
        try
        {
            response = check.execute();
        }
        catch (GenericCheckException e)
        {
            fail("Generic exception : " + e);
        }
        assertFalse(response.isSuccessfull());
        vfsSet.clear();
        
        vfs6 = new VirtualFS(true);
        vfs6.setFSType("Z");
        try
        {
            vfs6.setRoot("/u/cc");
        }
        catch (NamespaceException e1)
        {
            //never thrown
        }
        vfsSet.add(vfs6);
        try
        {
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
        }
        response = null;
        try
        {
            response = check.execute();
        }
        catch (GenericCheckException e)
        {
            fail("Generic exception : " + e);
        }
        assertFalse(response.isSuccessfull());
        vfsSet.clear();
        
        vfs7 = new VirtualFS(true);
        vfs7.setFSType("W");
        try
        {
            vfs7.setRoot("/notexistent");
        }
        catch (NamespaceException e1)
        {
            //never thrown
        }
        vfsSet.add(vfs7);
        try
        {
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
        }
        response = null;
        try
        {
            response = check.execute();
        }
        catch (GenericCheckException e)
        {
            fail("Generic exception : " + e);
        }
        assertFalse(response.isSuccessfull());
        
        
    }
    
    /**
     * Test method for {@link it.grid.storm.check.sanity.filesystem.NamespaceFSAssociationCheck#getName()}.
     */
    @Test
    public void testGetName()
    {
        NamespaceFSAssociationCheck check = null;
        HashMap<String, String> mountPoints = new HashMap<String, String>();
        HashSet<VirtualFSInterface> vfsSet = new HashSet<VirtualFSInterface>();
        VirtualFS vfs = new VirtualFS(true);
        vfs.setFSType("test_type");
        try
        {
            vfs.setRoot("test_root");
        }
        catch (NamespaceException e1)
        {
            //never thrown
        }
        vfsSet.add(vfs);
        try
        {
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
        }
        String name = check.getName();
        assertNotNull(name);
    }


    /**
     * Test method for {@link it.grid.storm.check.sanity.filesystem.NamespaceFSAssociationCheck#getDescription()}.
     */
    @Test
    public void testGetDescription()
    {
        NamespaceFSAssociationCheck check = null;
        HashMap<String, String> mountPoints = new HashMap<String, String>();
        HashSet<VirtualFSInterface> vfsSet = new HashSet<VirtualFSInterface>();
        VirtualFS vfs = new VirtualFS(true);
        vfs.setFSType("test_type");
        try
        {
            vfs.setRoot("test_root");
        }
        catch (NamespaceException e1)
        {
            //never thrown
        }
        vfsSet.add(vfs);
        try
        {
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
        }
        String description = check.getDescription();
        assertNotNull(description);
    }


    /**
     * Test method for {@link it.grid.storm.check.sanity.filesystem.NamespaceFSAssociationCheck#isCritical()}.
     */
    @Test
    public void testIsCritical()
    {
        NamespaceFSAssociationCheck check = null;
        HashMap<String, String> mountPoints = new HashMap<String, String>();
        HashSet<VirtualFSInterface> vfsSet = new HashSet<VirtualFSInterface>();
        VirtualFS vfs = new VirtualFS(true);
        vfs.setFSType("test_type");
        try
        {
            vfs.setRoot("test_root");
        }
        catch (NamespaceException e1)
        {
            //never thrown
        }
        vfsSet.add(vfs);
        try
        {
            check = new NamespaceFSAssociationCheck(mountPoints, vfsSet);
        }
        catch (IllegalArgumentException e)
        {
        }
        boolean critical = check.isCritical();
        assertTrue(critical);
    }
}
