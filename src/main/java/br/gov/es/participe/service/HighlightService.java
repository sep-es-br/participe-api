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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class HighlightService {

  private final HighlightRepository highlightRepository;

  private final PersonService personService;

  private final PlanItemService planItemService;

  private final MeetingService meetingService;

  private final CommentService commentService;

  private final LocalityService localityService;

  private final ConferenceService conferenceService;

  private static final Logger log = LoggerFactory.getLogger(HighlightService.class);

  @Autowired
  public HighlightService(
      HighlightRepository highlightRepository,
      PersonService personService,
      PlanItemService planItemService,
      MeetingService meetingService,
      CommentService commentService,
      LocalityService localityService,
      ConferenceService conferenceService) {
    this.highlightRepository = highlightRepository;
    this.personService = personService;
    this.planItemService = planItemService;
    this.meetingService = meetingService;
    this.commentService = commentService;
    this.localityService = localityService;
    this.conferenceService = conferenceService;
  }



  public Highlight save(Highlight highlight, String from) {

    if(highlight.getConference() == null){
      throw new IllegalArgumentException("conference cannot be null:highlight");
    }
    if(highlight.getLocality() == null){
      throw new IllegalArgumentException("Locality cannot be null:highlight");
    }

    PlanItem planItem = this.planItemService.find(highlight.getPlanItem().getId());

    Person person = this.personService.find(highlight.getPersonMadeBy().getId());

    log.info(
      "Consultando um highlight com os parâmetros personId={}, planItemId={}, conferenceId={} e localityId={}",
      person.getId(),
      planItem.getId(),
      highlight.getConference().getId(),
      Optional.ofNullable(highlight.getLocality()).map(Locality::getId).orElse(null)
    );
    Highlight highlightBD = this.highlightRepository.findByIdPersonAndIdPlanItem(
        person.getId(),
        planItem.getId(),
        highlight.getConference().getId(),
        highlight.getLocality() != null ? highlight.getLocality().getId() : null
    );

    if (highlightBD == null) {
      log.info(
        "Não foi encontrado o highlight com os parâmetros personId={}, planItemId={}, conferenceId={} e localityId={}",
        person.getId(),
        planItem.getId(),
        highlight.getConference().getId(),
        Optional.ofNullable(highlight.getLocality()).map(Locality::getId).orElse(null)
      );
      return this.createHighlight(highlight, from, planItem, person);
    } else {
      highlightBD.setConference(highlight.getConference());
      log.info(
        "Alterando conference de oldConferenceId={} para newConferenceId={} do highlightId={}",
        highlightBD.getConference().getId(),
        highlight.getConference().getId(),
        highlightBD.getId()
      );
      highlightBD.setLocality(highlight.getLocality());
      log.info(
        "Alterando conference de oldLocalityId={} para newLocalityId={} do highlightId={}",
        highlightBD.getLocality().getId(),
        highlight.getLocality().getId(),
        highlightBD.getId()
      );
      return this.removeHighlight(highlightBD);
    }
  }

  public Highlight removeHighlight(Highlight highlight) {
    Long idLocality = highlight.getLocality() != null ? highlight.getLocality().getId() : null;

    log.info(
      "Consultando lista de comentários relacionados ao highlightId={} utilizando os parâmetros personMadeById={}, planItemId={}, conferenceId={} e localityId={}",
      highlight.getId(),
      highlight.getPersonMadeBy().getId(),
      highlight.getPlanItem().getId(),
      highlight.getConference().getId(),
      idLocality
    );

    List<Comment> commentLst = this.commentService.find(
        highlight.getPersonMadeBy().getId(),
        highlight.getPlanItem().getId(),
        highlight.getConference().getId(),
        idLocality
    );

    // If there is no valid comments for that highlight, delete it.
    if (commentLst == null || commentLst.isEmpty()) {
      log.info(
        "Não foi encontrado nenhum comentário relacionado ao highlightId={}, apagando o Highlight",
        highlight.getId()
      );
      highlightRepository.deleteById(highlight.getId());
      return null;
    }

    return highlight;
  }


  public boolean isPresentialMeeting(Meeting m) {
    return PRESENCIAL.equals(m.getTypeMeetingEnum()) || PRESENCIAL_VIRTUAL.equals(m.getTypeMeetingEnum());
  }

  public boolean isTodayInMeetingPeriod(Date date, Meeting m) {
    return date.after(m.getBeginDate()) && date.before(m.getEndDate());
  }


  private Highlight createHighlight(Highlight highlight, String from, PlanItem planItem, Person person) {
    Meeting meeting = null;
    if (highlight.getMeeting() != null) {
      meeting = this.meetingService.find(highlight.getMeeting().getId());
    }

    Locality locality = null;

    if (highlight.getLocality() != null) {
      locality = this.localityService.find(highlight.getLocality().getId());
    }

    Conference conference = null;
    if (highlight.getConference() != null) {
      conference = this.conferenceService.find(highlight.getConference().getId());
    }

    highlight.setConference(conference);
    highlight.setFrom(from);
    highlight.setMeeting(meeting);
    highlight.setPlanItem(planItem);
    highlight.setLocality(locality);
    highlight.setPersonMadeBy(person);
    highlight.setTime(new Date());

    highlight = this.highlightRepository.save(highlight);

    log.info("Highlight criado com sucesso highlightId={} com parâmetros conferenceId={}, from={}, meetingId={}, planItemId={}, localityId={}, personId={}",
            highlight.getId(),
            Optional.ofNullable(conference).map(Conference::getId).orElse(null),
            from,
            Optional.ofNullable(meeting).map(Meeting::getId).orElse(null),
            planItem.getId(),
            Optional.ofNullable(locality).map(Locality::getId).orElse(null),
            person.getId()
    );
    return highlight;
  }


  public void deleteAllByIdPerson(Long id) {
    log.info("Consultando highlights relacionados a personId={} para remover", id);
    List<Highlight> highlights = highlightRepository.findByIdPerson(id);
    log.info("Foram encontrados {} relacionados a personId={}", highlights.size(), id);
    for (Highlight highlight : highlights) {
      log.info("Removendo highlightId={} relacionado a personId={}", highlight.getId(), id);
      highlightRepository.delete(highlight);
    }
  }

  public void deleteById(Long highlightId) {
    this.highlightRepository.deleteById(highlightId);
  }


  public boolean delete(Highlight highlight) {
    List<Comment> comment = this.commentService.find(highlight.getPersonMadeBy().getId(),
        highlight.getPlanItem().getId(), highlight.getConference().getId(),
        highlight.getLocality().getId());

    if (comment == null || comment.isEmpty()) {
      highlightRepository.delete(highlight);
      return true;
    }
    return false;
  }

  public Highlight find(Long idPerson, Long idPlanItem, Long idConference, Long idLocality) {
    Highlight hlReturn = highlightRepository.findByIdPersonAndIdPlanItem(
        idPerson,
        idPlanItem,
        idConference,
        idLocality);

    if (hlReturn != null) {
      hlReturn.setConference(conferenceService.find(idConference));
      hlReturn.setPlanItem(planItemService.find(idPlanItem));
      hlReturn.setLocality(localityService.find(idLocality));
      hlReturn.setPersonMadeBy(personService.find(idPerson));
    }
    return hlReturn;
  }

  public List<Highlight> findAll(Long idPerson, Long idPlanItem, Long idConference, Long idLocality) {
    return highlightRepository.findAllByIdPersonAndIdPlanItemAndIdConferenceAndIdLocality(
        idPerson,
        idPlanItem,
        idConference,
        idLocality);
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

  public Integer countHighlightPresentialOriginByConference(Long id, List<Long> meetings) {
    return highlightRepository.countHighlightPresentialOriginByConference(id, meetings);
  }

}
