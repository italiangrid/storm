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

/**
 * This class represents the TOverwriteMode of an Srm request.
 * 
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Avril, 2005
 * @version 1.0
 */

package it.grid.storm.srm.types;


public class TOverwriteMode
{

    private String mode = null;

    public static final TOverwriteMode EMPTY = new TOverwriteMode("Empty");
    public static final TOverwriteMode NEVER = new TOverwriteMode("Never");
    public static final TOverwriteMode ALWAYS = new TOverwriteMode("Always");
    public static final TOverwriteMode WHENFILESAREDIFFERENT = new TOverwriteMode("WhenFilesAreDifferent");

    private TOverwriteMode(String mode)
    {
        this.mode = mode;
    }

    public String toString()
    {
        return mode;
    }

    public String getValue()
    {
        return mode;
    }

    /**
     * @param mode
     * @return
     * @throws IllegalArgumentException
     */
    public static TOverwriteMode getTOverwriteMode(String mode) throws IllegalArgumentException
    {
        if(mode == null)
        {
            throw new IllegalArgumentException("Received null mode parameter");
        }
        if (mode.equals(EMPTY.getValue()))
            return EMPTY;
        if (mode.equals(NEVER.getValue()))
            return NEVER;
        if (mode.equals(ALWAYS.getValue()))
            return ALWAYS;
        if (mode.equals(WHENFILESAREDIFFERENT.getValue()))
            return WHENFILESAREDIFFERENT;
        throw new IllegalArgumentException("No matching TOverwriteMode for String \'" + mode + "\'");
    }

}
