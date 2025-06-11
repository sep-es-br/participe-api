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
    
    @Relationship(type = "MADE_BY", direction = Relationship.OUTGOING)
    private Person madeBy;
    
    private Boolean isAuthority;
    
    private String organization;
    private String organizationShort;
    private String role;

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

    public PreRegistration(Meeting meeting, Person madeByPerson, Person representingPerson, String organization, String organizationShort, String role ){
        this.isAuthority = true;
        this.meeting = meeting;
        this.madeBy = madeByPerson;
        this.person = representingPerson;
        this.organization = organization;
        this.organizationShort = organizationShort;
        this.role = role;
        this.created = new Date();

    }

    public String getOrganizationShort() {
        return organizationShort;
    }

    public void setOrganizationShort(String organizationShort) {
        this.organizationShort = organizationShort;
    }

    public Person getMadeBy() {
        return madeBy;
    }

    public void setMadeBy(Person madeBy) {
        this.madeBy = madeBy;
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
