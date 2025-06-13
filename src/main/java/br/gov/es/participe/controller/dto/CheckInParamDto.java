package br.gov.es.participe.controller.dto;

public class CheckInParamDto {
    private Long personId;
    private Long meetingId;
    private String timeZone;
    private Boolean isAuthority;
    private String organization;
    private String role;
    private Boolean toAnnounce;

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

    public Boolean getIsAuthority() {
        return isAuthority;
    }

    public void setIsAuthority(Boolean isAuthority) {
        this.isAuthority = isAuthority;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getToAnnounce() {
        return toAnnounce;
    }

    public void setToAnnounce(Boolean toAnnounce) {
        this.toAnnounce = toAnnounce;
    }

    
}
