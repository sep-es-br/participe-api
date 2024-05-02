package br.gov.es.participe.controller.dto;

import org.springframework.data.neo4j.annotation.QueryResult;

import br.gov.es.participe.model.ProposalEvaluation;

// import java.util.Optional;

public class ProposalEvaluationDto {
    
    private String personName;
    private String description;
  

    public ProposalEvaluationDto() {      
    }


    public String getPersonName() {
        return personName;
    }


    public void setPersonName(String personName) {
        this.personName = personName;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }
}

/*
    private Long id;
    private LocalityDto locality; // microrregiao
    private PlanItemDto planItem; // categoria orçamentaria / desafio
    private PlanItemDto planItemArea; // area temática
    private String description; // texto da proposta (comentário)
    private String status; // "Avaliado / "Não Avaliado"
    // private Boolean includedInLOA; // "incluso na LOA do próximo ano?""
    // includedInLOA == true:
    //unidade orçamentária
    //ação orçamentária
    //plano orçamentário
    //includedInLOA == false:
    //razão -> "Entrega já realizada", "Conclusão prevista no ano vigente", etc.
 */
