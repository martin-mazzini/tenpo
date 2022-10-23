package com.example.tenpo.service.impl;

import com.example.tenpo.domain.RequestLog;
import com.example.tenpo.repository.RequestLogRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestToDatabaseRunnable implements Runnable {



    private final Logger logger = LoggerFactory.getLogger(RequestToDatabaseRunnable.class);
    private RequestLog requestLog;
    private RequestLogRepository requestLogRepository;

    public RequestToDatabaseRunnable(RequestLog requestLog, RequestLogRepository requestLogRepository) {
        this.requestLog = requestLog;
        this.requestLogRepository = requestLogRepository;
    }

    @Override
    public void run() {
        saveRequestLogToDatabase();
    }

    private void saveRequestLogToDatabase() {
        logger.debug("Starting saving request to db");
        requestLogRepository.save(requestLog);
        logger.debug("Finished saving request to db");
    }


}
