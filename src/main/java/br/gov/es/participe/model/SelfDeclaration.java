package br.gov.es.participe.model;

import org.neo4j.ogm.annotation.Relationship;

import br.gov.es.participe.controller.dto.SelfDeclarationDto;

public class SelfDeclaration extends Entity {

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
