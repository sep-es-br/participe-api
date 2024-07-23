package br.gov.es.participe.exception;

public class ApiAcessoCidadaoException extends RuntimeException {

    public ApiAcessoCidadaoException(String error) {
        super(error);
    }

}