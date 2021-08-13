package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.CheckInParamDto;
import br.gov.es.participe.controller.dto.CheckedInAtDto;
import br.gov.es.participe.controller.dto.MeetingDto;
import br.gov.es.participe.controller.dto.MeetingParamDto;
import br.gov.es.participe.controller.dto.PersonDto;
import br.gov.es.participe.controller.dto.PersonMeetingDto;
import br.gov.es.participe.controller.dto.PlanItemComboDto;
import br.gov.es.participe.model.CheckedInAt;
import br.gov.es.participe.model.Meeting;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.service.MeetingService;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.util.interfaces.ApiPageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping(value = "/meetings")
public class MeetingController {

  @Autowired
  private MeetingService meetingService;

  @Autowired
  private PersonService personService;

  @GetMapping("/{idConference}/page-number")
  public ResponseEntity<Object> findPageNumberByConference(
    @ApiIgnore Pageable pageable,
    @PathVariable("idConference") Long idConference,
    @RequestParam(value = "currentDate")  @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date currentDate,
    @RequestParam(value = "name", required = false) String name,
    @RequestParam(value = "beginDate", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date beginDate,
    @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date endDate,
    @RequestParam(value = "localities", required = false) List<Long> localities
  ) {
    Integer pageNumber = meetingService.findNumberPageMeeting(
      currentDate,
      idConference,
      name,
      beginDate,
      endDate,
      localities,
      pageable
    );

    return ResponseEntity.status(200).body(new Object(){
      final Integer page = pageNumber;

      public Integer getPage() {
        return page;
      }
    });
  }

  @ApiPageable
  @GetMapping("/{idConference}")
  public ResponseEntity<Page<MeetingDto>> index(
    @ApiIgnore Pageable pageable,
    @PathVariable("idConference") Long idConference,
    @RequestParam(value = "name", required = false) String name,
    @RequestParam(value = "beginDate", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date beginDate,
    @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date endDate,
    @RequestParam(value = "localities", required = false) List<Long> localities
  ) {

    Page<MeetingDto> meetings = meetingService.findAll(
      idConference,
      name,
      beginDate,
      endDate,
      localities,
      pageable
    );

    return ResponseEntity.status(200).body(meetings);
  }

  @GetMapping
  public ResponseEntity<MeetingDto> findById(@RequestParam(name = "meetingId", required = false) Long meetingId) {

    Meeting meeting = meetingService.findById(meetingId);
    MeetingDto meetingDto = new MeetingDto(meeting);

    return ResponseEntity.status(200).body(meetingDto);
  }

  @GetMapping("/dashboard/{idConference}")
  public ResponseEntity<List<MeetingDto>> dashboardIndex(@PathVariable Long idConference) {

    List<Meeting> meetings = meetingService.findAllDashboard(idConference);
    List<MeetingDto> response = new ArrayList<>();

    meetings.forEach(meeting -> response.add(new MeetingDto(meeting)));
    return ResponseEntity.status(200).body(response);
  }

  @PostMapping
  public ResponseEntity<MeetingDto> store(@RequestBody MeetingParamDto meetingParamDto) {
    Meeting meeting = new Meeting(meetingParamDto, false);
    Meeting saveMeeting = meetingService.save(meeting, meetingParamDto);

    if(saveMeeting != null) {
      MeetingDto response = new MeetingDto(saveMeeting, false);
      return ResponseEntity.status(200).body(response);
    }
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<MeetingDto> update(@PathVariable Long id, @RequestBody MeetingParamDto meetingParamDto) {
    Meeting meeting = meetingService.findWithoutConference(id);

    if(meeting != null) {
      Meeting meetingUpdated = meetingService.update(meeting, meetingParamDto);
      MeetingDto response = new MeetingDto(meetingUpdated, false);
      return ResponseEntity.ok().body(response);
    }
    else {
      return ResponseEntity.noContent().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Boolean> destroy(@PathVariable Long id) {
    Boolean response = meetingService.delete(id);
    return ResponseEntity.status(200).body(response);
  }

  @PostMapping("/checkIn")
  public ResponseEntity<CheckedInAtDto> checkInOnMeeting(@RequestBody CheckInParamDto checkInParamDto) {
    if(checkInParamDto == null || checkInParamDto.getPersonId() == null
       || checkInParamDto.getMeetingId() == null) {
      throw new IllegalArgumentException("An object with Person Id and Meeting Id parameters must be informed.");
    }
    CheckedInAt checkedInAt = meetingService.checkInOnMeeting(checkInParamDto.getPersonId(),
                                                              checkInParamDto.getMeetingId(),
                                                              checkInParamDto.getTimeZone()
    );
    if(checkedInAt != null) {
      CheckedInAtDto checkedInAtDto = new CheckedInAtDto(checkedInAt);
      return ResponseEntity.ok().body(checkedInAtDto);
    }
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{conferenceId}/targeted-by/plan-items")
  public ResponseEntity<List<PlanItemComboDto>> findAllItems(@PathVariable Long conferenceId) {
    List<PlanItemComboDto> response = meetingService.findPlanItemsFromConference(conferenceId);
    return ResponseEntity.ok().body(response);
  }

  @ApiPageable
  @GetMapping("/{meetingId}/participants")
  public ResponseEntity<Page<PersonMeetingDto>> findMeetingParticipants(@PathVariable Long meetingId,
                                                                        @RequestParam(name = "localities", required = false, defaultValue = "") List<Long> localities,
                                                                        @RequestParam(name = "name", required = false) String name, @ApiIgnore Pageable page) {
    Page<PersonMeetingDto> personMeetingDto = personService.findPersonsCheckedInOnMeeting(meetingId, localities,
                                                                                          name, page
    );
    return ResponseEntity.ok().body(personMeetingDto);
  }

  @GetMapping("/{meetingId}/participants/total")
  public ResponseEntity<Long> findMeetingParticipantsNumber(@PathVariable Long meetingId) {
    Long participantsQuantity = personService.findPeopleQuantityOnMeeting(meetingId);
    return ResponseEntity.ok().body(participantsQuantity);
  }

  @DeleteMapping("/{meetingId}/remove-participation/{personId}")
  public ResponseEntity<Boolean> removeMeetingParticipation(@PathVariable Long personId,
                                                            @PathVariable Long meetingId) {
    Boolean response = meetingService.deleteParticipation(personId, meetingId);
    return ResponseEntity.ok().body(response);
  }

  @ApiPageable
  @GetMapping("/{meetingId}/persons")
  public ResponseEntity<Page<PersonMeetingDto>> findPersonForMeeting(@PathVariable Long meetingId,
                                                                     @RequestParam(name = "name", required = false, defaultValue = "") String name, Pageable pageable) {

    Page<PersonMeetingDto> personMeetingDtoPage = personService.findPersonForMeeting(meetingId, name, pageable);
    return ResponseEntity.status(200).body(personMeetingDtoPage);
  }

  @GetMapping("/receptionistByEmail")
  public ResponseEntity<PersonDto> findReceptionistByEmail(@RequestParam("email") String email) {
    Optional<Person> personOpt = personService.findByContactEmail(email);

    return personOpt.map(
      person -> ResponseEntity.status(200).body(new PersonDto(person))
    ).orElseGet(() -> ResponseEntity.noContent().build());
  }
}
