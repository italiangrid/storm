package component.filesystem;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Jdk14Logger;
import org.apache.commons.logging.impl.Log4JLogger;
import java.io.File;
import org.apache.log4j.PropertyConfigurator;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.filesystem.Filesystem;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.filesystem.swig.genericfs;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;



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
public class TestLocalFile {

  private static Log log = LogFactory.getLog(TestLocalFile.class);

  private void init() {

    boolean jdk14Logger = (log instanceof Jdk14Logger);
    boolean log4jlog = (log instanceof Log4JLogger);

    if (jdk14Logger) {
      System.out.println("Using Jdk14Logger = " + jdk14Logger);
    }
    if (log4jlog) {
      System.out.println("Using Log14Logger = " + log4jlog);
      String logConfigFile = System.getProperty("user.dir") + File.separator +
          "unittest" + File.separator + "log4j_for_testing.properties";
      System.out.println("config file = " + logConfigFile);
      PropertyConfigurator.configure(logConfigFile);
    }

  }


  private LocalFile createNewLocalFile(String filename, Filesystem fs){
    LocalFile localFile = new LocalFile(filename, fs);
    return localFile;
  }

  private Filesystem getFilesystem(String className, String rootPath) throws NamespaceException {
    Filesystem fsWrapper = new Filesystem(makeFSInstance(className, rootPath));
    return fsWrapper;
  }

  private genericfs makeFSInstance(String className, String rootPath) throws NamespaceException {

    Class fsDriver = null;
    try {
      fsDriver = Class.forName(className);
    }
    catch (ClassNotFoundException ex2) {
      log.error("Unable to instantiate class " + className, ex2);
      throw new NamespaceException("Unable to instantiate class " + className, ex2);
    }

    genericfs fs = null;

    Class fsArgumentsClass[] = new Class[1];
    fsArgumentsClass[0] = String.class;
    Object[] fsArguments = new Object[] {
        rootPath};
    Constructor fsConstructor = null;
    try {
      fsConstructor = fsDriver.getConstructor(fsArgumentsClass);
    }
    catch (SecurityException ex) {
      log.error("Unable to retrieve the FS Driver Constructor. Security problem.", ex);
      throw new NamespaceException("Unable to retrieve the FS Driver Constructor. Security problem.", ex);
    }
    catch (NoSuchMethodException ex) {
      log.error("Unable to retrieve the FS Driver Constructor. Security problem.", ex);
      throw new NamespaceException("Unable to retrieve the FS Driver Constructor. No such constructor.", ex);
    }
    try {
      fs = (genericfs) fsConstructor.newInstance(fsArguments);
    }
    catch (InvocationTargetException ex1) {
      log.error("Unable to instantiate the FS Driver. Wrong target.", ex1);
      throw new NamespaceException("Unable to instantiate the FS Driver. ", ex1);
    }
    catch (IllegalArgumentException ex1) {
      log.error("Unable to instantiate the FS Driver. Using wrong argument.", ex1);
      throw new NamespaceException("Unable to instantiate the FS Driver. Using wrong argument.", ex1);
    }
    catch (IllegalAccessException ex1) {
      log.error("Unable to instantiate the FS Driver. Illegal Access.", ex1);
      throw new NamespaceException("Unable to instantiate the FS Driver. Illegal Access.", ex1);
    }
    catch (InstantiationException ex1) {
      log.error("Unable to instantiate the FS Driver. Generic problem..", ex1);
      throw new NamespaceException("Unable to instantiate the FS Driver. Generic problem..", ex1);
    }

    return fs;
  }


  private void testCreate(){
    String driverClassName = "it.grid.storm.filesystem.swig.gpfs";
    String rootPath = "/mnt/gpfs/";
    String fileName = "prova1.txt";
    LocalFile file = null;
    try {
      file = createNewLocalFile(fileName, getFilesystem(driverClassName, rootPath));
    }
    catch (NamespaceException ex) {
      ex.printStackTrace();
      log.error("CREATION TEST : FAILED!");
    }
    if (file!=null) {
      log.debug("Local File created : "+file.getPath());
    }
  }


  public static void main(String[] args) {
    TestLocalFile test = new TestLocalFile();
    test.init();

  }



}
