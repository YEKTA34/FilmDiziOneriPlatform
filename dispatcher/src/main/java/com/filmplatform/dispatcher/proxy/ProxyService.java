package com.filmplatform.dispatcher.proxy;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProxyService {

    private final RestTemplate restTemplate;

    public ProxyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> forward(String targetUrl, HttpMethod method, HttpHeaders headers, String body) {
        try {
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            return restTemplate.exchange(targetUrl, method, entity, String.class);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("{\"error\": \"Servis su an erisilebilir degil\"}");
        }
    }
}