package br.gov.es.participe.controller.dto;

public class StructureParamDto {

    private Long id;
    private String name;
    private Boolean regionalization;

    public StructureParamDto() {}

    public StructureParamDto(StructureDto structure) {
        id = structure.getId();
        name = structure.getName();
        regionalization = structure.isRegionalization();
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

    public Boolean getRegionalization() {
        return regionalization;
    }

    public void setRegionalization(Boolean regionalization) {
        this.regionalization = regionalization;
    }
}
