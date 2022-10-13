package br.gov.es.participe.model;

import java.io.Serializable;
import java.util.Date;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.DateString;


@Node
public abstract class Attend implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  private String from;

  @Relationship(type = "MADE_BY")
  private Person personMadeBy;

  @Relationship(type = "ABOUT")
  private Conference conference;

  @Relationship(type = "ABOUT")
  private PlanItem planItem;

  @Relationship(type = "ABOUT")
  private Locality locality;

  @Relationship(type = "DURING")
  private Meeting meeting;

  @DateString
  private Date time;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public Person getPersonMadeBy() {
    return personMadeBy;
  }

  public void setPersonMadeBy(Person personMadeBy) {
    this.personMadeBy = personMadeBy;
  }

  public PlanItem getPlanItem() {
    return planItem;
  }

  public void setPlanItem(PlanItem planItem) {
    this.planItem = planItem;
  }

  public Locality getLocality() {
    return locality;
  }

  public void setLocality(Locality locality) {
    this.locality = locality;
  }

  public Meeting getMeeting() {
    return meeting;
  }

  public void setMeeting(Meeting meeting) {
    this.meeting = meeting;
  }

  public Conference getConference() {
    return conference;
  }

  public void setConference(Conference conference) {
    this.conference = conference;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }
}
