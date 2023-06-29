/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.rest.auth;

import static it.grid.storm.rest.auth.RestTokenFilter.TOKEN_HEADER_NAME;
import static it.grid.storm.rest.auth.RestTokenFilter.TOKEN_INIT_PARAM_NAME;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.FilterHolder;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;

public class RestTokenFilterTest {

  private final static String TOKEN = "abracadabra";
  private final static String WRONG_TOKEN = "alakazam";

  private final static String TMP_FILENAME = "tmp.txt";

  private HttpServletRequest getMockRequest(String token) {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Mockito.when(request.getHeader(TOKEN_HEADER_NAME)).thenReturn(token);
    return request;
  }

  private HttpServletResponse getMockResponse() throws IOException {
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    PrintWriter writer = new PrintWriter(TMP_FILENAME);
    Mockito.when(response.getWriter()).thenReturn(writer);
    return response;
  }

  private FilterChain getMockFilterChain() throws IOException, ServletException {
    FilterChain chain = Mockito.mock(FilterChain.class);
    return chain;
  }

  private RestTokenFilter getRestTokenFilter(String token) throws Exception {
    FilterHolder filterHolder = new FilterHolder(new RestTokenFilter());
    filterHolder.setInitParameter(TOKEN_INIT_PARAM_NAME, token);
    filterHolder.start();
    return (RestTokenFilter) filterHolder.getFilter();
  }

  @Test
  public void testMisconfiguration() throws Exception {

    try {
      getRestTokenFilter(null);
      fail();
    } catch (ServletException e) {
      assertEquals(e.getMessage(), "Not found init parameter: " + TOKEN_INIT_PARAM_NAME);
    }
  }

  @Test
  public void testAuthorized() throws Exception {

    RestTokenFilter filter = getRestTokenFilter(TOKEN);
    HttpServletRequest request = getMockRequest(TOKEN);
    HttpServletResponse response = getMockResponse();
    FilterChain chain = getMockFilterChain();
    filter.doFilter(request, response, chain);
    verify(chain).doFilter(request, response);
    filter.destroy();
  }

  @Test
  public void testInvalidToken() throws Exception {

    RestTokenFilter filter = getRestTokenFilter(TOKEN);
    HttpServletRequest request = getMockRequest(WRONG_TOKEN);
    HttpServletResponse response = getMockResponse();
    FilterChain chain = getMockFilterChain();
    filter.doFilter(request, response, chain);
    verify(chain, times(0)).doFilter(request, response);
    verify(response).setStatus(UNAUTHORIZED.getStatusCode());
    response.getWriter().flush();
    assertEquals(readFileToString(new File(TMP_FILENAME), "UTF-8"), "Invalid token provided");
    filter.destroy();
  }

  @Test
  public void testNullToken() throws Exception {

    RestTokenFilter filter = getRestTokenFilter(TOKEN);
    HttpServletRequest request = getMockRequest(null);
    HttpServletResponse response = getMockResponse();
    FilterChain chain = getMockFilterChain();
    filter.doFilter(request, response, chain);
    verify(chain, times(0)).doFilter(request, response);
    verify(response).setStatus(UNAUTHORIZED.getStatusCode());
    response.getWriter().flush();
    assertEquals(readFileToString(new File(TMP_FILENAME), "UTF-8"), "Invalid token provided");
    filter.destroy();
  }

  @After
  public void removeTmpFile() {
    File f = new File(TMP_FILENAME);
    if (f.exists()) {
      f.delete();
    }
  }
}
