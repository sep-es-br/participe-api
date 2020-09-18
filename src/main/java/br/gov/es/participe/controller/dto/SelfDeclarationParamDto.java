package br.gov.es.participe.controller.dto;

public class SelfDeclarationParamDto {

	private Long id;
	private Long conference;
	private Long locality;
	private Long person;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getConference() {
		return conference;
	}

	public void setConference(Long conference) {
		this.conference = conference;
	}

	public Long getLocality() {
		return locality;
	}

	public void setLocality(Long locality) {
		this.locality = locality;
	}

	public Long getPerson() {
		return person;
	}

	public void setPerson(Long person) {
		this.person = person;
	}
}
