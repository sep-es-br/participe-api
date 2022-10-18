package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.MeetingDto;
import br.gov.es.participe.controller.dto.MeetingParamDto;
//import br.gov.es.participe.controller.dto.PersonParamDto;
import br.gov.es.participe.controller.dto.PlanItemComboDto;
import br.gov.es.participe.model.Channel;
import br.gov.es.participe.model.CheckedInAt;
import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Meeting;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.repository.CheckedInAtRepository;
import br.gov.es.participe.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

  @Transactional
  public Meeting save(Meeting meeting, MeetingParamDto meetingParamDto) {
    validateMeetingFields(meeting, meetingParamDto);

    Conference conference = conferenceService.find(meeting.getConference().getId());

    validateMeetingIntervalDate(conference, meetingParamDto);

    Set<Locality> localityCovers = extractLocalityFrom(meeting);

    Set<PlanItem> planItems = extractPlanItemFrom(meetingParamDto);

    if (localityCovers.isEmpty()) {
      return null;
    }

    if (!meetingParamDto.getType().equals(VIRTUAL)) {
      Locality localityPlace = localityService.find(meeting.getLocalityPlace().getId());
      if (localityPlace == null) {
        return null;
      }
      meeting.setLocalityPlace(localityPlace);
    }

    saveOrUpdateChannel(meeting, meetingParamDto);

    meeting.setConference(conference);
    meeting.setPlanItems(planItems);
    meeting.setTypeMeetingEnum(meetingParamDto.getType());
    meeting.setLocalityCovers(localityCovers);

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

  @Transactional
  public Meeting update(Meeting meeting, MeetingParamDto meetingParamDto) {
    validate(meeting, meetingParamDto);

    meeting.setName(meetingParamDto.getName());
    meeting.setAddress(meetingParamDto.getAddress());
    meeting.setPlace(meetingParamDto.getPlace());
    meeting.setBeginDate(meetingParamDto.getBeginDate());
    meeting.setEndDate(meetingParamDto.getEndDate());

    updateRelationships(meeting, meetingParamDto);

    return meetingRepository.save(meeting);
  }
  
  @Transactional
  private void updateRelationships(Meeting meeting, MeetingParamDto meetingParamDto) {
    Meeting meetingUpdate = findWithoutConference(meeting.getId());
    if (meetingUpdate.getConference() == null
        || !meetingUpdate.getConference().getId().equals(meetingParamDto.getConference())) {
      Conference conf = conferenceService.find(meetingParamDto.getConference());
      if (conf != null) {
        meetingUpdate.setConference(null);
        meeting.setConference(conf);
      }
    }
    if (!meetingUpdate.getLocalityPlace().getId().equals(meetingParamDto.getLocalityPlace())) {
      Locality newLocality = localityService.find(meetingParamDto.getLocalityPlace());

      if (newLocality != null) {
        meetingUpdate.setLocalityPlace(null);
        meeting.setLocalityPlace(newLocality);
      }
    }
    if (meetingParamDto.getSegmentations() != null && !meetingParamDto.getSegmentations().isEmpty()) {
      meetingUpdate.getPlanItems().clear();
      Set<PlanItem> planItems = new HashSet<>();
      meetingParamDto.getSegmentations().forEach(planItemId -> {
        planItems.add(planItemService.find(planItemId));
        meeting.setPlanItems(planItems);
      });
    }
    if (meetingParamDto.getLocalityCovers() != null && !meetingParamDto.getLocalityCovers().isEmpty()) {
      Set<Locality> covers = new HashSet<>();
      meetingUpdate.getLocalityCovers().clear();
      meetingParamDto.getLocalityCovers().forEach(locality -> {
        covers.add(localityService.find(locality));
        meeting.setLocalityCovers(covers);
      });
    }

    if (VIRTUAL.equals(meetingParamDto.getType())
        || PRESENCIAL_VIRTUAL.equals(meetingParamDto.getType())) {
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
          recInDb.getWelcomesMeetings().removeIf((rec) -> (rec.getId() == meetingUpdate.getId()));
          personService.save(recInDb, true);
          recs2Remove.add(recInDb);
        }
      });
      meetingUpdate.getReceptionists().removeAll(recs2Remove);

      // Add new receptionists
      meetingParamDto.getReceptionists().forEach((recId) -> {
        if (!(meetingUpdate.getReceptionists().stream().anyMatch((recInDb) -> (recInDb.getId() == recId)))) {
          Person newRec = personService.find(recId);
          if (newRec.getWelcomesMeetings() == null) {
            newRec.setWelcomesMeetings(new HashSet<Meeting>());
          }
          if (!newRec.getWelcomesMeetings().contains(meetingUpdate)) {
            newRec.getWelcomesMeetings().add(meetingUpdate);
          }
          //Person p = personService.find(newRec.getId());
       //   p.setWelcomesMeetings(newRec.getWelcomesMeetings());
          personService.save(newRec, true);
          if (meetingUpdate.getReceptionists() == null) {
            meetingUpdate.setReceptionists(new HashSet<Person>());
          }
          Set<Person> newRecList = meetingUpdate.getReceptionists();
          newRecList.add(newRec);
          meetingUpdate.setReceptionists(newRecList);
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

  @Transactional
  public Boolean delete(Long id) {
    Set<CheckedInAt> checkedInAt = this.findCheckedInAtByMeeting(id);
    if (!checkedInAt.isEmpty()) {
      throw new IllegalArgumentException("Meeting cannot be deleted as it has registration of participant(s)");
    }
    Meeting meeting = this.find(id);
    meetingRepository.delete(meeting);
    return true;
  }

  @Transactional
  public CheckedInAt checkInOnMeeting(Long personId, Long meetingId, String timeZone) {
    Meeting meeting = this.find(meetingId);
    Person person = personService.find(personId);
    if (person != null && meeting != null) {
      Optional<CheckedInAt> checkedInAt = checkedInAtRepository.findByPersonAndMeeting(personId, meetingId);
      if (!checkedInAt.isPresent()) {
        CheckedInAt newParticipant = timeZone == null ? new CheckedInAt(person, meeting)
            : new CheckedInAt(person, meeting, timeZone);
        return checkedInAtRepository.save(newParticipant);
      }
      throw new IllegalArgumentException("Person is already participating.");
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

  @Transactional
  public Boolean deleteParticipation(Long personId, Long meetingId) {
    Meeting meeting = this.find(meetingId);
    Person person = personService.find(personId);
    if (person != null && meeting != null) {
      Optional<CheckedInAt> checkedInAt = checkedInAtRepository.findByPersonAndMeeting(personId, meetingId);

      if (checkedInAt.isPresent()) {
        checkedInAtRepository.delete(checkedInAt.get());
        return true;
      }
    }
    return false;
  }

  public List<CheckedInAt> findCheckedInMeetingsByPerson(Long id) {
    return this.meetingRepository.findAllPersonCheckedIn(id);
  }
}
