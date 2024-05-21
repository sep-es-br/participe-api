package br.gov.es.participe.model;

import org.neo4j.ogm.annotation.NodeEntity;

import br.gov.es.participe.controller.dto.EvaluatorParamDto;

@NodeEntity
public class Evaluator extends Entity {
    
    private String organization;
    private String sections;
    private String servers;
    
    public Evaluator() {

    }

    public Evaluator(EvaluatorParamDto evaluatorParamDto) {
        this.organization = evaluatorParamDto.getOrganizationGuid();
        this.sections = evaluatorParamDto.getSectionsGuid();
        this.servers = evaluatorParamDto.getServersGuid();
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getSections() {
        return sections;
    }

    public void setSections(String sections) {
        this.sections = sections;
    }

    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }

}