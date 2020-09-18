package br.gov.es.participe.model;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.neo4j.ogm.annotation.typeconversion.DateString;

import java.util.Date;

@RelationshipEntity(type = "CHECKED_IN_AT")
public class CheckedInAt extends Entity {

    @DateString
    private Date time;

    @StartNode
    private Person person;

    @EndNode
    private Meeting meeting;

    public CheckedInAt() {
    }

    public CheckedInAt(Person person, Meeting meeting) {
        this.time = new Date();
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
}
