package com.groupeisi.commerceenligne.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class APIException extends Exception {
    private final String message;
    private final HttpStatus status;
    private final LocalDateTime timestamp;
}
