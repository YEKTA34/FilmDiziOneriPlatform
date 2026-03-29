package com.filmplatform.dispatcher.routing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoutingServiceTest {

    private RoutingService routingService;

    @BeforeEach
    void setUp() {
        routingService = new RoutingService();
    }

    @Test
    void should_route_content_request_to_content_service() {
        String url = routingService.resolveServiceUrl("/api/films/123");
        assertEquals("http://content-service:8082/api/films/123", url);
    }

    @Test
    void should_route_review_request_to_review_service() {
        String url = routingService.resolveServiceUrl("/api/reviews/123");
        assertEquals("http://review-service:8083/api/reviews/123", url);
    }

    @Test
    void should_route_auth_request_to_auth_service() {
        String url = routingService.resolveServiceUrl("/auth/login");
        assertEquals("http://auth-service:8081/auth/login", url);
    }

    @Test
    void should_return_null_for_unknown_path() {
        String url = routingService.resolveServiceUrl("/unknown/path");
        assertNull(url);
    }
}