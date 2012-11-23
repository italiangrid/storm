package unitTests;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.FQAN;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.surl.ExpiredTokenException;
import it.grid.storm.synchcall.surl.SurlStatusStore;
import it.grid.storm.synchcall.surl.TokenDuplicationException;
import it.grid.storm.synchcall.surl.UnknownSurlException;
import it.grid.storm.synchcall.surl.UnknownTokenException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class SurlStatusStoreTestNew
{
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public static final char[] LETTERS_ALPHABET = {
        'A','B','C','D','E','F','G','H',
        'I','J','K','L','M','N','O','P',
        'Q','R','S','T','U','V','W','X',
        'Y','Z','a','b','c','d','e','f',
        'g','h','i','j','k','l','m','n',
        'o','p','q','r','s','t','u','v',
        'w','x','y','z',
    };
    
    
    private static final String FE_HOST = "omii005-vm03.cnaf.infn.it";
    
    private static final String EXPIRED_TOKEN_PREFIX = "Expired-";
    
    private static final Random randomGenerator = new Random();

    private static final GridUserInterface gridUser = GridUserManager.makeGridUser(buildRandomDn());
    
    private static final GridUserInterface vomsGridUser = GridUserManager.makeVOMSGridUser(buildRandomDn(), buildRandomFQANs());
    
    private static final Logger logger;

    private static final int MAX_STRING_SIZE = 40; 
    static{
        logger = ((Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME));
    }
    
    
    @Before
    public void setUp() throws Exception
    {
        logger.setLevel(Level.TRACE);
        try
        {
            SurlStatusStore.getInstance().checkIntegrity();
        } catch(Exception e)
        {
            try
            {
                SurlStatusStore.getInstance().checkIntegrity();
            } catch(Exception e1)
            {
                e1.printStackTrace();
            }
        }
        logger.setLevel(Level.INFO);
    }
    
    @Test
    public void testRandomString()
    {
        String s = randomString();
        assertNotNull(s);
        assertTrue(s.length() > 0);
    }
    
    @Test
    public void testRandomStringInt()
    {
        int size = randomGenerator.nextInt(MAX_STRING_SIZE);
        String s = randomString(size);
        assertEquals(size, s.length());
    }
    
    @Test
    public void testRandomStringIntBaseCase()
    {
        String s = randomString(1);
        assertEquals(1, s.length());
    }
    
    @Test
    public void testRandomMailAddress()
    {
        String s = randomMailAddress();
        assertNotNull(s);
        assertTrue(s.length() > 0);
        assertTrue(s.lastIndexOf('@') > 0);
        assertTrue(s.substring(s.lastIndexOf('@')).length() >= 4);
        assertTrue(s.lastIndexOf('.') > 0);
        assertTrue(s.substring(s.lastIndexOf('.')).length() ==  3);
    }
    
    @Test
    public void testRandomMail()
    {
        String mail = randomMail();
        assertNotNull(mail);
        System.out.println(mail);
    }
    
    @Test
    public void testBuildRandomDn()
    {
        String dn = buildRandomDn();
        assertNotNull(dn);
        System.out.println(dn);
    }
    
    @Test
    public void fai()
    {
        String ciao = "a";
        System.out.println(ciao + " ? " + ciao.matches("(/[\\w-\\.]+)+"));
    }
    
    private static FQAN[] buildRandomFQANs()
    {
        int size = randomGenerator.nextInt(5);
        size = (size == 0 ? 1 : size);
        FQAN[] fquans = new FQAN[size];
        for(int i = 0; i < size ; i++)
        {
            fquans[i] = new FQAN(randomString(), '/' + randomString(), randomString(), randomString());    
        }
        return fquans;
    }
    
    private static String buildRandomDn()
    {
        char dnFieldsSeparatoChar = ',';
        String dn = "";
        dn += "C=" + randomString(2) + dnFieldsSeparatoChar;
        dn += "ST=" + randomString() + dnFieldsSeparatoChar;    
        dn += "O=" + randomString() + dnFieldsSeparatoChar;
        for(int i = randomGenerator.nextInt(5); i > 0 ; i--)
        {
            dn += "OU=" + randomString() + dnFieldsSeparatoChar;    
        }
        
        dn += "L=" + randomString() + dnFieldsSeparatoChar;
        
        for(int i = randomGenerator.nextInt(5); i >= 0 ; i--)
        {
            dn += "CN=" + randomString() + dnFieldsSeparatoChar;
        }
        for(int i = randomGenerator.nextInt(5); i > 0 ; i--)
        {
            dn += "DC=" + randomString() + dnFieldsSeparatoChar;    
        }
        if(randomGenerator.nextBoolean())
        {
            dn += randomMail();
        }
        if(dnFieldsSeparatoChar == dn.charAt(dn.length() -1))
        {
            dn.substring(0, dn.length() -2);    
        }
        return dn;
    }

    private static String randomMail()
    {
        switch (randomGenerator.nextInt(3))
        {
            case 0:
                return "Email=" + randomMailAddress();
            case 1:
                return "E=" + randomMailAddress();
            default:
                return "EMailAddress=" + randomMailAddress();
        }
    }
    
    private static String randomMailAddress()
    {
        return randomString() + "@" + randomString() + "." + randomString(2);   
    }

    private static String randomString(int i)
    {
        if(i == 0)
        {
            return "";
        }
        String startingString = randomString();
        if(startingString.length() < i)
        {
            char[] padding = new char[i - startingString.length()];
            Arrays.fill(padding, startingString.charAt(startingString.length() - 1));
            startingString += new String(padding);
        }
        return startingString.substring(0, i);
    }

    private static String randomString()
    {
        int size = randomGenerator.nextInt(MAX_STRING_SIZE);
        size = (size == 0 ? 1 : size);
        char[] stringChars = new char[size];
        for(int i = 0 ; i < size ; i++)
        {
            stringChars[i] = LETTERS_ALPHABET[randomGenerator.nextInt(LETTERS_ALPHABET.length)]; 
        }
        return new String(stringChars);
    }

    @After
    public void tearDown() 
    {
        logger.setLevel(Level.TRACE);
        try
        {
            SurlStatusStore.getInstance().checkIntegrity();
        } catch(Exception e)
        {
            try
            {
                SurlStatusStore.getInstance().checkIntegrity();
            } catch(Exception e1)
            {
                e1.printStackTrace();
            }
        }
        logger.setLevel(Level.INFO);
    }

    @Test
    public final void testGetInstance()
    {
        assertNotNull("Null instance!" , SurlStatusStore.getInstance());
    }

    @Test
    public final void testStore() throws IllegalArgumentException, TokenDuplicationException
    {
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(buildRandomSurl(), TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
    }
    
    
    @Test
    public final void testStoreWithGridUser() throws IllegalArgumentException, TokenDuplicationException
    {
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), gridUser, buildSurlStatusMap(buildRandomSurl(), TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
    }
    
    @Test
    public final void testStoreWithVomsGridUser() throws IllegalArgumentException, TokenDuplicationException
    {
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), vomsGridUser, buildSurlStatusMap(buildRandomSurl(), TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
    }
    
    @Test
    public final void testStoreExistentToken() throws IllegalArgumentException, TokenDuplicationException
    {
        TRequestToken token = TRequestToken.getRandom();
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(buildRandomSurl(), TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(TokenDuplicationException.class);
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(buildRandomSurl(), TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
    }
    
    @Test
    public final void testStoreNullToken() throws IllegalArgumentException, TokenDuplicationException
    {
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().store(null, buildSurlStatusMap(buildRandomSurl(), TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
    }

    @Test
    public final void testStoreNullSurlStatuses() throws IllegalArgumentException, TokenDuplicationException
    {
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), null);
    }
    
    @Test
    public final void testStoreEmptySurlStatuses() throws IllegalArgumentException, TokenDuplicationException
    {
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(new TSURL[0] , null, null));
    }
    
    @Test
    public final void testStoreMissingStatusSurlStatuses() throws IllegalArgumentException, TokenDuplicationException
    {
        HashMap<TSURL, TReturnStatus> surlStatusMap = new HashMap<TSURL, TReturnStatus>(1);
        surlStatusMap.put(buildRandomSurl(), null);
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), surlStatusMap);
    }
    
    @Test
    public final void testStoreMixedSurlStatuses() throws IllegalArgumentException, TokenDuplicationException
    {
        HashMap<TSURL, TReturnStatus> map = buildSurlStatusMap(buildRandomSurl() , TStatusCode.SRM_AUTHENTICATION_FAILURE, "heat more vegetables");
        addSurlStatus(map, null, TStatusCode.SRM_AUTHENTICATION_FAILURE, "stop eating pizza");
        map.put(buildRandomSurl(), null);
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), map);
    }

    @Test
    public final void testUpdateTRequestTokenTSURLTStatusCode() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        SurlStatusStore.getInstance().update(token, surl, TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testUpdateTRequestTokenTSURLTStatusCodeNewSurl() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(buildRandomSurl(), TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(UnknownSurlException.class);
        SurlStatusStore.getInstance().update(token, buildRandomSurl(), TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testUpdateTRequestTokenTSURLTStatusCodeNewToken() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCode()");
        thrown.expect(UnknownTokenException.class);
        SurlStatusStore.getInstance().update(TRequestToken.getRandom(), buildRandomSurl(), TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testUpdateTRequestTokenTSURLTStatusCodeNullToken() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCode()");
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().update(null, buildRandomSurl(), TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testUpdateTRequestTokenTSURLTStatusCodeNullStatusCode() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().update(token, surl, null);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testUpdateTRequestTokenTSURLTStatusCodeString() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        SurlStatusStore.getInstance().update(token, surl, TStatusCode.SRM_INTERNAL_ERROR, "nasty stuff");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testUpdateTRequestTokenTSURLTStatusCodeStringNewToken() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCodeString()");
        TSURL surl = buildRandomSurl();
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(UnknownTokenException.class);
        SurlStatusStore.getInstance().update(TRequestToken.getRandom(), surl, TStatusCode.SRM_INTERNAL_ERROR, "nasty stuff");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testUpdateTRequestTokenTSURLTStatusCodeStringNewSurl() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(buildRandomSurl(), TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(UnknownSurlException.class);
        SurlStatusStore.getInstance().update(token, buildRandomSurl(), TStatusCode.SRM_INTERNAL_ERROR, "nasty stuff");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testUpdateTRequestTokenTSURLTStatusCodeStringNullToken() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCodeString()");
        TSURL surl = buildRandomSurl();
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().update(null, surl, TStatusCode.SRM_INTERNAL_ERROR, "nasty stuff");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    
    @Test
    public final void testUpdateTRequestTokenTSURLTStatusCodeStringNullStatusCode() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().update(token, surl, null, "nasty stuff");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testUpdateTRequestTokenTSURLTStatusCodeStringNullExplanation() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().update(token, surl, TStatusCode.SRM_INTERNAL_ERROR, null);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testUpdateTRequestTokenListOfTSURLTStatusCode() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        SurlStatusStore.getInstance().update(token, surls, TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testUpdateTRequestTokenListOfTSURLTStatusCodeNewToken() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCode()");
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surls, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(UnknownTokenException.class);
        SurlStatusStore.getInstance().update(TRequestToken.getRandom(), surls, TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testUpdateTRequestTokenListOfTSURLTStatusCodeNewSurl() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(new TSURL[]{buildRandomSurl(), buildRandomSurl()}, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(UnknownSurlException.class);
        SurlStatusStore.getInstance().update(token, Arrays.asList(new TSURL[]{buildRandomSurl()}), TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testUpdateTRequestTokenListOfTSURLTStatusCodeMixedSurls() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        surls.add(buildRandomSurl());
        thrown.expect(UnknownSurlException.class);
        SurlStatusStore.getInstance().update(token, surls, TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testUpdateTRequestTokenListOfTSURLTStatusCodeEmptySurls()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        SurlStatusStore.getInstance().store(token,
                                            buildSurlStatusMap(buildRandomSurl(),
                                                               TStatusCode.SRM_AUTHENTICATION_FAILURE,
                                                               "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().update(token, new ArrayList<TSURL>(), TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testUpdateTRequestTokenListOfTSURLTStatusCodeNullValuedSurls()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2);
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        SurlStatusStore.getInstance().store(token,
                                            buildSurlStatusMap(surls, TStatusCode.SRM_AUTHENTICATION_FAILURE,
                                                               "Autentication failed"));
        surls.add(null);
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().update(token, surls, TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testUpdateTRequestTokenListOfTSURLTStatusCodeString() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        SurlStatusStore.getInstance().update(token, surls, TStatusCode.SRM_INTERNAL_ERROR, "got problems");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testUpdateTRequestTokenListOfTSURLTStatusCodeStringNewToken() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surls, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(UnknownTokenException.class);
        SurlStatusStore.getInstance().update(TRequestToken.getRandom(), surls, TStatusCode.SRM_INTERNAL_ERROR, "got problems");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testUpdateTRequestTokenListOfTSURLTStatusCodeStringNewSurl() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(new TSURL[]{buildRandomSurl(), buildRandomSurl()}, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(UnknownSurlException.class);
        SurlStatusStore.getInstance().update(token, Arrays.asList(new TSURL[]{buildRandomSurl()}), TStatusCode.SRM_INTERNAL_ERROR, "got problems");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testUpdateTRequestTokenListOfTSURLTStatusCodeStringMixedSurls() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        surls.add(buildRandomSurl());
        thrown.expect(UnknownSurlException.class);
        SurlStatusStore.getInstance().update(token, surls, TStatusCode.SRM_INTERNAL_ERROR, "infernal error");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testUpdateTRequestTokenListOfTSURLTStatusCodeStringEmptySurls()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        SurlStatusStore.getInstance().store(token,
                                            buildSurlStatusMap(buildRandomSurl(),
                                                               TStatusCode.SRM_AUTHENTICATION_FAILURE,
                                                               "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().update(token, new ArrayList<TSURL>(), TStatusCode.SRM_INTERNAL_ERROR, "dead cow");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testUpdateTRequestTokenListOfTSURLTStatusCodeStringNullValuedSurls()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2);
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        SurlStatusStore.getInstance().store(token,
                                            buildSurlStatusMap(surls, TStatusCode.SRM_AUTHENTICATION_FAILURE,
                                                               "Autentication failed"));
        surls.add(null);
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().update(token, surls, TStatusCode.SRM_INTERNAL_ERROR, "the pirate is laughting");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testUpdateTRequestTokenListOfTSURLTStatusCodeStringNullStatusCode()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().update(token, surls, null, "we cannot be friend");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testUpdateTRequestTokenListOfTSURLTStatusCodeStringNullExplanation()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().update(token, surls, TStatusCode.SRM_INTERNAL_ERROR, null);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCode() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, code, "Autentication failed"));
        SurlStatusStore.getInstance().checkAndUpdate(token, surl, code, TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCodeNewToken() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCode()");
        TSURL surl = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surl, code, "Autentication failed"));
        thrown.expect(UnknownTokenException.class);
        SurlStatusStore.getInstance().checkAndUpdate(TRequestToken.getRandom(), surl, code, TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCodeNewSurl() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(buildRandomSurl(), code, "Autentication failed"));
        thrown.expect(UnknownSurlException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, buildRandomSurl(), code, TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCodeNullExpectedStatusCode()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl(); 
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, surl, null, TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCodeNullNewStatusCode()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl(); 
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, code, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, surl, code, null);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCodeString() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, code, "Autentication failed"));
        SurlStatusStore.getInstance().checkAndUpdate(token, surl, code, TStatusCode.SRM_INTERNAL_ERROR, "we are dead!");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCodeStringNewToken() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCode()");
        TSURL surl = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surl, code, "Autentication failed"));
        thrown.expect(UnknownTokenException.class);
        SurlStatusStore.getInstance().checkAndUpdate(TRequestToken.getRandom(), surl, code, TStatusCode.SRM_INTERNAL_ERROR, "I cry for you");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCodeStringNewSurl() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(buildRandomSurl(), code, "Autentication failed"));
        thrown.expect(UnknownSurlException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, buildRandomSurl(), code, TStatusCode.SRM_INTERNAL_ERROR, "not lucky man");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCodeStringMixedSurls() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, code, "Autentication failed"));
        surls.add(buildRandomSurl());
        thrown.expect(UnknownSurlException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, surls, code, TStatusCode.SRM_INTERNAL_ERROR, "should do something better");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCodeStringEmptySurls()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token,
                                            buildSurlStatusMap(buildRandomSurl(),
                                                               code,
                                                               "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, new ArrayList<TSURL>(), code, TStatusCode.SRM_INTERNAL_ERROR , "come one!");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCodeStringNullValuedSurls()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2);
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token,
                                            buildSurlStatusMap(surls, code,
                                                               "Autentication failed"));
        surls.add(null);
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, surls, code, TStatusCode.SRM_INTERNAL_ERROR, "look at the sky");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCodeStringNullExpectedStatusCode()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, surls, null, TStatusCode.SRM_INTERNAL_ERROR, "we cannot be friend");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCodeStringNullNewStatusCode()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, code, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, surls, code, null, "we cannot be friend");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCodeStringNullExplanation()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, code, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, surls, code, TStatusCode.SRM_INTERNAL_ERROR, null);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testUpdateTSURLTStatusCodeString() throws IllegalArgumentException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTSURLTStatusCodeString()");
        TSURL surl = buildRandomSurl();
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        SurlStatusStore.getInstance().update(surl, TStatusCode.SRM_INTERNAL_ERROR, "zombies are coming!");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testUpdateTSURLTStatusCodeStringNewSurl() throws IllegalArgumentException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTSURLTStatusCodeString()");
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(buildRandomSurl(), TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(UnknownSurlException.class);
        SurlStatusStore.getInstance().update(buildRandomSurl(), TStatusCode.SRM_INTERNAL_ERROR, "zombies are coming!");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testUpdateTSURLTStatusCodeStringNullSurl() throws IllegalArgumentException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTSURLTStatusCodeString()");
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(buildRandomSurl(), TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().update(null, TStatusCode.SRM_INTERNAL_ERROR, "zombies are coming!");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testUpdateTSURLTStatusCodeStringNullStatusCode() throws IllegalArgumentException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTSURLTStatusCodeString()");
        TSURL surl = buildRandomSurl();
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().update(surl, null, "zombies are coming!");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testUpdateTSURLTStatusCodeStringNullExplanation() throws IllegalArgumentException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTSURLTStatusCodeString()");
        TSURL surl = buildRandomSurl();
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().update(surl, TStatusCode.SRM_INTERNAL_ERROR, null);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testCheckAndUpdateTSURLTStatusCodeTStatusCodeString() throws IllegalArgumentException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTSURLTStatusCodeTStatusCodeString()");
        TSURL surl = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surl, code, "Autentication failed"));
        SurlStatusStore.getInstance().checkAndUpdate(surl, code, TStatusCode.SRM_INTERNAL_ERROR, "I've seen the white Wheel!");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTSURLTStatusCodeTStatusCodeStringNewSurl() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTRequestTokenTSURLTStatusCodeTStatusCode()");
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(buildRandomSurl(), code, "Autentication failed"));
        thrown.expect(UnknownSurlException.class);
        SurlStatusStore.getInstance().checkAndUpdate(buildRandomSurl(), code, TStatusCode.SRM_INTERNAL_ERROR, "not lucky man");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testCheckAndUpdateTSURLTStatusCodeTStatusCodeStringNullExpectedStatusCode()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TSURL surl = buildRandomSurl(); 
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(surl, null, TStatusCode.SRM_INTERNAL_ERROR, "we cannot be friend");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTSURLTStatusCodeTStatusCodeStringNullNewStatusCode()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TSURL surl = buildRandomSurl(); 
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surl, code, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(surl, code, null, "we cannot be friend");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTSURLTStatusCodeTStatusCodeStringNullExplanation()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TSURL surl = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surl, code, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(surl, code, TStatusCode.SRM_INTERNAL_ERROR, null);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCode() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, code, "Autentication failed"));
        SurlStatusStore.getInstance().checkAndUpdate(token, surls, code, TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCodeNewToken() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCode()");
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surls, code, "Autentication failed"));
        thrown.expect(UnknownTokenException.class);
        SurlStatusStore.getInstance().checkAndUpdate(TRequestToken.getRandom(), surls, code, TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCodeNewSurls() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(new TSURL[]{buildRandomSurl(),buildRandomSurl()}, code, "Autentication failed"));
        thrown.expect(UnknownSurlException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, Arrays.asList(new TSURL[]{buildRandomSurl(),buildRandomSurl()}), code, TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCodeMixedSurls() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, code, "Autentication failed"));
        surls.add(buildRandomSurl());
        thrown.expect(UnknownSurlException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, surls, code, TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCodeEmptySurls()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token,
                                            buildSurlStatusMap(buildRandomSurl(),
                                                               code,
                                                               "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, new ArrayList<TSURL>(), code, TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCodeNullValuedSurls()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2);
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token,
                                            buildSurlStatusMap(surls, code,
                                                               "Autentication failed"));
        surls.add(null);
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, surls, code, TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCodeNullExpectedStatusCode()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, surls, null, TStatusCode.SRM_INTERNAL_ERROR);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCodeNullNewStatusCode()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, code, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, surls, code, null);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCodeString() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, code, "Autentication failed"));
        SurlStatusStore.getInstance().checkAndUpdate(token, surls, code, TStatusCode.SRM_INTERNAL_ERROR, "the hell is coming");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCodeStringNewToken() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCode()");
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surls, code, "Autentication failed"));
        thrown.expect(UnknownTokenException.class);
        SurlStatusStore.getInstance().checkAndUpdate(TRequestToken.getRandom(), surls, code, TStatusCode.SRM_INTERNAL_ERROR, "go jiants");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCodeStringNewSurls() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCode()");
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        TRequestToken token = TRequestToken.getRandom();
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(new TSURL[]{buildRandomSurl(),buildRandomSurl()}, code, "Autentication failed"));
        thrown.expect(UnknownSurlException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, Arrays.asList(new TSURL[]{buildRandomSurl(),buildRandomSurl()}), code, TStatusCode.SRM_INTERNAL_ERROR, "uppete");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCodeStringMixedSurls() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, code, "Autentication failed"));
        surls.add(buildRandomSurl());
        thrown.expect(UnknownSurlException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, surls, code, TStatusCode.SRM_INTERNAL_ERROR, "dont bother me");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCodeStringEmptySurls()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token,
                                            buildSurlStatusMap(buildRandomSurl(),
                                                               code,
                                                               "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, new ArrayList<TSURL>(), code, TStatusCode.SRM_INTERNAL_ERROR, "i'm a server");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCodeStringNullValuedSurls()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenTSURLTStatusCode()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2);
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token,
                                            buildSurlStatusMap(surls, code,
                                                               "Autentication failed"));
        surls.add(null);
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, surls, code, TStatusCode.SRM_INTERNAL_ERROR, "pity!");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCodeStringNullExpectedStatusCode()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, surls, null, TStatusCode.SRM_INTERNAL_ERROR, "no more...");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCodeStringNullNewStatusCode()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, code, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, surls, code, null, "out of service");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCodeStringNullExplaination()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, code, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, surls, code, TStatusCode.SRM_INTERNAL_ERROR, null);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testCheckAndUpdateTRequestTokenTStatusCodeTStatusCodeString() throws UnknownTokenException, ExpiredTokenException, IllegalArgumentException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTRequestTokenTStatusCodeTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(new TSURL[]{buildRandomSurl(), buildRandomSurl()}, code, "Autentication failed"));
        SurlStatusStore.getInstance().checkAndUpdate(token, code, TStatusCode.SRM_INTERNAL_ERROR, "berlusconi is immortal");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenTStatusCodeTStatusCodeStringNewToken() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckAndUpdateTRequestTokenListOfTSURLTStatusCodeTStatusCode()");
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(new TSURL[]{buildRandomSurl(), buildRandomSurl()}, code, "Autentication failed"));
        thrown.expect(UnknownTokenException.class);
        SurlStatusStore.getInstance().checkAndUpdate(TRequestToken.getRandom(), code, TStatusCode.SRM_INTERNAL_ERROR, "go jiants");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenTStatusCodeTStatusCodeStringNullExpectedStatusCode()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(new TSURL[]{buildRandomSurl(), buildRandomSurl()}, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, null, TStatusCode.SRM_INTERNAL_ERROR, "no more...");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenTStatusCodeTStatusCodeStringNullNewStatusCode()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(new TSURL[]{buildRandomSurl(), buildRandomSurl()}, code, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, code, null, "out of service");
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testCheckAndUpdateTRequestTokenTStatusCodeTStatusCodeStringNullExplaination()
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException,
            TokenDuplicationException, UnknownSurlException
    {
        System.out.println("\nSurlStatusStoreTestNew.testUpdateTRequestTokenListOfTSURLTStatusCodeString()");
        TRequestToken token = TRequestToken.getRandom();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(new TSURL[]{buildRandomSurl(), buildRandomSurl()}, code, "Autentication failed"));
        thrown.expect(IllegalArgumentException.class);
        SurlStatusStore.getInstance().checkAndUpdate(token, code, TStatusCode.SRM_INTERNAL_ERROR, null);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testGetSurlsStatusTRequestToken() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testGetSurlsStatusTRequestToken()");
        TRequestToken token = TRequestToken.getRandom();
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(buildRandomSurl(), TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        SurlStatusStore.getInstance().getSurlsStatus(token);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testGetSurlsStatusTRequestTokenTSURL() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testGetSurlsStatusTRequestTokenTSURL()");
        TRequestToken token = TRequestToken.getRandom();
        TSURL surl = buildRandomSurl();
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        SurlStatusStore.getInstance().getSurlsStatus(token, surl);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testGetSurlsStatusTRequestTokenCollectionOfTSURL() throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testGetSurlsStatusTRequestTokenCollectionOfTSURL()");
        TRequestToken token = TRequestToken.getRandom();
        ArrayList<TSURL> surls = new ArrayList<TSURL>(2); 
        surls.add(buildRandomSurl());
        surls.add(buildRandomSurl());
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surls, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        SurlStatusStore.getInstance().getSurlsStatus(token, surls);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testGetSurlStatuses() throws IllegalArgumentException, UnknownSurlException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testGetSurlStatuses()");
        TSURL surl = buildRandomSurl();
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        SurlStatusStore.getInstance().getSurlStatuses(surl);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testGetSurlPerTokenStatuses() throws IllegalArgumentException, UnknownSurlException, TokenDuplicationException
    {
        System.out.println("\nSurlStatusStoreTestNew.testGetSurlPerTokenStatuses()");
        TSURL surl = buildRandomSurl();
        SurlStatusStore.getInstance().store(TRequestToken.getRandom(), buildSurlStatusMap(surl, TStatusCode.SRM_AUTHENTICATION_FAILURE, "Autentication failed"));
        SurlStatusStore.getInstance().getSurlPerTokenStatuses(surl);
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }

    @Test
    public final void testGetExpiredTokens()
    {
        System.out.println("\nSurlStatusStoreTestNew.testGetExpiredTokens()");
        SurlStatusStore.getInstance().getExpiredTokens();
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    @Test
    public final void testPurge() throws Exception
    {
        TRequestToken token = buildExpiredToken();
        assertTrue("expiredToken should have an ExpirationDate", token.hasExpirationDate());
        assertTrue("expiredToken should be expired", token.isExpired());
        TSURL surl = buildRandomSurl();
        TStatusCode code = TStatusCode.SRM_AUTHENTICATION_FAILURE;
        String explanation = "Autentication failed";
        SurlStatusStore.getInstance().store(token, buildSurlStatusMap(surl, code, explanation));
        
        Object lock = new Object();
        synchronized (lock)
        {
            lock.wait((Configuration.getInstance().getRequestPurgerPeriod() * 1000) + (Configuration.getInstance().getRequestPurgerDelay() * 1000) + 1);
        }
        
        thrown.expect(UnknownTokenException.class);
        SurlStatusStore.getInstance().getSurlsStatus(token, surl);
    }

    @Test
    public final void testCheckIntegrity() throws Exception
    {
        System.out.println("\nSurlStatusStoreTestNew.testCheckIntegrity()");
        SurlStatusStore.getInstance().checkIntegrity();
        System.out.println("SurlStatusStoreTestNew.enclosing_method()");
    }
    
    private void addSurlStatus(HashMap<TSURL, TReturnStatus> map, TSURL surl, TStatusCode code, String explanation)
    {
        map.put(surl, buildStatus(code, explanation));    
    }
    
    private static HashMap<TSURL, TReturnStatus> buildSurlStatusMap(TSURL surl, TStatusCode code, String explanation)
    {
        return buildSurlStatusMap(new TSURL[]{surl}, code, explanation);
    }
    
    private HashMap<TSURL, TReturnStatus> buildSurlStatusMap(ArrayList<TSURL> surls,
            TStatusCode code, String explanation)
    {
        return buildSurlStatusMap(surls.toArray(new TSURL[surls.size()]), code, explanation);
    }

    private static HashMap<TSURL, TReturnStatus> buildSurlStatusMap(TSURL[] surls, TStatusCode code, String explanation)
    {
        HashMap<TSURL, TReturnStatus> surlStatusMap = new HashMap<TSURL, TReturnStatus>(surls.length);
        for(TSURL surl : surls)
        {
            surlStatusMap.put(surl, buildStatus(code, explanation));    
        }
        return surlStatusMap;
    }

    private static TReturnStatus buildStatus(TStatusCode statusCode, String explaination)
            throws IllegalStateException
    {
        try
        {
            return new TReturnStatus(statusCode, explaination);
        } catch(InvalidTReturnStatusAttributeException e1)
        {
            // Never thrown
            throw new IllegalStateException("Unexpected InvalidTReturnStatusAttributeException "
                    + "in building TReturnStatus: " + e1.getMessage());
        }
    }
    
    private TSURL buildRandomSurl()
    {
        return buildSurl("srm://" + FE_HOST + ":8444/" + randomGenerator.nextInt());
    }

    private TSURL buildSurl(String string)
    {
        TSURL surl = null;
        try
        {
            surl = TSURL.makeFromStringWellFormed(string);
        } catch(InvalidTSURLAttributesException e)
        {
           fail("Unable to build the TSURL: " + e);
        }
        return surl;
    }
    
    private TRequestToken buildExpiredToken()
    {
        Calendar expiredTime = Calendar.getInstance();
        expiredTime.setTimeInMillis(expiredTime.getTimeInMillis() - ((Configuration.getInstance().getExpiredRequestTime() + 1) * 1000));
        try
        {
            return new TRequestToken(EXPIRED_TOKEN_PREFIX.concat(UUID.randomUUID()
                                                                           .toString()
                                                                           .substring(EXPIRED_TOKEN_PREFIX.length())) , expiredTime.getTime());
        } catch(InvalidTRequestTokenAttributesException e)
        {
            throw new IllegalStateException("Unexpected InvalidTRequestTokenAttributesException: " + e);
        }
    }

}
