package br.gov.es.participe.controller.dto;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.gov.es.participe.model.Conference;

public class ConferenceDto {

    private Long id;
    private String name;
    private Boolean hasAttend;
    private String description;
    private String titleAuthentication;
    private String subtitleAuthentication;
    private String titleParticipation;
    private String subtitleParticipation;
    private String titleRegionalization;
    private String subtitleRegionalization;
    private Boolean isActive;

    private PlanDto plan;
    private LocalityTypeDto localityType;
    private List<MeetingDto> meeting;
    private FileDto fileParticipation;
    private FileDto fileAuthentication;
    private List<SelfDeclarationDto> selfDeclaration;
    private List<PersonDto> moderators;

    //@JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    @JsonFormat(pattern="dd/MM/yyyy")
    private Date beginDate;
    @JsonFormat(pattern="dd/MM/yyyy")
    private Date endDate;

    public ConferenceDto() {
    }

    public ConferenceDto(Conference conference) {
        if (conference == null) return;

        this.description = conference.getDescription();
        this.id = conference.getId();
        this.name = conference.getName();
        this.hasAttend = conference.getHasAttend();
        this.beginDate = conference.getBeginDate();
        this.plan = new PlanDto(conference.getPlan(), false);
        this.endDate = conference.getEndDate();
        this.titleAuthentication = conference.getTitleAuthentication();
        this.titleParticipation = conference.getTitleParticipation();
        this.titleRegionalization = conference.getTitleRegionalization();
        this.subtitleAuthentication = conference.getSubtitleAuthentication();
        this.subtitleParticipation = conference.getSubtitleParticipation();
        this.subtitleRegionalization = conference.getSubtitleRegionalization();
        this.fileParticipation = new FileDto(conference.getFileParticipation());
        this.fileAuthentication = new FileDto(conference.getFileAuthentication());
        this.localityType = new LocalityTypeDto(conference.getLocalityType());
        
        if(conference.getSelfDeclaration() != null && !conference.getSelfDeclaration().isEmpty()) {
	        this.selfDeclaration = new ArrayList<>();
	        conference.getSelfDeclaration().forEach(self -> new SelfDeclarationDto(self, true));
        }
        	
        if(conference.getMeeting() != null && !conference.getMeeting().isEmpty()) {
        	this.meeting = new ArrayList<>();
        	conference.getMeeting().forEach(meet -> this.meeting.add(new MeetingDto(meet, false)));
        }

        if(conference.getModerators() != null && !conference.getModerators().isEmpty()) {
            this.moderators = new ArrayList<>();
            conference.getModerators().forEach(moderator -> this.moderators.add(new PersonDto(moderator)));
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getHasAttend() {
		return hasAttend;
	}

	public void setHasAttend(Boolean hasAttend) {
		this.hasAttend = hasAttend;
	}

	public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public PlanDto getPlan() {
        return plan;
    }

    public void setPlan(PlanDto plan) {
        this.plan = plan;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

	public String getTitleAuthentication() {
		return titleAuthentication;
	}

	public void setTitleAuthentication(String titleAuthentication) {
		this.titleAuthentication = titleAuthentication;
	}

	public String getSubtitleAuthentication() {
		return subtitleAuthentication;
	}

	public void setSubtitleAuthentication(String subtitleAuthentication) {
		this.subtitleAuthentication = subtitleAuthentication;
	}

	public String getTitleParticipation() {
		return titleParticipation;
	}

	public void setTitleParticipation(String titleParticipation) {
		this.titleParticipation = titleParticipation;
	}

	public String getSubtitleParticipation() {
		return subtitleParticipation;
	}

	public void setSubtitleParticipation(String subtitleParticipation) {
		this.subtitleParticipation = subtitleParticipation;
	}

	public String getTitleRegionalization() {
		return titleRegionalization;
	}

	public void setTitleRegionalization(String titleRegionalization) {
		this.titleRegionalization = titleRegionalization;
	}

	public String getSubtitleRegionalization() {
		return subtitleRegionalization;
	}

	public void setSubtitleRegionalization(String subtitleRegionalization) {
		this.subtitleRegionalization = subtitleRegionalization;
	}

	public FileDto getFileParticipation() {
		return fileParticipation;
	}

	public List<MeetingDto> getMeeting() {
		return meeting;
	}

	public void setMeeting(List<MeetingDto> meeting) {
		this.meeting = meeting;
	}

	public LocalityTypeDto getLocalityType() {
		return localityType;
	}

	public void setLocalityType(LocalityTypeDto locality) {
		this.localityType = locality;
	}

	public void setFileParticipation(FileDto fileParticipation) {
		this.fileParticipation = fileParticipation;
	}

	public FileDto getFileAuthentication() {
		return fileAuthentication;
	}

	public void setFileAuthentication(FileDto fileAuthentication) {
		this.fileAuthentication = fileAuthentication;
	}

	public List<SelfDeclarationDto> getSelfDeclaration() {
		return selfDeclaration;
	}

	public void setSelfDeclaration(List<SelfDeclarationDto> selfDeclaration) {
		this.selfDeclaration = selfDeclaration;
	}

    public List<PersonDto> getModerators() {
        return moderators;
    }

    public void setModerators(List<PersonDto> moderators) {
        this.moderators = moderators;
    }
}
