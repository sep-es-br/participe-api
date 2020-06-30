package br.gov.es.participe.controller.dto;

import java.util.List;

public class LeanLocalityResultDto {
    String regionalizable;
    List<LocalityDto> localities;

    public LeanLocalityResultDto() {
    }

    public LeanLocalityResultDto(String regionalizable, List<LocalityDto> localities) {
        this.regionalizable = regionalizable;
        this.localities = localities;
    }

    public String getRegionalizable() {
        return regionalizable;
    }

    public void setRegionalizable(String regionalizable) {
        this.regionalizable = regionalizable;
    }

    public List<LocalityDto> getLocalities() {
        return localities;
    }

    public void setLocalities(List<LocalityDto> localities) {
        this.localities = localities;
    }
}
