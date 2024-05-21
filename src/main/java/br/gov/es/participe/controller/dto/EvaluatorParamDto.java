package br.gov.es.participe.controller.dto;

import javax.validation.constraints.NotBlank;

public class EvaluatorParamDto {

    @NotBlank
    private String organizationGuid;
    
    @NotBlank
    private String sectionsGuid;
    
    private String rolesGuid;
    
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
    public String getRolesGuid() {
        return rolesGuid;
    }
    public void setRolesGuid(String rolesGuid) {

        this.rolesGuid = rolesGuid;

    } 
}
