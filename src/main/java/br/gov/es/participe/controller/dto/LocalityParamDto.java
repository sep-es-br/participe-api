package br.gov.es.participe.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LocalityParamDto {

    private Long id;
    private String name;
    private LocalityTypeDto type;
    private DomainParamDto domain;
    private LocalityParamDto parent;

    public LocalityParamDto() {
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

    public LocalityTypeDto getType() {
        return type;
    }

    public void setType(LocalityTypeDto type) {
        this.type = type;
    }

    public DomainParamDto getDomain() {
        return domain;
    }

    public void setDomain(DomainParamDto domain) {
        this.domain = domain;
    }

    public LocalityParamDto getParent() {
        return parent;
    }

    public void setParent(LocalityParamDto parent) {
        this.parent = parent;
    }
}
