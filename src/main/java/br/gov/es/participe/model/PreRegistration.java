package br.gov.es.participe.model;

import org.neo4j.ogm.annotation.*;
import org.neo4j.ogm.annotation.typeconversion.DateString;

import br.gov.es.participe.controller.dto.PreRegistrationParamDto;

import java.io.*;
import java.util.*;

@NodeEntity
public class PreRegistration extends Entity implements Serializable {
    
    @Relationship(type = "PRE_REGISTRATION", direction = Relationship.OUTGOING)
    private Meeting meeting;

    @Relationship(type = "PRE_REGISTRATION", direction = Relationship.OUTGOING)
    private Person person;

    @DateString
    private Date created;

    @DateString
    private Date checkin;

    public PreRegistration() {

    }

    public PreRegistration(PreRegistration preRegistration) {
        this.meeting = preRegistration.meeting;
        this.person = preRegistration.person;
        this.created = preRegistration.created;
        this.checkin = preRegistration.checkin;

    }

    public PreRegistration(Meeting meeting, Person person ){
        this.meeting = meeting;
        this.person = person;
        this.created = new Date();

    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public Date getCheckIn() {
        return checkin;
    }

    public void setCheckIn(Date checkin) {
        this.checkin = checkin;
    }

    public Date getPreRegistration() {
        return created;
    }

    public void setPreRegistration(Date created) {
        this.created = created;
    }
    
}
