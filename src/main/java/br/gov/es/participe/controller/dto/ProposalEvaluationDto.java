package br.gov.es.participe.controller.dto;

import org.springframework.data.neo4j.annotation.QueryResult;


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

