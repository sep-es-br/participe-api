package br.gov.es.participe.controller.dto;

public class ConferenceNameDto {
    private String conferenceName;

    public ConferenceNameDto() {}

    public ConferenceNameDto(String name) {
        this.conferenceName = name;
    }

    public String getConferenceName() {
        return conferenceName;
    }

    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
    }
}
