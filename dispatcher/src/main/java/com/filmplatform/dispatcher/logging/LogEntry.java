package com.filmplatform.dispatcher.logging;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "logs")
public class LogEntry {

    @Id
    private String id;
    private String method;
    private String uri;
    private int statusCode;
    private String clientIp;
    private LocalDateTime timestamp;
    private long durationMs;

    public LogEntry(String method, String uri, int statusCode, String clientIp, long durationMs) {
        this.method = method;
        this.uri = uri;
        this.statusCode = statusCode;
        this.clientIp = clientIp;
        this.durationMs = durationMs;
        this.timestamp = LocalDateTime.now();
    }
}