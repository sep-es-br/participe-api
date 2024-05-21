package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.Evaluator;

public class EvaluatorDto extends EvaluatorParamDto {
    private Long id;

    public EvaluatorDto() {
    
    }

    // public EvaluatorDto(Evaluator evaluator) {
    //     this.id = evaluator.getId();
    //     this.setOrganizationGuid(evaluator.getOrganization());
    //     this.setSectionsGuid(evaluator.getSections());
    //     this.setServersGuid(evaluator.getServers());
    // }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}


