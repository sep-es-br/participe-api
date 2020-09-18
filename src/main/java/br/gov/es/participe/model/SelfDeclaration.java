package br.gov.es.participe.model;

import java.io.Serializable;

import org.neo4j.ogm.annotation.Relationship;

import br.gov.es.participe.controller.dto.SelfDeclarationDto;
import br.gov.es.participe.controller.dto.SelfDeclarationParamDto;

public class SelfDeclaration extends Entity implements Serializable {

	@Relationship(type = "TO")
	private Conference conference;
	
	@Relationship(type = "AS_BEING_FROM")
	private Locality locality;
	
	@Relationship(type ="MADE", direction = Relationship.INCOMING)
	private Person person;
	
	public SelfDeclaration() {
	}
	
	public SelfDeclaration(Conference conference, Locality locality, Person person){
		
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
}
