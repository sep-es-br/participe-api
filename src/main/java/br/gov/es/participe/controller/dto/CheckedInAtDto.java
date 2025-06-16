package br.gov.es.participe.controller.dto;

import java.util.Date;

import br.gov.es.participe.model.CheckedInAt;

public class CheckedInAtDto {
	private PersonDto person;
	private MeetingDto meeting;
	private Date time;
        
        
    
        private Boolean isAuthority;
        private Boolean toAnnounce;
        private Boolean isAnnounced;
        private String organization;
        private String role;
	
	public CheckedInAtDto (){
	}

	public CheckedInAtDto (CheckedInAt checkedInAt){
		if(checkedInAt == null)
			return;

		this.time = checkedInAt.getTime();
                this.isAuthority = checkedInAt.getIsAuthority();
                this.isAnnounced = checkedInAt.getIsAnnounced();
                this.toAnnounce = checkedInAt.getToAnnounce();
                this.organization = checkedInAt.getOrganization();
                this.role = checkedInAt.getRole();
                
		if(checkedInAt.getPerson() != null) {
			this.person = new PersonDto(checkedInAt.getPerson());
		}
		if(checkedInAt.getMeeting() != null) {
			this.meeting = new MeetingDto(checkedInAt.getMeeting(), false);
			this.meeting.setLocalityCovers(null);
			this.meeting.setReceptionists(null);
		}
	}

    public Boolean getIsAuthority() {
        return isAuthority;
    }

    public Boolean getToAnnounce() {
        return toAnnounce;
    }

    public void setToAnnounce(Boolean toAnnounce) {
        this.toAnnounce = toAnnounce;
    }

    public void setIsAuthority(Boolean isAuthority) {
        this.isAuthority = isAuthority;
    }

    public Boolean getIsAnnounced() {
        return isAnnounced;
    }

    public void setIsAnnounced(Boolean isAnnounced) {
        this.isAnnounced = isAnnounced;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
