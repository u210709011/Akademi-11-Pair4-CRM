package com.etiya.crm.productservice.business.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.etiya.crm.productservice.constants.LogMessages;
import com.etiya.crm.productservice.constants.MessageKeys;
import com.etiya.crm.shared.contracts.error.AbstractDownstreamExceptionHandler;
import com.etiya.crm.shared.contracts.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends AbstractDownstreamExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource, ObjectMapper objectMapper) {
        super(objectMapper);
        this.messageSource = messageSource;
    }

    @Override
    protected String downstreamCallFailedMessage() {
        return resolve(MessageKeys.DOWNSTREAM_CALL_FAILED);
    }

    @Override
    protected String downstreamUnavailableMessage() {
        return resolve(MessageKeys.DOWNSTREAM_UNAVAILABLE);
    }

    // B-15: HttpRequestMethodNotSupportedException gibi framework istisnalari ozel bir handler
    // bulamayinca @ExceptionHandler(Exception.class) dalina duşup 500'e sizardi - artik bu 5
    // hook'u AbstractDownstreamExceptionHandler'daki ilgili handler'lar kullaniyor.
    @Override
    protected String parameterTypeMismatchMessage(String parameterName) {
        return resolve(MessageKeys.PARAMETER_TYPE_MISMATCH, parameterName);
    }

    @Override
    protected String invalidRequestParameterMessage() {
        return resolve(MessageKeys.INVALID_REQUEST_PARAMETER);
    }

    @Override
    protected String missingParameterMessage(String parameterName) {
        return resolve(MessageKeys.MISSING_REQUEST_PARAMETER, parameterName);
    }

    @Override
    protected String methodNotSupportedMessage(String httpMethod) {
        return resolve(MessageKeys.METHOD_NOT_SUPPORTED, httpMethod);
    }

    @Override
    protected String routeNotFoundMessage() {
        return resolve(MessageKeys.ROUTE_NOT_FOUND);
    }

    // NOT: yeni "...NotFoundException" eklendikçe buraya listeye eklenecek.
    @ExceptionHandler({ ProductSpecNotFoundException.class, ProductNotFoundException.class,
            ProductOfferingNotFoundException.class, ProductCatalogNotFoundException.class,
            ProductCatalogOfferingNotFoundException.class, CampaignNotFoundException.class,
            ProductRelationNotFoundException.class,
            ProductSpecResourceSpecNotFoundException.class,
            ProductSpecServiceSpecNotFoundException.class,
            ProductCharacteristicValueNotFoundException.class,
            ResourceSpecNotFoundException.class,
            ServiceSpecNotFoundException.class,
            CharacteristicNotFoundException.class,
            CharacteristicValueNotFoundException.class,
            ProductOfferingRelationNotFoundException.class,
            CampaignOfferingNotFoundException.class,
            LookupValueNotFoundException.class })
    public ResponseEntity<ErrorResponse> handleNotFound(BusinessException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex, request);
    }

    @ExceptionHandler(ProductOfferingCharUseDuplicateException.class)
    public ResponseEntity<ErrorResponse> handleConflict(BusinessException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
                                                                   HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), resolve(MessageKeys.VALIDATION_FAILED),
                request.getRequestURI(), errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error(LogMessages.UNEXPECTED_ERROR, ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, resolve(MessageKeys.UNEXPECTED_ERROR), request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, BusinessException ex, HttpServletRequest request) {
        return build(status, resolve(ex.getMessageKey(), ex.getArgs()), request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.of(status.value(), status.getReasonPhrase(), message,
                request.getRequestURI());
        return ResponseEntity.status(status).body(errorResponse);
    }

    private String resolve(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}