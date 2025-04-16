package br.gov.es.participe.model;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Organization extends Evaluator {

    public Organization() {
    }
    
    public Organization(String guid, String name) {
        this.setGuid(guid);
        this.setName(name);
    }

}
