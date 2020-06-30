package br.gov.es.participe.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.gov.es.participe.controller.dto.MeetingDto;
import br.gov.es.participe.controller.dto.MeetingParamDto;
import br.gov.es.participe.model.Meeting;
import br.gov.es.participe.service.MeetingService;

@RestController
@CrossOrigin
@RequestMapping(value = "/meetings")
public class MeetingController {

	@Autowired
    private MeetingService meetingService;
	
	@GetMapping("/{idConference}")
	public ResponseEntity index(@PathVariable Long idConference) {
		
		List<Meeting> meetings = meetingService.findAll(idConference);
		List<MeetingDto> response = new ArrayList<>(); 
		
		meetings.forEach(meeting -> response.add(new MeetingDto(meeting)));
		return ResponseEntity.status(200).body(response);
	}
	
	@PostMapping
	public ResponseEntity store(@RequestBody MeetingParamDto meetingParamDto) {
		Meeting meeting = new Meeting(meetingParamDto);
		MeetingDto response = new MeetingDto(meetingService.save(meeting));
		return ResponseEntity.status(200).body(response);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity update(@PathVariable Long id, @RequestBody MeetingDto meetingDto) {
		meetingDto.setId(id);
		Meeting meeting = new Meeting(meetingDto);
		MeetingDto response = new MeetingDto(meetingService.save(meeting));
		return ResponseEntity.status(200).body(response);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity destroy(@PathVariable Long id) {
		meetingService.delete(id);
		return ResponseEntity.status(200).build();
	}
}
