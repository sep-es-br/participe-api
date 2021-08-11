package br.gov.es.participe.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import br.gov.es.participe.controller.dto.StructureDto;
import br.gov.es.participe.controller.dto.StructureParamDto;

@NodeEntity
public class Structure extends Entity implements Serializable {

    private String name;

    @Relationship(type = "COMPOSES", direction = Relationship.INCOMING)
    private Set<StructureItem> items;

    @Relationship(type = "OBEYS", direction = Relationship.INCOMING)
    private Set<Plan> plans;

    private Boolean regionalization;

    public Structure() {
    }

    public Structure(StructureDto structureDto) {
        if (structureDto == null)
            return;

        setId(structureDto.getId());
        this.name = structureDto.getName();
    }

    public Structure(StructureParamDto structureDto) {
        if (structureDto == null)
            return;

        setId(structureDto.getId());
        this.regionalization = structureDto.getRegionalization();
        this.name = structureDto.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<StructureItem> getItems() {
        if (items == null)
            return Collections.emptySet();
        return Collections.unmodifiableSet(items);
    }

    public void addItem(StructureItem item) {
        if (items == null)
            items = new HashSet<>();
        items.add(item);
    }

    public void removePlan(Long id) {
        Plan planToRemove = null;
        if (plans == null) {
            return;
        }
        for (Plan plan : plans) {
            if (plan.getId().equals(id)) {
                planToRemove = plan;
                break;
            }
        }

        if (planToRemove != null) {
            plans.remove(planToRemove);
        }
    }

    public Set<Plan> getPlans() {
        if (plans == null)
            return Collections.emptySet();
        return Collections.unmodifiableSet(plans);
    }

    public Boolean getRegionalization() {
        return regionalization;
    }

    public void setRegionalization(Boolean regionalization) {
        this.regionalization = regionalization;
    }
}
