package br.gov.es.participe.controller.dto;

public class LocalityParamDto {

    private Long id;
    private String name;
    private LocalityTypeDto type;
    private DomainDto domains;
    private LocalityParamDto parents;

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

    public DomainDto getDomains() {
        return domains;
    }

    public void setDomains(DomainDto domains) {
        this.domains = domains;
    }

    public LocalityParamDto getParents() {
        return parents;
    }

    public void setParents(LocalityParamDto parents) {
        this.parents = parents;
    }
}
