package com.sunnysuni.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorCodeTest {

  @Test
  @DisplayName("ErrorCode는 기대하는 HTTP 상태 코드를 가진다.")
  void errorCodeHasExpectedStatus() {
    // Given

    // When
    int invalidInputStatus = ErrorCode.INVALID_INPUT_VALUE.getStatus();
    int unauthorizedStatus = ErrorCode.UNAUTHORIZED.getStatus();
    int notFoundStatus = ErrorCode.ENTITY_NOT_FOUND.getStatus();
    int internalServerErrorStatus = ErrorCode.INTERNAL_SERVER_ERROR.getStatus();

    // Then
    assertEquals(400, invalidInputStatus);
    assertEquals(401, unauthorizedStatus);
    assertEquals(404, notFoundStatus);
    assertEquals(500, internalServerErrorStatus);
  }
}
