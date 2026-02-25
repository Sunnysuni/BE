package com.sunnysuni.store.auth.controller;

import com.sunnysuni.common.dto.ApiResponse;
import com.sunnysuni.store.auth.dto.LoginRequest;
import com.sunnysuni.store.auth.dto.SignupRequest;
import com.sunnysuni.store.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest request) {
    authService.signup(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("회원가입이 완료되었습니다.", null));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<Void>> login(
      @Valid @RequestBody LoginRequest request,
      HttpServletRequest httpRequest
  ) {
    authService.login(request, httpRequest);
    return ResponseEntity.ok(ApiResponse.success("로그인이 완료되었습니다.", null));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
    authService.logout(request);
    return ResponseEntity.ok(ApiResponse.success("로그아웃이 완료되었습니다.", null));
  }
}
