package br.gov.es.participe.model;

import org.apache.commons.lang3.time.DateUtils;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.neo4j.ogm.annotation.typeconversion.DateString;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RelationshipEntity(type = "CHECKED_IN_AT")
public class CheckedInAt extends Entity {

    @StartNode
    private Person person;

    @EndNode
    private Meeting meeting;
    
    @DateString
    private Date time;
    
    private Boolean isAuthority;
    private Boolean isAnnounced;
    private Boolean toAnnounce;
    private String organization;
    private String organizationShort;
    private String role;

    public CheckedInAt() {
    }

    public Boolean getToAnnounce() {
        return toAnnounce;
    }

    public void setToAnnounce(Boolean toAnnounce) {
        this.toAnnounce = toAnnounce;
    }

    public String getOrganizationShort() {
        return organizationShort;
    }

    public void setOrganizationShort(String organizationShort) {
        this.organizationShort = organizationShort;
    }
    
    public CheckedInAt(Person person, Meeting meeting) {
        this.time = new Date();
        this.person = person;
        this.meeting = meeting;
    }

    public CheckedInAt(Person person, Meeting meeting, String timeZone) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of(timeZone));
        String format = localDateTime.format(formatter);

        try {
            this.time = DateUtils.parseDate(format, "dd/MM/yyyy HH:mm:ss");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.person = person;
        this.meeting = meeting;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
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

    public Boolean getIsAnnounced() {
        return isAnnounced;
    }

    public void setIsAnnounced(Boolean isAnnounced) {
        this.isAnnounced = isAnnounced;
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

    

    
}
