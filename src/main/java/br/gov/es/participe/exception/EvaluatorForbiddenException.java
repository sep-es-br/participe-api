package br.gov.es.participe.exception;

public class EvaluatorForbiddenException extends RuntimeException {
    
    public EvaluatorForbiddenException() {
        super("O usuário não tem permissão para avaliar propostas.");
    }

}
