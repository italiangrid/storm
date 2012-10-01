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

package it.grid.storm.srm.types;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * This class represents a Request Token
 *
 * @author  Magnoni Luca
 * 
 */


@SuppressWarnings("serial")
public class TRequestToken implements Serializable 
{
    public static final String PNAME_REQUESTOKEN = "requestToken";

    private String token; //string representing the token!

    private TRequestToken() {
        token = "";
    }
    
    /**
     * Constructor that requires a String representing the token. If it is null,
     * then an InvalidAttributeException is thrown.
     */
    public TRequestToken(String token) throws InvalidTRequestTokenAttributesException
    {
        if (token == null)
            throw new InvalidTRequestTokenAttributesException(token);
        this.token = token;
    }

    
    public static TRequestToken decode(Map inputParam, String fieldName)
                    throws InvalidTRequestTokenAttributesException
    {
        String requestToken = (String) inputParam.get(fieldName);
        return new TRequestToken(requestToken);
    }

    public static TRequestToken getRandom(){
        UUID token = UUID.randomUUID();
        try
        {
            return new TRequestToken(token.toString());
        } catch(InvalidTRequestTokenAttributesException e)
        {
            //never thrown
            throw new IllegalStateException("Unexpected InvalidTRequestTokenAttributesException", e);
        }
    }
    
    
    public static TRequestToken buildLocalRT(String localRequestToken) {
        TRequestToken result = new TRequestToken();
        result.token = localRequestToken;
        return result;
    }
    
    public boolean isLocalToken() {
        if (token.startsWith("local")) return true;
        else return false;
    }
    
    
    public String getValue()
    {
        return token;
    }

    public String toString()
    {
        return token;
    }

    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof TRequestToken))
            return false;
        TRequestToken st = (TRequestToken) o;
        return token.equals(st.token);
    }

    public int hashCode()
    {
        int hash = 17;
        return 37 * hash + token.hashCode();
    }




}
