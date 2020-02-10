package br.gov.es.participe.model;

import br.gov.es.participe.controller.dto.DomainDto;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Domain extends Entity {

    private String name;

    @Relationship(type = "IS_LOCATED_IN", direction = Relationship.INCOMING)
    private Set<Locality> localities;

    @Relationship(type = "APPLIES_TO", direction = Relationship.INCOMING)
    private Set<Plan> plans;

    public Domain() {
    }

    public Domain(DomainDto domainDto) {
        if (domainDto == null) return;

        setId(domainDto.getId());
        name = domainDto.getName();
    }

    public Domain(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Locality> getLocalities() {
        if (localities == null) return Collections.emptySet();
        return Collections.unmodifiableSet(localities);
    }

    public void addLocality(Locality locality) {
        if (localities == null) localities = new HashSet<>();
        localities.add(locality);
    }

    public void removeLocality(Long id) {
        Locality localityToRemove = null;
        for (Locality locality : localities) {
            if (locality.getId().equals(id)) {
                localityToRemove = locality;
                break;
            }
        }

        if (localityToRemove != null) {
            localities.remove(localityToRemove);
        }
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
