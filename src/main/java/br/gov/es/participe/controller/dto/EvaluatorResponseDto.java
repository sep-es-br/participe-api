package br.gov.es.participe.controller.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.neo4j.annotation.QueryResult;

import br.gov.es.participe.model.Organization;
import br.gov.es.participe.model.Role;
import br.gov.es.participe.model.Section;

@QueryResult
public class EvaluatorResponseDto {
    
    private Long id;
    private String organizationGuid;
    private List<String> sectionsGuid;
    private List<String> rolesGuid;

    public EvaluatorResponseDto() {

    }

    public EvaluatorResponseDto(Long id, String organizationGuid, List<String> sectionsGuid, List<String> rolesGuid) {
        this.id = id;
        this.organizationGuid = organizationGuid;
        this.sectionsGuid = sectionsGuid;
        this.rolesGuid = rolesGuid;
    }

    public EvaluatorResponseDto(Organization evaluatorOrganization, Set<Section> evaluatorSections, Set<Role> evaluatorRoles) {
        this.id = evaluatorOrganization.getId();
        this.organizationGuid = evaluatorOrganization.getGuid();
        this.sectionsGuid = this.getSectionsGuidFromSectionSet(evaluatorSections);
        this.rolesGuid = this.getRolesGuidFromRolesSet(evaluatorRoles);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    private List<String> getSectionsGuidFromSectionSet(Set<Section> evaluatorSections) {
        List<String> sectionsGuid = new ArrayList<String>(); 
        
        evaluatorSections.iterator().forEachRemaining((section) -> {
            sectionsGuid.add(section.getGuid());
        });

        return sectionsGuid;
    }

    public List<String> getRolesGuid() {
        return rolesGuid;
    }

    public void setRolesGuid(List<String> rolesGuid) {
        this.rolesGuid = rolesGuid;
    }

    private List<String> getRolesGuidFromRolesSet(Set<Role> evaluatorRoles) {
        List<String> rolesGuid = new ArrayList<String>();

        evaluatorRoles.iterator().forEachRemaining((role) -> {
            rolesGuid.add(role.getGuid());
        });

        return rolesGuid;
    }

}
