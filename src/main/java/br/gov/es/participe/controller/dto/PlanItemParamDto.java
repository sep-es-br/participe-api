package br.gov.es.participe.controller.dto;

import java.util.Set;

public class PlanItemParamDto {

    private Long id;
    private String description;
    private String name;
    private StructureItemParamDto structureItem;
    private PlanParamDto plan;
    private PlanItemParamDto parent;
    private FileDto file;
    private Set<Long> localitiesIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlanParamDto getPlan() {
        return plan;
    }

    public void setPlan(PlanParamDto plan) {
        this.plan = plan;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileDto getFile() {
        return file;
    }

    public void setFile(FileDto file) {
        this.file = file;
    }

    public StructureItemParamDto getStructureItem() {
        return structureItem;
    }

    public void setStructureItem(StructureItemParamDto structureItem) {
        this.structureItem = structureItem;
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
