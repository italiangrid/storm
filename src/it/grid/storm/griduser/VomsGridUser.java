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
  private ArrayList<Fqan> fqans = new ArrayList<Fqan> ();



    private static final Logger log = Logger.getLogger(VomsGridUser.class);

    // --- protected members --- //

    protected static final Pattern DN_RE = Pattern.compile("^/.+/CN=", Pattern.CASE_INSENSITIVE);
    // see http://grid.racf.bnl.gov/GUMS/components/business/apidocs/gov/bnl/gums/FQAN.html
    protected static final Pattern FQAN_RE = Pattern.compile("^/([a-zA-Z][a-zA-Z0-9-]*[a-zA-Z0-9]\\.)*[a-zA-Z][a-zA-Z0-9-]*[a-zA-Z0-9](/[\\w-]+)*(/Role=[\\w-]+)?(/Capability=[\\w-]+)?$",
            Pattern.CASE_INSENSITIVE);

    protected String _pemEncodedCertificateChain;

    protected String _certificateSubjectDn;
    protected boolean _hasVoms;
    protected Fqan[] _fqans;

    protected boolean _hasBeenMapped;
    protected LocalUser _localUser;

    /** To map Grid credentials to local UNIX account credentials. */
    protected MapperInterface _mapper;

    // --- public factory methods --- //

    /**
     * Factory method taking explicit subject DN and the PEM-encoded
     * proxy certificate chain.  See
     * {@link #VomsGridUser(String, Fqan[], String)} for a discussion
     * of the constructor parameters.
     *
     * @param dn     The user identity (subject DN)
     * @param proxy  A string containing the PEM-encoded certificate chain.
     * @return A VomsGridUser object, encapsulating the given credentials
     * @see    #VomsGridUser(String, Fqan[], String)
     */

//    public static VomsGridUser make(final String dn, final String proxy)
//    {
//        return new VomsGridUser(dn, new Fqan[0], proxy, new LcmapsMapper());
//    }


    /**
     * Factory method taking explicit subject DN and FQANs (VOMS
     * attributes), and the PEM-encoded * proxy certificate chain.
     * See {@link #VomsGridUser(String, Fqan[], String)} for a discussion
     * of the constructor parameters.
     *
     * @param dn     The user identity (subject DN)
     * @param fqans  An array of VOMS attributes
     * @param proxy  A string containing the PEM-encoded certificate chain.
     * @return A VomsGridUser object, encapsulating the given credentials
     * @see    #VomsGridUser(String, Fqan[], String)
     */
//    public static VomsGridUser make(final String dn, final Fqan[] fqans, final String proxy)
//    {
//        return new VomsGridUser(dn, fqans, proxy, new LcmapsMapper());
//    }


    /**
     * Factory method taking explicit subject DN and FQANs (VOMS attributes).
     * See {@link #VomsGridUser(String, Fqan[], String)} for a discussion
     * of the constructor parameters.
     *
     * <p> As no proxy certificate is passed, {@link
     * #getUserCredentials()} will return <code>null</code> for
     * objects constructed this way.
     *
     * @param dn     The user identity (subject DN)
     * @param fqans  An array of VOMS attributes
     * @return A VomsGridUser object, encapsulating the given credentials
     * @see    #VomsGridUser(String, Fqan[], String)
     */
//    public static VomsGridUser make(final String dn, final Fqan[] fqans)
//    {
//        return new VomsGridUser(dn, fqans, null, new LcmapsMapper());
//    }


    /**
     * Factory method taking explicit subject DN.
     *
     * See {@link #VomsGridUser(String, Fqan[], String)} for a
     * discussion of the constructor parameters.
     *
     * <p> As no proxy certificate is passed, {@link
     * #getUserCredentials()} will return <code>null</code> for
     * objects constructed this way.
     *
     * @param dn     The user identity (subject DN)
     * @return A VomsGridUser object, encapsulating the given credentials
     * @see    #VomsGridUser(String, Fqan[], String)
     */
