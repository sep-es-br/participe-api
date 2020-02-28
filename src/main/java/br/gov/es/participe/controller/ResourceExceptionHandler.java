package br.gov.es.participe.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.gov.es.participe.util.dto.MessageDto;

@ControllerAdvice
public class ResourceExceptionHandler {

    @Autowired
    private Logger log;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageDto> handleException(Exception e, HttpServletRequest req){
        log.error("Error ", e);
        return ResponseEntity.status(500).body(new MessageDto(500, e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageDto> handleException(IllegalArgumentException e, HttpServletRequest req){
        log.error("Bad Request", e);
        return ResponseEntity.status(400).body(new MessageDto(400, e.getMessage()));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<MessageDto> handleException(IOException e, HttpServletRequest req){
        log.error("Error", e);
        return ResponseEntity.status(500).body(new MessageDto(500, e.getMessage() + " -> " + e.getCause()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageDto> handleException(RuntimeException e, HttpServletRequest req){
        log.error("Error", e);
        return ResponseEntity.status(500).body(new MessageDto(500, e.getMessage()));
    }
}
