package com.filmplatform.dispatcher.logging;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private final MongoTemplate mongoTemplate;

    public LogService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void log(String method, String uri, int statusCode, String clientIp, long durationMs) {
        LogEntry entry = new LogEntry(method, uri, statusCode, clientIp, durationMs);
        mongoTemplate.save(entry);
    }
}