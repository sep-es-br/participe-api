package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.Person;

public class PreRegistrationParamDto {
    // private Long id;
    private Long meetingId;
    private Long personId;

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

   
}
