package com.MoveInSync.vendorManagement.authorization;

import com.MoveInSync.vendorManagement.dto.ErrorResponseDto;
import com.MoveInSync.vendorManagement.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private JwtService jwtService;

    @Before("@annotation(requiresPermission)")
    public void checkPermission(JoinPoint joinPoint, RequiresPermission requiresPermission) {
        try {
            System.out.println("Permission check");

            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) {
                throw new AccessDeniedException("No request context available");
            }

            HttpServletRequest request = attrs.getRequest();
            HttpServletResponse response = attrs.getResponse(); // ✅ Get response to write JSON

            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                sendErrorResponse(response, "Missing or invalid Authorization header");
                throw new AccessDeniedException("Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);
            Claims claims = jwtService.extractAllClaims(token);

            List<String> permissions = (List<String>) claims.get("permissions");
            String required = requiresPermission.value();

            if (permissions == null || !permissions.contains(required)) {
                sendErrorResponse(response, "You do not have permission: " + required);
                throw new AccessDeniedException("You do not have permission: " + required);
            }

            System.out.println("Permission check passed");

        } catch (AccessDeniedException ex) {
            throw ex; // handled by sendErrorResponse() already
        } catch (Exception e) {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                sendErrorResponse(attrs.getResponse(), "Unexpected error during permission check: " + e.getMessage());
            }
            throw new AccessDeniedException("Unexpected permission error");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message) {
        try {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");

            ErrorResponseDto error = new ErrorResponseDto(
                    message,
                    HttpServletResponse.SC_FORBIDDEN,
                    java.time.LocalDateTime.now().toString()
            );

            String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(error);
            response.getWriter().write(json);
            response.getWriter().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
