package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.SelfDeclaration;

public class SelfDeclarationDto {

	private Long id;
	private ConferenceDto conference;
	private LocalityDto locality;
	private PersonDto person;
	
	public SelfDeclarationDto() {
	}
	
	public SelfDeclarationDto(SelfDeclaration selfdeclaration, boolean loadattributes) {
		
		this.id = selfdeclaration.getId();
		if(loadattributes) {
			if(selfdeclaration.getConference() != null && selfdeclaration.getConference().getId() != null)
				this.conference = new ConferenceDto(selfdeclaration.getConference());
			
			if(selfdeclaration.getLocality() != null && selfdeclaration.getLocality().getId() != null)
				this.locality = new LocalityDto(selfdeclaration.getLocality());
			
			if(selfdeclaration.getPerson() != null && selfdeclaration.getPerson().getId() != null)
				this.person = new PersonDto(selfdeclaration.getPerson());
		}
	}

	public ConferenceDto getConference() {
		return conference;
	}

	public void setConference(ConferenceDto conference) {
		this.conference = conference;
	}

	public LocalityDto getLocality() {
		return locality;
	}

	public void setLocality(LocalityDto locality) {
		this.locality = locality;
	}

	public PersonDto getPerson() {
		return person;
	}

	public void setPerson(PersonDto person) {
		this.person = person;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
