package br.gov.es.participe.model;

import br.gov.es.participe.controller.dto.DomainDto;
import br.gov.es.participe.controller.dto.DomainParamDto;
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

    public Domain(DomainParamDto domainParamDto) {
        if (domainParamDto == null) return;
        setId(domainParamDto.getId());
        name = domainParamDto.getName();
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

    public Locality getRootLocality() {
        if (this.getLocalities().isEmpty()) {
            return null;
        }

        return this.getLocalities()
                .stream()
                .filter(l -> l.getParents().isEmpty()).findFirst()
                .orElse(null);
    }
}
