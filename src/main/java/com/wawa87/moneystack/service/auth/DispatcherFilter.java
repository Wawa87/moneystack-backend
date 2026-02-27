package com.wawa87.moneystack.service.auth;

import jakarta.servlet.*;

import java.io.IOException;

public class DispatcherFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletResponse.getWriter().println("DispatcherFilter...");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
