package br.gov.es.participe.exception;

public class ApiOrganogramaException extends RuntimeException {

    public ApiOrganogramaException(String error) {
        super("Erro ao comunicar com a API do Organograma.\n" + error);
    }

}
