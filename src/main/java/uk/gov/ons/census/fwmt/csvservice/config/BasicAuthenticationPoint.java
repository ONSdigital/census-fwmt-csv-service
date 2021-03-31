package uk.gov.ons.census.fwmt.csvservice.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class BasicAuthenticationPoint extends BasicAuthenticationEntryPoint {
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx) {
    try {
    response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    PrintWriter writer = response.getWriter();
    writer.println("HTTP Status 401 - " + authEx.getMessage());
    } catch (Exception e) {
      throw new RuntimeException("issue with Basic Authentication", e);
    }
  }

  @Override
  public void afterPropertiesSet(){
    setRealmName("census-fwmt-csv-service");
    super.afterPropertiesSet();
  }
}
