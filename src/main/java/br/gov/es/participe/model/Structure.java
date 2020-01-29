package br.gov.es.participe.model;

import br.gov.es.participe.controller.dto.StructureDto;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Structure extends Entity {

    private String name;

    @Relationship(type = "BELONGS_TO", direction = Relationship.INCOMING)
    private Set<StructureItem> items;

    @Relationship(type = "OBEYS", direction = Relationship.INCOMING)
    private Set<Plan> plans;

    public Structure() {
    }

    public Structure(StructureDto structureDto) {
        if (structureDto == null) return;

        setId(structureDto.getId());
        this.name = structureDto.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<StructureItem> getItems() {
        if (items == null) return Collections.emptySet();
        return Collections.unmodifiableSet(items);
    }

    public void addItem(StructureItem item) {
        if (items == null) items = new HashSet<>();
        items.add(item);
    }

    public Set<Plan> getPlans() {
        if (plans == null) return Collections.emptySet();
        return Collections.unmodifiableSet(plans);
    }

    public void addPlan(Plan plan) {
        if (plans == null) plans = new HashSet<>();
        plans.add(plan);
    }

    public void removePlan(Long id) {
        Plan planToRemove = null;
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
}
