package br.gov.es.participe.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public abstract class Attend {
	
	@Id @GeneratedValue
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

}
