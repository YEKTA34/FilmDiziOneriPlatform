package com.filmplatform.dispatcher.routing;

import org.springframework.stereotype.Service;

@Service
public class RoutingService {

    private static final String AUTH_SERVICE = "http://auth-service:8081";
    private static final String CONTENT_SERVICE = "http://content-service:8082";
    private static final String REVIEW_SERVICE = "http://review-service:8083";

    public String resolveServiceUrl(String requestUri) {
        if (requestUri.startsWith("/auth/")) {
            return AUTH_SERVICE + requestUri;
        } else if (requestUri.startsWith("/content/")) {
            return CONTENT_SERVICE + requestUri;
        } else if (requestUri.startsWith("/reviews/")) {
            return REVIEW_SERVICE + requestUri;
        }
        return null;
    }
}