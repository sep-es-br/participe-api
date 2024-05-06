package br.gov.es.participe.controller.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.gov.es.participe.model.Organization;
import br.gov.es.participe.model.Person;

public class EvaluationSectionsDto {
    
    private Long id;
    private String organizationName;
    private List<String> sectionsNames;
    private List<String> serversNames;
    
    public EvaluationSectionsDto() {

    }

    public EvaluationSectionsDto(Organization organization) {
        this.id = organization.getId();
        this.organizationName = organization.getName();
        this.sectionsNames = new ArrayList<String>(organization.getSectionsNames());
        this.serversNames = getPersonNameFromOrganizationEvaluators(organization.getEvaluators());
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOrganizationName() {
        return organizationName;
    }
    
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
    
    public List<String> getSectionsNames() {
        return sectionsNames;
    }
    
    public void setSectionsNames(List<String> sectionsNames) {
        this.sectionsNames = sectionsNames;
    }
    
    public List<String> getServersNames() {
        return serversNames;
    }
    
    public void setServersNames(List<String> serversNames) {
        this.serversNames = serversNames;
    }

    private List<String> getPersonNameFromOrganizationEvaluators(Set<Person> evaluators) {
        List<String> serversNames = new ArrayList<>();
        
        evaluators.iterator().forEachRemaining((person) -> {
            serversNames.add(person.getName());
        });

        return serversNames;
    }

    
}
