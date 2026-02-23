package com.sunnysuni.admin.auth.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminAuthControllerIntegrationTest {
  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("관리자 로그인 성공 시 200 OK와 세션 인증 정보를 반환합니다")
  void loginSuccessReturnsOkAndStoresSecurityContextInSession() throws Exception {
    // Given
    String requestBody = """
        {
          "username": "admin",
          "password": "admin1234"
        }
        """;

    // When
    MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        // Then
        .andExpect(status().isOk())
        .andReturn();

    assertThat(result.getRequest().getSession(false)).isNotNull();
    assertThat(result.getRequest().getSession(false)
        .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY))
        .isNotNull();
  }

  @Test
  @DisplayName("관리자 로그인 실패 시 401 Unauthorized를 반환합니다")
  void loginFailureReturnsUnauthorized() throws Exception {
    // Given
    String requestBody = """
        {
          "username": "admin",
          "password": "wrong-password"
        }
        """;

    // When & Then
    mockMvc.perform(post("/api/v1/auth/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("관리자 로그아웃 성공 시 204 No Content와 세션 무효화를 반환합니다")
  void logoutSuccessReturnsNoContentAndInvalidatesSession() throws Exception {
    // Given
    String loginRequestBody = """
        {
          "username": "admin",
          "password": "admin1234"
        }
        """;

    MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginRequestBody))
        .andExpect(status().isOk())
        .andReturn();

    MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
    assertThat(session).isNotNull();

    // When
    mockMvc.perform(post("/api/v1/auth/logout")
            .with(csrf())
            .session(session))
        // Then
        .andExpect(status().isNoContent());

    assertThat(session.isInvalid()).isTrue();
  }
}
