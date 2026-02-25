package com.sunnysuni.admin.auth.controller;

import com.sunnysuni.admin.auth.dto.LoginRequest;
import com.sunnysuni.admin.auth.service.AdminAuthService;
import com.sunnysuni.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AdminAuthController {
  private final AdminAuthService adminAuthService;

  public AdminAuthController(AdminAuthService adminAuthService) {
    this.adminAuthService = adminAuthService;
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<Void>> login(
      @Valid @RequestBody LoginRequest request,
      HttpServletRequest httpRequest
  ) {
    adminAuthService.login(request, httpRequest);
    return ResponseEntity.ok(ApiResponse.success("로그인이 완료되었습니다.", null));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
    adminAuthService.logout(request);
    return ResponseEntity.ok(ApiResponse.success("로그아웃이 완료되었습니다.", null));
  }
}
