package br.gov.es.participe.controller.dto;

public class CheckInParamDto {
    private Long personId;
    private Long meetingId;
    private String timeZone;

    public CheckInParamDto() {
    }

    public CheckInParamDto(Long personId, Long meetingId) {
        this.personId = personId;
        this.meetingId = meetingId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
