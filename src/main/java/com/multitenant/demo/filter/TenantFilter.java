package com.multitenant.demo.filter;

import com.multitenant.demo.dto.TenantContext;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TenantFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String activeTenant = request.getHeader("tenantId");
        TenantContext.setActiveTenant(activeTenant);
        filterChain.doFilter(request, response);
        TenantContext.clearActiveTenant();
    }
}
