package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.CheckedInAt;
import br.gov.es.participe.model.Meeting;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.service.MeetingService;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.service.QRCodeService;

import br.gov.es.participe.util.interfaces.ApiPageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.logging.Level;
import java.util.logging.Logger;
/* Início das importações */
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.ContentDisposition;
/* Fim das importações */
import javax.imageio.ImageIO;

import com.google.zxing.WriterException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;



@RestController
@CrossOrigin
@RequestMapping(value = "/meetings")
public class MeetingController {

  @Autowired
  private MeetingService meetingService;

  @Autowired
  private PersonService personService;

  @Autowired
  private QRCodeService qrCodeService;

  @GetMapping("/{idConference}/page-number")
  public ResponseEntity<Object> findPageNumberByConference(
      @ApiIgnore Pageable pageable,
      @PathVariable("idConference") Long idConference,
      @RequestParam(value = "currentDate") @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date currentDate,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "beginDate", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date beginDate,
      @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date endDate,
      @RequestParam(value = "localities", required = false) List<Long> localities) {
    Integer pageNumber = meetingService.findNumberPageMeeting(
        currentDate,
        idConference,
        name,
        beginDate,
        endDate,
        localities,
        pageable);

    return ResponseEntity.status(200).body(new Object() {
      final Integer page = pageNumber;

      @SuppressWarnings("unused")
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
      @RequestParam(value = "localities", required = false) List<Long> localities) {

    Page<MeetingDto> meetings = meetingService.findAll(
        idConference,
        name,
        beginDate,
        endDate,
        localities,
        pageable);

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


  @Transactional
  @PostMapping
  public ResponseEntity<MeetingDto> store(
      @RequestHeader(name = "Authorization") String token,
      @RequestBody MeetingParamDto meetingParamDto) {

    if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
      return ResponseEntity.status(401).body(null);
    }
    Meeting meeting = new Meeting(meetingParamDto, false);
    Meeting saveMeeting = meetingService.save(meeting, meetingParamDto);

    if (saveMeeting != null) {
      MeetingDto response = new MeetingDto(saveMeeting, false);
      return ResponseEntity.status(200).body(response);
    }
    return ResponseEntity.noContent().build();
  }


  @Transactional
  @PutMapping("/{id}")
  public ResponseEntity<MeetingDto> update(
      @RequestHeader(name = "Authorization") String token,
      @PathVariable Long id,
      @RequestBody MeetingParamDto meetingParamDto) {
    if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
      return ResponseEntity.status(401).body(null);
    }
    Meeting meeting = meetingService.findWithoutConference(id);

    if (meeting != null) {
      Meeting meetingUpdated = meetingService.update(meeting, meetingParamDto);
      MeetingDto response = new MeetingDto(meetingUpdated, false);
      return ResponseEntity.ok().body(response);
    } else {
      return ResponseEntity.noContent().build();
    }
  }

  
  @Transactional
  @DeleteMapping("/{id}")
  public ResponseEntity<Boolean> destroy(
      @RequestHeader(name = "Authorization") String token,
      @PathVariable Long id) {
    if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
      return ResponseEntity.status(401).body(null);
    }
    Boolean response = meetingService.delete(id);
    return ResponseEntity.status(200).body(response);
  }


  @Transactional
  @PostMapping("/checkIn")
  public ResponseEntity<CheckedInAtDto> checkInOnMeeting(
      @RequestHeader(name = "Authorization") String token,
      @RequestBody CheckInParamDto checkInParamDto) {
    if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Recepcionist" })) {
      return ResponseEntity.status(401).body(null);
    }
    if (checkInParamDto == null ||
        checkInParamDto.getPersonId() == null ||
        checkInParamDto.getMeetingId() == null) {
      throw new IllegalArgumentException("An object with Person Id and Meeting Id parameters must be informed.");
    }

    CheckedInAt checkedInAt = meetingService.checkInOnMeeting(
        checkInParamDto.getPersonId(),
        checkInParamDto.getMeetingId(),
        checkInParamDto.getTimeZone());

    if (checkedInAt != null) {
      return ResponseEntity.ok().body(new CheckedInAtDto(checkedInAt));
    }

