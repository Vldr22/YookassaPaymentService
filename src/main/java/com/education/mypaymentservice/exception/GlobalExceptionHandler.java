package com.education.mypaymentservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> handleAllExceptions(Exception e, HttpServletRequest request) {
        log.error("Внутренняя ошибка сервера | URI: {} | Method: {} | Client IP: {} | Error: {} | Stack trace: {}",
                request.getRequestURI(),
                request.getMethod(),
                request.getRemoteAddr(),
                e.getMessage(),
                Arrays.toString(e.getStackTrace())
        );

        return new ResponseEntity<>(
                new ResponseError(
                        "INTERNAL_SERVER_ERROR", e.getMessage().split("\n")[0]),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ResponseError> handleUnauthorizedExceptions(UnauthorizedException e, HttpServletRequest request) {
        log.error("Попытка несанкционированного доступа | URI: {} | Method: {} | Client IP: {} | Error: {} | Stack trace: {}",
                request.getRequestURI(),
                request.getMethod(),
                request.getRemoteAddr(),
                e.getMessage(),
                Arrays.toString(e.getStackTrace())
        );

        return new ResponseEntity<>(
                new ResponseError("UNAUTHORIZED", e.getMessage().split("\n")[0]),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(PaymentServiceException.class)
    public ResponseEntity<ResponseError> handlePaymentServiceException(PaymentServiceException e, HttpServletRequest request) {
        log.error("Введен некорректный запрос | URI: {} | Method: {} | Client IP: {} | Error: {} | Stack trace: {}",
                request.getRequestURI(),
                request.getMethod(),
                request.getRemoteAddr(),
                e.getMessage(),
                Arrays.toString(e.getStackTrace())
        );

        return new ResponseEntity<>(
                new ResponseError("PAYMENT_EXCEPTION", e.getMessage().split("\n")[0]),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseError> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.error("Введен некорректный запрос | URI: {} | Method: {} | Client IP: {} | Error: {} | Stack trace: {}",
                request.getRequestURI(),
                request.getMethod(),
                request.getRemoteAddr(),
                e.getMessage(),
                Arrays.toString(e.getStackTrace())
        );

        return new ResponseEntity<>(
                new ResponseError("BAD_REQUEST", e.getMessage().split("\n")[0]),
                HttpStatus.BAD_REQUEST
        );
    }

}

