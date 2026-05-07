package com.bank.account.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {

    private String errorCode;

    private String message;

    private LocalDateTime timestamp;
}