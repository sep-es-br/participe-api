package br.gov.es.participe.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Meeting;
import br.gov.es.participe.repository.MeetingRepository;

@Service
public class MeetingService {
	
	@Autowired
	private MeetingRepository meetingRepository;
	
	@Autowired
	private LocalityService localityService;
	
	
	@Autowired
	private ConferenceService conferenceService;
	
	
	public List<Meeting> findAll(Long idConference) {
		List<Meeting> meetings = new ArrayList<>();
				
		meetingRepository
			.findAll(idConference)
			.iterator()
			.forEachRemaining(meetings::add);
		return meetings;
	}
	
	@Transactional
	public Meeting save(Meeting meeting) {
		if(meeting.getLocalityPlace() == null || meeting.getLocalityPlace().getId() == null) {
			throw new IllegalArgumentException("Locality to 'TAKES_PLACE_AT' is required");
		}
		
		if(meeting.getLocalityCovers() == null || meeting.getLocalityCovers().isEmpty()) {
			throw new IllegalArgumentException("coverage locations is required");
		}
		
		if(meeting.getConference() == null || meeting.getConference().getId() == null) {
			throw new IllegalArgumentException("Conference is required");
		}
		
		meeting.setConference(conferenceService.find(meeting.getConference().getId()));
		
		if(meeting.getId() != null) {
			Meeting m = meetingRepository.findMeetingById(meeting.getId());
			
			if(m.getLocalityPlace().getId() != meeting.getLocalityPlace().getId()) {
				m.setLocalityPlace(null);
				Locality newLocality = localityService.find(meeting.getLocalityPlace().getId());
				//newLocality.setType(localityTypeService.findByIdLocality(newLocality.getId()));
				meeting.setLocalityPlace(newLocality);
			}
			
			if(!m.getLocalityCovers().isEmpty()) {
				m.getLocalityCovers().clear();
			}
			meetingRepository.save(m);
			
			Set<Locality> covers = new HashSet<>();
			meeting.getLocalityCovers().forEach(locality -> covers.add(localityService.find(locality.getId())));
			meeting.setLocalityCovers(covers);
		}
		return meetingRepository.save(meeting);
	}
	
	public Meeting find(Long id) {
		return meetingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Meeting not found: " + id));
	}
	
	@Transactional
	public void delete(Long id) {
		Meeting meeting = find(id);
		meetingRepository.delete(meeting);
	}
}
