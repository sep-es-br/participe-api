package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.Highlight;

public class HighlightDto {

	private Long id;
	private String from;
	
	private PersonDto personMadeBy;
	
	private PlanItemDto planItem;
	
	private LocalityDto locality;
	
	private MeetingDto meeting;
	
	public HighlightDto(){
	}
	
	
	public HighlightDto(Highlight highlight){
		
		id = highlight.getId();
		from = highlight.getFrom();
		
		if(highlight.getLocality() != null)
			locality = new LocalityDto(highlight.getLocality());
		
		if(highlight.getMeeting() != null) {
			highlight.getMeeting().setConference(null);
			meeting = new MeetingDto(highlight.getMeeting());
		}
		
		if(highlight.getPersonMadeBy() != null) {
			highlight.getPersonMadeBy().setSelfDeclaretions(null);
			personMadeBy = new PersonDto(highlight.getPersonMadeBy());
		}
		if(highlight.getPlanItem() != null) 
			planItem = new PlanItemDto(highlight.getPlanItem(),false);
	}

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

	public PersonDto getPersonMadeBy() {
		return personMadeBy;
	}

	public void setPersonMadeBy(PersonDto personMadeBy) {
		this.personMadeBy = personMadeBy;
	}

	public PlanItemDto getPlanItem() {
		return planItem;
	}

	public void setPlanItem(PlanItemDto planItem) {
		this.planItem = planItem;
	}

	public LocalityDto getLocality() {
		return locality;
	}

	public void setLocality(LocalityDto locality) {
		this.locality = locality;
	}

	public MeetingDto getMeeting() {
		return meeting;
	}

	public void setMeeting(MeetingDto meeting) {
		this.meeting = meeting;
	}
}
