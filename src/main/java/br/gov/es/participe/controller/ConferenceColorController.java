package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.ConferenceColorDto;
import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.ConferenceColor;
import br.gov.es.participe.service.ConferenceColorService;
import br.gov.es.participe.service.ConferenceService;
import br.gov.es.participe.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/color")
public class ConferenceColorController {
    @Autowired
    private ConferenceColorService conferenceColorService;

    @GetMapping("/{idConference}")
    public ResponseEntity<ConferenceColor> getConferenceColor(@PathVariable("idConference") Long idConference){

        Conference conference = conferenceColorService.findByIdConference(idConference);
        if(conference != null){
            ConferenceColor conferenceColor = conferenceColorService.findByConferenceColor(idConference);
            if(conferenceColor != null){
                return ResponseEntity.ok().body(conferenceColor);
            }
        }
        return ResponseEntity.ok().body(new ConferenceColor());
    }
}
