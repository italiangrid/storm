/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * @file   FilesystemError.java
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Source for the it.grid.storm.filesystem.FilesystemError class.
 */
/*
 * Copyright (c) 2006 Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 * You may copy, modify and distribute this file under the same terms
 * as StoRM itself.
 */

package it.grid.storm.filesystem;

/** Root class for all exceptions thrown by classes in the {@link
 * it.grid.storm.filesystem} package.
 * 
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 * @version $Revision: 1.1 $
 */
public class FilesystemError 
    extends RuntimeException
{
    /** Constructor, taking error message. Chains the parameter to the
     * superclass. 
     */
    public FilesystemError(final String msg)
    {
        super(msg);
    }

    /** Constructor with no parameters; for subclasses that implement
     * their own getMessage() method only.
     */
    protected FilesystemError()
    {
        // subclasses need to implement getMessage()
    }
}
