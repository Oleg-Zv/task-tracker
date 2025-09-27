package com.zhvavyy.backend.web.handler;

import com.zhvavyy.backend.exception.UnauthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;

import static org.springframework.data.crossstore.ChangeSetPersister.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleValidationExc(BadCredentialsException ex, WebRequest request) {
        log.warn("Invalid credentials:{}, Request details:{}", ex, request.getDescription(false));
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), "Invalid username or password");
        detail.setTitle("Authentication failed");
        detail.setProperty("path", request.getDescription(false));

        return detail;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ProblemDetail handleUnauthorizedExc(UnauthorizedException ex, WebRequest request) {
        log.warn("Unauthorized access: {}, Details: {}", ex, request.getDescription(false));
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), ex.getMessage());
        detail.setProperty("path", request.getDescription(false));
        return detail;
    }


    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeneidExc(AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied: {}, Details: {}", ex, request.getDescription(false));
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), "You are not authorized to access this resource");
        detail.setTitle("Access denied!");
        detail.setProperty("path", request.getDescription(false));
        return detail;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFoundException(NotFoundException e, WebRequest request) {
        log.warn("Not found: {}, Request details: {}", e, request.getDescription(false));
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), e.getMessage());
        detail.setTitle("Not found!");
        detail.setProperty("path", request.getDescription(false));
        return detail;
    }
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ExpiredJwtException.class)
    public ProblemDetail handleExpiredJwtException(ExpiredJwtException e, WebRequest request) {
        log.warn("Expired: {}, Request details: {}", e, request.getDescription(false));
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), "The JWT token has expired");
        detail.setTitle("Expired!");
        detail.setProperty("path", request.getDescription(false));
        return detail;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleUnknownExc(Exception e, WebRequest request){
        log.error("Unknown: {}, Request details: {}", e, request.getDescription(false));
        return ProblemDetail.forStatusAndDetail
                (HttpStatusCode.valueOf(500),
                        "Unknown internal server error.");

    }




}
