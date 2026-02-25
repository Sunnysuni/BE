package com.sunnysuni.store.auth.controller;

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
class AuthControllerIntegrationTest {
  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("로그인 성공 시 200 OK를 반환하고 세션에 인증 정보를 저장합니다.")
  void loginSuccessReturnsOkAndStoresSecurityContextInSession() throws Exception {
    // Given
    String requestBody = """
        {
          "username": "buyer",
          "password": "buyer1234"
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
  @DisplayName("로그인 실패 시 401 Unauthorized를 반환합니다.")
  void loginFailureReturnsUnauthorized() throws Exception {
    // Given
    String requestBody = """
        {
          "username": "buyer",
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
  @DisplayName("로그아웃 성공 시 200 OK를 반환하고 세션을 무효화합니다.")
  void logoutSuccessReturnsOkAndInvalidatesSession() throws Exception {
    // Given
    String loginRequestBody = """
        {
          "username": "buyer",
          "password": "buyer1234"
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
        .andExpect(status().isOk());

    assertThat(session.isInvalid()).isTrue();
  }
}
