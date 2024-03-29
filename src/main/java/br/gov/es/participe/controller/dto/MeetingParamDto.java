package br.gov.es.participe.controller.dto;

import br.gov.es.participe.enumerator.TypeMeetingEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import br.gov.es.participe.enumerator.AttendanceListEnum;

public class MeetingParamDto {

	private String name;
	private String address;
	private String place;
	private Long localityPlace;
	private List<Long> localityCovers;
	private Long conference;
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private Date endDate;
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private Date beginDate;
	private List<Long> receptionists;
	private List<String> receptionistEmails;
	private List<Long> participants;

	private List<ChannelDto> channels;
	private TypeMeetingEnum type;
	private List<Long> segmentations;
	private AttendanceListEnum attendanceListMode;
  


	public MeetingParamDto() {

	}



	public AttendanceListEnum getAttendanceListMode() {
		return attendanceListMode;
	  }
	
	  public void setAttendanceListMode(AttendanceListEnum attendanceListMode) {
		this.attendanceListMode = attendanceListMode;
	  }
	
	

	public List<Long> getSegmentations() {
		return segmentations;
	}

	public void setSegmentations(List<Long> segmentations) {
		this.segmentations = segmentations;
	}

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

	public Long getLocalityPlace() {
		return localityPlace;
	}

	public void setLocalityPlace(Long localityPlace) {
		this.localityPlace = localityPlace;
	}

	public List<Long> getLocalityCovers() {
		return localityCovers;
	}

	public void setLocalityCovers(List<Long> localityCovers) {
		this.localityCovers = localityCovers;
	}

	public Long getConference() {
		return conference;
	}

	public void setConference(Long conference) {
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

	public LocalityDto getLocalityPlaceAsDto() {
		LocalityDto localityDto = new LocalityDto();
		localityDto.setId(this.localityPlace);
		return localityDto;
	}

	public List<LocalityDto> getLocalityCoversAsDto() {
		List<LocalityDto> localitiesDto = new ArrayList<>();
		this.localityCovers.forEach(elem -> {
			LocalityDto localityDto = new LocalityDto();
			localityDto.setId(elem);
			localitiesDto.add(localityDto);
		});
		return localitiesDto;
	}

	public ConferenceDto getConferenceAsDto() {
		ConferenceDto conferenceDto = new ConferenceDto();
		conferenceDto.setId(this.conference);
		return conferenceDto;
	}

	public List<PersonDto> getReceptionistsAsDto() {
		List<PersonDto> receptionistsDto = new ArrayList<>();
		this.receptionists.forEach(elem -> {
			PersonDto personDto = new PersonDto();
			personDto.setId(elem);
			receptionistsDto.add(personDto);
		});
		return receptionistsDto;
	}

	public List<PersonDto> getParticipantsAsDto() {
		List<PersonDto> participantsDto = new ArrayList<>();
		this.participants.forEach(elem -> {
			PersonDto personDto = new PersonDto();
			personDto.setId(elem);
			participantsDto.add(personDto);
		});
		return participantsDto;
	}

	public List<Long> getReceptionists() {
		return receptionists;
	}

	public void setReceptionists(List<Long> receptionists) {
		this.receptionists = receptionists;
	}

	public List<String> getReceptionistEmails() {
		return receptionistEmails;
	}

	public void setReceptionistEmails(List<String> receptionistEmails) {
		this.receptionistEmails = receptionistEmails;
	}

	public List<Long> getParticipants() {
		return participants;
	}

	public void setParticipants(List<Long> participants) {
		this.participants = participants;
	}

	public List<ChannelDto> getChannels() {
		return channels;
	}

	public void setChannels(List<ChannelDto> channels) {
		this.channels = channels;
	}

	public TypeMeetingEnum getType() {
		return type;
	}

	public void setType(TypeMeetingEnum type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "MeetingParamDto{" + "name='" + name + '\'' +
					 ", address='" + address + '\'' +
					 ", place='" + place + '\'' +
					 ", localityPlace=" + localityPlace +
					 ", localityCovers=" + localityCovers +
					 ", conference=" + conference +
					 ", endDate=" + endDate +
					 ", beginDate=" + beginDate +
					 '}';
	}
}
