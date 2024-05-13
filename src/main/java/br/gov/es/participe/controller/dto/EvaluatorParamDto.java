package br.gov.es.participe.controller.dto;

import javax.validation.constraints.NotBlank;

public class EvaluatorParamDto {

    @NotBlank
    private String organizationGuid;
    
    @NotBlank
    private String sectionsGuid;
    
    private String serversGuid;
    
    public EvaluatorParamDto() {

    }
    
    public String getOrganizationGuid() {
        return organizationGuid;
    }
    public void setOrganizationGuid(String organizationGuid) {
        this.organizationGuid = organizationGuid;
    }
    public String getSectionsGuid() {
        return sectionsGuid;
    }
    public void setSectionsGuid(String sectionsGuid) {
        this.sectionsGuid = sectionsGuid;
    }
    public String getServersGuid() {
        return serversGuid;
    }
    public void setServersGuid(String serversGuid) {

        this.serversGuid = serversGuid;

    } 
}
