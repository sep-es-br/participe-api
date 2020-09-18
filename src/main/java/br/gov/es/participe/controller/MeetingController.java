package br.gov.es.participe.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.CheckedInAt;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.util.interfaces.ApiPageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.gov.es.participe.model.Meeting;
import br.gov.es.participe.service.MeetingService;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@CrossOrigin
@RequestMapping(value = "/meetings")
public class MeetingController {

	@Autowired
    private MeetingService meetingService;

	@Autowired
	private PersonService personService;

	@ApiPageable
	@GetMapping("/{idConference}")
	public ResponseEntity<Page<MeetingDto>> index(@ApiIgnore Pageable pageable,
												  @PathVariable Long idConference,
												  @RequestParam(value = "name", required = false, defaultValue = "") String name,
												  @RequestParam(value = "beginDate", required = false) @DateTimeFormat(pattern="dd/MM/yyyy HH:mm:ss") Date beginDate,
												  @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern="dd/MM/yyyy HH:mm:ss") Date endDate,
												  @RequestParam(value = "localities", required = false, defaultValue = "") List<Long> localities) {

		Page<Meeting> meetings = meetingService.findAll(idConference, name, beginDate, endDate, localities, pageable);
		List<MeetingDto> response = new ArrayList<>();

		meetings.forEach(meeting -> response.add(new MeetingDto(meeting, false)));

		return ResponseEntity.status(200).body(new PageImpl<>(response, pageable, meetings.getTotalElements()));
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
			MeetingDto response = new MeetingDto(saveMeeting);
			return ResponseEntity.status(200).body(response);
		}
		return ResponseEntity.noContent().build();
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<MeetingDto> update(@PathVariable Long id, @RequestBody MeetingParamDto meetingParamDto) {
		Meeting meeting = meetingService.findWithoutConference(id);

		if(meeting != null) {
			MeetingDto response = new MeetingDto(meetingService.update(meeting,meetingParamDto));
			return ResponseEntity.ok().body(response);
		} else {
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
		if(checkInParamDto == null || checkInParamDto.getPersonId() == null || checkInParamDto.getMeetingId() == null) {
			throw new IllegalArgumentException("An object with Person Id and Meeting Id parameters must be informed.");
		}
		CheckedInAt checkedInAt = meetingService
				.checkInOnMeeting(checkInParamDto.getPersonId(), checkInParamDto.getMeetingId());

		if(checkedInAt != null) {
			CheckedInAtDto checkedInAtDto = new CheckedInAtDto(checkedInAt);
			return ResponseEntity.ok().body(checkedInAtDto);
		}
		return ResponseEntity.noContent().build();
	}

	@ApiPageable
	@GetMapping("/{meetingId}/participants")
	public ResponseEntity<Page<PersonMeetingDto>> findMeetingParticipants(@PathVariable Long meetingId,
																		  @RequestParam(name = "localities", required = false, defaultValue = "") List<Long> localities,
																		  @RequestParam(name = "name", required = false) String name,
																		  @ApiIgnore Pageable page) {
		Page<PersonMeetingDto> personMeetingDto = personService.findPersonsCheckedInOnMeeting(meetingId, localities, name, page);
		return ResponseEntity.ok().body(personMeetingDto);
	}

	@GetMapping("/{meetingId}/participants/total")
	public ResponseEntity<Long> findMeetingParticipantsNumber(@PathVariable Long meetingId) {
		Long participantsQuantity = personService.findPeopleQuantityOnMeeting(meetingId);
		return ResponseEntity.ok().body(participantsQuantity);
	}

	@DeleteMapping("/{meetingId}/remove-participation/{personId}")
	public ResponseEntity<Boolean> removeMeetingParticipation(@PathVariable Long personId, @PathVariable Long meetingId) {
		Boolean response = meetingService.deleteParticipation(personId, meetingId);
		return ResponseEntity.ok().body(response);
	}

	@ApiPageable
	@GetMapping("/{meetingId}/persons")
	public ResponseEntity<Page<PersonMeetingDto>> findPersonForMeeting(@PathVariable Long meetingId,
																	   @RequestParam(name = "name", required = false, defaultValue = "") String name,
																	   Pageable pageable) {

		Page<PersonMeetingDto> personMeetingDtoPage = personService.findPersonForMeeting(meetingId, name, pageable);
		return ResponseEntity.status(200).body(personMeetingDtoPage);
	}

	@GetMapping("/receptionistByEmail")
	public ResponseEntity<PersonDto> findReceptionistByEmail(@RequestParam("email") String email) {
		Optional<Person> personOpt = personService.findByContactEmail(email);

		if(personOpt.isPresent()) {
			return ResponseEntity.status(200).body(new PersonDto(personOpt.get()));
		} else {
			return ResponseEntity.noContent().build();
		}
	}
}
