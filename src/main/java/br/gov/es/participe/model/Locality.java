package br.gov.es.participe.model;

import br.gov.es.participe.controller.dto.LocalityDto;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Locality extends Entity {

    private String name;

    @Relationship(type = "IS_LOCATED_IN")
    private Set<Domain> domains;

    @Relationship(type = "OF_TYPE")
    private LocalityType type;

    @Relationship(type = "IS_LOCATED_IN")
    private Set<Locality> parents;

    @Relationship(type = "IS_LOCATED_IN", direction = Relationship.INCOMING)
    private Set<Locality> children;

    public Locality() {
    }

    public Locality(LocalityDto localityDto) {
        if (localityDto == null) return;

        setId(localityDto.getId());
        name = localityDto.getName();
        type = new LocalityType(localityDto.getType());

        if (localityDto.getParents() != null && !localityDto.getParents().isEmpty()) {
            parents = new HashSet<>();
            localityDto.getParents().forEach(parent -> parents.add(new Locality(parent)));
        }

        if (localityDto.getDomains() != null && !localityDto.getDomains().isEmpty()) {
            domains = new HashSet<>();
            localityDto.getDomains().forEach(domain -> domains.add(new Domain(domain)));
        }

        if (localityDto.getChildren() != null && !localityDto.getChildren().isEmpty()) {
            children = new HashSet<>();
            localityDto.getChildren().forEach(child -> children.add(new Locality(child)));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Domain> getDomains() {
        if (domains == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(domains);
    }

    public void addDomain(Domain domain) {
        if (domains == null) domains = new HashSet<>();
        domains.add(domain);
    }

    public void addDomains(Set<Domain> domains) {
        if (this.domains == null) this.domains = new HashSet<>();
        for (Domain domain : domains) {
            this.domains.add(domain);
        }
    }

    public void removeDomain(Long id) {
        Domain domainToRemove = null;
        for (Domain domain : domains) {
            if (domain.getId().equals(id)) {
                domainToRemove = domain;
                break;
            }
        }

        if (domainToRemove != null) {
            domains.remove(domainToRemove);
        }
    }

    public LocalityType getType() {
        return type;
    }

    public void setType(LocalityType type) {
        this.type = type;
    }

    public Set<Locality> getParents() {
        if (parents == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(parents);
    }

    public void addParent(Locality locality) {
        if (parents == null) parents = new HashSet<>();
        parents.add(locality);
    }

    public Set<Locality> getChildren() {
        if (children == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(children);
    }

    public void addChild(Locality locality) {
        if (children == null) children = new HashSet<>();
        children.add(locality);
    }

    public boolean belongsToDomain(Long idDomain) {
        if (domains == null || domains.isEmpty()) return false;

        for (Domain domain : domains) {
            if (domain.getId().equals(idDomain)) return true;
        }

        return false;
    }
}
