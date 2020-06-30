package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.Comment;
import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.service.AcessoCidadaoService;
import br.gov.es.participe.service.CommentService;
import br.gov.es.participe.service.ConferenceService;
import br.gov.es.participe.service.HighlightService;
import br.gov.es.participe.util.domain.ProfileType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;

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

    @GetMapping
    public ResponseEntity index(@RequestParam(value = "name", required = false, defaultValue = "") String name
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
    public ResponseEntity validate(@RequestParam(value = "name", required = false, defaultValue = "") String name
            , @RequestParam(value = "id", required = false) Long id) {
        return ResponseEntity.status(200).body(conferenceService.validate(name, id));
    }

    @PostMapping
    public ResponseEntity store(@RequestBody ConferenceParamDto conferenceParamDto) {
        Conference conference = new Conference(conferenceParamDto);
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
        Boolean response = conferenceService.delete(id);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/AuthenticationScreen/{id}")
    public ResponseEntity getAuthenticationScreen(@PathVariable Long id, UriComponentsBuilder uriComponentsBuilder) {
        AuthenticationScreenDto auth = new AuthenticationScreenDto();
        conferenceService.generateAuthenticationScreen(id, auth, uriComponentsBuilder);
        return ResponseEntity.status(200).body(auth);
    }

    @GetMapping("/moderators")
    public ResponseEntity moderators(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "email", required = false, defaultValue = "") String email
    ) throws Exception {
        List<PersonDto> moderators = acessoCidadaoService.listPersonsByPerfil(ProfileType.MODERATOR, name, email);
        return ResponseEntity.status(200).body(moderators);
    }

    @GetMapping("/{id}/moderators")
    public ResponseEntity moderators(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) throws Exception {
        List<PersonDto> moderators = conferenceService.findModeratorsByConferenceId(id);
        return ResponseEntity.status(200).body(moderators);
    }

    @GetMapping("/receptionists")
    public ResponseEntity receptionists(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "email", required = false, defaultValue = "") String email
    ) throws Exception {
        List<PersonDto> moderators = acessoCidadaoService.listPersonsByPerfil(ProfileType.MODERATOR, name, email);
        return ResponseEntity.status(200).body(moderators);
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

}
