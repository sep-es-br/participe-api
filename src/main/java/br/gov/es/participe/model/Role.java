package br.gov.es.participe.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Role extends Evaluator {

    @Relationship(value = "BELONGS_TO", direction = Relationship.OUTGOING)
    private Section section;

    private String lotacao;

    public Role() {
    }
    
    public Role(String guid, String name) {
        this.setGuid(guid);
        this.setName(name);
    }
    
    public String getLotacao() {
        return lotacao;
    }

    public void setLotacao(String lotacao) {
        this.lotacao = lotacao;
    }
    
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }
}
