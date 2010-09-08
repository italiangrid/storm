/**
 * Class for creating fo StoRI by file
 * 
 * Edited by Michele Dibenedetto
 */

package it.grid.storm.namespace.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathCreator {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(PathCreator.class);

    private File file;
    private boolean recursive;
    private int level;

    public PathCreator(File file, boolean recursive, int level) {
        this.file = file;
        log.debug("<PathCreator>: New Path Created: " + file.toString());
        this.recursive = recursive;
        this.level = level;

    }

    public Collection<String> generateChildrenNoFolders() {
    	return generateChildrenNoFolders(this.file , this.recursive , this.level);
    }
    
    /**
     * If file is a directory returns the list of files contained 
     * in the directory, then on each subfolder if recursive is true or level is greater than zero 
     * call itself recursively on it decreasing the level.
     * If file is file returns the file itself
     * 
     * @param file
     * @param recursive
     * @param level
     * @return
     */
	private Collection<String> generateChildrenNoFolders(File file, boolean recursive, int level) {

		ArrayList<String> children = new ArrayList<String>();
		log.debug("Generating children of = " + file + " with recursive option = " + recursive
			+ " and recursion level = " + level);
		if(file.isDirectory())
		{
			log.debug("Is a directory");
			if(recursive || (level > 0))
			{
				log.debug("Recursion permitted. Inspectiong the content");
				String[] arrayOfNames = file.list();
				if(arrayOfNames != null)
				{
					for(String filePath : arrayOfNames)
					{
						log.debug("Analizing child = " + filePath);
						if(!(filePath.startsWith(".")))
						{
							File child = new File(file, filePath);
							if(child.isDirectory())
							{
								log.debug("It's a directory, calling recursive procedure"
									+ " with level " + (level - 1));
								children.addAll(generateChildrenNoFolders(child, recursive,
									level - 1));
							}
							else
							{
								log.debug("It is a file. Reached a leaf");
								children.add(child.toString());
							}
						}
					}
				}
			}
		}
		else
		{
			log.debug("It's a file, adding to the return collection");
			children.add(file.toString());
		}
		return children;
	}


	/**
	 * @param list
	 * @return
	 */
	public Collection<String> generateChildren() {

		ArrayList<String> children = new ArrayList<String>();
		String[] arrayOfNames = null;
		log.debug("Generating children of = " + file + " with recursive option = " + recursive + " and recursion level = " + level);
		if(file.isDirectory() && (recursive || level > 0))
		{
			log.debug("Is a directory with recursion permitted. Inspectiong the content");
			arrayOfNames = file.list();
			if(arrayOfNames != null)
			{
				for(String filePath : arrayOfNames)
				{
					log.debug("Analizing child = " + filePath);
					if(!(filePath.startsWith(".")))
					{
						PathCreator path =
										   new PathCreator(new File(file, filePath), recursive,
											   level - 1);
						children.addAll(path.generateChildren());
					}
				}
			}

		}
		else
		{
			children.add(file.toString());
		}
		return children;
	}

}
