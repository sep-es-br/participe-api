package br.gov.es.participe.controller.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class EvaluatorRequestDto {
    
    @NotBlank
    private String organizationGuid;

    @NotEmpty
    private List<String> sectionsGuid;
    
    private List<String> rolesGuid;

    public EvaluatorRequestDto() {

    }

    public String getOrganizationGuid() {
        return organizationGuid;
    }

    public void setOrganizationGuid(String organizationGuid) {
        this.organizationGuid = organizationGuid;
    }

    public List<String> getSectionsGuid() {
        return sectionsGuid;
    }

    public void setSectionsGuid(List<String> sectionsGuid) {
        this.sectionsGuid = sectionsGuid;
    }

    public List<String> getRolesGuid() {
        return rolesGuid;
    }

    public void setRolesGuid(List<String> rolesGuid) {
        this.rolesGuid = rolesGuid;
    }
}
