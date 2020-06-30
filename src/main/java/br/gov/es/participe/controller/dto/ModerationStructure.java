package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.model.StructureItem;

public class ModerationStructure {
    PlanItemDto planItem;
    StructureItemDto structureItem;

    public ModerationStructure() {
    }

    public ModerationStructure(PlanItemDto planItem, StructureItemDto structureItem) {
        this.planItem = planItem;
        this.structureItem = structureItem;
    }

    public ModerationStructure(PlanItem planItem) {
        this.planItem = new PlanItemDto();
        this.structureItem = new StructureItemDto();
        this.planItem.setId(planItem.getId());
        this.planItem.setName(planItem.getName());
    }

    public PlanItemDto getPlanItem() {
        return planItem;
    }

    public void setPlanItem(PlanItemDto planItem) {
        this.planItem = planItem;
    }

    public StructureItemDto getStructureItem() {
        return structureItem;
    }

    public void setStructureItem(StructureItemDto structureItem) {
        this.structureItem = structureItem;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ModerationStructure{");
        sb.append("planItem=").append(planItem);
        sb.append(", structureItem=").append(structureItem);
        sb.append('}');
        return sb.toString();
    }

    public void setStructureItemWithEntity(StructureItem structureItem) {
        this.structureItem.setId(structureItem.getId());
        this.structureItem.setName(structureItem.getName());
    }
}
