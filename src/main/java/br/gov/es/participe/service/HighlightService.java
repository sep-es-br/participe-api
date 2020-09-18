package br.gov.es.participe.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.es.participe.model.Comment;
import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.Highlight;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Meeting;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.repository.HighlightRepository;

@Service
public class HighlightService {

	@Autowired
	private HighlightRepository highlightRepository;
	
	@Autowired 
	private PersonService personService;
	
	@Autowired
	private PlanItemService planItemService;
	
	@Autowired
	private MeetingService meetingService;
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private LocalityService localityService;
	
	@Autowired
	private ConferenceService conferenceService;
	
	public Highlight save(Highlight highlight, String from) {
		
		PlanItem planItem = planItemService.find(highlight.getPlanItem().getId());
		Person person = personService.find(highlight.getPersonMadeBy().getId());
		
		Highlight highlightBD = highlightRepository.findByIdPersonAndIdPlanItem(person.getId(),
																				planItem.getId(), highlight.getConference().getId());
		if(highlightBD == null) {
			Meeting meeting = null;
			if(highlight.getMeeting() != null)
				meeting = meetingService.find(highlight.getMeeting().getId());
						
			Locality locality = localityService.find(highlight.getLocality().getId());
			
			Conference conference = null;
			if(highlight.getConference() != null)
				conference = conferenceService.find(highlight.getConference().getId());
			
			highlight.setConference(conference);
			highlight.setFrom(from);
			highlight.setMeeting(meeting);
			highlight.setPlanItem(planItem);
			highlight.setLocality(locality);
			highlight.setPersonMadeBy(person);
			highlight.setTime(new Date());
		
			return highlightRepository.save(highlight);
		}
		List<Comment> comment = commentService.find(highlightBD.getPersonMadeBy().getId(),
													highlightBD.getPlanItem().getId(), 
													highlight.getConference().getId(), 
													highlight.getLocality().getId());
		
		if(comment == null || comment.isEmpty()) {
			highlightRepository.delete(highlightBD);
			return null;
		}
		return highlightBD;
	}
	
	@Transactional
	public void deleteAllByIdPerson(Long id) {
		List<Highlight> highlights = highlightRepository.findByIdPerson(id);
		
		for(Highlight highlight: highlights) {
			highlightRepository.delete(highlight);
		}
	}
	
	@Transactional
	public boolean delete(Highlight highlight) {
		List<Comment> comment = commentService.find(highlight.getPersonMadeBy().getId(),
												highlight.getPlanItem().getId(), highlight.getConference().getId(), highlight.getLocality().getId());
	
		if(comment == null || comment.isEmpty()) {
			highlightRepository.delete(highlight);
			return true;
		}
		return false;
	}
	
	public Highlight find(Long idPerson, Long idPlanItem, Long idConference) {
		return highlightRepository.findByIdPersonAndIdPlanItem(idPerson, idPlanItem, idConference);
	}
	
	public List<Highlight> findAll(Long idPerson, Long idPlanItem, Long idConference, Long idLocality) {
		return highlightRepository.findAllByIdPersonAndIdPlanItemAndIdConferenceAndIdLoclity(idPerson, idPlanItem, idConference, idLocality);
	}
	
	public Integer countHighlightByConference(Long id) {
		return highlightRepository.countHighlightByConference(id);
	}
}
