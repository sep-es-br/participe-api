package br.gov.es.participe.controller.dto;

public class DomainParamDto {

    private String name;
    private Long id;

    public DomainParamDto() {
    }

    public DomainParamDto(DomainDto domain) {
        id = domain.getId();
        name = domain.getName();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
