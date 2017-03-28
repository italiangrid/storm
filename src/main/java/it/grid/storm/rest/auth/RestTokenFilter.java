package it.grid.storm.rest.auth;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestTokenFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(RestTokenFilter.class);

	public static final String TOKEN_HEADER_NAME = "token";
	public static final String TOKEN_INIT_PARAM_NAME = "token";

	private String token;

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		if (isTokenValid(getToken(request))) {

			log.debug("Token verified!");
			chain.doFilter(request, response);

		} else {

			log.warn("Invalid token provided", request);
			response.setStatus(SC_UNAUTHORIZED);
			response.getWriter().print("Invalid token provided");
		}
	}

	private String getToken(HttpServletRequest httpRequest) {

		return httpRequest.getHeader(TOKEN_HEADER_NAME);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		token = filterConfig.getInitParameter(TOKEN_INIT_PARAM_NAME);
		if (token == null) {
			throw new ServletException("Not found init parameter: " + TOKEN_INIT_PARAM_NAME);
		}
	}

	private boolean isTokenValid(String token) throws ServletException {

		return this.token.equals(token);
	}

}
