package br.gov.es.participe.controller.dto;

import br.gov.es.participe.util.interfaces.QueryResult;

@QueryResult
public class LocalitiesOptionsDto {

    private Long localityId;
    private String localityName;

    public Long getLocalityId() {
        return localityId;
    }

    public void setLocalityId(Long localityId) {
        this.localityId = localityId;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }
}
