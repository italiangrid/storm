package it.grid.storm.griduser;

import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import it.grid.storm.common.types.VO;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;

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
 * @author  Riccardo Murri <riccardo.murri@ictp.it>, EGRID Project, ICTP
 * @version $Revision: 1.32 $
 *
 */
public class VomsGridUser extends AbstractGridUser implements GridUserInterface {

    private MapperInterface mapper = null;
    private String[] fqanStrings = null;
    private List<FQAN> fqans = new ArrayList<FQAN> ();



    private static final Logger log = Logger.getLogger(VomsGridUser.class);

    // --- protected members --- //

    protected static final Pattern DN_RE = Pattern.compile("^/.+/CN=", Pattern.CASE_INSENSITIVE);
    // see http://grid.racf.bnl.gov/GUMS/components/business/apidocs/gov/bnl/gums/FQAN.html
    protected static final Pattern FQAN_RE = Pattern.compile("^/([a-zA-Z][a-zA-Z0-9-]*[a-zA-Z0-9]\\.)*[a-zA-Z][a-zA-Z0-9-]*[a-zA-Z0-9](/[\\w-]+)*(/Role=[\\w-]+)?(/Capability=[\\w-]+)?$",
            Pattern.CASE_INSENSITIVE);

    protected String _pemEncodedCertificateChain;

    protected String _certificateSubjectDn;
    protected boolean _hasVoms;
    protected FQAN[] _fqans;

    protected boolean _hasBeenMapped;
    protected LocalUser _localUser;

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
        //this.setFqanStrings(fqans);
        this.fqans = Arrays.asList(fqansArray);
        //this.fqanStrings = (String[]) this.fqans.toArray();
    }

/**
    void setFqanStrings(String[] fqans) {
        this.fqanStrings = fqans;
    }
**/

    void setFqans(List<FQAN> fqans) {
        this.fqans = new ArrayList<FQAN>(fqans);
    }

   public void addFqan(FQAN fqan) {
       this.fqans.add(fqan);
   }

/**
    public List<FQAN> getFqansList() {
        if (fqans.size()==0) {
            fqans = new ArrayList<FQAN>(populateFqanList());
        }
        return fqans;
    }

    private List<FQAN> populateFqanList() {
        ArrayList<FQAN> result = new ArrayList<FQAN>();
        if (fqanStrings!=null) {
            for (int i=0; i<fqanStrings.length; i++) {
                result.add(new FQAN(fqanStrings[i]));
            }
        }
        return result;
    }

**/

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
        return _hasVoms;
    }




    /**
     * Return the DN that is stored in this object (see {@link
     * #getPermanentIdentifier()}.  <em>Beware,</em> it may be a proxy
     * DN, containing appended 'CN=...'  fields, so always use {@link
     * matchesDn} for comparison purposes.
     *
     * @return  The DN of the certificate that is stored in this object.
     *
     * @see #getPermanentIdentifier()
     *
     */
    public String getDn()
    {
        return _certificateSubjectDn;
    }


    /**
     * Returns an array holding all FQANs stored in the object.
     *
     * @return  Array holding all FQANs stored in the object,
     *          empty array if no VOMS extensions are stored in this object.
     */
    public FQAN[] getFqans()
    {
        return _fqans;
    }


    /**
     * Get the "main" VO name.  By convention, the main VO is the
     * first in the list of VOMS attributes.
     *
     * @return      The name of the first VO in the FQAN list, or
     *              <code>null</code> if no VOMS attributes are present.
     * @deprecated  It is not yet clear whether this convention (that the first
     *              VO in the list of VOMS attributes is the "main" one) is
     *              in effect or if its use would break some software.  Until
     *              status is ascertained, please refrain from using it.
     */
    public VO getMainVo()
    {
        //assert (_hasVoms&& (null==_fqans)):
        //        "VomsGridUser asserts VOMS extensions, but the FQANs attributes array is null."; assert (_hasVoms&&
        //        (_fqans.length==0)):"VomsGridUser asserts VOMS extensions, but the FQANs attributes array is empty.";

        if (hasVoms()) {
            return VO.make(getFqans()[0].getVo());
        }
        else {
            return null;
        }
    }




    /**
     * Returns the local POSIX user this Grid user is mapped onto.
     *
     * @return  LocalUser object holding the local credentials
     *          this grid user is mapped onto.
     */
    public LocalUser getLocalUser() throws CannotMapUserException
    {
        if (null==_localUser) {

            log.debug("VomsGridUser.getLocalUser");

            // call LCMAPS and do the mapping
            String[] fqanStrings = new String[_fqans.length];

	    for (int i = 0; i<_fqans.length; i++) {
                fqanStrings[i] = _fqans[i].toString();
            }
            try {
	        _localUser = _mapper.map(getDn(), fqanStrings);
           }
            catch (CannotMapUserException x) {
                // log the operation that failed
                log.error("Error in mapping '"+_certificateSubjectDn+"' to a local user: "+x.getMessage());
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

            //NDC.pop();
        }
        if (_localUser==null) throw new CannotMapUserException("Null local user!");
        return _localUser;
    }



    /**
     * Print a string representation of this object, in the form
     * <code>GridUser:"</code><i>subject DN</i><code>"</code>.
     */
    public String toString()
    {
        return "VomsGridUser:\""+getDn()+"\"";
    }


    /**
     * Return the certificate chain passed as argument to constructor.
     * See {@link VomsGridUser(String)} for details on this string
     * contents.
     *
     * @return The string passed as <code>pem</code> parameter to the
     *          {@link #VomsGridUser(String, Fqan[], String)} constructor; in
     *          particular, this can be <code>null</code> if factory methods
     *          omitting the PEM-encoded certificate chain were used.
     *
     * @see #VomsGridUser(String)
     * @see #VomsGridUser(String, Fqan[])
     */
    public String getUserCredentials()
    {
        return _pemEncodedCertificateChain;
    }
}
