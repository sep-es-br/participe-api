package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StructureDto {

    private Long id;
    private String name;
    private List<StructureItemDto> items;
    private boolean hasPlan;
    private Boolean regionalization;

    public StructureDto() {
    }

    public StructureDto(Structure structure, boolean loadItems) {
        if (structure == null) return;

        this.id = structure.getId();
        this.name = structure.getName();
        this.hasPlan = structure.getPlans() != null && !structure.getPlans().isEmpty();
        this.regionalization = structure.getRegionalization() != null ?
                                 structure.getRegionalization() : null;
        if (loadItems && structure.getItems() != null && !structure.getItems().isEmpty()) {
            items = new ArrayList<>();
            structure.getItems()
                    .stream()
                    .filter(item -> item.getParent() == null)
                    .collect(Collectors.toList())
                    .forEach(item -> items.add(new StructureItemDto(item, structure, true, true
                    )));
        }
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

    public List<StructureItemDto> getItems() {
        return items;
    }

    public void setItems(List<StructureItemDto> items) {
        this.items = items;
    }

    public boolean isHasPlan() {
        return hasPlan;
    }

    public void setHasPlan(boolean hasPlan) {
        this.hasPlan = hasPlan;
    }

    public Boolean isRegionalization() {
        return regionalization;
    }

    public void setRegionalization(Boolean regionalization) {
        this.regionalization = regionalization;
    }
}
