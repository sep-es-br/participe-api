package br.gov.es.participe.exception;

public class ApiAcessoCidadaoException extends RuntimeException {

    public ApiAcessoCidadaoException(String error) {
        super("Erro ao comunicar com a API do Acesso Cidadão.\n" + error);
    }

}