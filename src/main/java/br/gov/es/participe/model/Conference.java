package br.gov.es.participe.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Transient;
import org.neo4j.ogm.annotation.typeconversion.DateString;

import br.gov.es.participe.controller.dto.ConferenceDto;
import br.gov.es.participe.controller.dto.ConferenceParamDto;

@NodeEntity
public class Conference extends Entity implements Serializable {

    private String name;
    private String description;
    
    @DateString
    private Date beginDate;

    @DateString
    private Date endDate;
    
    private String titleAuthentication;
    private String subtitleAuthentication;
    private String titleParticipation;
    private String subtitleParticipation;
    private String titleRegionalization;
    private String subtitleRegionalization;

    @Relationship(type = "TARGETS")
    private Plan plan;
    
    @Relationship(type = "FEATURES_PARTICIPATION_IMAGE")
    private File fileParticipation;
    
    @Relationship(type = "FEATURES_AUTHENTICATION_IMAGE")
    private File fileAuthentication;
    
    @Relationship(type = "LOCALIZES_CITIZEN_BY")
    private LocalityType localityType;
    
    @Relationship(type = "OCCURS_IN", direction = Relationship.INCOMING)
    private Set<Meeting> meeting;
    
    @Relationship(type = "MADE", direction = Relationship.INCOMING)
    private Set<SelfDeclaration> selfDeclaration;

    @Relationship(type = "MODERATORS")
    private Set<Person> moderators;

    @Transient
    private Boolean hasAttend;

    public Conference() {
    }

    public Conference(ConferenceDto conferenceDto) {
        if (conferenceDto == null) return;

        setId(conferenceDto.getId());
        this.name = conferenceDto.getName();
        this.description = conferenceDto.getDescription();
        if(conferenceDto.getPlan() != null && conferenceDto.getPlan().getId() != null){
            this.plan = new Plan(conferenceDto.getPlan());
        }
        
        if(conferenceDto.getFileParticipation() != null && conferenceDto.getFileParticipation().getId() != null) {
        	this.fileParticipation = new File(conferenceDto.getFileParticipation());
        }
        
        if(conferenceDto.getFileAuthentication() != null && conferenceDto.getFileAuthentication().getId() != null) {
        	this.fileAuthentication = new File(conferenceDto.getFileAuthentication());
        }
        
        if(conferenceDto.getLocalityType() != null && conferenceDto.getLocalityType().getId() != null) {
        	this.localityType = new LocalityType(conferenceDto.getLocalityType());
        }
        
        if(conferenceDto.getMeeting() != null && !conferenceDto.getMeeting().isEmpty()) {
        	this.meeting = new HashSet<>();
        	conferenceDto.getMeeting().forEach(meet -> this.meeting.add(new Meeting(meet)));
        }
        
        if(conferenceDto.getSelfDeclaration() != null && !conferenceDto.getSelfDeclaration().isEmpty()) {
        	this.selfDeclaration =  new HashSet<>();
        	conferenceDto.getSelfDeclaration().forEach(self -> selfDeclaration.add(new SelfDeclaration(self)));				
        }

        if(conferenceDto.getModerators() != null && !conferenceDto.getModerators().isEmpty()) {
        	this.moderators =  new HashSet<>();
        	conferenceDto.getModerators().forEach(person -> moderators.add(new Person(person)));
        }

        this.beginDate = conferenceDto.getBeginDate();
        this.endDate = conferenceDto.getEndDate();
        this.titleAuthentication = conferenceDto.getTitleAuthentication();
        this.titleParticipation = conferenceDto.getTitleParticipation();
        this.titleRegionalization = conferenceDto.getTitleRegionalization();
        this.subtitleAuthentication = conferenceDto.getSubtitleAuthentication();
        this.subtitleParticipation = conferenceDto.getSubtitleParticipation();
        this.subtitleRegionalization = conferenceDto.getSubtitleRegionalization();
    }

    public Conference(ConferenceParamDto conferenceParamDto) {
        if (conferenceParamDto == null) return;

        setId(conferenceParamDto.getId());
        this.description = conferenceParamDto.getDescription();
        if(conferenceParamDto.getPlan() != null && conferenceParamDto.getPlan().getId() != null){
            this.plan = new Plan(conferenceParamDto.getPlan());
        }
        
        if(conferenceParamDto.getFileParticipation() != null && conferenceParamDto.getFileParticipation().getId() != null) {
        	this.fileParticipation = new File(conferenceParamDto.getFileParticipation());
        }
        
        if(conferenceParamDto.getFileAuthentication() != null && conferenceParamDto.getFileAuthentication().getId() != null) {
        	this.fileAuthentication = new File(conferenceParamDto.getFileAuthentication());
        }
        
        if(conferenceParamDto.getLocalityType() != null && conferenceParamDto.getLocalityType().getId() != null) {
        	this.localityType = new LocalityType(conferenceParamDto.getLocalityType());
        }
        
        if(conferenceParamDto.getMeeting() != null && !conferenceParamDto.getMeeting().isEmpty()) {
        	this.meeting = new HashSet<>();
        	conferenceParamDto.getMeeting().forEach(meet -> this.meeting.add(new Meeting(meet)));
        }
        
        if(conferenceParamDto.getSelfDeclaration() != null && !conferenceParamDto.getSelfDeclaration().isEmpty()) {
        	this.selfDeclaration =  new HashSet<>();
        	conferenceParamDto.getSelfDeclaration().forEach(self -> selfDeclaration.add(new SelfDeclaration(self)));				
        }

        if(conferenceParamDto.getModerators() != null && !conferenceParamDto.getModerators().isEmpty()) {
            this.moderators = new HashSet<>();
            conferenceParamDto.getModerators().forEach(person -> moderators.add(new Person(person)));
        }

        this.beginDate = conferenceParamDto.getBeginDate();
        this.name = conferenceParamDto.getName();
        this.endDate = conferenceParamDto.getEndDate();
        this.titleAuthentication = conferenceParamDto.getTitleAuthentication();
        this.titleParticipation = conferenceParamDto.getTitleParticipation();
        this.titleRegionalization = conferenceParamDto.getTitleRegionalization();
        this.subtitleAuthentication = conferenceParamDto.getSubtitleAuthentication();
        this.subtitleParticipation = conferenceParamDto.getSubtitleParticipation();
        this.subtitleRegionalization = conferenceParamDto.getSubtitleRegionalization();
        
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

	public File getFileParticipation() {
		return fileParticipation;
	}

	public void setFileParticipation(File fileParticipation) {
		this.fileParticipation = fileParticipation;
	}

	public File getFileAuthentication() {
		return fileAuthentication;
	}

	public void setFileAuthentication(File fileAuthentication) {
		this.fileAuthentication = fileAuthentication;
	}

	public LocalityType getLocalityType() {
		return localityType;
	}

	public void setLocalityType(LocalityType localityType) {
		this.localityType = localityType;
	}

	public Set<Meeting> getMeeting() {
		return meeting;
	}

	public void setMeeting(Set<Meeting> meeting) {
		this.meeting = meeting;
	}

	public Set<SelfDeclaration> getSelfDeclaration() {
		return selfDeclaration;
	}

	public void setSelfDeclaration(Set<SelfDeclaration> selfDeclaration) {
		this.selfDeclaration = selfDeclaration;
	}

	public Boolean getHasAttend() {
		return hasAttend;
	}

	public void setHasAttend(Boolean hasAttend) {
		this.hasAttend = hasAttend;
	}

    public Set<Person> getModerators() {
        return moderators;
    }

    public void setModerators(Set<Person> moderators) {
        this.moderators = moderators;
    }
}
