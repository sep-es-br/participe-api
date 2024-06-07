package br.gov.es.participe.exception;

public class QRCodeGenerateException extends RuntimeException {
    
        public QRCodeGenerateException(String message) {
            super(message);
        }
    
        public QRCodeGenerateException(String message, Throwable cause) {
            super(message, cause);
        }
        
}
