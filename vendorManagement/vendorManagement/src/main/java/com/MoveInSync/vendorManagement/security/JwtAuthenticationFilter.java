package com.MoveInSync.vendorManagement.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        final String username = jwtService.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {

                // Build attributes from the authenticated user instead of JWT claims
                CustomUserDetails cud = (CustomUserDetails) userDetails;
                var user = cud.getUser();
                var vendor = user.getVendor();
                var roleEntity = vendor != null ? vendor.getRole() : null;
                Set<com.MoveInSync.vendorManagement.entity.Permission> perms =
                        roleEntity != null ? roleEntity.getPermissions() : java.util.Set.of();
                List<String> permissions = perms.stream().map(com.MoveInSync.vendorManagement.entity.Permission::getName).collect(Collectors.toList());
                String role = roleEntity != null ? roleEntity.getName() : null;

                // ✅ Make attributes accessible to controllers
                request.setAttribute("permissions", permissions);
                request.setAttribute("role", role);
                request.setAttribute("vendorId", vendor != null ? vendor.getVendorId() : null);
                request.setAttribute("vendorLevel", vendor != null ? vendor.getLevel() : null);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
