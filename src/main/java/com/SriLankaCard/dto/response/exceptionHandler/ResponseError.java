package com.SriLankaCard.dto.response.exceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;

    @Data
    @AllArgsConstructor
    public class ResponseError {
        private int status;
        private String message;
        private Instant timestamp;
        private String path;

    }


