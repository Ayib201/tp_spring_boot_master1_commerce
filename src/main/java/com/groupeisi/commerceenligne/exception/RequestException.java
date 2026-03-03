package com.groupeisi.commerceenligne.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class RequestException extends RuntimeException {
    private final String message;
    private final HttpStatus status;
}
