package com.learnings.iot_platform.exception_handling;

import com.learnings.iot_platform.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;



import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleMethodArgumentNotValidException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError("object", "field", "Field is required");
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<List<String>> response = globalExceptionHandler.handleMethodArgumentNotValidException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("field: Field is required"));
    }

    @Test
    void testHandleBadCredentialsException() {

        BadCredentialsException ex = new BadCredentialsException("Bad Credentials: Invalid username or password");


        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleBadCredentialsException(ex);


        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Bad Credentials: Invalid username or password", response.getBody().getMessage());
    }

    @Test
    void testHandleAuthenticationCredentialsNotFoundException() {
        AuthenticationCredentialsNotFoundException ex = mock(AuthenticationCredentialsNotFoundException.class);

        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleUnauthorized(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized: You are not authenticated or your token is invalid", response.getBody().getMessage());
    }

    @Test
    void testHandleAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Forbidden");

        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleForbidden(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Forbidden: You do not have permission to access this resource", response.getBody().getMessage());
    }

    @Test
    void testHandleAuthenticationException() {
        AuthenticationException ex = new AuthenticationException("Authentication Error") {};

        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleUnauthorized(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Authentication Error", response.getBody().getMessage());
    }
}
