package br.gov.es.participe.exception;

public class EvaluationSectionsNotFoundException extends RuntimeException {

    public EvaluationSectionsNotFoundException(Long id) {
        super("Não foi encontrado um setor avaliador com id {" + id + "} na base de dados");
    }

}
