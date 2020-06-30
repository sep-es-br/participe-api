package br.gov.es.participe.model;

import java.util.Date;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateString;

import br.gov.es.participe.controller.dto.CommentDto;
import br.gov.es.participe.controller.dto.CommentParamDto;

@NodeEntity
public class Comment extends Attend{

	private String text;
	private String type;
	private String status;
	private String classification;
	private Boolean moderated;

	@DateString
	private Date time;
	
	@Relationship(type = "LIKED_BY")
	private Set<Person> personLiked;
	
	@Relationship(type = "MODERATED_BY")
	private Person moderator;
	
	public Comment() {
	}
	
	public Comment(CommentDto comment) {
		this.time = comment.getTime();
		this.text = comment.getText();
		this.type = comment.getType();
		this.status = comment.getStatus();
		this.setId(comment.getId());
		this.setFrom(comment.getFrom());
		this.moderated = false;
	}
	
	public Comment(CommentParamDto comment) {
		this.setId(comment.getId());
		this.text = comment.getText();
		this.type = comment.getType();
		this.setFrom(comment.getFrom());
		this.status = comment.getStatus();
		this.time = new Date();
		if(comment.getLocality() != null) {
			Locality l = new Locality();
			l.setId(comment.getLocality());
			this.setLocality(l);
		}
		
		if(comment.getMeeting() != null) {
			Meeting m = new Meeting();
			m.setId(comment.getMeeting());
			this.setMeeting(m);
		}
		
		if(comment.getPlanItem() != null) {
			PlanItem pItem = new PlanItem();
			pItem.setId(comment.getPlanItem());
			this.setPlanItem(pItem);
		}
		
		if (comment.getConference() != null) {
			Conference conference = new Conference();
			conference.setId(comment.getConference());
			this.setConference(conference);
		}
		
		if(comment.getPerson() != null) {
			this.setPersonMadeBy(new Person(comment.getPerson()));
		}
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

	public Set<Person> getPersonLiked() {
		return personLiked;
	}

	public void setPersonLiked(Set<Person> personLiked) {
		this.personLiked = personLiked;
	}

	public Person getModerator() {
		return moderator;
	}

	public void setModerator(Person moderator) {
		this.moderator = moderator;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public Boolean getModerated() {
		return moderated;
	}

	public void setModerated(Boolean moderated) {
		this.moderated = moderated;
	}
}
