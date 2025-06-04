package br.gov.es.participe.controller.dto;

import java.util.*;

import org.springframework.data.neo4j.annotation.QueryResult;

import br.gov.es.participe.model.PreRegistration;

@QueryResult
public class PreRegistrationAuthorityDto {
    private Long id;
    private MeetingDto meeting;
    private PersonDto person;
    private PersonDto madeByPerson;
    private String organization;
    private String role;
    private Date checkInDate;
    private Date preRegistrationDate;
    private String qrcode;

    public PreRegistrationAuthorityDto() {
    }

    public PreRegistrationAuthorityDto(PreRegistration preRegistration,byte[] qrcode) {
        if (preRegistration == null) return;

        this.id = preRegistration.getId();
        this.meeting = new MeetingDto(preRegistration.getMeeting(),false);
        this.person = new PersonDto(preRegistration.getPerson());
        this.madeByPerson = new PersonDto(preRegistration.getMadeBy());
        this.checkInDate = preRegistration.getCheckIn();
        this.preRegistrationDate = preRegistration.getPreRegistration();
        this.qrcode = Base64.getEncoder().encodeToString(qrcode);
        this.organization = preRegistration.getOrganization();
        this.role = preRegistration.getRole();
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

    public PersonDto getMadeByPerson() {
        return madeByPerson;
    }

    public void setMadeByPerson(PersonDto madeByPerson) {
        this.madeByPerson = madeByPerson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PersonDto getPerson() {
        return person;
    }

    public void setPersonId(PersonDto person) {
        this.person = person;
    }

    public MeetingDto getMeeting() {
        return meeting;
    }

    public void setMeetingId(MeetingDto meeting) {
        this.meeting = meeting;
    }

    public Date getCheckIn() {
        return checkInDate;
    }

    public void setCheckIn(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getPreRegistration() {
        return preRegistrationDate;
    }

    public void setPreRegistration(Date preRegistrationDate) {
        this.preRegistrationDate = preRegistrationDate;
    }

    public String getQRCode() {
        return qrcode;
    }

    public void setQRCode(String qrcode) {
        this.qrcode = qrcode;
    }
    

}
