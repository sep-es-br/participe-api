package br.gov.es.participe.exception;

public class ErrorHandlingQRCodeException extends RuntimeException {
    
        public ErrorHandlingQRCodeException(String message) {
            super(message);
        }
    
        public ErrorHandlingQRCodeException(String message, Throwable cause) {
            super(message, cause);
        }
        
}
