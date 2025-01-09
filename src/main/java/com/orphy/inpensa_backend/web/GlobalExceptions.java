package com.orphy.inpensa_backend.web;

import com.orphy.inpensa_backend.exceptions.data.ResourceNotFoundException;
import com.orphy.inpensa_backend.exceptions.data.UnExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptions {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstrainViolation(SQLIntegrityConstraintViolationException exception) {
        logger.warn("Sql Constrain Violation Error", exception);
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setDetail("Sorry, constraint violation");
        return ResponseEntity.of(detail).build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(UnExpectedException.class)
    public ResponseEntity<ProblemDetail> handleUnExpectedException(UnExpectedException exception) {
        logger.error("Unexpected Exception", exception);
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        detail.setDetail("Sorry, Unexpected Error Occurred");
        return ResponseEntity.of(detail).build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFound(ResourceNotFoundException exception) {
        logger.warn("Resource not found", exception);
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        detail.setDetail("Sorry, Resouce not found");
        return ResponseEntity.of(detail).build();
    }
}
