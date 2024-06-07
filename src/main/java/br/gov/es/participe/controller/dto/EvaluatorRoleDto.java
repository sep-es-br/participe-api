package br.gov.es.participe.controller.dto;

public class EvaluatorRoleDto extends EvaluatorDataDto {

    private String lotacao;

    public EvaluatorRoleDto(String guid, String name, String lotacao) {
        super(guid, name);
        this.lotacao = lotacao;
    }

    public String getLotacao() {
        return lotacao;
    }

    public void setLotacao(String lotacao) {
        this.lotacao = lotacao;
    }
}
