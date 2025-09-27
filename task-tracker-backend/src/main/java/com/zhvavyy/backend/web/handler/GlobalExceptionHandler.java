package com.zhvavyy.backend.web.handler;

import com.zhvavyy.backend.exception.TaskNotFoundException;
import com.zhvavyy.backend.exception.UnauthorizedException;
import com.zhvavyy.backend.exception.UserNotFoundCustomException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleValidationExc(BadCredentialsException ex, WebRequest request) {
        log.warn("Invalid credentials:{}, Request details:{}", ex, request.getDescription(false));
       return getProblemDetail(HttpStatusCode.valueOf(401),
               "Invalid username or password",
               "Authetificated failed:",request);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeneidExc(AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied: {}, Details: {}", ex, request.getDescription(false));
    return getProblemDetail(HttpStatusCode.valueOf(403),
                "You are not authorized to access this resource",
                "Access denied!", request);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ProblemDetail handleNotFoundException(UsernameNotFoundException e, WebRequest request) {
        log.warn("Not found: {}, Request details: {}", e, request.getDescription(false));
       return getProblemDetail(HttpStatusCode.valueOf(404),
                      e.getMessage(),
                "Not found!",request);

    }
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(SignatureException.class)
    public ProblemDetail handleSignatureJwtException(SignatureException e, WebRequest request) {
        log.warn("Signature exc: {}, Request details: {}", e, request.getDescription(false));
       return getProblemDetail(HttpStatusCode.valueOf(403),
               "The JWT signature is invalid",
                "Signature invalid!", request);
    }
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ExpiredJwtException.class)
    public ProblemDetail handleExpiredJwtException(ExpiredJwtException e, WebRequest request) {
        log.warn("Expired: {}, Request details: {}", e, request.getDescription(false));
       return getProblemDetail(HttpStatusCode.valueOf(403),
                "The JWT token has expired",
                "Expired!", request);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UsernameAlreadyException.class)
    public ProblemDetail handleDataAlreadyExistException(UsernameAlreadyException e, WebRequest request) {
        log.warn("Conflict data: {}, Request details: {}", e, request.getDescription(false));
       return getProblemDetail(HttpStatusCode.valueOf(409),
                e.getMessage(),
                "Conflict!",request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PasswordIncorrectException.class)
    public ProblemDetail handlePasswordNoMatchException(PasswordIncorrectException e, WebRequest request) {
        log.warn("Password Invalid: {}, Request details: {}", e, request.getDescription(false));
       return getProblemDetail(HttpStatusCode.valueOf(400),
                e.getMessage(),
                "Password Incorrect!",request);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundCustomException.class)
    public ProblemDetail handleCustomNotFoundException(UserNotFoundCustomException e, WebRequest request) {
        log.warn("User invalid: {}, Request details: {}", e, request.getDescription(false));
       return getProblemDetail(HttpStatusCode.valueOf(404),
                e.getMessage(),
                "User Not Found!",request);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TaskNotFoundException.class)
    public ProblemDetail handleTaskNotFoundException(TaskNotFoundException e, WebRequest request) {
        log.warn("Task invalid: {}, Request details: {}", e, request.getDescription(false));
       return getProblemDetail(HttpStatusCode.valueOf(404),
                e.getMessage(),
                "Task Not Found!",request);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ProblemDetail handleUnauthorizedExc(UnauthorizedException ex, WebRequest request) {
        log.warn("Unauthorized access: {}, Details: {}", ex, request.getDescription(false));
       return getProblemDetail(HttpStatusCode.valueOf(401),
                ex.getMessage(),
                "Unauthorized user",request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleUnknownExc(Exception e, WebRequest request){
        log.error("Unknown: {}, Request details: {}", e, request.getDescription(false));
        return ProblemDetail.forStatusAndDetail
                (HttpStatusCode.valueOf(500),
                        "Unknown internal server error.");

    }

    private ProblemDetail getProblemDetail(HttpStatusCode status, String message,String title, WebRequest request){
        ProblemDetail detail= ProblemDetail.forStatusAndDetail(status,message);
        detail.setTitle(title);
        detail.setProperty("path", request.getDescription(false));
        return detail;
    }
}
