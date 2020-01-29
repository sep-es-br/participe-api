package br.gov.es.participe.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.participe.controller.dto.ConferenceDto;
import br.gov.es.participe.model.Conference;
import br.gov.es.participe.service.ConferenceService;

@RestController
@CrossOrigin
@RequestMapping(value = "/conferences")
public class ConferenceController {

    @Autowired
    private ConferenceService conferenceService;

    @GetMapping
    public ResponseEntity index(@RequestParam(value = "name", required = false, defaultValue = "") String name
        , @RequestParam(value = "plan", required = false) Long plan
        , @RequestParam(value = "month", required = false) Integer month
        , @RequestParam(value = "year", required = false) Integer year) {
        List<Conference> conferences = conferenceService.findAll(name, plan, month, year);
        List<ConferenceDto> response = new ArrayList<>();

        conferences.forEach(conference -> response.add(new ConferenceDto(conference)));

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/validate")
    public ResponseEntity validate(@RequestParam(value = "name", required = false, defaultValue = "") String name
        , @RequestParam(value = "id", required = false) Long id) {
        return ResponseEntity.status(200).body(conferenceService.validate(name, id));
    }

    @PostMapping
    public ResponseEntity store(@RequestBody ConferenceDto conferenceDto) {
        Conference conference = new Conference(conferenceDto);
        ConferenceDto response = new ConferenceDto(conferenceService.save(conference));
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity show(@PathVariable Long id) {
        ConferenceDto response = new ConferenceDto(conferenceService.find(id));
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Long id, @RequestBody ConferenceDto conferenceDto) {
        conferenceDto.setId(id);
        Conference conference = new Conference(conferenceDto);
        ConferenceDto response = new ConferenceDto(conferenceService.save(conference));
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity destroy(@PathVariable Long id) {
        conferenceService.delete(id);
        return ResponseEntity.status(200).build();
    }
}
