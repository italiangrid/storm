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

package it.grid.storm.check;


import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto
 *         THIS CLASS HAS TO BE TESTED
 */
public class SimpleClassLoaderCheckManager extends CheckManager
{

    private static final Logger log = LoggerFactory.getLogger(SimpleClassLoaderCheckManager.class);

    private ArrayList<Check> checks = new ArrayList<Check>();

    @Override
    protected Logger getLogger()
    {
        return log;
    }

    @Override
    protected void loadChecks()
    {
        CodeSource source = SimpleClassLoaderCheckManager.class.getProtectionDomain().getCodeSource();
        URL location = null;
        if (source != null)
        {
            location = source.getLocation();
            System.out.println(location);
        }
        String packageResourcePath = "it" + File.separatorChar + "grid" + File.separatorChar + "storm"
                + File.separatorChar + "check" + File.separatorChar + "sanity";
        List<String> classes = getClasseNamesInPackage(location.toString(), packageResourcePath);
        for (String className : classes)
        {
            Class classe = null;
            try
            {
                classe = Class.forName(className);
            }
            catch (ClassNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Constructor constructor;
            try
            {
                constructor = classe.getConstructor();
                try
                {
                    Check check = (Check) constructor.newInstance();
                    checks.add(check);
                }
                catch (IllegalArgumentException e)
                {

                }
                catch (InstantiationException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IllegalAccessException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (InvocationTargetException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            catch (SecurityException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            catch (NoSuchMethodException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    private List<String> getClasseNamesInPackage(String jarName, String packageName)
    {
        ArrayList<String> arrayList = new ArrayList<String>();
        packageName = packageName.replaceAll("\\.", "" + File.separatorChar);
        try
        {
            JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName));
            JarEntry jarEntry;
            while (true)
            {
                jarEntry = jarFile.getNextJarEntry();
                if (jarEntry == null)
                {
                    break;
                }
                if ((jarEntry.getName().startsWith(packageName)) && (jarEntry.getName().endsWith(".class")))
                {
                    arrayList.add(jarEntry.getName().replaceAll("" + File.separatorChar, "\\."));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return arrayList;
    }

    @Override
    protected List<Check> prepareSchedule()
    {
        return checks;
    }
}
