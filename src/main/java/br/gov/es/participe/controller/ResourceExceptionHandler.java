package br.gov.es.participe.controller;

import br.gov.es.participe.exception.QRCodeGenerateException;

import br.gov.es.participe.exception.ParticipeServiceException;
import br.gov.es.participe.exception.ApiAcessoCidadaoException;
import br.gov.es.participe.exception.ApiOrganogramaException;
import br.gov.es.participe.exception.EvaluatorForbiddenException;
import br.gov.es.participe.exception.NotFoundException;
import br.gov.es.participe.util.dto.MessageDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.MediaType;

@ControllerAdvice
public class ResourceExceptionHandler {

  @Autowired
  private Logger log = Logger.getGlobal();

  @ExceptionHandler(Exception.class)
  public ResponseEntity<MessageDto> handleException(Exception e, HttpServletRequest req) {
    log.log(Level.SEVERE, "Error ", e);
    return ResponseEntity.status(500).body(new MessageDto(500, e.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<MessageDto> handleException(IllegalArgumentException e, HttpServletRequest req) {
    log.log(Level.SEVERE, "Bad Request", e);
    return ResponseEntity.status(400).body(new MessageDto(400, e.getMessage()));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<MessageDto> handleException(IllegalStateException e, HttpServletRequest req) {
    log.log(Level.SEVERE, "Bad Request", e);
    return ResponseEntity.badRequest().body(new MessageDto(HttpStatus.BAD_REQUEST.value(),
                                                           e.getMessage()
    ));
  }

  @ExceptionHandler(IOException.class)
  public ResponseEntity<MessageDto> handleException(IOException e, HttpServletRequest req) {
    log.log(Level.SEVERE, "Error", e);
    return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).body(
      new MessageDto(500, e.getMessage() + " -> " + e.getCause()));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<MessageDto> handleException(RuntimeException e, HttpServletRequest req) {
    log.log(Level.SEVERE, "Error", e);
    return ResponseEntity.status(500).body(new MessageDto(500, e.getMessage()));
  }


  @ExceptionHandler(QRCodeGenerateException.class)
  public ResponseEntity<MessageDto> handleException(QRCodeGenerateException e, HttpServletRequest req) {
    log.log(Level.SEVERE, "Error", e);
    return ResponseEntity.status(400).body(new MessageDto(400, e.getMessage()));
  }

  @ExceptionHandler(ParticipeServiceException.class)
  public ResponseEntity<MessageDto> participeServiceHandler(ParticipeServiceException e) {
    log.log(Level.SEVERE, "Error", e);
    return ResponseEntity.status(400).body(new MessageDto(400, e.getMessage()));
  }

  @ExceptionHandler(ApiAcessoCidadaoException.class)
  public ResponseEntity<MessageDto> handleException(ApiAcessoCidadaoException e) {
    log.log(Level.SEVERE, "Error", e);
    return ResponseEntity.status(500).body(new MessageDto(500, e.getMessage()));
  }

  @ExceptionHandler(ApiOrganogramaException.class)
  public ResponseEntity<MessageDto> handleException(ApiOrganogramaException e) {
    log.log(Level.SEVERE, "Error", e);
    return ResponseEntity.status(500).body(new MessageDto(500, e.getMessage()));
  }

  @ExceptionHandler(EvaluatorForbiddenException.class)
  public ResponseEntity<MessageDto> handleException(EvaluatorForbiddenException e) {
    log.log(Level.SEVERE, "Error", e);
    return ResponseEntity.status(403).body(new MessageDto(403, e.getMessage()));
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<MessageDto> handleException(NotFoundException e){
    log.log(Level.SEVERE, "Error", e);
    return ResponseEntity.status(404).body(new MessageDto(404, e.getMessage()));
  }
}
