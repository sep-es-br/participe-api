package br.gov.es.participe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.participe.controller.dto.ProposalsDto;
import br.gov.es.participe.controller.dto.ProposalsFilterDto;
import br.gov.es.participe.controller.dto.integration.SpoProposalListRequestDto;
import br.gov.es.participe.service.CommentService;
import br.gov.es.participe.service.ConferenceService;
import br.gov.es.participe.service.ProposalsService;
import br.gov.es.participe.service.TokenService;
import br.gov.es.participe.util.domain.TokenType;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@CrossOrigin
@RequestMapping(value = "/integration", produces = MediaType.APPLICATION_JSON_VALUE)
public class IntegrationController {
	
    @Autowired
    private ProposalsService proposalsService;
    
    @Autowired
    private ConferenceService conferenceSrv;

    @PostMapping("spo/proposalsList")
    public ResponseEntity<?> getSpoProposalList(
            @RequestBody SpoProposalListRequestDto request
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
    
    @GetMapping("spo/lastConferenceId")
    public ResponseEntity<?> getSpoLastConferenceId(){
        
        return ResponseEntity.ok(Map.of("id", conferenceSrv.getLastConference().getId()));
    }
        
}
