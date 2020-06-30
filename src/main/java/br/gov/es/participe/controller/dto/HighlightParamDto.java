package br.gov.es.participe.controller.dto;

public class HighlightParamDto {

	private Long id;
	private String from;
	
	private PersonDto personMadeBy;
	
	private PlanItemDto planItem;
	
	private LocalityDto locality;
	
	private MeetingDto meeting;
	
	private Long conference;
	
	public HighlightParamDto(){
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

	public Long getConference() {
		return conference;
	}

	public void setConference(Long conference) {
		this.conference = conference;
	}
}
