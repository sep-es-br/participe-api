package br.gov.es.participe.service;

import static br.gov.es.participe.enumerator.TypeMeetingEnum.PRESENCIAL;
import static br.gov.es.participe.enumerator.TypeMeetingEnum.PRESENCIAL_VIRTUAL;
import br.gov.es.participe.model.Comment;
import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.Highlight;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Meeting;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.repository.HighlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class HighlightService {

  private final HighlightRepository highlightRepository;

  private final PersonService personService;

  private final PlanItemService planItemService;

  private final MeetingService meetingService;

  private final CommentService commentService;

  private final LocalityService localityService;

  private final ConferenceService conferenceService;

  @Autowired
  public HighlightService(
    HighlightRepository highlightRepository,
    PersonService personService,
    PlanItemService planItemService,
    MeetingService meetingService,
    CommentService commentService,
    LocalityService localityService,
    ConferenceService conferenceService
  ) {
    this.highlightRepository = highlightRepository;
    this.personService = personService;
    this.planItemService = planItemService;
    this.meetingService = meetingService;
    this.commentService = commentService;
    this.localityService = localityService;
    this.conferenceService = conferenceService;
  }

  public Highlight save(Highlight highlight, String from) {

    PlanItem planItem = planItemService.find(highlight.getPlanItem().getId());

    Person person = personService.find(highlight.getPersonMadeBy().getId());

    Highlight highlightBD = highlightRepository.findByIdPersonAndIdPlanItem(
      person.getId(),
      planItem.getId(),
      highlight.getConference().getId(),
      highlight.getLocality() != null ? highlight.getLocality().getId() : null
    );

    if(highlightBD == null) {
      return createHighlight(highlight, from, planItem, person);
    }
    else {
      return removeHighlight(highlight, highlightBD);
    }
  }

  private Highlight removeHighlight(Highlight highlight, Highlight highlightBD) {
    Long idLocality = highlight.getLocality() != null ? highlight.getLocality().getId() : null;

    List<Comment> comment = commentService.find(
      highlightBD.getPersonMadeBy().getId(),
      highlightBD.getPlanItem().getId(),
      highlight.getConference().getId(),
      idLocality
    );

    if(comment == null || comment.isEmpty()) {
      highlightRepository.delete(highlightBD);
      return null;
    }
    return highlightBD;
  }

  
  public boolean isPresentialMeeting(Meeting m) {
    return PRESENCIAL.equals(m.getTypeMeetingEnum()) || PRESENCIAL_VIRTUAL.equals(m.getTypeMeetingEnum());
  }

  public boolean isTodayInMeetingPeriod(Date date, Meeting m) {
    return date.after(m.getBeginDate()) && date.before(m.getEndDate());
  }



  private Highlight createHighlight(Highlight highlight, String from, PlanItem planItem, Person person) {
    Meeting meeting = null;
    if(highlight.getMeeting() != null) {
      meeting = meetingService.find(highlight.getMeeting().getId());
    }

    Locality locality = null;

    if(highlight.getLocality() != null) {
      locality = localityService.find(highlight.getLocality().getId());
    }

    Conference conference = null;
    if(highlight.getConference() != null) {
      conference = conferenceService.find(highlight.getConference().getId());
    }

    highlight.setConference(conference);
    highlight.setFrom(from);
    highlight.setMeeting(meeting);
    highlight.setPlanItem(planItem);
    highlight.setLocality(locality);
    highlight.setPersonMadeBy(person);
    highlight.setTime(new Date());

    highlight = highlightRepository.save(highlight);
    return highlight;
  }

  @Transactional
  public void deleteAllByIdPerson(Long id) {
    List<Highlight> highlights = highlightRepository.findByIdPerson(id);

    for(Highlight highlight : highlights) {
      highlightRepository.delete(highlight);
    }
  }

  public void deleteById(Long highlightId) {
    this.highlightRepository.deleteById(highlightId);
  }


  @Transactional
  public boolean delete(Highlight highlight) {
    List<Comment> comment = commentService.find(highlight.getPersonMadeBy().getId(),
                                                highlight.getPlanItem().getId(), highlight.getConference().getId(),
                                                highlight.getLocality().getId()
    );

    if(comment == null || comment.isEmpty()) {
      highlightRepository.delete(highlight);
      return true;
    }
    return false;
  }

  public Highlight find(Long idPerson, Long idPlanItem, Long idConference, Long idLocality) {
    return highlightRepository.findByIdPersonAndIdPlanItem(
      idPerson,
      idPlanItem,
      idConference,
      idLocality
    );
  }

  public List<Highlight> findAll(Long idPerson, Long idPlanItem, Long idConference, Long idLocality) {
    return highlightRepository.findAllByIdPersonAndIdPlanItemAndIdConferenceAndIdLocality(
      idPerson,
      idPlanItem,
      idConference,
      idLocality
    );
  }

  public Integer countHighlightByConference(Long id) {
    return highlightRepository.countHighlightByConference(id);
  }
  
  public Integer countHighlightAllOriginsByConference(Long id) {
	    return highlightRepository.countHighlightAllOriginsByConference(id);
  }
  
  public Integer countHighlightRemoteOriginByConference(Long id) {
	    return highlightRepository.countHighlightRemoteOriginByConference(id);
  }
  
  public Integer countHighlightPresentialOriginByConference(Long id,List<Long> meetings) {
	    return highlightRepository.countHighlightPresentialOriginByConference(id,meetings);
}
  
}
