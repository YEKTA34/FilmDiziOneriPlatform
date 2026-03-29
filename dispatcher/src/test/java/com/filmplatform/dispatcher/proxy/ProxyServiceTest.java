package com.filmplatform.dispatcher.proxy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProxyServiceTest {

    @InjectMocks
    private ProxyService proxyService;

    @Mock
    private RestTemplate restTemplate;

    @Test
    void should_forward_request_and_return_response() {
        String targetUrl = "http://content-service:8082/content/films";
        HttpHeaders headers = new HttpHeaders();
        String body = null;

        ResponseEntity<String> mockResponse = new ResponseEntity<>("{}", HttpStatus.OK);
        when(restTemplate.exchange(
                eq(targetUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(mockResponse);

        ResponseEntity<String> result = proxyService.forward(targetUrl, HttpMethod.GET, headers, body);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void should_return_503_when_service_unavailable() {
        String targetUrl = "http://content-service:8082/content/films";
        HttpHeaders headers = new HttpHeaders();

        when(restTemplate.exchange(
                eq(targetUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new RuntimeException("Service unavailable"));

        ResponseEntity<String> result = proxyService.forward(targetUrl, HttpMethod.GET, headers, null);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, result.getStatusCode());
    }
}