package br.gov.es.participe.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Section extends Evaluator {

    @Relationship(value = "BELONGS_TO", direction = Relationship.OUTGOING)
    private Organization organization;

    public Section(String guid) {
        this.setGuid(guid);
    }
    
    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
