package br.gov.es.participe.service;

import java.util.*;

import br.gov.es.participe.controller.dto.MeetingParamDto;
import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.CheckedInAtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.es.participe.repository.MeetingRepository;

@Service
public class MeetingService {
	
	@Autowired
	private MeetingRepository meetingRepository;
	
	@Autowired
	private LocalityService localityService;

	@Autowired
	private ConferenceService conferenceService;

	@Autowired
	private PersonService personService;

	@Autowired
	private CheckedInAtRepository checkedInAtRepository;

	public List<Meeting> findAllDashboard(Long idConference) {
		List<Meeting> meetings = new ArrayList<>();
				
		meetingRepository
			.findAllDashboard(idConference)
			.iterator()
			.forEachRemaining(meetings::add);
		return meetings;
	}

	public Page<Meeting> findAll(Long idConference, String name, Date beginDate, Date endDate, List<Long> localities, Pageable pageable) {
		List<Long> loc = localities == null ? new ArrayList<>() : localities;
		return meetingRepository.findAll(idConference, name, beginDate, endDate, loc, pageable);
	}

	public Meeting findById(Long meetingId) {
		if(meetingId == null) {
			throw new IllegalArgumentException("Meeting id must be informed.");
		}

		return meetingRepository
				.findById(meetingId).orElseThrow(() -> new IllegalArgumentException("Meeting not found"));
	}

	@Transactional
	public Meeting save(Meeting meeting, MeetingParamDto meetingParamDto) {
		if(meeting.getLocalityPlace() == null || meeting.getLocalityPlace().getId() == null) {
			throw new IllegalArgumentException("Locality to 'TAKES_PLACE_AT' is required");
		}
		
		if(meeting.getLocalityCovers() == null || meeting.getLocalityCovers().isEmpty()) {
			throw new IllegalArgumentException("coverage locations is required");
		}
		
		if(meeting.getConference() == null || meeting.getConference().getId() == null) {
			throw new IllegalArgumentException("Conference is required");
		}

		Conference conf = conferenceService.find(meeting.getConference().getId());
		validateMeetingIntervalDate(conf, meetingParamDto);

		Locality localityPlace = localityService.find(meeting.getLocalityPlace().getId());
		Set<Locality> localityCovers = new HashSet<>();

		meeting.getLocalityCovers().forEach(locality -> {
			Locality localityTemp = localityService.find(locality.getId());
			if(localityTemp != null) {
				localityCovers.add(localityTemp);
			}
		});

		if(conf != null && localityPlace != null && !localityCovers.isEmpty()) {
			meeting.setConference(conf);
			meeting.setLocalityPlace(localityPlace);
			meeting.setLocalityCovers(localityCovers);
			Meeting meetingResponse = meetingRepository.save(meeting);

			Meeting meetingUpdate = findWithoutConference(meetingResponse.getId());
			loadReceptionist(meeting, meetingUpdate, meetingParamDto);
			return meetingRepository.save(meetingUpdate);
		}
		return null;
	}

	public Meeting findWithoutConference(Long id) {
		return meetingRepository.findMeetingWithoutConference(id).orElseThrow(() -> new IllegalArgumentException("Meeting not found: " + id));
	}

	public Meeting find(Long id) {
		return meetingRepository.findMeetingWithRelationshipsById(id).orElseThrow(() -> new IllegalArgumentException("Meeting not found: " + id));
	}

	@Transactional
	public Meeting update(Meeting meeting, MeetingParamDto meetingParamDto) {
		validate(meeting, meetingParamDto);
		
		meeting.setName(meetingParamDto.getName());
		meeting.setAddress(meetingParamDto.getAddress());
		meeting.setPlace(meetingParamDto.getPlace());
		meeting.setBeginDate(meetingParamDto.getBeginDate());
		meeting.setEndDate(meetingParamDto.getEndDate());
		
		loadAttributes(meeting, meetingParamDto);
		
		return meetingRepository.save(meeting);
	}
	
	private void loadAttributes(Meeting meeting, MeetingParamDto meetingParamDto) {
		Meeting meetingUpdate = findWithoutConference(meeting.getId());
		if(meetingUpdate.getConference() == null || !meetingUpdate.getConference().getId().equals(meetingParamDto.getConference())) {
			Conference conf = conferenceService.find(meetingParamDto.getConference());
			if(conf != null) {
				meetingUpdate.setConference(null);
				meeting.setConference(conf);
			}
		}
		if(meetingUpdate.getLocalityPlace().getId() != meetingParamDto.getLocalityPlace()) {
			Locality newLocality = localityService.find(meetingParamDto.getLocalityPlace());

			if(newLocality != null) {
				meetingUpdate.setLocalityPlace(null);
				meeting.setLocalityPlace(newLocality);
			}
		}

		if(meetingParamDto.getLocalityCovers() != null && !meetingParamDto.getLocalityCovers().isEmpty()) {
			Set<Locality> covers = new HashSet<>();
			meetingUpdate.getLocalityCovers().clear();
			meetingParamDto.getLocalityCovers().forEach(locality -> {
				covers.add(localityService.find(locality));
				meeting.setLocalityCovers(covers);
			});
		}
		loadReceptionist(meeting, meetingUpdate, meetingParamDto);
		meetingRepository.save(meetingUpdate);
	}
	
