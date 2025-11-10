package com.MoveInSync.vendorManagement.authorization;

import com.MoveInSync.vendorManagement.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private JwtService jwtService;

    @Before("@annotation(requiresPermission)")
    public void checkPermission(JoinPoint joinPoint, RequiresPermission requiresPermission) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new AccessDeniedException("No request context available");
        }
        HttpServletRequest request = attrs.getRequest();

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AccessDeniedException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        Claims claims = jwtService.extractAllClaims(token);

        List<String> permissions = (List<String>) claims.get("permissions");
        String required = requiresPermission.value();

        if (permissions == null || !permissions.contains(required)) {
            throw new AccessDeniedException("You do not have permission: " + required);
        }
    }
}
