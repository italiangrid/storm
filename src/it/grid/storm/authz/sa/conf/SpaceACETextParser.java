package it.grid.storm.authz.sa.conf;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.sa.model.AceType;
import it.grid.storm.authz.sa.model.SpaceACE;
import it.grid.storm.authz.sa.model.SpaceAccessMask;
import it.grid.storm.authz.sa.model.SpaceOperation;
import it.grid.storm.authz.sa.model.SubjectPattern;
import it.grid.storm.authz.sa.model.SubjectType;

import org.slf4j.Logger;

public class SpaceACETextParser {

    private static final Logger LOG = AuthzDirector.getLogger();

    private SpaceACETextParser() {
    }

    public static SpaceACE parse(String aceString) throws AuthzDBReaderException {
        return parseACE(aceString);
    }


    /**

    String invalidReason = "none";
    String[] fields = new String[4];
    //
    //  Retrieve fields from the line;
    //
    //== ACE # ==
    int i0 = 0;
    int i1 = line.indexOf('=');
    if (i1>0) {
      fields[0] = line.substring(i0, i1);
    } else {
        valid = false;
        invalidReason = "Unable to found 'ace.xx='";
    }
    // == DNPattern|FQANPattern # ==
    i0 = line.indexOf("dn:");
    i1 = line.indexOf("fqan:");
    if ((i0 < 0) && (i1 < 0)) {
        valid = false;
        invalidReason = "Unable to found 'dn:' or 'fqan:'";
    } else {
        if (i0 > 0) { // found DN
            if (i1 > 0) { // found also fqan! ERROR
                valid = false;
                invalidReason = "Found both 'dn:' and 'fqan:'. Only one is allowed.";
            } else {  // only DN
                subjectType = SubjectType.DN;
                fields[1] =
            }
        } else { // found FQAN

        }
    }


    if (validate) {
        valid = validate(line);
    }
    return result;

     **/
    /**
     * ace.1=dn:/DC=ch/DC=cern/OU=Organic
     * Units/OU=Users/CN=elanciot/CN=576215/CN=Elisa Lanciotti:DURWSCP:ALLOW
     * ace.3=fqan:EVERYONE:RQ:ALLOW - field[0] = ace number - field[0] = subject
     * string ==> subject-type + subject - field[1] = space access mask -
     * field[2] = ace type
     */
    private static SpaceACE parseACE(String aceString) throws AuthzDBReaderException {

        int aceNumber = parseAceNumber(aceString);

        //Remove ace number from ACE String
        aceString = aceString.substring(aceString.indexOf('='));

        String patternStr = ":";
        String[] fields = aceString.split(patternStr);
        if (fields.length!=4) {
            throw new AuthzDBReaderException("ACEString :"+aceString+" is not well formed");
        }
        for (int i = 0; i < fields.length; i++) {
            LOG.debug("Field["+i+"]='"+fields[i]+"'");
        }
        // FIELD 0 = SubjectType + SubjectPattern
        SubjectType subjectType = parseSubjectType(fields[0].substring(1));
        SubjectPattern subjectPattern = parseSubjectPattern(fields[0]);
        
        // FIELD 1 = Space Access Mask
        SpaceAccessMask spaceAccessMask = parseSpaceAccessMask(fields[1]);

        // FIELD 2 = ACE Type
        AceType aceType = parseACEType(fields[2]);

        //Build the result
        SpaceACE spaceACE = new SpaceACE();
        spaceACE.setAceNumber(aceNumber);
        spaceACE.setSpaceAccessMask(spaceAccessMask);
        spaceACE.setSubjectType(subjectType);
        spaceACE.setSubjectPattern(subjectPattern);

        spaceACE.setAceType(aceType);
        return spaceACE;
    }

    /**
     * Parsing the number of ACE string (ace.<b>NR</b>).
     * 
     * @param aceNumber
     * @return
     */
    private static int parseAceNumber(String aceString) throws AuthzDBReaderException {
        int aceNumber = -1;
        int index = aceString.indexOf('=');
        String prefix = aceString.substring(0, 4);
        if (!(prefix.equals(SpaceACE.ACE_PREFIX))) {
            throw new AuthzDBReaderException("Prefix of ACE '"+aceString+"' is not compliant with 'ace.'");
        }
        String numb = aceString.substring(4, index);
        try {
            aceNumber = Integer.parseInt(numb);
        } catch (Exception e) {
            throw new AuthzDBReaderException("Number Format error in '"+aceString+"'");
        }
        LOG.debug("number = "+aceNumber);
        return aceNumber;
    }


    /**
     * @param string
     * @return
     */
    private static SubjectType parseSubjectType(String subjectTp) throws AuthzDBReaderException {
        SubjectType st = SubjectType.getSubjectType(subjectTp);
        if (st.equals(SubjectType.UNKNOWN)) {
            throw new AuthzDBReaderException("Unknown Subject Type in '"+subjectTp+"'");
        }
        return st;
    }


    /**
     * @param string
     * @return
     */
    private static SubjectPattern parseSubjectPattern(String subjectPattern) throws AuthzDBReaderException {
        //SubjectPattern sp = new SubjectPattern();
        return null;
    }


    /**
     * @param string
     * @return
     */
    private static SpaceAccessMask parseSpaceAccessMask(String accessMaskStr) throws AuthzDBReaderException {
        SpaceAccessMask spAM = new SpaceAccessMask();
        if (accessMaskStr != null) {
            char[] spAMarray = accessMaskStr.toCharArray();
            for (char element : spAMarray) {
                spAM.addSpaceOperation(SpaceOperation.getSpaceOperation(element));
            }
        }
        return spAM;
    }


    /**
     * @param string
     * @return
     */
    private static AceType parseACEType(String aceTypeStr) throws AuthzDBReaderException {
        return AceType.getAceType(aceTypeStr);
    }







}
