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

package it.grid.storm.authz.sa.model;

public class AceType {

    public final static AceType ALLOW = new AceType("ALLOW");
    public final static AceType DENY = new AceType("DENY");
    public final static AceType UNKNOWN = new AceType("UNKNOWN");

    private String aceType;

    private AceType(String aceType) {
        this.aceType = aceType;
    }

    public static AceType getAceType(String aceTp) {
        if (aceTp.toUpperCase().equals(ALLOW.toString())) return AceType.ALLOW;
        if (aceTp.toUpperCase().equals(DENY.toString())) return AceType.DENY;
        return AceType.UNKNOWN;
    }

    public String toString() {
        return this.aceType;
    }

}