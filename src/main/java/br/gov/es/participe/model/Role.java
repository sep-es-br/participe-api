package br.gov.es.participe.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Role extends Evaluator {

    @Relationship(value = "BELONGS_TO", direction = Relationship.OUTGOING)
    private Section section;

    public Role(String guid) {
        this.setGuid(guid);
    }
    
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }
}
