package com.sunnysuni.admin.global.error;

import com.sunnysuni.common.dto.ApiResponse;
import com.sunnysuni.common.exception.BusinessException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiResponse<Void>> handleBadCredentials() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.error("아이디 또는 비밀번호가 올바르지 않습니다."));
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
    return ResponseEntity.status(exception.getErrorCode().getStatus())
        .body(ApiResponse.error(exception.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException exception) {
    String message = exception.getBindingResult().getFieldErrors().stream()
        .findFirst()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .orElse("요청 값이 올바르지 않습니다.");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(message));
  }
}
