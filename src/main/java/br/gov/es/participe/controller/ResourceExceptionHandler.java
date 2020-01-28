package br.gov.es.participe.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.gov.es.participe.util.dto.MensagemDto;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MensagemDto> handleException(Exception e, HttpServletRequest req){
        e.printStackTrace();
        MensagemDto mensagemDto = new MensagemDto(500, e.getMessage());
        return ResponseEntity.status(500).body(mensagemDto);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<MensagemDto> handleException(IOException e, HttpServletRequest req){
        e.printStackTrace();
        MensagemDto mensagemDto = new MensagemDto(500, e.getMessage() + " -> " + e.getCause());
        return ResponseEntity.status(500).body(mensagemDto);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MensagemDto> handleException(RuntimeException e, HttpServletRequest req){
        e.printStackTrace();
        MensagemDto mensagemDto = new MensagemDto(500, e.getMessage());
        return ResponseEntity.status(500).body(mensagemDto);
    }
}
