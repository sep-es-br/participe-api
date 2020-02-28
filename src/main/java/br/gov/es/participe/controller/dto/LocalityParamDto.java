package br.gov.es.participe.controller.dto;

public class LocalityParamDto {

    private Long id;
    private String name;
    private LocalityTypeDto type;
    private DomainParamDto domain;
    private LocalityParamDto parent;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalityTypeDto getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
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
