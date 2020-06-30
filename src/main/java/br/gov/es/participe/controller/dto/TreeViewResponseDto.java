package br.gov.es.participe.controller.dto;

import java.util.ArrayList;
import java.util.List;

public class TreeViewResponseDto {
    StructureItemDto structureItem;
    List<PlanItemDto> planItems;

    public TreeViewResponseDto(StructureItemDto structureItem, List<PlanItemDto> planItems) {
        this.structureItem = structureItem;
        this.planItems = new ArrayList<>();
        planItems.forEach(pi -> this.planItems.add(pi));
    }

    public StructureItemDto getStructureItem() {
        return structureItem;
    }

    public void setStructureItem(StructureItemDto structureItem) {
        this.structureItem = structureItem;
    }

    public List<PlanItemDto> getPlanItems() {
        return planItems;
    }

    public void setPlanItems(List<PlanItemDto> planItems) {
        this.planItems = planItems;
    }
}
