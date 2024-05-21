package br.gov.es.participe.model;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Section extends Evaluator {
    
    public Section(String guid) {
        this.setGuid(guid);
    }

}
