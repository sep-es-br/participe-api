package br.gov.es.participe.controller.dto;

import java.util.Date;

public class CommentParamDto {

  private Long id;
  private Date time;
  private String text;
  private String type;
  private String status;
  private String from;
  private PersonDto person;
  private Long planItem;
  private Long locality;
  private Long meeting;
  private Long conference;
  private String classification;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public PersonDto getPerson() {
    return person;
  }

  public void setPerson(PersonDto person) {
    this.person = person;
  }

  public Long getPlanItem() {
    return planItem;
  }

  public void setPlanItem(Long planItem) {
    this.planItem = planItem;
  }

  public Long getLocality() {
    return locality;
  }

  public void setLocality(Long locality) {
    this.locality = locality;
  }

  public Long getMeeting() {
    return meeting;
  }

  public void setMeeting(Long meeting) {
    this.meeting = meeting;
  }

  public Long getConference() {
    return conference;
  }

  public void setConference(Long conference) {
    this.conference = conference;
  }

  public String getClassification() {
    return classification;
  }

  public void setClassification(String classification) {
    this.classification = classification;
  }
}
