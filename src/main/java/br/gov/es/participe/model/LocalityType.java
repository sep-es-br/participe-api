package br.gov.es.participe.model;

import br.gov.es.participe.controller.dto.LocalityTypeDto;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Node;
import java.io.Serializable;
import java.util.Set;

@Node
public class LocalityType extends Entity implements Serializable {
    private String name;

    @Relationship(type = "OF_TYPE", direction = Relationship.Direction.INCOMING)
    private Set<Locality> localities;
    
    @Relationship(type = "REGIONALIZABLE", direction = Relationship.Direction.INCOMING)
    private Set<Plan> plans;

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
