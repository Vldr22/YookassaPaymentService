package com.education.mypaymentservice.exception;

import com.education.mypaymentservice.model.common.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResponse<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Ошибка валидации данных"
        );
        problemDetail.setProperty("validationError", errors);
        return CommonResponse.error(problemDetail);
    }


    @ExceptionHandler(UnauthorizedException.class)
    public CommonResponse<ProblemDetail> handleUnauthorizedExceptions(UnauthorizedException e, HttpServletRequest request) {
        logError(e, request);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setDetail(e.getMessage() + ": " + e.getResource());
        return CommonResponse.error(problemDetail);
    }

    @ExceptionHandler(ForbiddenException.class)
    public CommonResponse<ProblemDetail> handleForbiddenExceptions(ForbiddenException e, HttpServletRequest request) {
        logError(e, request);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setDetail("Проблема прав доступа " + e.getMessage());
        return CommonResponse.error(problemDetail);
    }

    @ExceptionHandler(PaymentServiceException.class)
    public CommonResponse<ProblemDetail> handlePaymentServiceException(PaymentServiceException e, HttpServletRequest request) {
        logError(e, request);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setDetail("Ошибка в запросе: " + e.getMessage());
        return CommonResponse.error(problemDetail);
    }

    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public CommonResponse<ProblemDetail> handleNotFoundException(ChangeSetPersister.NotFoundException e, HttpServletRequest request) {
        logError(e, request);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setDetail("Запрашиваемый ресурс не найден: " + e.getMessage());
        return CommonResponse.error(problemDetail);
    }

    @ExceptionHandler(BadRequestException.class)
    public CommonResponse<ProblemDetail> handleBadRequestExceptions(Exception e, HttpServletRequest request) {
        logError(e, request);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setDetail(MessageFormat.format("Некорректный запрос: {0}", e.getMessage()));
        return CommonResponse.error(problemDetail);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public CommonResponse<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        logError(e, request);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setDetail(MessageFormat.format("Некорректный запрос: {0}", e.getMessage()));
        return CommonResponse.error(problemDetail);
    }

    @ExceptionHandler(RuntimeException.class)
    public CommonResponse<ProblemDetail> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        logError(e, request);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_IMPLEMENTED);
        problemDetail.setDetail("Произошла неизвестная ошибка. Обратитесь в службу поддержки.");
        return CommonResponse.error(problemDetail);
    }

    private void logError(Exception e, HttpServletRequest request) {
        log.error("Ошибка! | URI: {} | Method: {} | Client IP: {} | Error: {} ",
                request.getRequestURI(),
                request.getMethod(),
                request.getRemoteAddr(),
                e.getMessage()
        );
    }

}

