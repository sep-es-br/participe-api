package br.gov.es.participe.controller.dto;

import java.util.Date;

import br.gov.es.participe.model.CheckedInAt;

public class CheckedInAtDto {
	private PersonDto person;
	private MeetingDto meeting;
	private Date time;
	
	public CheckedInAtDto (){
	}

	public CheckedInAtDto (CheckedInAt checkedInAt){
		if(checkedInAt == null)
			return;

		this.time = checkedInAt.getTime();
		if(checkedInAt.getPerson() != null) {
			this.person = new PersonDto(checkedInAt.getPerson());
		}
		if(checkedInAt.getMeeting() != null) {
			this.meeting = new MeetingDto(checkedInAt.getMeeting(), false);
			this.meeting.setLocalityCovers(null);
			this.meeting.setReceptionists(null);
		}
	}

	public PersonDto getPerson() {
		return person;
	}

	public void setPerson(PersonDto person) {
		this.person = person;
	}

	public MeetingDto getMeeting() {
		return meeting;
	}

	public void setMeeting(MeetingDto meeting) {
		this.meeting = meeting;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

}
