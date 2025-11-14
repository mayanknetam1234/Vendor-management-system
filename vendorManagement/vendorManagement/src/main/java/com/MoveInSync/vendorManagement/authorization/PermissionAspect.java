package com.MoveInSync.vendorManagement.authorization;

import com.MoveInSync.vendorManagement.dto.ErrorResponseDto;
import com.MoveInSync.vendorManagement.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private JwtService jwtService;

    @Before("@annotation(requiresPermission)")
    public void checkPermission(JoinPoint joinPoint, RequiresPermission requiresPermission) throws Throwable {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return; // no web context

        HttpServletRequest request = attrs.getRequest();
        HttpServletResponse response = attrs.getResponse();

        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                sendJsonResponse(response, "Missing or invalid Authorization header", HttpServletResponse.SC_FORBIDDEN);
                // ⛔️ Stop controller execution
                throw new StopExecutionException();
            }

            String token = authHeader.substring(7);
            Claims claims = jwtService.extractAllClaims(token);
            List<String> permissions = (List<String>) claims.get("permissions");
            String required = requiresPermission.value();

            if (permissions == null || !permissions.contains(required)) {
                sendJsonResponse(response, "Access Denied: You do not have permission → " + required, HttpServletResponse.SC_FORBIDDEN);
                // ⛔️ Stop controller execution
                throw new StopExecutionException();
            }

            System.out.println("✅ Permission check passed: " + required);

        } catch (StopExecutionException stop) {
            // absorb — prevents controller from executing
            throw stop;
        } catch (Exception e) {
            sendJsonResponse(response, "Unexpected error during permission check: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new StopExecutionException();
        }
    }

    private void sendJsonResponse(HttpServletResponse response, String message, int status) {
        if (response == null) return;
        try {
            response.setStatus(status);
            response.setContentType("application/json");
            ErrorResponseDto error = new ErrorResponseDto(
                    message,
                    status,
                    LocalDateTime.now().toString()
            );
            new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValue(response.getWriter(), error);
        } catch (IOException ignored) {}
    }

    // 🧩 Custom runtime exception to STOP execution silently
    static class StopExecutionException extends RuntimeException {}
}