//    public static VomsGridUser make(final String dn)
//    {
//	log.debug(" VomsGridUser making with DN = "+dn);
//        return VomsGridUser.make(dn, new Fqan[0], null);
//    }

    /**
     * Factory method taking a XML structure.
     *
     * <p> As no proxy certificate is passed, {@link
     * #getUserCredentials()} will return <code>null</code> for
     * objects constructed this way.
     *
     * @param inputParam The XML structure
     * @return A VomsGridUser object, encapsulating the given credentials
     * @see    #VomsGridUser(String, Fqan[], String)
     */
/**
    public static VomsGridUser decode(Map inputParam)
    {
        // Member name for VomsGridUser Creation
        String member_DN = new String("userDN");
        String member_Fqans = new String("userFQANS");

        // Get DN and FQANs[]
        String DN = (String) inputParam.get(member_DN);

        List fqans_vector = null;
        try {
        	fqans_vector = Arrays.asList((Object[]) inputParam.get(member_Fqans));
        } catch (NullPointerException e ) {
        	//log.debug("Empty FQAN[] found.");
        }

        // Destination Fqans array
        Fqan[] fqans = null;

        if (fqans_vector != null) {
            // Define FQAN[]
            fqans = new Fqan[fqans_vector.size()];
            log.debug("fqans_vector Size: " + fqans_vector.size());

            for (int i=0; i<fqans_vector.size(); i++) {
                String fqan_string = (String) fqans_vector.get(i);
                log.debug("FQAN[" + i + "]:" + fqan_string);
                // Create Fqan
                Fqan fq = new Fqan(fqan_string);
                // Add this into Array of Fqans
                fqans[i] = fq;
            }
        }

        // Setting up VomsGridUser
        if (DN != null) {
            log.debug("DN: " + DN);
            // Creation of srm GridUser type
            if (fqans_vector != null) {
                log.debug("VomsGU with FQAN");
                return VomsGridUser.make(DN, fqans);
            } else {
                return VomsGridUser.make(DN);
            }
        }
        return null;
    }
**/
    // --- constructors --- //

    /**
     * Constructor taking explicit DN and FQANs values.
     *
     * <p> As no proxy certificate is passed, {@link
     * #getUserCredentials()} will return <code>null</code> for
     * objects constructed this way.
     *
     * <p> This constructor will perform the following syntax checks on
     * the passed data: <em>(XXX: currently commented out!)</em>
     * <ul>
     *
     * <li> The <code>dn</code> argument must begin with
     * '<code>/</code>' and contain at least one occurence of the
     * "CN=..." field;
     *
     * <li> Each of the members of the <code>fqan</code> array must
     * match the regular expression
     * <code>/([a-zA-Z][a-zA-Z0-9-]*[a-zA-Z0-9]\\.)*[a-zA-Z][a-zA-Z0-9-]*[a-zA-Z0-9](/[\\w-]+)*(/Role=[\\w-]+)?(/Capability=[\\w-]+)?</code>
     * - see
     * http://grid.racf.bnl.gov/GUMS/components/business/apidocs/gov/bnl/gums/FQAN.html
     * </ul>
     *
     * <p> The passed <code>pem</code> string parameter should have
     * exactly the same contents as the PEM-encoded proxy/certificate;
     * in particular, it may contain more than one certificate: from
     * the first one we take the Subject DN and the time validity
     * interval; the VOMS library will extract attributes from
     * anywhere in the cahin and verify that the chain is valid.
     *
     * <p> Indeed, VOMS use cases actually need the whole certificate
     * chain, e.g., the proxy that gets propagated to a WN has no VOMS
     * extensions in it; the VOMS ACs are higher up in the chain, in
     * the proxy the user made with <code>voms-proxy-init</code>.
     *
     * @param dn     The user identity (subject DN)
     * @param fqans  An array of VOMS attributes
     * @param pem    A string containing the PEM-encoded proxy certificate
     * @param mapper A {@link it.grid.storm.griduser.MapperInterface}
     * implementation, that will be used to map Grid credentials onto
     * local account credentials.
     *
     */
