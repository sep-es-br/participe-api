package br.gov.es.participe.controller.dto;

import java.util.List;

public class EvaluatorsNamesRequestDto {
    private List<String> organizationsGuidList; 
    private List<String> sectionsGuidList;
    private List<String> rolesGuidList;

    public EvaluatorsNamesRequestDto() {
        
    }
   
    public List<String> getOrganizationsGuidList() {
        return organizationsGuidList;
    }

    public void setOrganizationsGuidList(List<String> organizationsGuidList) {
        this.organizationsGuidList = organizationsGuidList;
    }

    public List<String> getSectionsGuidList() {
        return sectionsGuidList;
    }

    public void setSectionsGuidList(List<String> sectionsGuidList) {
        this.sectionsGuidList = sectionsGuidList;
    }

    public List<String> getRolesGuidList() {
        return rolesGuidList;
    }

    public void setRolesGuidList(List<String> rolesGuidList) {
        this.rolesGuidList = rolesGuidList;
    }

}
