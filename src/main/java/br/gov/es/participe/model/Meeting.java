package br.gov.es.participe.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateString;

import br.gov.es.participe.controller.dto.MeetingDto;
import br.gov.es.participe.controller.dto.MeetingParamDto;

@NodeEntity
public class Meeting extends Entity {
	
	private String name;
	@DateString
    private Date beginDate;

    @DateString
    private Date endDate;
    private String address;
    private String place;
    
    @Relationship(type = "TAKES_PLACE_AT")
    private Locality localityPlace;
    
    @Relationship(type = "COVERS")
    private Set<Locality> localityCovers;
    
    @Relationship(type = "OCCURS_IN")
    private Conference conference;
    
    @Relationship(type = "DURING")
    private Set<Attend> attends;
    
    
    public Meeting() {
    	
    }
    
    public Meeting( MeetingDto meeting) {
    	
    	setId(meeting.getId());
    	this.name = meeting.getName();
    	this.address = meeting.getAddress();
        this.place = meeting.getPlace();
        this.localityPlace = new Locality(meeting.getLocalityPlace());
        this.conference = new Conference(meeting.getConference());
        this.endDate = meeting.getEndDate();
        this.beginDate = meeting.getBeginDate();
        
        if(meeting.getLocalityCovers() != null && !meeting.getLocalityCovers().isEmpty()) {
        	this.localityCovers = new HashSet<>();
          	meeting.getLocalityCovers().forEach(locality -> localityCovers.add(new Locality(locality)));
        }
    }
    
    public Meeting( MeetingParamDto meeting) {
    	this.name = meeting.getName();
    	this.address = meeting.getAddress();
        this.place = meeting.getPlace();
        this.localityPlace = new Locality(meeting.getLocalityPlace());
        this.conference = new Conference(meeting.getConference());
        this.endDate = meeting.getEndDate();
        this.beginDate = meeting.getBeginDate();
        
        if(meeting.getLocalityCovers() == null || !meeting.getLocalityCovers().isEmpty()) {
        	this.localityCovers = new HashSet<>();
          	meeting.getLocalityCovers().forEach(locality -> localityCovers.add(new Locality(locality)));
        }
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Locality getLocalityPlace() {
		return localityPlace;
	}

	public void setLocalityPlace(Locality localityPlace) {
		this.localityPlace = localityPlace;
	}

	public Set<Locality> getLocalityCovers() {
		return localityCovers;
	}

	public void setLocalityCovers(Set<Locality> localityCovers) {
		this.localityCovers = localityCovers;
	}

	public Conference getConference() {
		return conference;
	}

	public void setConference(Conference conference) {
		this.conference = conference;
	}

	public Set<Attend> getAttends() {
		return attends;
	}

	public void setAttends(Set<Attend> attends) {
		this.attends = attends;
	}
    
    
}
