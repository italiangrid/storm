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

package it.grid.storm.tape.recalltable.model;

import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TSURL;

import java.util.StringTokenizer;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PutTapeRecallStatusValidator {

    private static final Logger log = LoggerFactory.getLogger(PutTapeRecallStatusValidator.class);

    private String requestToken = null;
    private StoRI stori = null;
    private String inputString = null;
    private Response validationResponse = null;

    public PutTapeRecallStatusValidator(String inputString) {
        this.inputString = inputString;
    }

    /**
     * Parse and validate input.
     * <p>
     * If this method returns <code>true</code> the input data can be retrieved with the methods:
     * {@link #getRequestToken()} and {@link #getStoRI()}.
     * <p>
     * If this method returns <code>false</code> the response can be retrieved with the method {@link #getResponse()}.
     * 
     * @return <code>true</code> for successful validation process, <code>false</code> otherwise.
     */
    public boolean validate() {

        StringTokenizer tokenizer = new StringTokenizer(inputString, "\n");
        
        if (tokenizer.countTokens() != 2) {
            
            log.trace("putTaskStatus() - input error");
            
            validationResponse = Response.status(400).build();
            return false;
            
        }

        String requestTokenInput = tokenizer.nextToken();
        String surlInput = tokenizer.nextToken();

        if ((!requestTokenInput.startsWith("requestToken=")) || (!surlInput.startsWith("surl="))) {
            
            log.trace("putTaskStatus() - input error");
            
            validationResponse = Response.status(400).build();
            return false;
            
        }

        requestToken = requestTokenInput.substring(requestTokenInput.indexOf('=') + 1);
        String surlString = surlInput.substring(surlInput.indexOf('=') + 1);

        if ((requestToken.length() == 0) || (surlString.length() == 0)) {
            
            log.trace("putTaskStatus() - input error");
            
            validationResponse = Response.status(400).build();
            return false;
            
        }
        
        if (!validateSurl(surlString)) {
            return false;
        }

        return true;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public StoRI getStoRI() {
        return stori;
    }

    public Response getResponse() {
        return validationResponse;
    }
    
    private boolean validateSurl(String surlString) {
        
        TSURL surl;
        
        try {
            
            surl = TSURL.makeFromStringValidate(surlString);
            
        } catch (InvalidTSURLAttributesException e) {
            validationResponse = Response.status(400).build();
            return false;
        }
        stori = NamespaceDirector.getNamespace().resolveStoRIbySURL(surl);
        return true;
    }
}
