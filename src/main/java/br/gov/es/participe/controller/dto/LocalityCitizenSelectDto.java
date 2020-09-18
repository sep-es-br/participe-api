package br.gov.es.participe.controller.dto;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class LocalityCitizenSelectDto {

    private Long localityTypeId;
    private String localityTypeName;
    private List<LocalitiesOptionsDto> localities;

    public Long getLocalityTypeId() {
        return localityTypeId;
    }

    public void setLocalityTypeId(Long localityTypeId) {
        this.localityTypeId = localityTypeId;
    }

    public String getLocalityTypeName() {
        return localityTypeName;
    }

    public void setLocalityTypeName(String localityTypeName) {
        this.localityTypeName = localityTypeName;
    }

    public List<LocalitiesOptionsDto> getLocalities() {
        return localities;
    }

    public void setLocalities(List<LocalitiesOptionsDto> localities) {
        this.localities = localities;
    }
}
