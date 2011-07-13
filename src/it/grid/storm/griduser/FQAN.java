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

package it.grid.storm.griduser;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Represents a single FQAN.  Provides methods to access the
 * individual parts of the FQAN, as well as the standard string
 * representation of an FQAN.
 *
 */
public class FQAN implements SubjectAttribute {

  static private Pattern fqanPattern = Pattern.compile("/[\\w-\\.]+(/[\\w-\\.]+)*(/Role=[\\w-\\.]+)?(/Capability=[\\w-\\.]+)?");
  private static final char VO_FQAN_ESCAPE_CHAR = '/';
  private String fqan;
  private String vo;
  private String group;
  private String role;
  private String capability;
  private boolean checkFormedness = true;


    // --- constructors --- //

    /**
     * Constructor, taking a single FQAN passed as string; assumes VO
     * name is first part of group name.
     */
    public FQAN(String fqan) {
      this(fqan, true);
    }

    public FQAN(String fqan, boolean checkFormedness)
    {
        this.checkFormedness = checkFormedness;
        setFqan(fqan);
        if (parseFqan(fqan))
        {
            generateFqan();
        }
        else
        {
            throw new IllegalArgumentException("The VO for a FQAN can't be null! FQAN string = " + fqan);
        }
    }

    public FQAN(String vo, String group, String role, String capability)
    {
        if (vo == null)
        {
            throw new IllegalArgumentException("The VO for a FQAN can't be null");
        }
        setVo(vo);
        setGroup(group);
        setRole(role);
        setCapability(capability);
        generateFqan();
    }
    
    /**
     * Produce an FQAN object for the provided VO name
     * 
     * @param voName
     * @return
     */
    public static FQAN makeVoFQAN(String voName)
    {
        return new FQAN(VO_FQAN_ESCAPE_CHAR + voName);
    }


    // --- public accessor methods --- //
    public String getVo() {
      return vo;
    }

    public String getGroup()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("/");
        sb.append(vo);
        if (group != null)
        {
            sb.append(group);
        }
        return sb.toString();
    }

    public String getSubGroup() {
      return group;
    }

    public String getRole() {
      return role;
    }

    public boolean isRoleNULL()
    {
        if ((role == null) || role.toUpperCase().equals("NULL"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isCapabilityNULL()
    {
        if ((capability == null) || capability.toUpperCase().equals("NULL"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    public String getCapability() {
      return capability;
    }

    private void generateFqan()
    {
        if (vo == null)
        {
            fqan = null;
            return;
        }
        StringBuffer bf = new StringBuffer();
        bf.append('/');
        bf.append(vo);
        if (group != null)
        {
            bf.append(group);
        }
        if (role != null)
        {
            bf.append("/Role=");
            bf.append(role);
        }
        if (capability != null)
        {
            bf.append("/Capability=");
            bf.append(capability);
        }
        fqan = bf.toString();
    }


    private boolean parseFqan(String fqan)
    {
        // Matches to the specification.
        Matcher m = fqanPattern.matcher(fqan);
        if (!m.matches())
        {
            if (checkFormedness)
            {
                throw new IllegalArgumentException("FQAN '" + fqan
                        + "' is malformed (syntax: /VO[/group[/subgroup(s)]][/Role=role][/Capability=cap])");
            }
            else
            {
                return false;
            }
        }

        vo = null;
        group = null;
        role = null;
        capability = null;

        StringTokenizer stk = new StringTokenizer(fqan, "/");
        if (!stk.hasMoreTokens())
        {
            return false;
        }
        vo = stk.nextToken();
        if (!stk.hasMoreTokens())
        {
            return true;
        }
        String tempGroup = "";
        String token = stk.nextToken();
        while ((!token.startsWith("Role=") && !token.startsWith("Capability=")))
        {
            tempGroup = tempGroup + "/" + token;
            group = tempGroup;
            if (!stk.hasMoreTokens())
            {
                return true;
            }
            token = stk.nextToken();
        }
        if (token.startsWith("Role="))
        {
            setRole(token.substring(5));
            if (!stk.hasMoreTokens())
            {
                return true;
            }
            token = stk.nextToken();
        }
        if (token.startsWith("Capability="))
        {
            setCapability(token.substring(11));
        }
        return true;
    }

    private void setCapability(String capability)
    {
        if ((capability != null) && (!capability.matches("[\\w-\\.]+")))
        {
            throw new IllegalArgumentException("The capability '" + capability + "' is malformed");
        }
        this.capability = capability;
    }


    private void setFqan(String fqan)
    {
        this.fqan = fqan;
    }


    private void setGroup(String group)
    {
        if ((group != null) && (!group.matches("(/[\\w-\\.]+)+")))
        {
            throw new IllegalArgumentException("The group '" + group + "' is malformed");
        }
        this.group = group;
    }

    private void setRole(String role)
    {
        if ((role != null) && (!role.matches("[\\w-\\.]+")))
        {
            throw new IllegalArgumentException("The role '" + role + "' is malformed");
        }
        this.role = role;
        if ("NULL".equalsIgnoreCase(role))
        {
            this.role = null;
        }
    }

    private void setVo(String vo) {
      if ( (vo != null) && (!vo.matches("[\\w-\\.]+"))) {
        throw new IllegalArgumentException("The vo '" + vo + "' is malformed");
      }
      this.vo = vo;
    }

    /**
     *
     * @return int
     */
    public int hashCode()
    {
        if (fqan == null)
        {
            return 0;
        }
        return fqan.hashCode();
    }


    /**
     *
     * @param obj Object
     * @return boolean
     */
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;            
        }
        FQAN fqan2 = (FQAN) obj;
        return (fqan2.fqan == null) ? fqan == null : fqan2.fqan.equalsIgnoreCase(fqan);
    }


    /**
     * Return the usual string representation of the FQAN.
     */
    public String toString() {
      StringBuffer sb = new StringBuffer();
      sb = sb.append(getGroup());
      sb.append("/Role=" + ((role != null) ? getRole() : "NULL"));
      if (capability != null) {
        sb.append("/Capability=" + getCapability());
      }
      return sb.toString();
    }
}
