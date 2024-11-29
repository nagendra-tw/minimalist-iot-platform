package com.learnings.iot_platform.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnings.iot_platform.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//        System.out.println(authException.getMessage());
//
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());

        System.out.println(authException.getMessage());

        // Set the response status to 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Set the response content type to application/json
        response.setContentType("application/json");

        // Create a response body object with the error details
        ApiResponse apiResponse = new ApiResponse("Unauthorized: " +  authException.getMessage());

        // Convert the errorResponse object to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);

        // Write the JSON response to the output stream
        response.getWriter().write(jsonResponse);
    }
}
