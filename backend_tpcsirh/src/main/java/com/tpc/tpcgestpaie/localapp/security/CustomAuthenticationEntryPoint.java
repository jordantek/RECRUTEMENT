package com.tpc.tpcgestpaie.localapp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json");

        ApiResponse<String> apiResponse = new ApiResponse<>(false, "Non authentifié : accès refusé", null);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
