package br.gov.es.participe.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.gov.es.participe.util.dto.MessageDto;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageDto> handleException(Exception e, HttpServletRequest req){
        e.printStackTrace();
        MessageDto messageDto = new MessageDto(500, e.getMessage());
        return ResponseEntity.status(500).body(messageDto);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageDto> handleException(IllegalArgumentException e, HttpServletRequest req){
        e.printStackTrace();
        MessageDto messageDto = new MessageDto(400, e.getMessage());
        return ResponseEntity.status(400).body(messageDto);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<MessageDto> handleException(IOException e, HttpServletRequest req){
        e.printStackTrace();
        MessageDto messageDto = new MessageDto(500, e.getMessage() + " -> " + e.getCause());
        return ResponseEntity.status(500).body(messageDto);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageDto> handleException(RuntimeException e, HttpServletRequest req){
        e.printStackTrace();
        MessageDto messageDto = new MessageDto(500, e.getMessage());
        return ResponseEntity.status(500).body(messageDto);
    }
}
