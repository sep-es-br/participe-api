package br.gov.es.participe.model;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import br.gov.es.participe.controller.dto.EvaluationSectionsDto;

@NodeEntity
public class Organization extends Entity {
    
    private String name;

    private Set<String> sectionsNames;

    @Relationship(type = "REPRESENTS", direction = Relationship.INCOMING)
    private Set<Person> evaluators;

    public Organization() {

    }

    public Organization(EvaluationSectionsDto evaluationSectionsDto) {
        this.setId(evaluationSectionsDto.getId());
        this.name = evaluationSectionsDto.getOrganizationName();
        this.sectionsNames = new HashSet<String>(evaluationSectionsDto.getSectionsNames());
        // this.evaluators = new HashSet<>(evaluationSectionsDto.getSectionsNames()); -> Talvez dê ruim
    }   

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Set<String> getSectionsNames() {
        return sectionsNames;
    }

    public void setSectionsNames(Set<String> sectionsNames) {
        this.sectionsNames = sectionsNames;
    }

    public Set<Person> getEvaluators() {
        return evaluators;
    }

    public void setEvaluators(Set<Person> evaluators) {
        this.evaluators = evaluators;
    }

}
