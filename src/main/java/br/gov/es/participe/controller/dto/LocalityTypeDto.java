package br.gov.es.participe.controller.dto;

import org.springframework.data.neo4j.annotation.QueryResult;

import br.gov.es.participe.model.LocalityType;

@QueryResult
public class LocalityTypeDto {

    private Long id;
    private String name;

    public LocalityTypeDto() {
    }

    public LocalityTypeDto(LocalityType localityType) {
        if (localityType == null) return;

        id = localityType.getId();
        name = localityType.getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
