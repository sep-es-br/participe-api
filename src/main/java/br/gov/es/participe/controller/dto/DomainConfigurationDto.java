package br.gov.es.participe.controller.dto;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class DomainConfigurationDto {
    
    private String localityTypeName;
    private String planItemTypeName;
    private String planItemAreaTypeName;
    
    // public DomainConfigurationDto() {

    // }
    
    public String getLocalityTypeName() {
        return localityTypeName;
    }

    public void setLocalityTypeName(String localityTypeName) {
        this.localityTypeName = localityTypeName;
    }

    public String getPlanItemTypeName() {
        return planItemTypeName;
    }

    public void setPlanItemTypeName(String planItemTypeName) {
        this.planItemTypeName = planItemTypeName;
    }

    public String getPlanItemAreaTypeName() {
        return planItemAreaTypeName;
    }

    public void setPlanItemAreaTypeName(String planItemAreaTypeName) {
        this.planItemAreaTypeName = planItemAreaTypeName;
    }

}
