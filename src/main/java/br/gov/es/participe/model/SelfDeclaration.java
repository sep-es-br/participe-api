package br.gov.es.participe.model;

import br.gov.es.participe.controller.dto.SelfDeclarationDto;
import br.gov.es.participe.controller.dto.SelfDeclarationParamDto;
import org.neo4j.ogm.annotation.Relationship;

import java.io.Serializable;
import java.util.Objects;

public class SelfDeclaration extends Entity implements Serializable {

  @Relationship(type = "TO")
  private Conference conference;

  @Relationship(type = "AS_BEING_FROM")
  private Locality locality;

  @Relationship(type = "MADE", direction = Relationship.INCOMING)
  private Person person;

  private Boolean answerSurvey;

  private Boolean receiveInformational;

  public SelfDeclaration(Long conferenceId, Long localityId, Long personId) {
    Objects.requireNonNull(conferenceId);
    Objects.requireNonNull(localityId);
    Objects.requireNonNull(personId);

    this.conference = new Conference();
    this.conference.setId(conferenceId);

    this.locality = new Locality();
    this.locality.setId(localityId);

    this.person = new Person();
    this.person.setId(personId);
  }

  public SelfDeclaration(Conference conference, Locality locality, Person person) {

    this.person = person;
    this.locality = locality;
    this.conference = conference;
  }

  public SelfDeclaration(SelfDeclarationDto selfDeclaration) {

    setId(selfDeclaration.getId());
    if(selfDeclaration.getConference() != null && selfDeclaration.getConference().getId() != null) {
      this.conference = new Conference(selfDeclaration.getConference());
    }

    if(selfDeclaration.getLocality() != null && selfDeclaration.getLocality().getId() != null) {
      this.locality = new Locality(selfDeclaration.getLocality());
    }

    if(selfDeclaration.getPerson() != null && selfDeclaration.getPerson().getId() != null) {
      this.person = new Person(selfDeclaration.getPerson());
    }

  }

  public SelfDeclaration(SelfDeclarationParamDto selfDeclaration) {

    setId(selfDeclaration.getId());
    if(selfDeclaration.getConference() != null) {
      this.conference = new Conference();
      this.conference.setId(selfDeclaration.getConference());
    }

    if(selfDeclaration.getLocality() != null) {
      this.locality = new Locality();
      this.locality.setId(selfDeclaration.getLocality());
    }

    if(selfDeclaration.getPerson() != null) {
      this.person = new Person();
      this.person.setId(selfDeclaration.getPerson());
    }

    if(selfDeclaration.getReceiveInformational() != null){
      this.receiveInformational = selfDeclaration.getReceiveInformational();
    }

  }

  public SelfDeclaration() {}

  public Boolean getAnswerSurvey() {
    return answerSurvey;
  }

  public void setAnswerSurvey(Boolean answerSurvey) {
    this.answerSurvey = answerSurvey;
  }

  public Conference getConference() {
    return conference;
  }

  public void setConference(Conference conference) {
    this.conference = conference;
  }

  public Locality getLocality() {
    return locality;
  }

  public void setLocality(Locality locality) {
    this.locality = locality;
  }

  public Person getPerson() {
    return person;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  public Boolean getReceiveInformational() {
    return receiveInformational;
  }

  public void setReceiveInformational(Boolean receiveInformational) {
    this.receiveInformational = receiveInformational;
  }
}
