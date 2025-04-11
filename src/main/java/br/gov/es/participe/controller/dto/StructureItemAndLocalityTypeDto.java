package br.gov.es.participe.controller.dto;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class StructureItemAndLocalityTypeDto {
    
    private String structureItemName;
    private String localityTypeName;

    public String getStructureItemName() {
        return structureItemName;
    }
    public void setStructureItemName(String structureItemName) {
        this.structureItemName = structureItemName;
    }
    public String getLocalityTypeName() {
        return localityTypeName;
    }
    public void setLocalityTypeName(String localityTypeName) {
        this.localityTypeName = localityTypeName;
    }

    
}