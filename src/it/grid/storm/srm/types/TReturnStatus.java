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
 * This class represents the TReturnStatus value in SRM request.
 * It is composed by a TStatusCode and an explanetion String
 *
 * @author  Magnoni Luca
 * @author  CNAF - INFN  Bologna
 * @date    Avril, 2005
 * @version 1.0
 */
package it.grid.storm.srm.types;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TReturnStatus implements Serializable
{
    private static final long serialVersionUID = -4550845540710062810L;
    
    private TStatusCode  statusCode   = null;
    private String       explanation = null;
    private Long lastUpdateTIme = null;
    
    private static final String UNDEFINED_EXPLANATION = "undefined";
    
    public static final String PNAME_RETURNSTATUS = "returnStatus";
    public static final String PNAME_STATUS       = "status";

    /**
     * Default constructor that makes a TReturnStatus with SRM_CUSTOM_STATUS, and
     * explanation String "undefined".
     * @throws InvalidTReturnStatusAttributeException 
     */
    public TReturnStatus() throws InvalidTReturnStatusAttributeException {
        this(TStatusCode.SRM_CUSTOM_STATUS);
    }
    
    public TReturnStatus(TReturnStatus original) throws InvalidTReturnStatusAttributeException  {
        this(original.statusCode, original.explanation);
    }

    public TReturnStatus(TStatusCode statusCode) throws InvalidTReturnStatusAttributeException
    {
        this(statusCode, UNDEFINED_EXPLANATION);
    }
    /**
     * Can be Explanation String a null value?
     */
    public TReturnStatus(TStatusCode statusCode, String explanation)
        throws InvalidTReturnStatusAttributeException
    {
        if (statusCode == null)
        {
            throw new InvalidTReturnStatusAttributeException(statusCode);
        }
        this.statusCode = statusCode;
        this.explanation = explanation;
        updated();
    }

    
    public TReturnStatus clone()
    {
        try
        {
            return new TReturnStatus(this);
        } catch(InvalidTReturnStatusAttributeException e)
        {
           //never thrown
            throw new IllegalStateException("unexpected InvalidTReturnStatusAttributeException "
                    + "in TReturnStatus: " + e.getMessage());
        }
    }

    public static TReturnStatus getInitialValue() {
        TReturnStatus result = null;
        try {
            result = new TReturnStatus(TStatusCode.SRM_CUSTOM_STATUS, "Initial status..");
        } catch (InvalidTReturnStatusAttributeException e) {
           //Never Happen!!
        }
        return result;
    }

    /**
     * Returns the status code
     * @return TStatusCode
     */
    public TStatusCode getStatusCode()
    {
        return statusCode;
    }

    /**
     * @param statusCode the statusCode to set
     */
    protected void setStatusCode(TStatusCode statusCode)
    {
        if (statusCode == null)
        {
            throw new IllegalArgumentException("Cannot set the status code, received null argument: statusCode=" + statusCode);
        }
        this.statusCode = statusCode;
        updated();
    }


    /**
     * Set explanation string
     * @param expl String
     */
    protected void setExplanation(String explanationString)
    {
        explanation = (explanationString == null ? "" : explanationString);
        updated();
    }

    /**
     * Returns the explanation string
     * @return String
     */
    public String getExplanation()
    {
        return explanation;
    }
    
    /**
     * @return the lastUpdateTIme
     */
    public Long getLastUpdateTIme()
    {
        return lastUpdateTIme;
    }

    private void updated()
    {
        this.lastUpdateTIme = Calendar.getInstance().getTimeInMillis(); 
    }

    /**
     * This metod encode a TReturnStatus Object into an Hashtable used for xmlrpc comunication.
     */
    public void encode(Map outputParam, String name)
    {
        //      Return STATUS OF REQUEST
        HashMap<String,String> globalStatus = new HashMap<String,String>();
//        String globalSCode = this.getStatusCode().getValue();
//        String member_globalSCode = new String("statusCode");
//        globalStatus.put(member_globalSCode, globalSCode);
//        String g_explanation = this.getExplanation();
//        String member_g_expl = new String("explanation");
//        globalStatus.put(member_g_expl, g_explanation);
        globalStatus.put("statusCode", this.getStatusCode().getValue());
        globalStatus.put("explanation", this.getExplanation());

        //Insert TReturnStatus struct into global Output structure
        outputParam.put(name, globalStatus);

    }

    public String toString()
    {
        return statusCode + ": " + explanation;
    }

	public boolean isSRM_SUCCESS() {
		if (statusCode.equals(TStatusCode.SRM_SUCCESS))
			return true;
		else
			return false;
	}

    //WARNING!!!
    //No equals(Object) and hashCode(): this may be dangerous
    //if these Objects get used in containers!

    /*   public static void main(String[] args) {
     //Testing object creation
     System.out.println("Testing default constructor...");
     TReturnStatus s1 = new TReturnStatus();
     System.out.println("Should see SRM_CUSTOM_STATUS: undefined");
     System.out.println(s1);
     System.out.println("Code: "+s1.getStatusCode()+"; explanation: "+s1.getExplanation());
     //
     //Testing constructor
     System.out.println("\n\nTesting constructor...");
     try {
     String str = "Cannot proceed because authentication failed.";
     TStatusCode stc = TStatusCode.SRM_AUTHENTICATION_FAILURE;
     TReturnStatus s2 = new TReturnStatus(stc,str);
     System.out.println("Should see "+stc+": "+str);
     System.out.println(s2);
     System.out.println("Code: "+s2.getStatusCode()+"; explanation: "+s2.getExplanation());
     } catch (Exception e) {
     System.out.println("Should not see this!");
     }
     try {
     String str = null;
     TStatusCode stc = TStatusCode.SRM_AUTHENTICATION_FAILURE;
     TReturnStatus s2 = new TReturnStatus(stc,str);
     System.out.println("\nShould see "+stc+": "+str);
     System.out.println(s2);
     System.out.println("Code: "+s2.getStatusCode()+"; explanation: "+s2.getExplanation());
     } catch (Exception e) {
     System.out.println("Should not see this!");
     }
     //
     //Testing Exception throwing
     System.out.println("\n\nTesting constructor Exception...");
     try {
     System.out.print("Trying with null TStatusCode: ");
     String str = "Cannot proceed because authentication failed.";
     TStatusCode stc = null;
     new TReturnStatus(stc,str);
     System.out.println("Should not see this!");
     } catch (InvalidTReturnStatusAttributeException e) {
     System.out.println(" OK creation failed as expeceted. "+e);
     }
     //
     //Testing setting of TStatusCode
     System.out.println("\n\nTesting settig of TStausCode...");
     TReturnStatus ss = new TReturnStatus();
     TStatusCode ns = TStatusCode.SRM_ABORTED;
     System.out.println("TReturnStatus: "+ss+"; now will set status to "+ns);
     try {
     ss.setStatus(ns);
     System.out.println("New TReturnStatus: "+ss);
     } catch (Exception e) {
     System.out.println("Should not see this!");
     }
     //
     //Testing setting of TStatusCode throwing an Exception
     System.out.println("\n\nTesting setStatus throwing an Exception...");
     TReturnStatus sss = new TReturnStatus();
     System.out.println("TReturnStatus: "+sss+"; now will set status to "+null);
     try {
     sss.setStatus(null);
     System.out.println("Should not see this!");
     } catch (InvalidTReturnStatusAttributeException e) {
     System.out.println("OK: setting failed as expected! "+e);
     System.out.println("TReturnStatus remained the same: "+sss);
     }
     }*/
}
