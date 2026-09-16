package com.tpc.tpcgestpaie.localapp.filter;

import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.service.AuditLogService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private final AuditLogService auditService;

    public RequestLoggingFilter(AuditLogService auditService) {
        this.auditService = auditService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (auth != null && auth.getPrincipal() instanceof User) {
            user = (User) auth.getPrincipal();
        }

        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Exemple générique
      /*  auditService.log(
                "API_REQUEST",
                "N/A", // ou extraire de l'URL si besoin
                null,
                method + " " + path,
                user,
                ip,
                userAgent
        );*/

        filterChain.doFilter(request, response);
    }
}
