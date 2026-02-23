package com.sunnysuni.store.auth.service;

import com.sunnysuni.store.auth.dto.LoginRequest;
import com.sunnysuni.store.auth.dto.SignupRequest;
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
public class AuthService {
  private final AuthenticationManager authenticationManager;

  public AuthService(AuthenticationManager authenticationManager) {
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

  public void signup(SignupRequest request) {
    // TODO: 회원 저장소 연동 후 실제 회원가입 처리
  }

  public void logout(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }
    SecurityContextHolder.clearContext();
  }
}
