package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.Domain;
import br.gov.es.participe.model.Plan;
import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.model.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlanItemDto {

    private Long id;
    private String description;
    private String name;
    private StructureItemDto structureItem;
    private FileDto file;
    private PlanDto plan;
    private List<LocalityDto> localities;
    private PlanItemDto parent;
    private List<PlanItemDto> children;
    private Set<Long> localitiesIds;

    public PlanItemDto() {
    }

    public PlanItemDto(PlanItem planItem, Plan parentPlan, boolean loadChildren) {
        if (planItem == null) return;

        Structure structure = parentPlan != null ? parentPlan.getStructure() : null;

        this.id = planItem.getId();
        this.description = planItem.getDescription();
        this.name = planItem.getName();
        this.structureItem = new StructureItemDto(planItem.getStructureItem(), structure, false, false);
        this.plan = parentPlan != null ? new PlanDto(parentPlan, false) : null;

        if (planItem.getFile() != null) {
            this.file = new FileDto(planItem.getFile());
        }

        Domain domain = this.plan != null && this.plan.getDomain() != null ? new Domain(this.plan.getDomain()) : null;
        if (planItem.getLocalities() != null && !planItem.getLocalities().isEmpty()) {
            this.localities = new ArrayList<>();
            planItem.getLocalities()
                    .stream()
                    .forEach(locality -> this.localities.add(new LocalityDto(locality, domain, false)));
        }

        if (planItem.getParent() != null) {
            this.parent = new PlanItemDto(planItem.getParent(), parentPlan, false);
        }

        if (loadChildren && planItem.getChildren() != null && !planItem.getChildren().isEmpty()) {
            this.children = new ArrayList<>();
            planItem.getChildren().forEach(child -> {
                PlanItemDto childPlanItem = new PlanItemDto(child, null, true);
                if (childPlanItem.getId() != null) {
                    this.children.add(childPlanItem);
                }
            });
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlanDto getPlan() {
        return plan;
    }

    public void setPlan(PlanDto plan) {
        this.plan = plan;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StructureItemDto getStructureItem() {
        return structureItem;
    }

    public void setStructureItem(StructureItemDto structureItem) {
        this.structureItem = structureItem;
    }

    public FileDto getFile() {
        return file;
    }

    public void setFile(FileDto file) {
        this.file = file;
    }

    public List<LocalityDto> getLocalities() {
        return localities;
    }

    public void setLocalities(List<LocalityDto> localities) {
        this.localities = localities;
    }

    public PlanItemDto getParent() {
        return parent;
    }

    public void setParent(PlanItemDto parent) {
        this.parent = parent;
    }

    public List<PlanItemDto> getChildren() {
        return children;
    }

    public void setChildren(List<PlanItemDto> children) {
        this.children = children;
    }

    public Set<Long> getLocalitiesIds() {
        return localitiesIds;
    }

    public void setLocalitiesIds(Set<Long> localitiesIds) {
        this.localitiesIds = localitiesIds;
    }
}
