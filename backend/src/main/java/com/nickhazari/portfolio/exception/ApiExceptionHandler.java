package com.nickhazari.portfolio.exception;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest request) {
    return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ApiError> handleConflict(ConflictException ex, HttpServletRequest request) {
    return buildError(HttpStatus.CONFLICT, ex.getMessage(), request);
  }

  @ExceptionHandler({ BadRequestException.class, IllegalArgumentException.class })
  public ResponseEntity<ApiError> handleBadRequest(RuntimeException ex, HttpServletRequest request) {
    return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
  }

  @ExceptionHandler({ UnauthorizedException.class, AuthenticationException.class })
  public ResponseEntity<ApiError> handleUnauthorized(RuntimeException ex, HttpServletRequest request) {
    return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
    return buildError(HttpStatus.FORBIDDEN, ex.getMessage(), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
    String message = ex.getBindingResult()
      .getAllErrors()
      .stream()
      .map(DefaultMessageSourceResolvable::getDefaultMessage)
      .collect(Collectors.joining("; "));
    return buildError(HttpStatus.BAD_REQUEST, message, request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleUnhandled(Exception ex, HttpServletRequest request) {
    return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred.", request);
  }

  private ResponseEntity<ApiError> buildError(HttpStatus status, String message, HttpServletRequest request) {
    ApiError error = new ApiError(
        Instant.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        request.getRequestURI());
    return ResponseEntity.status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(error);
  }
}
