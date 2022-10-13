package br.gov.es.participe.model;

import br.gov.es.participe.controller.dto.MeetingDto;
import br.gov.es.participe.controller.dto.MeetingParamDto;
import br.gov.es.participe.enumerator.TypeMeetingEnum;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.DateString;
import org.springframework.data.neo4j.core.schema.Node;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Node
public class Meeting extends Entity implements Serializable {

  private String name;

  @DateString
  private Date beginDate;

  @DateString
  private Date endDate;

  private TypeMeetingEnum typeMeetingEnum;

  private String address;

  private String place;

  @Relationship(type = "TAKES_PLACE_AT")
  private Locality localityPlace;

  @Relationship(type = "COVERS")
  private Set<Locality> localityCovers;

  @Relationship(type = "OCCURS_IN")
  private Conference conference;

  @Relationship(type = "DURING")
  private Set<Attend> attends;

  @Relationship(type = "IS_RECEPTIONIST_OF", direction = Relationship.Direction.INCOMING)
  private Set<Person> receptionists;

  @Relationship(type = "IS_CHANNEL_OF", direction = Relationship.Direction.INCOMING)
  private Set<Channel> channels;

  @Relationship(type = "CHECKED_IN_AT", direction = Relationship.Direction.INCOMING)
  private Set<Person> participants;

  @Relationship(type = "IS_PLAN_ITEM_OF", direction = Relationship.Direction.INCOMING)
  private Set<PlanItem> planItems;

  public Meeting() {

  }

  public Meeting(MeetingDto meeting) {

    setId(meeting.getId());
    this.name = meeting.getName();
    this.address = meeting.getAddress();
    this.place = meeting.getPlace();
    this.localityPlace = new Locality(meeting.getLocalityPlace());
    this.conference = new Conference(meeting.getConference());
    this.endDate = meeting.getEndDate();
    this.beginDate = meeting.getBeginDate();

    if (meeting.getLocalityCovers() != null && !meeting.getLocalityCovers().isEmpty()) {
      this.localityCovers = new HashSet<>();
      meeting.getLocalityCovers().forEach(locality -> localityCovers.add(new Locality(locality)));
    }

    if (meeting.getReceptionists() != null && !meeting.getReceptionists().isEmpty()) {
      this.receptionists = new HashSet<>();
      meeting.getReceptionists().forEach(receptionist -> this.receptionists.add(new Person(receptionist)));
    }
    if (meeting.getParticipants() != null && !meeting.getParticipants().isEmpty()) {
      this.participants = new HashSet<>();
      meeting.getParticipants().forEach(participant -> this.receptionists.add(new Person(participant)));
    }
  }

  public Meeting(MeetingParamDto meeting, Boolean loadPersons) {
    this.name = meeting.getName();
    this.address = meeting.getAddress();
    this.place = meeting.getPlace();
    this.localityPlace = new Locality(meeting.getLocalityPlaceAsDto());
    this.conference = new Conference(meeting.getConferenceAsDto());
    this.endDate = meeting.getEndDate();
    this.beginDate = meeting.getBeginDate();

    if (meeting.getLocalityCovers() != null && !meeting.getLocalityCovers().isEmpty()) {
      this.localityCovers = new HashSet<>();
      meeting.getLocalityCoversAsDto().forEach(locality -> localityCovers.add(new Locality(locality)));
    }

    if (loadPersons) {
      if (meeting.getReceptionists() != null && !meeting.getReceptionists().isEmpty()) {
        this.receptionists = new HashSet<>();
        meeting.getReceptionistsAsDto()
            .forEach(receptionist -> this.receptionists.add(new Person(receptionist)));
      }
      if (meeting.getParticipants() != null && !meeting.getParticipants().isEmpty()) {
        this.participants = new HashSet<>();
        meeting.getParticipantsAsDto().forEach(participant -> this.receptionists.add(new Person(participant)));
      }
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getBeginDate() {
    return beginDate;
  }

  public void setBeginDate(Date beginDate) {
    this.beginDate = beginDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPlace() {
    return place;
  }

  public void setPlace(String place) {
    this.place = place;
  }

  public Locality getLocalityPlace() {
    return localityPlace;
  }

  public void setLocalityPlace(Locality localityPlace) {
    this.localityPlace = localityPlace;
  }

  public Set<Locality> getLocalityCovers() {
    return localityCovers;
  }

  public void setLocalityCovers(Set<Locality> localityCovers) {
    this.localityCovers = localityCovers;
  }

  public Conference getConference() {
    return conference;
  }

  public void setConference(Conference conference) {
    this.conference = conference;
  }

  public Set<Attend> getAttends() {
    if (this.attends == null) {
      return new HashSet<>();
    }

    return attends;
  }

  public void setAttends(Set<Attend> attends) {
    this.attends = attends;
  }

  public Set<Person> getReceptionists() {
    if (this.receptionists == null) {
      return new HashSet<>();
    }

    return receptionists;
  }

  public void setReceptionists(Set<Person> receptionists) {
    this.receptionists = receptionists;
  }

  public Set<Person> getParticipants() {
    if (this.participants == null) {
      return new HashSet<>();
    }

    return participants;
  }

  public void setParticipants(Set<Person> participants) {
    this.participants = participants;
  }

  public TypeMeetingEnum getTypeMeetingEnum() {
    return typeMeetingEnum;
  }

  public void setTypeMeetingEnum(TypeMeetingEnum typeMeetingEnum) {
    this.typeMeetingEnum = typeMeetingEnum;
  }

  public Set<Channel> getChannels() {
    if (this.channels == null) {
      return new HashSet<>();
    }
    return channels;
  }

  public void setChannels(Set<Channel> channels) {
    this.channels = channels;
  }

  public Set<PlanItem> getPlanItems() {
    if (this.planItems == null) {
      return new HashSet<>();
    }

    return planItems;
  }

  public void setPlanItems(Set<PlanItem> planItems) {
    this.planItems = planItems;
  }
}
