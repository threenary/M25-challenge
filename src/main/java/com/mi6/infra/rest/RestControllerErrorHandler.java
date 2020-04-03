package com.n26.infra.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.n26.infra.exceptions.TransactionOutdatedException;
import com.n26.infra.exceptions.UnprocessableEntityException;

@ControllerAdvice
public class RestControllerErrorHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(RestControllerErrorHandler.class);


    @ExceptionHandler(Throwable.class)
    @ResponseBody
    ResponseEntity<?> handleException(final HttpServletRequest request, final Throwable e)
    {
        LOG.error("Internal Error while processing request {}", e);
        return new ResponseEntity<List<MethodArgumentNotValidException>>(HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        return new ResponseEntity<List<MethodArgumentNotValidException>>(HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<?> badParametersRequest(final HttpServletRequest request, final MethodArgumentTypeMismatchException e)
    {
        return new ResponseEntity<List<HttpMessageNotReadableException>>(HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e)
    {
        return new ResponseEntity<List<HttpMessageNotReadableException>>(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(TransactionOutdatedException.class)
    @ResponseBody
    ResponseEntity<?> handleTransactionOutdatedException(final HttpServletRequest request, final TransactionOutdatedException e)
    {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    @ResponseBody
    ResponseEntity<?> handleUnprocessableEntittyException(final HttpServletRequest request, final UnprocessableEntityException e)
    {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    }
    
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    ResponseEntity<?> mediaTypeNotAcceptable()
    {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }
}
