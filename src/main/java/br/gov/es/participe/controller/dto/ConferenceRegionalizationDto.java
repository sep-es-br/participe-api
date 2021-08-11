package br.gov.es.participe.controller.dto;

public class ConferenceRegionalizationDto {

  private final Boolean regionalization;

  public ConferenceRegionalizationDto(Boolean regionalization) {
    this.regionalization = regionalization;
  }

  public Boolean getRegionalization() {
    return regionalization;
  }
}
