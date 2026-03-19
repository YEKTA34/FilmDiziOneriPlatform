package com.filmplatform.dispatcher;

import com.filmplatform.dispatcher.logging.LogService;
import com.filmplatform.dispatcher.proxy.ProxyService;
import com.filmplatform.dispatcher.routing.RoutingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
public class DispatcherController {

    private final RoutingService routingService;
    private final ProxyService proxyService;
    private final LogService logService;

    public DispatcherController(RoutingService routingService,
                                ProxyService proxyService,
                                LogService logService) {
        this.routingService = routingService;
        this.proxyService = proxyService;
        this.logService = logService;
    }

    @RequestMapping("/**")
    public ResponseEntity<String> dispatch(
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request) {

        long startTime = System.currentTimeMillis();
        String uri = request.getRequestURI();
        String clientIp = request.getRemoteAddr();

        String targetUrl = routingService.resolveServiceUrl(uri);


        if (targetUrl == null) {
            logService.log(method.name(), uri, 404, clientIp, 0);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Endpoint bulunamadi\"}");
        }

        HttpHeaders headers = new HttpHeaders();
        Collections.list(request.getHeaderNames())
                .forEach(headerName -> headers.add(headerName, request.getHeader(headerName)));

        ResponseEntity<String> response = proxyService.forward(targetUrl, method, headers, body);


        long duration = System.currentTimeMillis() - startTime;
        logService.log(method.name(), uri, response.getStatusCode().value(), clientIp, duration);

        return response;
    }
}