package br.gov.es.participe.controller.dto;

import java.util.Set;

public class PlanItemParamDto {

    private Long id;
    private String name;
    private String description;
    private PlanParamDto plan;
    private StructureItemParamDto structureItem;
    private FileDto file;
    private PlanItemParamDto parent;
    private Set<Long> localitiesIds;

    public PlanItemParamDto() {

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PlanParamDto getPlan() {
        return plan;
    }

    public void setPlan(PlanParamDto plan) {
        this.plan = plan;
    }

    public StructureItemParamDto getStructureItem() {
        return structureItem;
    }

    public void setStructureItem(StructureItemParamDto structureItem) {
        this.structureItem = structureItem;
    }

    public FileDto getFile() {
        return file;
    }

    public void setFile(FileDto file) {
        this.file = file;
    }

    public PlanItemParamDto getParent() {
        return parent;
    }

    public void setParent(PlanItemParamDto parent) {
        this.parent = parent;
    }

    public Set<Long> getLocalitiesIds() {
        return localitiesIds;
    }

    public void setLocalitiesIds(Set<Long> localitiesIds) {
        this.localitiesIds = localitiesIds;
    }
}
