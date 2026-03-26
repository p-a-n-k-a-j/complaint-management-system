package com.pankaj.complaintmanagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pankaj.complaintmanagement.exception.payload.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomEntryPoint implements AuthenticationEntryPoint {
    private ObjectMapper objectMapper;
    public CustomEntryPoint(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ErrorResponse error = new ErrorResponse.Builder()
                .message("Access Denied: Missing token or expired")
                .status(HttpStatus.UNAUTHORIZED)
                .path(request.getRequestURI())
                .build();
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
