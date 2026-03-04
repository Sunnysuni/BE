package com.sunnysuni.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BusinessExceptionTest {

  @Test
  @DisplayName("BusinessException은 ErrorCode 기본 메시지를 사용한다.")
  void businessExceptionUsesDefaultMessage() {
    // Given
    ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

    // When
    BusinessException exception = new BusinessException(errorCode);

    // Then
    assertEquals(errorCode, exception.getErrorCode());
    assertEquals(errorCode.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("EntityNotFoundException은 ENTITY_NOT_FOUND 코드와 id 정보를 가진다.")
  void entityNotFoundExceptionHasExpectedValues() {
    // Given

    // When
    EntityNotFoundException exception = new EntityNotFoundException("Product", 1L);

    // Then
    assertEquals(ErrorCode.ENTITY_NOT_FOUND, exception.getErrorCode());
    assertTrue(exception.getMessage().contains("Product"));
    assertTrue(exception.getMessage().contains("id=1"));
  }
}
