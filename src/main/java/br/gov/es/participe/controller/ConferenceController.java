package br.gov.es.participe.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.es.participe.model.Person;
import br.gov.es.participe.service.*;
import br.gov.es.participe.util.domain.TokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.gov.es.participe.controller.dto.AuthenticationScreenDto;
import br.gov.es.participe.controller.dto.ConferenceDto;
import br.gov.es.participe.controller.dto.ConferenceParamDto;
import br.gov.es.participe.controller.dto.PersonDto;
import br.gov.es.participe.model.Conference;
import br.gov.es.participe.util.domain.ProfileType;

@RestController
@CrossOrigin
@RequestMapping(value = "/conferences")
public class ConferenceController {

    @Autowired
    private ConferenceService conferenceService;

    @Autowired
    private AcessoCidadaoService acessoCidadaoService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HighlightService highlightService;

    @Autowired
    private PersonService personService;

    @Autowired
    private TokenService tokenService;

    @GetMapping
    public ResponseEntity<List<ConferenceDto>> index(@RequestParam(value = "name", required = false, defaultValue = "") String name
            , @RequestParam(value = "plan", required = false) Long plan
            , @RequestParam(value = "month", required = false) Integer month
            , @RequestParam(value = "year", required = false) Integer year
    ) {
        List<Conference> conferences = conferenceService.findAll(name, plan, month, year);
        List<ConferenceDto> response = new ArrayList<>();

        conferences.forEach(conference -> response.add(new ConferenceDto(conference)));

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestParam(value = "name", required = false, defaultValue = "") String name
            , @RequestParam(value = "id", required = false) Long id) {
        return ResponseEntity.status(200).body(conferenceService.validate(name, id));
    }

    @PostMapping
    public ResponseEntity<ConferenceDto> store(@RequestBody ConferenceParamDto conferenceParamDto) {
        Conference conference = new Conference(conferenceParamDto);
        ConferenceDto response = new ConferenceDto(conferenceService.save(conference));
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConferenceDto> show(@PathVariable Long id) {
        ConferenceDto response = new ConferenceDto(conferenceService.find(id));
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConferenceDto> update(@PathVariable Long id, @RequestBody ConferenceDto conferenceDto) {
        conferenceDto.setId(id);
        Conference conference = new Conference(conferenceDto);
        ConferenceDto response = new ConferenceDto(conferenceService.save(conference));
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> destroy(@PathVariable Long id) {
        Boolean response = conferenceService.delete(id);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/AuthenticationScreen/{id}")
    public ResponseEntity<AuthenticationScreenDto> getAuthenticationScreen(@PathVariable Long id, UriComponentsBuilder uriComponentsBuilder) {
        AuthenticationScreenDto auth = new AuthenticationScreenDto();
        conferenceService.generateAuthenticationScreen(id, auth, uriComponentsBuilder);
        return ResponseEntity.status(200).body(auth);
    }

    @GetMapping("/moderators")
    public ResponseEntity<List<PersonDto>> moderators(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "email", required = false, defaultValue = "") String email
    ) throws IOException {
        List<PersonDto> moderators = acessoCidadaoService.listPersonsByPerfil(ProfileType.MODERATOR, name, email);
        return ResponseEntity.status(200).body(moderators);
    }

    @GetMapping("/{id}/moderators")
    public ResponseEntity<List<PersonDto>> moderators(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        List<PersonDto> moderators = conferenceService.findModeratorsByConferenceId(id);
        return ResponseEntity.status(200).body(moderators);
    }

    @GetMapping("/receptionists")
    public ResponseEntity<List<PersonDto>> receptionists(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "email", required = false, defaultValue = "") String email
    ) throws IOException  {
        List<PersonDto> receptionists = acessoCidadaoService.listPersonsByPerfil(ProfileType.RECEPCIONIST, name, email);
        return ResponseEntity.status(200).body(receptionists);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<Integer> comments(@PathVariable Long id) {
        Integer response = commentService.countCommentByConference(id);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}/highlights")
    public ResponseEntity<Integer> highlights(@PathVariable Long id) {
        Integer response = highlightService.countHighlightByConference(id);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}/selfdeclarations")
    public ResponseEntity<Integer> selfdeclarations(@PathVariable Long id) {
        Integer response = conferenceService.countSelfDeclarationById(id);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/with-meetings")
    public ResponseEntity<List<ConferenceDto>> findConferencesWithMeeting(@RequestHeader("Authorization") String token,
                                                                          @RequestParam(value = "date", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date date) {
        String[] keys = token.split(" ");
        Long idPerson = tokenService.getPersonId(keys[1], TokenType.AUTHENTICATION);
        Person person = personService.find(idPerson);
        boolean adm = person.getRoles() != null && person.getRoles().contains("Administrator");

        List<Conference> conferences;
        if(adm) {
            conferences = conferenceService.findAllWithMeetings(date, null);
        } else {
            conferences = conferenceService.findAllWithMeetings(date, person.getId());
        }
        List<ConferenceDto> response = new ArrayList<>();
        conferences.forEach(conference -> {
            ConferenceDto conferenceDto = new ConferenceDto(conference);
            conferenceDto.setPlan(null);
            conferenceDto.setLocalityType(null);
            conferenceDto.setFileAuthentication(null);
            conferenceDto.setFileParticipation(null);
            response.add(conferenceDto);
        });
        return ResponseEntity.status(200).body(response);
    }
}
