package com.sunnysuni.store.global.error;

import com.sunnysuni.common.dto.ApiResponse;
import com.sunnysuni.common.exception.BusinessException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

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

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ApiResponse<Void>> handleHandlerMethodValidation(HandlerMethodValidationException exception) {
    String message = exception.getParameterValidationResults().stream()
        .flatMap(result -> result.getResolvableErrors().stream())
        .findFirst()
        .map(MessageSourceResolvable::getDefaultMessage)
        .orElse("요청 값이 올바르지 않습니다.");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(message));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException exception) {
    String message = exception.getConstraintViolations().stream()
        .findFirst()
        .map(violation -> violation.getMessage())
        .orElse("요청 값이 올바르지 않습니다.");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(message));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameter(
      MissingServletRequestParameterException exception
  ) {
    String message = exception.getParameterName() + " 파라미터는 필수입니다.";
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(message));
  }
}
