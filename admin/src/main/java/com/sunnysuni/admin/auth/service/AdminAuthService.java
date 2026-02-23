package com.sunnysuni.admin.auth.service;

import com.sunnysuni.admin.auth.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Service
public class AdminAuthService {
  private final AuthenticationManager authenticationManager;

  public AdminAuthService(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  public void login(LoginRequest request, HttpServletRequest httpRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username(), request.password())
    );

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);

    HttpSession session = httpRequest.getSession(true);
    session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, context);
  }

  public void logout(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }
    SecurityContextHolder.clearContext();
  }

}
