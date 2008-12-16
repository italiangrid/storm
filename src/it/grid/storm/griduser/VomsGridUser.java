package it.grid.storm.griduser;


import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
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

    private static final Logger log = Logger.getLogger(VomsGridUser.class);

    private MapperInterface mapper = null;
    private List<FQAN> fqans = new ArrayList<FQAN> ();

    // --- protected members --- //
    protected String _pemEncodedCertificateChain;


    /** To map Grid credentials to local UNIX account credentials. */
    protected MapperInterface _mapper;


    // --- public accessor methods --- //

    VomsGridUser(Class mapperClass) {
        super(mapperClass);
    }


    VomsGridUser(Class mapper, String distinguishedName) {
        super(mapper);
        this.setDistinguishedName(distinguishedName);
    }


    VomsGridUser(Class mapper, String distinguishedName, String proxy) {
        super(mapper);
        this.setDistinguishedName(distinguishedName);
        this.setProxyString(proxy);
    }


    VomsGridUser(Class mapper, String distinguishedName, String proxy, FQAN[] fqansArray) {
        super(mapper);
        this.setDistinguishedName(distinguishedName);
        this.setProxyString(proxy);
        this.fqans = Arrays.asList(fqansArray);
    }


    // --- SETTER Methods --- //

    void setFqans(List<FQAN> fqans) {
        this.fqans = new ArrayList<FQAN> (fqans);
    }

    public void addFqan(FQAN fqan) {
        this.fqans.add(fqan);
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
     * Returns the local POSIX user this Grid user is mapped onto.
     *
     * @return  LocalUser object holding the local credentials
     *          this grid user is mapped onto.
     */
    public LocalUser getLocalUser() throws CannotMapUserException
    {
        if (null==localUser) {

            log.debug("VomsGridUser.getLocalUser");

            // call LCMAPS and do the mapping
            String[] fqanStrings = getFQANsString();


            try {
	        localUser = _mapper.map(getDn(), fqanStrings);
           }
            catch (CannotMapUserException x) {
                // log the operation that failed
                log.error("Error in mapping '"+getDn()+"' to a local user: "+x.getMessage());
                // re-thorw same exception
                throw x;
            }
            catch (Exception e) {
                //catch any exception that the C code may throw
                throw new CannotMapUserException("Got problem from native C code! " + e);
            }
            catch (Error err) {
                //THIS IS A TEMPORARY SOLUTION TO CATCH A SIGFAULT IN NATIVE C CODE!
                throw new CannotMapUserException("FATAL ERROR! GOT A Java ERROR WHEN CALLING NATIVE c CODE! "+err);
            }
        }
        if (localUser==null)
            throw new CannotMapUserException("Null local user!");
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
