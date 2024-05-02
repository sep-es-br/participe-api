package br.gov.es.participe.controller;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.participe.controller.dto.LocalityDto;
import br.gov.es.participe.controller.dto.PlanItemDto;
import br.gov.es.participe.controller.dto.ProposalEvaluationDto;
import br.gov.es.participe.model.ProposalEvaluation;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.service.ProposalEvaluationService;
// import br.gov.es.participe.service.TokenService;

@RestController
@CrossOrigin
@RequestMapping(value = "/proposal-evaluation")
public class ProposalEvaluationController {

    @Autowired
    private PersonService personService;
    
    // @Autowired
    // private TokenService tokenService;

    @Autowired
    private ProposalEvaluationService propEvalService;


    @GetMapping
    public ResponseEntity<List<ProposalEvaluationDto>> listProposalEvaluationsByConferenceId(
        @RequestHeader(name = "Authorization") String token,
        @RequestParam(value = "conferenceId", required = true ) Long conferenceId
    ) {

        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
            return ResponseEntity.status(401).body(null);
        }
        
        List<ProposalEvaluationDto> response = propEvalService.listProposalEvaluationsByConferenceId(conferenceId);

        return ResponseEntity.status(200).body(response);
    }
}
