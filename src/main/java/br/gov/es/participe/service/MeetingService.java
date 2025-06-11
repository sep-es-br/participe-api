package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.ConferenceDto;
import br.gov.es.participe.controller.dto.MeetingDto;
import br.gov.es.participe.controller.dto.MeetingParamDto;
import br.gov.es.participe.controller.dto.PlanItemComboDto;
import br.gov.es.participe.model.Channel;
import br.gov.es.participe.model.CheckedInAt;
import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Meeting;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.model.PortalServer;
import br.gov.es.participe.repository.CheckedInAtRepository;
import br.gov.es.participe.repository.MeetingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import static br.gov.es.participe.enumerator.TypeMeetingEnum.PRESENCIAL_VIRTUAL;
import static br.gov.es.participe.enumerator.TypeMeetingEnum.VIRTUAL;

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
  private PlanItemService planItemService;

  @Autowired
  private ChannelService channelService;

  @Autowired
  private CheckedInAtRepository checkedInAtRepository;

  @Autowired
  private PortalServerService portalServerService ;

  @Autowired
  private PreRegistrationService preRegistrationService;

  private final static Logger log = LoggerFactory.getLogger(MeetingService.class);


  public List<Meeting> findAllDashboard(Long idConference) {
    List<Meeting> meetings = new ArrayList<>();

    meetingRepository.findAllDashboard(idConference).iterator().forEachRemaining(meetings::add);
    return meetings;
  }

  public Integer findNumberPageMeeting(
      Date currentDate,
      Long idConference,
      String name,
      Date beginDate,
      Date endDate,
      List<Long> localities,
      Pageable pageable) {

    Integer pageNumber = 0;
    Page<Meeting> meetingsPage;

    do {
      meetingsPage = meetingRepository.findAll(
          idConference,
          name,
          beginDate,
          endDate,
          localities,
          pageable);

      List<Meeting> meetings = meetingsPage.getContent();

      boolean containsCurrentDate = this.verifyIfContainsCurrentDate(currentDate, meetings);

      if (containsCurrentDate) {
        return pageable.getPageNumber();
      }

      pageable = pageable.next();

    } while (meetingsPage.hasNext());

    return pageNumber;
  }

  private boolean verifyIfContainsCurrentDate(Date currentDate, List<Meeting> meetings) {
    for (Meeting meeting : meetings) {

      Calendar meetingDateCalendar = Calendar.getInstance();
      meetingDateCalendar.setTime(meeting.getBeginDate());

      Calendar currentDateCalendar = Calendar.getInstance();
      currentDateCalendar.setTime(currentDate);

      boolean sameYear = meetingDateCalendar.get(Calendar.YEAR) == currentDateCalendar.get(Calendar.YEAR);
      boolean sameMonth = meetingDateCalendar.get(Calendar.MONTH) == currentDateCalendar.get(Calendar.MONTH);
      boolean sameOrGreaterDay = meetingDateCalendar.get(Calendar.DAY_OF_MONTH) >= currentDateCalendar
          .get(Calendar.DAY_OF_MONTH);

      if (sameOrGreaterDay && sameMonth && sameYear) {
        return true;
      }
    }
    return false;
  }

  public Page<MeetingDto> findAll(
      Long idConference,
      String name,
      Date beginDate,
      Date endDate,
      List<Long> localities,
      Pageable pageable) {
    return meetingRepository.findAll(
        idConference,
        name,
        beginDate,
        endDate,
        localities,
        pageable).map(meeting -> new MeetingDto(meeting, false));
  }

  public Meeting findById(Long meetingId) {
    if (meetingId == null) {
      throw new IllegalArgumentException("Meeting id must be informed.");
    }

    return meetingRepository.findById(meetingId)
        .orElseThrow(() -> new IllegalArgumentException("Meeting not found"));
  }

  public List<PlanItemComboDto> findPlanItemsFromConference(Long idConference) {
    List<PlanItemComboDto> returnDto = new ArrayList<>();
    List<PlanItem> result = planItemService.findByIdConference(idConference);
    for (PlanItem planItem : result) {
      returnDto.add(new PlanItemComboDto(planItem.getId(), planItem.getName()));
    }

    return returnDto;
  }


  public Meeting save(Meeting meeting, MeetingParamDto meetingParamDto) {
    validateMeetingFields(meeting, meetingParamDto);

    Conference conference = conferenceService.find(meeting.getConference().getId());

    validateMeetingIntervalDate(conference, meetingParamDto);

    Set<Locality> localityCovers = extractLocalityFrom(meeting);

    log.info("{} localities encontradas para a meetingId={}", localityCovers.size(), meeting.getId());

    Set<PlanItem> planItems = extractPlanItemFrom(meetingParamDto);

    log.info("{} planItems encontradas para a meetingId={}", planItems.size(), meeting.getId());

    if (localityCovers.isEmpty()) {
      return null;
    }

    if (!meetingParamDto.getType().equals(VIRTUAL)) {
      Locality localityPlace = localityService.find(meeting.getLocalityPlace().getId());
      if (localityPlace == null) {
        return null;
      }
      log.info(
        "Meeting não é do tipo VIRTUAL, vinculando localityPlace localityId={} a meetingId={}",
        localityPlace.getId(),
        meeting.getId()
      );
      meeting.setLocalityPlace(localityPlace);
    }

    saveOrUpdateChannel(meeting, meetingParamDto);

    meeting.setConference(conference);
    meeting.setPlanItems(planItems);
    meeting.setTypeMeetingEnum(meetingParamDto.getType());
    meeting.setAttendanceListMode(meetingParamDto.getAttendanceListMode());
    meeting.setLocalityCovers(localityCovers);

    log.info(
      "Salvando meeting com os atributos conferenceId={}, {} planItems, typeMeeting={}, attendanceListMode={}, {} localityCovers",
      conference.getId(),
      planItems.size(),
      meetingParamDto.getType(),
      meetingParamDto.getAttendanceListMode(),
      localityCovers.size()
    );
    Meeting meetingResponse = meetingRepository.save(meeting);

    Meeting meetingUpdate = findWithoutConference(meetingResponse.getId());

    if (!meetingParamDto.getType().equals(VIRTUAL)) {
      makeRelationshipWithReceptionist(meetingUpdate, meetingParamDto);
    }

    return meetingRepository.save(meetingUpdate);
  }

  private Set<PlanItem> extractPlanItemFrom(MeetingParamDto meetingParamDto) {
    Set<PlanItem> planItems = new HashSet<>();
    meetingParamDto.getSegmentations().forEach(idSegment -> {
      PlanItem planItem = planItemService.find(idSegment);
      planItems.add(planItem);
    });
    return planItems;
  }

  private Set<Locality> extractLocalityFrom(Meeting meeting) {
    Set<Locality> localityCovers = new HashSet<>();

    meeting.getLocalityCovers().forEach(locality -> {
      Locality localityTemp = localityService.find(locality.getId());
      if (localityTemp != null) {
        localityCovers.add(localityTemp);
      }
    });
    return localityCovers;
  }

  private void saveOrUpdateChannel(Meeting meeting, MeetingParamDto meetingParamDto) {
    if (VIRTUAL.equals(meetingParamDto.getType())
        || PRESENCIAL_VIRTUAL.equals(meetingParamDto.getType())) {
      Set<Channel> channels = channelService.saveChannelsMeeting(meetingParamDto.getChannels(), meeting);
      log.info("Criando vinculo da meetingId={} com {} channels", meeting.getId(), channels.size());
      meeting.setChannels(channels);
    }
  }

  private void validateMeetingFields(Meeting meeting, MeetingParamDto meetingParamDto) {
    if ((meeting.getLocalityPlace() == null || meeting.getLocalityPlace().getId() == null)
        && !meetingParamDto.getType().equals(VIRTUAL)) {
      throw new IllegalArgumentException("Locality to 'TAKES_PLACE_AT' is required");
    }

    if (meeting.getLocalityCovers() == null || meeting.getLocalityCovers().isEmpty()) {
      throw new IllegalArgumentException("coverage locations is required");
    }

    if (meeting.getConference() == null || meeting.getConference().getId() == null) {
      throw new IllegalArgumentException("Conference is required");
    }

    if ((meetingParamDto.getSegmentations() == null || meetingParamDto.getSegmentations().isEmpty())
        && (meeting.getConference().getStructureItems() != null
            && !meeting.getConference().getStructureItems().isEmpty())) {
      throw new IllegalArgumentException("Segment is required");
    }
  }

  public Meeting findWithoutConference(Long id) {
    return meetingRepository.findMeetingWithoutConference(id)
        .orElseThrow(() -> new IllegalArgumentException("Meeting not found: " + id));
  }

  public Meeting find(Long id) {
    return meetingRepository.findMeetingWithRelationshipsById(id)
        .orElseThrow(() -> new IllegalArgumentException("Meeting not found: " + id));
  }


  public Meeting update(Meeting meeting, MeetingParamDto meetingParamDto) {
    validate(meeting, meetingParamDto);

    meeting.setName(meetingParamDto.getName());
    meeting.setAddress(meetingParamDto.getAddress());
    meeting.setPlace(meetingParamDto.getPlace());
    meeting.setBeginDate(meetingParamDto.getBeginDate());
    meeting.setEndDate(meetingParamDto.getEndDate());
    meeting.setAttendanceListMode(meetingParamDto.getAttendanceListMode());

    updateRelationships(meeting, meetingParamDto);

    return meetingRepository.save(meeting);
  }


  private void updateRelationships(Meeting meeting, MeetingParamDto meetingParamDto) {
    Meeting meetingUpdate = findWithoutConference(meeting.getId());
    if (meetingUpdate.getConference() == null
        || !meetingUpdate.getConference().getId().equals(meetingParamDto.getConference())) {
      Conference conf = conferenceService.find(meetingParamDto.getConference());
      if (conf != null) {
        meetingUpdate.setConference(null);
        log.info("Criando vinculo entre meetingId={} e conferenceId={}", meetingUpdate.getId(), conf.getId());
        meeting.setConference(conf);
      }
    }
    if (!meetingUpdate.getLocalityPlace().getId().equals(meetingParamDto.getLocalityPlace())) {
      Locality newLocality = localityService.find(meetingParamDto.getLocalityPlace());

      if (newLocality != null) {
        meetingUpdate.setLocalityPlace(null);
        meeting.setLocalityPlace(newLocality);
        log.info("Criando vinculo entre meetingId={} e localityPlaceId={}", meetingUpdate.getId(), newLocality.getId());
      }
    }
    if (meetingParamDto.getSegmentations() != null && !meetingParamDto.getSegmentations().isEmpty()) {
      meetingUpdate.getPlanItems().clear();
      Set<PlanItem> planItems = new HashSet<>();
      meetingParamDto.getSegmentations().forEach(planItemId -> {
        planItems.add(planItemService.find(planItemId));
        meeting.setPlanItems(planItems);
        log.info("Criando vinculo entre meetingId={} e planItemId={}", meetingUpdate.getId(), planItemId);
      });
    }
    if (meetingParamDto.getLocalityCovers() != null && !meetingParamDto.getLocalityCovers().isEmpty()) {
      Set<Locality> covers = new HashSet<>();
      meetingUpdate.getLocalityCovers().clear();
      meetingParamDto.getLocalityCovers().forEach(locality -> {
        covers.add(localityService.find(locality));
        meeting.setLocalityCovers(covers);
        log.info("Criando vinculo entre meetingId={} e localityCoversId={}", meetingUpdate.getId(), locality);
      });
    }

    if (VIRTUAL.equals(meetingParamDto.getType())
        || PRESENCIAL_VIRTUAL.equals(meetingParamDto.getType())) {
      log.info("Meeting com meetingId={} é VIRTUAL ou PRESENCIAL_VIRTUAL meetingType={}", meetingUpdate.getId(), meetingParamDto.getType());
      saveOrUpdateChannel(meeting, meetingParamDto);
    }

    makeRelationshipWithReceptionist(meeting, meetingParamDto);
    meetingRepository.save(meetingUpdate);
  }

  private void makeRelationshipWithReceptionist(Meeting meetingUpdate, MeetingParamDto meetingParamDto) {
    if (meetingParamDto.getReceptionists() != null) {

      // Remove excluded
      Set<Person> recs2Remove = new HashSet<Person>();
      meetingUpdate.getReceptionists().forEach((recInDb) -> {
        if (!(meetingParamDto.getReceptionists().contains(recInDb.getId()))) {
          recInDb.getWelcomesMeetings().removeIf((rec) -> (rec.getId().equals(meetingUpdate.getId())));
          personService.save(recInDb, true);
          log.info("Desvinculando receptionistId={} da meetingId={}", recInDb.getId(), meetingUpdate.getId());
          recs2Remove.add(recInDb);
        }
      });
      meetingUpdate.getReceptionists().removeAll(recs2Remove);

      // Add new receptionists
      meetingParamDto.getReceptionists().forEach((recId) -> {
        if (meetingUpdate.getReceptionists().stream().noneMatch((recInDb) -> (recInDb.getId().equals(recId)))) {
          log.info("Novo receptionist receptionistId={} informado para a meetingId={}", recId, meetingUpdate.getId());
          Person newRec = personService.find(recId);
          if (newRec.getWelcomesMeetings() == null) {
            newRec.setWelcomesMeetings(new HashSet<Meeting>());
          }
          if (!newRec.getWelcomesMeetings().contains(meetingUpdate)) {
            log.info("Criando relacionamento entre o receptionistId={} e a meetingId={}", newRec.getId(), meetingUpdate.getId());
            newRec.getWelcomesMeetings().add(meetingUpdate);
          }
          //Person p = personService.find(newRec.getId());
          //p.setWelcomesMeetings(newRec.getWelcomesMeetings());
          personService.save(newRec, true);
          if (meetingUpdate.getReceptionists() == null) {
            meetingUpdate.setReceptionists(new HashSet<Person>());
          }
          Set<Person> newRecList = meetingUpdate.getReceptionists();
          newRecList.add(newRec);
          meetingUpdate.setReceptionists(newRecList);
          log.info("Criando relacionamento entre o a meetingId={} e receptionistId={}", meetingUpdate.getId(), newRec.getId());
        }
      });
    }
  }

  private void validate(Meeting meeting, MeetingParamDto meetingParamDto) {
    if (meetingParamDto.getLocalityPlace() == null) {
      throw new IllegalArgumentException("Locality to 'TAKES_PLACE_AT' is required");
    }

    if (meetingParamDto.getLocalityCovers() == null || meetingParamDto.getLocalityCovers().isEmpty()) {
      throw new IllegalArgumentException("coverage locations is required");
    }

    if (meetingParamDto.getConference() == null) {
      throw new IllegalArgumentException("Conference is required");
    }

    Conference conf = conferenceService.find(meetingParamDto.getConference());
    validateMeetingIntervalDate(conf, meetingParamDto);

    if (meeting.getParticipants() != null && !meeting.getParticipants().isEmpty()) {
      throw new IllegalArgumentException("Meeting cannot be updated as it has registration of participant(s)");
    }
  }

  private void validateMeetingIntervalDate(Conference conf, MeetingParamDto meetingParamDto) {
    if (meetingParamDto.getBeginDate().before(conf.getBeginDate())
        || meetingParamDto.getBeginDate().after(conf.getEndDate())) {
      throw new IllegalArgumentException("conference.meeting.error.beginDateOutOfRange");
    }
    if (meetingParamDto.getEndDate().before(conf.getBeginDate())
        || meetingParamDto.getEndDate().after(conf.getEndDate())) {
      throw new IllegalArgumentException("conference.meeting.error.endDateOutOfRange");
    }
    if (meetingParamDto.getBeginDate().after(meetingParamDto.getEndDate())) {
      throw new IllegalArgumentException("conference.meeting.error.beginDateAfterEndDate");
    }
  }


  public Boolean delete(Long id) {
    log.info("Iniciando remoção da meeting com meetingId={}", id);
    Set<CheckedInAt> checkedInAt = this.findCheckedInAtByMeeting(id);
    log.info("Encontrado {} checkins para a meetingId={}", checkedInAt.size(), id);
    if (!checkedInAt.isEmpty()) {
      throw new IllegalArgumentException("Meeting cannot be deleted as it has registration of participant(s)");
    }
    Meeting meeting = this.find(id);
    log.info("Removendo meetingId={}", id);
    meetingRepository.delete(meeting);
    return true;
  }

  public CheckedInAt editCheckInOnMeeting(Long personId, Long meetingId, String timeZone, Boolean isAuthority, String organization, String role) {
    Meeting meeting = this.find(meetingId);
    Person person = personService.find(personId);

    if (person != null && meeting != null) {
        Optional<CheckedInAt> optionalCheckIn = checkedInAtRepository.findByPersonAndMeeting(personId, meetingId);

        if (optionalCheckIn.isPresent()) {
            CheckedInAt checkIn = optionalCheckIn.get();
            checkIn.setIsAuthority(Boolean.TRUE.equals(isAuthority) ? true : null);
            checkIn.setIsAnnounced(Boolean.TRUE.equals(isAuthority) ? false : null);
            checkIn.setOrganization(organization);
            checkIn.setRole(role);

            return checkedInAtRepository.save(checkIn);
        } else {
            throw new IllegalArgumentException("Check-in não encontrado para edição.");
        }
    }

    throw new IllegalArgumentException("Person ou Meeting não encontrados.");
}


  public CheckedInAt checkInOnMeeting(Long personId, Long meetingId, String timeZone, Boolean isAuthority, String organization, String role) {
    Meeting meeting = this.find(meetingId);
    Person person = personService.find(personId);
    if (person != null && meeting != null) {
      Optional<CheckedInAt> checkedInAt = checkedInAtRepository.findByPersonAndMeeting(personId, meetingId);
      
      
      
      return checkedInAt
              .map(checkIn -> {
                  checkIn.setIsAuthority(isAuthority);
                  checkIn.setOrganization(organization);
                  checkIn.setRole(role);
                  
                  return checkedInAtRepository.save(checkIn);
              
              })
              .orElseGet(() -> {
                     CheckedInAt newParticipant = timeZone == null ? new CheckedInAt(person, meeting)
                    : new CheckedInAt(person, meeting, timeZone);
                    newParticipant.setIsAuthority(Boolean.TRUE.equals(isAuthority) ? true : null);
                    newParticipant.setIsAnnounced(Boolean.TRUE.equals(isAuthority) ? false : null);
                    newParticipant.setOrganization(organization);
                    newParticipant.setRole(role);

                    preRegistrationService.saveCheckIn(personId, meetingId);
                log.info("Realizando checkin da personId={} na meetingId={} com timezone={}", personId, meetingId, timeZone);
                return checkedInAtRepository.save(newParticipant);
            });
    }
    throw new IllegalArgumentException("Person or Meeting not found.");
  }

  public Set<CheckedInAt> findCheckedInAtByMeeting(Long meetingId) {
    Meeting meeting = this.find(meetingId);
    if (meeting != null) {
      return checkedInAtRepository.findByMeeting(meetingId);
    }
    return Collections.emptySet();
  }

  public CheckedInAt findByPersonAndMeeting(Long personId, Long meetingId){
    Optional<CheckedInAt> checkedInAt = checkedInAtRepository.findByPersonAndMeeting(personId, meetingId);

    if (checkedInAt.isPresent())
      return checkedInAt.get();
    return null;
  }


  public Boolean deleteParticipation(Long personId, Long meetingId) {
    Meeting meeting = this.find(meetingId);
    Person person = personService.find(personId);
    if (person != null && meeting != null) {
      Optional<CheckedInAt> checkedInAt = checkedInAtRepository.findByPersonAndMeeting(personId, meetingId);

      if (checkedInAt.isPresent()) {
        log.info("Removendo participação da personId={} na meetingId={}", personId, meetingId);
        checkedInAtRepository.delete(checkedInAt.get());
        return true;
      }
    }
    return false;
  }

  public List<CheckedInAt> findCheckedInMeetingsByPerson(Long id) {
    return this.meetingRepository.findAllPersonCheckedIn(id);
  }

  public String generateMeetingLink(Long id) {
    Meeting meeting = this.find(id);
    PortalServer portalMeeting = this.portalServerService.findByIdConference(meeting.getConference().getId()).get();
    var urlMeeting = portalMeeting.getUrl() + "#/registration/"+ meeting.getConference().getId() +"/meeting/" + id;

    return urlMeeting;
  }

  public String generateAutoCheckInLink(Long id) {
    Meeting meeting = this.find(id);
    PortalServer portalMeeting = this.portalServerService.findByIdConference(meeting.getConference().getId()).get();
    var urlMeeting = portalMeeting.getUrl() + "#/self-check-in/"+ meeting.getConference().getId() +"/meeting/" + id;;

    return urlMeeting;
  }

  public Boolean selfCheckInIsOpen(Long id){

    Boolean selfCheckIn = meetingRepository.selfCheckInIsOpen(id);

    if(selfCheckIn != null){
      return selfCheckIn;
    }else{
      return false;
    }
  }

  public Map<String, Boolean> preRegistrationIsOpenAndMeetingStarted(Long id){

    Map<String, Boolean> preRegistration = new HashMap<>();

    Boolean preRegistrationMeetingStarted = meetingRepository.preRegistrationIsOpenAndMeetingStarted(id);

    Boolean preRegistrationMeetingClosed = meetingRepository.preRegistrationIsOpenAndMeetingClosed(id);
    
    preRegistration.put("preRegistrationMeetingStarted", preRegistrationMeetingStarted);
    preRegistration.put("preRegistrationMeetingClosed", preRegistrationMeetingClosed);

    return preRegistration;

  }
}
