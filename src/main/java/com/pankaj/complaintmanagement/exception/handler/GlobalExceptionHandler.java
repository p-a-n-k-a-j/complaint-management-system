package com.pankaj.complaintmanagement.exception.handler;

import com.pankaj.complaintmanagement.exception.custom.*;
import com.pankaj.complaintmanagement.exception.payload.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ComplaintNotFoundException.class,
            UserNotFoundException.class,
            UsernameNotFoundException.class,
            UserProfileNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(HttpServletRequest request, RuntimeException ex){
       return buildErrorResponseEntity(request, ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({UnauthorizedActionException.class,BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleUnauthorized(HttpServletRequest request, RuntimeException ex){
     return buildErrorResponseEntity(request, ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(HttpServletRequest request, RuntimeException ex){
    return buildErrorResponseEntity(request, ex, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({UserAlreadyExistsException.class,
            IllegalStateException.class})
    public ResponseEntity<ErrorResponse> handleConflict(HttpServletRequest request, RuntimeException ex){
        return buildErrorResponseEntity(request, ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class

    })
    public ResponseEntity<ErrorResponse> handleBadRequest(HttpServletRequest request, RuntimeException ex){
    return buildErrorResponseEntity(request, ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalServer(HttpServletRequest request,Exception ex){
        return buildErrorResponseEntity(request,  new RuntimeException(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //this is handled exception
    public ResponseEntity<ErrorResponse> buildErrorResponseEntity(HttpServletRequest request, RuntimeException ex, HttpStatus status){
        ErrorResponse errorResponse = new ErrorResponse.Builder()
                .message(ex.getMessage())
                .status(status)
                .path(request.getRequestURI()).build();
        return ResponseEntity.status(status).body(errorResponse);
    }

}
