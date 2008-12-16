package it.grid.storm.griduser;


import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import it.grid.storm.common.types.VO;

/**
 * Encapsulates user Grid credentials access, and maps those to a local
 * user account.  Has methods to extract the permanent identifier
 * (subject DN), VO and VOMS group/role membership from the X.509
 * certificate / GSI proxy that the user presented to StoRM.  Will
 * also invoke LCMAPS library to map the Grid credentials to a local
 * user account.
 *
 * @todo implement a flyweight pattern, so that we don't have 1'000
 * different GridUser objects for 1'000 requests from the same user...
 *
 *
 */
public class VomsGridUser extends AbstractGridUser implements GridUserInterface {


    private List<FQAN> fqans = new ArrayList<FQAN> ();
    private List<String> fqansString = new ArrayList<String>();
    private String[] fqanStrings = null;



    // --- public accessor methods --- //

    VomsGridUser(MapperInterface mapperClass) {
        super(mapperClass);
    }


    VomsGridUser(MapperInterface mapper, String distinguishedName) {
        super(mapper);
        this.setDistinguishedName(distinguishedName);
    }


    VomsGridUser(MapperInterface mapper, String distinguishedName, String proxy) {
        super(mapper);
        this.setDistinguishedName(distinguishedName);
        this.setProxyString(proxy);
    }


    VomsGridUser(MapperInterface mapper, String distinguishedName, String proxy, FQAN[] fqansArray) {
        super(mapper);
        this.setDistinguishedName(distinguishedName);
        this.setProxyString(proxy);
        this.fqans = Arrays.asList(fqansArray);
    }


    // --- SETTER Methods --- //

    void setFqans(List<FQAN> fqans) {
        this.fqans = new ArrayList<FQAN> (fqans);
        for (FQAN fqan: fqans) {
            this.fqansString.add(fqan.toString());
        }
        this.fqanStrings = fqansString.toArray(new String[0]);
    }

    public void addFqan(FQAN fqan) {
        this.fqans.add(fqan);
        this.fqansString.add(fqan.toString());
    }

    public List<FQAN> getFQANsList() {
        return this.fqans;
    }

    // --- GETTER Methods --- //

    public String[] getFQANsString() {
        String[] fqansString = new String[fqans.size()];
        for (int count = 0; count<fqans.size(); count++ ) {
            FQAN fqan = fqans.get(count);
            fqansString[count] = fqan.toString();
        }
        return fqansString;
    }


    /**
     * Return <code>true</code> if any VOMS attributes are stored in
     * this object.
     *
     * <p> If the explicit constructor {@link VomsGridUser(String,
     * Fqan[], String)} was used, then this flag will be true if the
     * <code>Fqan[]</code> parameter was not null in the constructor
     * invocation.
     *
     * @return <code>true</code> if any VOMS attributes are stored in
     *         this object.
     */
    public boolean hasVoms()
    {
        if ((this.fqans!=null)&&(this.fqans.size()>0)) return true;
        else return false;
    }




    /**
     * Return the local user on wich the GridUser is mapped.
     *
     * @throws CannotMapUserException
     * @return LocalUser
     */
    public LocalUser getLocalUser() throws CannotMapUserException {
        if (null == localUser) {

            log.debug("VomsGridUser.getLocalUser");

            // call LCMAPS and do the mapping
            if ((fqanStrings==null)||fqanStrings.length==0)
                this.fqanStrings = getFQANsString();

            try {
                localUser = userMapperClass.map(getDn(), fqanStrings);
            }
            catch (CannotMapUserException ex) {
                // log the operation that failed
                log.error("Error in mapping '" + this +"' to a local user: " + ex.getMessage());
                // re-thorw same exception
                throw ex;
            }
        }
        return localUser;
    }




    /**
     * Print a string representation of this object, in the form
     * <code>GridUser:"</code><i>subject DN</i><code>"</code>.
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Grid User (VOMS) = ");
        sb.append(" DN:'"+getDistinguishedName().getX500DN_rfc1779()+"'");
        sb.append(" FQANS:"+fqans);
        return sb.toString();
    }




    public VO getVO() {
        VO result = VO.makeNoVo();
        if ( (fqans != null) && (fqans.size() > 0)) {
            FQAN firstFQAN = fqans.get(0);
            String voName = firstFQAN.getVo();
            result = VO.make(voName);
        }
        return result;
    }

}
