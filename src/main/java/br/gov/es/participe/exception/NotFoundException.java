package br.gov.es.participe.exception;

public class NotFoundException extends RuntimeException {
    
    public NotFoundException() {
        super("Recurso não encontrado.");
    }

    public NotFoundException(String message) {
        super("Recurso não encontrado: " + message);
    }

}
