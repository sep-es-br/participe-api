package br.gov.es.participe.model;

import java.util.Collections;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import br.gov.es.participe.controller.dto.PlanDto;
import br.gov.es.participe.controller.dto.PlanParamDto;

@NodeEntity
public class Plan extends Entity {

    private String name;

    @Relationship(type = "OBEYS")
    private Structure structure;

    @Relationship(type = "APPLIES_TO")
    private Domain domain;

    @Relationship(type = "COMPOSES", direction = Relationship.INCOMING)
    private Set<PlanItem> items;

    public Plan() {
    }

    public Plan(PlanDto planDto) {
        if (planDto == null) return;

        setId(planDto.getId());
        this.name = planDto.getName();
        this.structure = new Structure(planDto.getStructure());
        this.domain = new Domain(planDto.getDomain());
    }

    public Plan(PlanParamDto planParamDto) {
        if (planParamDto == null) return;

        setId(planParamDto.getId());
        this.name = planParamDto.getName();
        this.structure = new Structure(planParamDto.getStructure());
        this.domain = new Domain(planParamDto.getDomain());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Set<PlanItem> getItems() {
        if (items == null) return Collections.emptySet();
        return Collections.unmodifiableSet(items);
    }

}
