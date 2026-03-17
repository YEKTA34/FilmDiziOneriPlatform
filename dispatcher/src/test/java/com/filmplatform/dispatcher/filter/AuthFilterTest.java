package com.filmplatform.dispatcher.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthFilterTest {

    @InjectMocks
    private AuthFilter authFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Test
    void should_pass_auth_endpoints_without_token() throws Exception {
        when(request.getRequestURI()).thenReturn("/auth/login");
        authFilter.doFilter(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void should_reject_request_without_token() throws Exception {
        when(request.getRequestURI()).thenReturn("/content/films");
        when(request.getHeader("Authorization")).thenReturn(null);
        authFilter.doFilter(request, response, filterChain);
        verify(response, times(1)).sendError(401, "Token bulunamadi");
    }

    @Test
    void should_reject_request_with_invalid_token() throws Exception {
        when(request.getRequestURI()).thenReturn("/content/films");
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        authFilter.doFilter(request, response, filterChain);
        verify(response, times(1)).sendError(403, "Gecersiz token");
    }

    @Test
    void should_pass_request_with_valid_token() throws Exception {
        when(request.getRequestURI()).thenReturn("/content/films");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        authFilter.doFilter(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }
}