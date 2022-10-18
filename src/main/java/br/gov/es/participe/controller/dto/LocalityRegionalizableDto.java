package br.gov.es.participe.controller.dto;

import br.gov.es.participe.util.interfaces.QueryResult;

@QueryResult
public class LocalityRegionalizableDto {
    private String locality;
    private Long superLocalityId;
    private String superLocality;
    private String regionalizable;

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getRegionalizable() {
        return regionalizable;
    }

    public void setRegionalizable(String regionalizable) {
        this.regionalizable = regionalizable;
    }

    public Long getSuperLocalityId() {
        return superLocalityId;
    }

    public void setSuperLocalityId(Long superLocalityId) {
        this.superLocalityId = superLocalityId;
    }

    public String getSuperLocality() {
        return superLocality;
    }

    public void setSuperLocality(String superLocality) {
        this.superLocality = superLocality;
    }
}
