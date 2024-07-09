package br.gov.es.participe.controller.dto;

import java.util.Base64;

import org.springframework.data.neo4j.annotation.QueryResult;

import br.gov.es.participe.model.Meeting;
import br.gov.es.participe.model.Person;

@QueryResult
public class AccreditationDto {
    private MeetingDto meeting;
    private PersonDto person;
    private String qrcode;

    public AccreditationDto() {
    }

    public AccreditationDto(Person person, Meeting meeting, byte[] qrcode) {
        if (person == null || meeting == null) return;
        this.meeting = new MeetingDto(meeting,false);
        this.person = new PersonDto(person);
        this.qrcode = Base64.getEncoder().encodeToString(qrcode);
    }

    public MeetingDto getMeeting() {
        return meeting;
    }

    public void setMeeting(MeetingDto meeting) {
        this.meeting = meeting;
    }

    public PersonDto getPerson() {
        return person;
    }

    public void setPerson(PersonDto person) {
        this.person = person;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

}
