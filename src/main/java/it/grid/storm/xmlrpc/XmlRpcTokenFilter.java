/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.xmlrpc;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

/**
 * Drop requests that does not contain the expected security token.
 * 
 * @author valerioventuri
 * 
 */
public class XmlRpcTokenFilter implements Filter {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory
		.getLogger(XMLRPCHttpServer.class);

	/**
	 * 
	 */
	public static final String STORM_HEADER_PATTERN_STRING = "^\\s*STORM/(\\S+).*$";

	/**
	 * 
	 */
	public static final Pattern STORM_HEADER_PATTERN = Pattern
		.compile(STORM_HEADER_PATTERN_STRING);

	/**
	 * This is the string that has to be provided for requests to go through.
	 */
	private String secret;

	/**
	 * Constructor.
	 * 
	 * @param secret
	 */
	public XmlRpcTokenFilter(String secret) {

		this.secret = secret;
	}

	/**
	 * Pass over to the next filter if and only if the request contains a security
	 * token that match the expected.
	 * 
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
		FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;

		String header = httpRequest.getHeader("User-Agent");

		String token = parseStormToken(header);

		if (token == null) {

			log
				.error("The XML-RPC request security token is missing. The calling service is probably misconfigured.");

			((HttpServletResponse) response).getWriter().write(
				prepareXml("The request security token is missing"));

			return;

		}

		if (!token.equals(this.secret)) {

			log
				.error("The XML-RPC request security token does not match. The calling service is probably misconfigured.");

			((HttpServletResponse) response).getWriter().write(
				prepareXml("The request security token does not match"));

			return;
		}

		chain.doFilter(request, response);
	}

	/**
	 * 
	 */
	public static final String parseStormToken(String headerContent) {

		Matcher m = STORM_HEADER_PATTERN.matcher(headerContent);

		if (m.matches())
			return m.group(1);

		return null;
	}

	private String prepareXml(String message) {

		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<methodResponse><fault><value><struct>"
			+ "<member><name>faultCode</name><value><i4>0</i4></value></member>"
			+ "<member><name>faultString</name><value>" + message + "</value></member>"
			+ "</struct></value></fault></methodResponse>";
	}
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

	@Override
	public void destroy() {

	}

}
