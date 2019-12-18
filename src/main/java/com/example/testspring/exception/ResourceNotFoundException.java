package com.example.testspring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * HttpStatus Throw implementation for inline usage. Example:
 * <code>sectionRepository.findById(sectionId).map().orElseThrow(() -> new ResourceNotFoundException("reason"))<code/>
 */

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}