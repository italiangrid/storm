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


import it.grid.storm.check.sanity.filesystem.NamespaceFSAssociationCheck;
import it.grid.storm.check.sanity.filesystem.NamespaceFSExtendedAttributeDeclarationCheck;
import it.grid.storm.check.sanity.filesystem.NamespaceFSExtendedAttributeUsageCheck;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto
 */
public class SimpleCheckManager extends CheckManager
{

    private static final Logger log = LoggerFactory.getLogger(SimpleCheckManager.class);

    /**
     * A list of checks to be executed
     */
    private ArrayList<Check> checks = new ArrayList<Check>();

    @Override
    protected Logger getLogger()
    {
        return log;
    }

    @Override
    protected void loadChecks()
    {
        /* Add by hand a new element for each requested check */
        checks.add(new NamespaceFSAssociationCheck());
        checks.add(new NamespaceFSExtendedAttributeDeclarationCheck());
        checks.add(new NamespaceFSExtendedAttributeUsageCheck());
    }

    @Override
    protected List<Check> prepareSchedule()
    {
        return checks;
    }
}