    return ResponseEntity.noContent().build();
  }

  @Transactional
  @PostMapping("/selfcheckIn")
  public ResponseEntity<CheckedInAtDto> selfCheckInOnMeeting(
      @RequestHeader(name = "Authorization") String token,
      @RequestBody CheckInParamDto checkInParamDto) {

    if (checkInParamDto == null ||
        checkInParamDto.getPersonId() == null ||
        checkInParamDto.getMeetingId() == null) {
      throw new IllegalArgumentException("An object with Person Id and Meeting Id parameters must be informed.");
    }

    CheckedInAt checkedInAt = meetingService.checkInOnMeeting(
        checkInParamDto.getPersonId(),
        checkInParamDto.getMeetingId(),
        checkInParamDto.getTimeZone());

    if (checkedInAt != null) {
      return ResponseEntity.ok().body(new CheckedInAtDto(checkedInAt));
    }

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/checkIn/{meetingId}")
  public ResponseEntity<CheckedInAtDto> CheckInOnMeetingByPerson(@PathVariable Long meetingId,
    @RequestParam(name = "personId", required = true) Long personId) {
      CheckedInAt checkinAt = meetingService.findByPersonAndMeeting(personId, meetingId);
      
      return ResponseEntity.ok().body(new CheckedInAtDto(checkinAt));

  }
  

  @GetMapping("/{conferenceId}/targeted-by/plan-items")
  public ResponseEntity<List<PlanItemComboDto>> findAllItems(@PathVariable Long conferenceId) {
    List<PlanItemComboDto> response = meetingService.findPlanItemsFromConference(conferenceId);
    return ResponseEntity.ok().body(response);
  }

  @ApiPageable
  @GetMapping("/{meetingId}/participants")
  public ResponseEntity<Page<PersonMeetingFilteredDto>> findMeetingParticipants(@PathVariable Long meetingId,
      @RequestParam(name = "localities", required = false, defaultValue = "") List<Long> localities,
      @RequestParam(name = "name", required = false) String name, 
      @RequestParam(name = "filter", required = true) String filter,
      @ApiIgnore Pageable page) {

      Page<PersonMeetingFilteredDto> personMeetingFilteredDto = personService.findPersonOnMeetingByAttendanceFilterPaged(meetingId, localities, name, filter, page);

      return ResponseEntity.ok().body(personMeetingFilteredDto);
    
  }

  @GetMapping("/{meetingId}/total")
  public ResponseEntity<Map<String, Long>> countTotalParticipantsInMeeting(
          @PathVariable Long meetingId,
          @RequestParam(name = "localities", required = false, defaultValue = "") List<Long> localities,
          @RequestParam(name = "name", required = false) String name,
          @RequestParam(name = "filter", required = true) String filter
  ) {

    Map<String, Long> totalParticipants = personService.countTotalParticipantsInMeeting(meetingId, localities, name, filter);

    return ResponseEntity.ok().body(totalParticipants);

  }
  
  @Transactional
  @DeleteMapping("/{meetingId}/remove-participation/{personId}")
  public ResponseEntity<Boolean> removeMeetingParticipation(
      @RequestHeader(name = "Authorization") String token,
      @PathVariable Long personId,
      @PathVariable Long meetingId) {
    if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Recepcionist" })) {
      return ResponseEntity.status(401).body(null);
    }
    Boolean response = meetingService.deleteParticipation(personId, meetingId);
    return ResponseEntity.ok().body(response);
  }
  @ApiPageable
  @GetMapping("/{meetingId}/persons")
  public ResponseEntity<Page<PersonMeetingDto>> findPersonForMeeting(
      @RequestHeader(name = "Authorization") String token,
      @PathVariable Long meetingId,
      @RequestParam(name = "name", required = false, defaultValue = "") String name,
      Pageable pageable) {
    if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Recepcionist" })) {
      return ResponseEntity.status(401).body(null);
    }
    Page<PersonMeetingDto> personMeetingDtoPage = personService.findPersonForMeeting(meetingId, name, pageable);
    return ResponseEntity.status(200).body(personMeetingDtoPage);
  }

  @GetMapping("/receptionistByEmail")
  public ResponseEntity<PersonDto> findRecepcionistByEmail(
      @RequestHeader(name = "Authorization") String token,
      @RequestParam("email") String email) {
    if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
      return ResponseEntity.status(401).body(null);
    }
    Optional<Person> personOpt = personService.findByContactEmail(email);

    return personOpt.map(
        person -> ResponseEntity.status(200).body(new PersonDto(person)))
        .orElseGet(() -> ResponseEntity.noContent().build());
  }

  @GetMapping("/{meetingId}/generate-link-pre-registration")
  @ResponseBody
  public ResponseEntity  generateMeetingLink(@PathVariable Long meetingId){
    String  UrlMeeting = meetingService.generateMeetingLink(meetingId);
    Map<String, Object> meeting = new HashMap<>();
    meeting.put("url",UrlMeeting);
    return ResponseEntity.status(200).body(meeting);
  }

  @GetMapping("/{meetingId}/generate-qr-code-check-in")
  public ResponseEntity<byte[]> getMethodName(@PathVariable Long meetingId) throws WriterException, IOException {
    String UrlMeeting = meetingService.generateAutoCheckInLink(meetingId);
    byte[] imageQR = qrCodeService.generateQRCode(UrlMeeting, 300, 300);
    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageQR);
    
  }

  @GetMapping("/{meetingId}/self-check-in")
  public ResponseEntity<Boolean> selfCheckInOpen(@PathVariable Long meetingId) {

        return ResponseEntity.ok(meetingService.selfCheckInIsOpen(meetingId));

  }

  @GetMapping("/{meetingId}/pre-registration")
  public ResponseEntity<Map<String, Boolean>> preRegistrationOpen(@PathVariable Long meetingId) {

    return ResponseEntity.ok(meetingService.preRegistrationIsOpenAndMeetingStarted(meetingId));

  }
  
  
}
