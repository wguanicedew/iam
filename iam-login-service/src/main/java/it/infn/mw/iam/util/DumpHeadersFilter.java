package it.infn.mw.iam.util;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.filter.CommonsRequestLoggingFilter;

public class DumpHeadersFilter extends CommonsRequestLoggingFilter {

  @Override
  protected void beforeRequest(HttpServletRequest request, String message) {

    super.beforeRequest(request, message);

    Enumeration<String> headerNames = request.getHeaderNames();

    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();

      logger.debug(
        String.format("%s -> %s", headerName, request.getHeader(headerName)));
    }

  }

}
