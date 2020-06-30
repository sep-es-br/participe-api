package br.gov.es.participe.controller.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class MeetingParamDto {
	
	private String name;
	private String address;
    private String place;
    private LocalityDto localityPlace;
    private List<LocalityDto> localityCovers;
    private ConferenceDto conference;
    
    @JsonFormat(pattern="dd/MM/yyyy")
    private Date endDate;
    @JsonFormat(pattern="dd/MM/yyyy")
    private Date beginDate;
    
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public LocalityDto getLocalityPlace() {
		return localityPlace;
	}
	public void setLocalityPlace(LocalityDto localityPlace) {
		this.localityPlace = localityPlace;
	}
	public List<LocalityDto> getLocalityCovers() {
		return localityCovers;
	}
	public void setLocalityCovers(List<LocalityDto> localityCovers) {
		this.localityCovers = localityCovers;
	}
	public ConferenceDto getConference() {
		return conference;
	}
	public void setConference(ConferenceDto conference) {
		this.conference = conference;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Date getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
}
