package br.gov.es.participe.model;

import br.gov.es.participe.controller.dto.LocalityTypeDto;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@NodeEntity
public class LocalityType extends Entity {
    private String name;

    @Relationship(type = "OF_TYPE", direction = Relationship.INCOMING)
    private Set<Locality> localities;

    public LocalityType(){
    }

    public LocalityType(LocalityTypeDto localityTypeDto) {
        if (localityTypeDto == null) return;

        setId(localityTypeDto.getId());
        name = localityTypeDto.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Locality> getLocalities() {
        return localities;
    }

    public void setLocalities(Set<Locality> localities) {
        this.localities = localities;
    }
}