/**
    protected VomsGridUser(final String dn, final Fqan[] fqans, final String pem, final MapperInterface mapper)
    // throws InvalidSubjectDnSyntax, InvalidFqanSyntax
    {
        assert (null!=dn); assert (null!=fqans); assert (null!=mapper);

        for (int i = 0; i < fqans.length; i++)
        	log.debug("VomsGridSUser with FQANs:"+fqans[i]);

        NDC.push("VomsGridUser constructor");

        String trimmedDn = dn.trim();
        _certificateSubjectDn = trimmedDn;

        _fqans = fqans;
        _hasVoms = false;
        if (0!=_fqans.length) {
            _hasVoms = true;
        }

        _hasBeenMapped = false;
        _localUser = null;

        _pemEncodedCertificateChain = pem;

        _mapper = mapper;

        NDC.pop();
    }
**/
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

    VomsGridUser(Class mapper, String distinguishedName, String proxy, String[] fqans) {
        super(mapper);
        this.setDistinguishedName(distinguishedName);
        this.setProxyString(proxy);
        this.setFqanStrings(fqans);
    }

    void setFqanStrings(String[] fqans) {
        this.fqanStrings = fqans;
    }


    void setFqans(List<Fqan> fqans) {
        this.fqans = new ArrayList<Fqan>(fqans);
    }

   public void addFqan(Fqan fqan) {
       this.fqans.add(fqan);
   }

    public List<Fqan> getFqansList() {
        if (fqans.size()==0) {
            fqans = new ArrayList<Fqan>(populateFqanList());
        }
        return fqans;
    }

    private List<Fqan> populateFqanList() {
        ArrayList<Fqan> result = new ArrayList<Fqan>();
        if (fqanStrings!=null) {
            for (int i=0; i<fqanStrings.length; i++) {
                result.add(new Fqan(fqanStrings[i]));
            }
        }
        return result;
    }


    private String[] getFqanStrings() {

        // For a set or list
        int count = 0;
        String[] results = new String[fqans.size()];
        for (Iterator it = fqans.iterator(); it.hasNext(); ) {
            results[count] = (String) it.next();
            count++;
        }
        return results;
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
        return _hasVoms;
    }


    /**
     * Return <code>true</code> if <code>dn</code> is the subject DN
     * of the user certificate that issued the proxy certificate that
     * was used to build this object.
     *
     * <p> A heuristics is used to determine the Holder DN from the
     * stored subject DN; see the comments in {@link
     * #getPermanentIdentifier()}.
     *
     * @return <code>true</code> if <code>dn</code> is the subject DN
     *         of the user certificate that issued the proxy certificate
     *         that was used to build this object.
     *
     * @see #getPermanentIdentifier()
     *
     */
    public boolean matchesDn(final String dn)
    {
        return (new ExactDnMatch()).match(getPermanentIdentifier(), dn);
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
    public Fqan[] getFqans()
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
     * Return the (maybe guessed) Holder DN, that is, the certificate
     * subject DN that uniquely identifies the user person on the
     * Grid.
     *
     * <p> Since we have no direct access to the (proxy) certificate,
     * then we adopt the heuristics of truncating the stored subject
     * DN after the first 'CN=...' field.  One example that will
     * certainly break this is certificates that have "/Email=..." or
     * "/serialNumber=..."  after the "/CN=..." field (see, for
     * instance, ...).  But the whole idea of passing just a string
     * instead of the true DN, which is a sequence of ASN.1 objects,
     * is flawed...
     *
     * @todo FIXME: until I don't know if this heuristics is good, I'm
     * keeping this method private.  One example that will certainly
     * break this is certificates that have "/Email=..." or
     * "/serialNumber=..." after the "/CN=..." field (see, for
     * instance, ...).  But the whole idea of passing just a string
     * instead of the true DN, which is a sequence of ASN.1 objects,
     * is flawed...
     */
    private String getPermanentIdentifier()
    {
        String[] parts = getDn().split("/CN=", 3);
        //return parts[0]+"/CN="+parts[1];

        //This heuristics does not works for DN with multiple CN field!
        //As the one from new CERN CA...
        //It's better to return the entire DN
        return getDn();

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
            //NDC.push("VomsGridUser.getLocalUser");
            log.debug("VomsGridUser.getLocalUser");

            // call LCMAPS and do the mapping
            String[] fqanStrings = new String[_fqans.length];

	    for (int i = 0; i<_fqans.length; i++) {
                fqanStrings[i] = _fqans[i].toString();
            }
            try {
	        _localUser = _mapper.map(getPermanentIdentifier(), fqanStrings);
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
