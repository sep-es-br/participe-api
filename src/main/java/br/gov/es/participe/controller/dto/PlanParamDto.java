package br.gov.es.participe.controller.dto;

public class PlanParamDto {

    private Long id;
    private String name;
    private StructureParamDto structure;
    private DomainParamDto domain;

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

    public StructureParamDto getStructure() {
        return structure;
    }

    public void setStructure(StructureParamDto structure) {
        this.structure = structure;
    }

    public DomainParamDto getDomain() {
        return domain;
    }

    public void setDomain(DomainParamDto domain) {
        this.domain = domain;
    }
}
