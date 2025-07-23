package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.PageResponseDto;
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
import br.gov.es.participe.controller.dto.integration.SpoProposalsListResponseDto;
import br.gov.es.participe.service.CommentService;
import br.gov.es.participe.service.ConferenceService;
import br.gov.es.participe.service.ProposalsService;
import br.gov.es.participe.service.TokenService;
import br.gov.es.participe.util.domain.TokenType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@CrossOrigin
@RequestMapping(value = "/integration", produces = MediaType.APPLICATION_JSON_VALUE)
public class IntegrationController {
	
    @Autowired
    private CommentService commentSrv;
    
    @Autowired
    private ConferenceService conferenceSrv;

    @PostMapping("spo/proposalsList")
    public ResponseEntity<?> getSpoProposalList(
            @RequestBody SpoProposalListRequestDto request
    ) {
        
        
        Page<SpoProposalsListResponseDto> resp = commentSrv.listProposalForSpo(
                conferenceSrv.getLastConference().getId(), 
                request.getBudgetUnitCodes(), 
                request.getPlanItemName(), 
                request.getTextFilter(), 
                request.getSyncedIds(), 
                request.getPageNumber(), 
                request.getPageSize()
                );
        
        return ResponseEntity.ok(new PageResponseDto<>(resp));
    }
    
    @GetMapping("spo/lastConferenceId")
    public ResponseEntity<?> getSpoLastConferenceId(){
        
        return ResponseEntity.ok(Map.of("id", conferenceSrv.getLastConference().getId()));
    }
        
}
