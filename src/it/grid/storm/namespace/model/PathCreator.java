/**
 * Class for creating fo StoRI by file
 */

package it.grid.storm.namespace.model;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

public class PathCreator {

    String[] array;
    /**
     * Logger.
     */
    private static final Logger log = Logger.getLogger("namespace");

    private File file;
    private boolean recursive;
    private int level;

    public PathCreator(File file, boolean recursive, int level) {
        this.file = file;
        log.debug("<PathCreator>: New Path Created: " + file.toString());
        this.recursive = recursive;
        this.level = level;

    }

    public Collection generateChild(ArrayList list) {

        //log.debug("--------DIRECTORY="+file.isDirectory()+"rec: "+recursive);

        if (file.isDirectory() && (recursive || level > 0)) {
            array = file.list();
            if(array != null) {
                for (int i = 0; i < array.length; i++) {
                    log.debug("Analizzando dir = " + array[i]);
                    if(!(array[i].startsWith("."))) {
                        PathCreator path = new PathCreator(new File(file, array[i]), recursive, level - 1);
                        path.generateChild(list);
                    }
                }
            }
            
        } else {
            list.add(file.toString());
        }
        //Return List only when visit is finished!!
        return list;
    }
    
    
    public Collection generateFirstLevelChild(ArrayList list) {

        if (file.isDirectory() && (recursive || level > 0)) {
            array = file.list();
            if(array != null) {
                for (int i = 0; i < array.length; i++) {
                    log.debug("Analizzando dir = " + array[i]);
                    if(!(array[i].startsWith("."))) {
                        PathCreator path = new PathCreator(new File(file, array[i]), recursive, level - 1);
                        path.generateChild(list);
                    }
                }
            }
            
        } else {
            list.add(file.toString());
        }
        //Return List only when visit is finished!!
        return list;
    }

}
