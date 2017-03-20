package it.grid.storm.rest.auth;

import static it.grid.storm.rest.auth.TokenVerifier.getTokenVerifier;

import javax.ws.rs.WebApplicationException;

import org.junit.Test;

public class TokenVerifierTest {

  private static final String TOKEN = "abracadabra";
  private static final String WRONG_TOKEN = "alakazam";

  @Test
  public void testTokenNotEnabled() {
    TokenVerifier t = getTokenVerifier(TOKEN, false);
    t.verify(TOKEN);
  }

  @Test
  public void testTokenSuccess() {
    TokenVerifier t = getTokenVerifier(TOKEN, true);
    t.verify(TOKEN);
  }

  @Test(expected=WebApplicationException.class)
  public void testTokenInvalidNull() {
    TokenVerifier t = getTokenVerifier(TOKEN, true);
    t.verify(null);
  }

  @Test(expected=WebApplicationException.class)
  public void testTokenInvalidWrongToken() {
    TokenVerifier t = getTokenVerifier(TOKEN, true);
    t.verify(WRONG_TOKEN);
  }

}