	private void loadReceptionist(Meeting meeting, Meeting meetingUpdate, MeetingParamDto meetingParamDto) {
		if((meetingParamDto.getReceptionists() != null && !meetingParamDto.getReceptionists().isEmpty()) ||
				(meetingParamDto.getReceptionistEmails() != null && !meetingParamDto.getReceptionistEmails().isEmpty())) {
			if(meetingUpdate.getReceptionists() != null) {
				meetingUpdate.getReceptionists().clear();
			}
			meeting.setReceptionists(new HashSet<>());
			meeting.getReceptionists().addAll(getReceptionist(meetingParamDto.getReceptionistEmails(), meetingParamDto.getReceptionists()));			
		}
	}
	
	private Set<Person> getReceptionist(List<String> emails, List<Long> idsReceptionist) {
		Set<Person> receptionists = new HashSet<>();
		if(idsReceptionist != null && !idsReceptionist.isEmpty()) {
			idsReceptionist.forEach(receptionist -> receptionists.add(personService.find(receptionist)));
		}
		if(emails != null && !emails.isEmpty()) {
			emails.forEach(receptionistEmail -> {
				Optional<Person> person = personService.findByContactEmail(receptionistEmail);
				if(person.isPresent()) {
					receptionists.add(person.get());
				}
			});
		}
		return receptionists;
	}
	
	private void validate(Meeting meeting, MeetingParamDto meetingParamDto) {
		if(meetingParamDto.getLocalityPlace() == null) {
			throw new IllegalArgumentException("Locality to 'TAKES_PLACE_AT' is required");
		}

		if(meetingParamDto.getLocalityCovers() == null || meetingParamDto.getLocalityCovers().isEmpty()) {
			throw new IllegalArgumentException("coverage locations is required");
		}

		if(meetingParamDto.getConference() == null) {
			throw new IllegalArgumentException("Conference is required");
		}

		Conference conf = conferenceService.find(meetingParamDto.getConference());
		validateMeetingIntervalDate(conf, meetingParamDto);

		if(meeting.getParticipants() != null) {
			throw new IllegalArgumentException("Meeting cannot be updated as it has registration of participant(s)");
		}
	}

	private void validateMeetingIntervalDate(Conference conf, MeetingParamDto meetingParamDto) {
		if(meetingParamDto.getBeginDate().before(conf.getBeginDate()) ||
				meetingParamDto.getBeginDate().after(conf.getEndDate())) {
			throw new IllegalArgumentException("Meeting begin date must be in Conference date range");
		}
		if(meetingParamDto.getEndDate().before(conf.getBeginDate()) ||
				meetingParamDto.getEndDate().after(conf.getEndDate())) {
			throw new IllegalArgumentException("Meeting end date must be in Conference date range");
		}
		if(meetingParamDto.getBeginDate().after(meetingParamDto.getEndDate())) {
			throw new IllegalArgumentException("Meeting begin date must be before end date");
		}
	}

	@Transactional
	public Boolean delete(Long id) {
		Set<CheckedInAt> checkedInAt = this.findCheckedInAtByMeeting(id);
		if(!checkedInAt.isEmpty()) {
			throw new IllegalArgumentException("Meeting cannot be deleted as it has registration of participant(s)");
		}
		Meeting meeting = this.find(id);
		meetingRepository.delete(meeting);
		return true;
	}

	@Transactional
	public CheckedInAt checkInOnMeeting(Long personId, Long meetingId) {
		Meeting meeting = this.find(meetingId);
		Person person = personService.find(personId);

		if(person != null && meeting != null) {
			Optional<CheckedInAt> checkedInAt = checkedInAtRepository.findByPersonAndMeeting(personId, meetingId);
			if(!checkedInAt.isPresent()) {
				CheckedInAt newParticipant = new CheckedInAt(person,meeting);
				return checkedInAtRepository.save(newParticipant);
			}
			throw new IllegalArgumentException("Person is already participating.");
		}
		throw new IllegalArgumentException("Person or Meeting not found.");
	}

	public Set<CheckedInAt> findCheckedInAtByMeeting(Long meetingId) {
		Meeting meeting = this.find(meetingId);
		if(meeting != null) {
			return checkedInAtRepository.findByMeeting(meetingId);
		}
		return Collections.emptySet();
	}

	@Transactional
	public Boolean deleteParticipation(Long personId, Long meetingId) {
		Meeting meeting = this.find(meetingId);
		Person person = personService.find(personId);
		if(person != null && meeting != null) {
			Optional<CheckedInAt> checkedInAt = checkedInAtRepository.findByPersonAndMeeting(personId, meetingId);

			if(checkedInAt.isPresent()) {
				checkedInAtRepository.delete(checkedInAt.get());
				return true;
			}
		}
		return false;
	}
}
