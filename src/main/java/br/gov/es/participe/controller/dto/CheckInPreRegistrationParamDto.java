package br.gov.es.participe.controller.dto;

public class CheckInPreRegistrationParamDto {
    private Long preRegistrationId;
    private Long meetingId;

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public Long getPreRegistrationId() {
        return preRegistrationId;
    }

    public void setPreRegistrationId(Long preRegistrationId) {
        this.preRegistrationId = preRegistrationId;
    }
}
