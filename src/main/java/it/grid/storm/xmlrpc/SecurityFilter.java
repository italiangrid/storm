package it.grid.storm.xmlrpc;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Drop requests that does not contain the expected security token.
 * 
 * @author valerioventuri
 *
 */
public class SecurityFilter implements Filter {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory
		.getLogger(XMLRPCHttpServer.class);
	
	/**
	 * This is the string that has to be provided for requests
	 * to go through.
	 */
	private String secret;

	private static String TOKEN_HEADER_NAME = "TBD";
	
	/**
	 * Constructor.
	 * 
	 * @param secret
	 */
	public SecurityFilter(String secret) {
		
		this.secret = secret;
	}
	
	/**
	 * Pass over to the next filter if and only if the request contains
	 * a security token that match the expected.
	 *
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
		FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		String token = httpRequest.getHeader(TOKEN_HEADER_NAME);
		
		if(token == null) {

			log.error("The XML-RPC request does not contain a security token");
			
			return;
		}
		
		if(!token.equals(this.secret)) {
			
			log.error("The XML-RPC request security token does not match");
			
			return;
		}

		chain.doFilter(request, response);
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {}

	@Override
	public void destroy() {}

}
