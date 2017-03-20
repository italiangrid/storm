package it.grid.storm.rest.auth;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenVerifier {

	private static final Logger log = LoggerFactory.getLogger(TokenVerifier.class);

	private final String TOKEN_VALUE;
	private final boolean TOKEN_ENABLED;

	public static TokenVerifier getTokenVerifier(String token, boolean isEnabled) {
		return new TokenVerifier(token, isEnabled);
	}

	private TokenVerifier(String token, boolean isEnabled) {
		this.TOKEN_VALUE = token;
		this.TOKEN_ENABLED = isEnabled;
	}

	public void verify(String token) {

		log.debug("Token authentication enabled: {}", TOKEN_ENABLED);
		if (!TOKEN_ENABLED) {
			return;
		}
		if (token != null && token.equals(TOKEN_VALUE)) {
			log.debug("Token verified!.");
			return;
		}
		throw new WebApplicationException("Invalid token", UNAUTHORIZED);
	}
}
