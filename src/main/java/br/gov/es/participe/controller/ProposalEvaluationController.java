package br.gov.es.participe.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.participe.controller.dto.BudgetOptionsDto;
import br.gov.es.participe.controller.dto.DomainConfigurationDto;
import br.gov.es.participe.controller.dto.LocalityInfoDto;
import br.gov.es.participe.controller.dto.PlanItemComboDto;
import br.gov.es.participe.controller.dto.ProposalEvaluationRequestDto;
import br.gov.es.participe.controller.dto.ProposalEvaluationResponseDto;
import br.gov.es.participe.controller.dto.ProposalEvaluationCommentResultDto;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.service.ProposalEvaluationService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@CrossOrigin
@RequestMapping("/proposal-evaluation")
public class ProposalEvaluationController {

    @Autowired
    private ProposalEvaluationService proposalEvaluationService;

    @Autowired
    private PersonService personService;

    @GetMapping("/is-evaluator/{personId}")
    public ResponseEntity<String> checkIsPersonEvaluator(
        @PathVariable(name = "personId") Long personId
    ) throws IOException {

        String response = proposalEvaluationService.checkIsPersonEvaluator((long) 6);

        return ResponseEntity.ok().body(response);
    }
    
    
    @GetMapping
    public ResponseEntity<Page<ProposalEvaluationCommentResultDto>> listProposalEvaluationsByConference(
        @RequestParam(value = "evaluationStatus", required = false, defaultValue = "") Boolean evaluationStatus,
        @RequestParam(value = "localityId", required = false, defaultValue = "") Long localityId,
        @RequestParam(value = "planItemAreaId", required = false, defaultValue = "") Long planItemAreaId,
        @RequestParam(value = "planItemId", required = false, defaultValue = "") Long planItemId,
        @RequestParam(value = "organizationGuid", required = false, defaultValue = "") String organizationGuid,
        @RequestParam(value = "loaIncluded", required = false, defaultValue = "") Boolean loaIncluded,
        @RequestParam(value = "commentText", required = false, defaultValue = "") String commentText,
        @RequestParam(value = "conferenceId", required = true) Long conferenceId,
        Pageable pageable
    ) {

        Page<ProposalEvaluationCommentResultDto> response = proposalEvaluationService.findAllCommentsForEvaluation(
            evaluationStatus, 
            localityId, 
            planItemAreaId, 
            planItemId,
            organizationGuid, 
            loaIncluded, 
            commentText, 
            conferenceId, 
            pageable);

        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/{proposalId}")
    public ResponseEntity<ProposalEvaluationResponseDto> getProposalEvaluationData(
        @PathVariable(name = "proposalId") Long proposalId
    ) {

        ProposalEvaluationResponseDto response = proposalEvaluationService.getProposalEvaluationData((long) 6);

        return ResponseEntity.ok().body(response);

    }

    @PostMapping
    public ResponseEntity<ProposalEvaluationResponseDto> createProposalEvaluation(
        @RequestBody ProposalEvaluationRequestDto proposalEvaluationRequestDto
    ) {

        ProposalEvaluationResponseDto response = proposalEvaluationService.createProposalEvaluation(proposalEvaluationRequestDto);

        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{evaluationId}")
    public ResponseEntity<ProposalEvaluationResponseDto> updateProposalEvaluation(
        @PathVariable(name = "evaluationId") Long evaluationId,
        @RequestBody ProposalEvaluationRequestDto proposalEvaluationRequestDto
    ) {
        
        ProposalEvaluationResponseDto response = proposalEvaluationService.createProposalEvaluation(proposalEvaluationRequestDto);

        return ResponseEntity.ok().body(response);

    }

    @PostMapping("/{proposalId}")
    public ResponseEntity<String> deleteProposalEvaluation(
        @PathVariable(name = "proposalId") Long proposalId,
        @RequestBody ProposalEvaluationRequestDto proposalEvaluationRequestDto
    ) {

        proposalEvaluationService.deleteProposalEvaluation(proposalEvaluationRequestDto);

        return ResponseEntity.ok().body("Avaliação de Proposta excluída com sucesso.");

    }

    @GetMapping("/options/locality")
    public ResponseEntity<List<LocalityInfoDto>> getLocalityOptionsByConferenceId(
        @RequestParam(value = "conferenceId") Long conferenceId
    ) {

        List<LocalityInfoDto> response = proposalEvaluationService.getLocalityOptionsByConferenceId(conferenceId);

        return ResponseEntity.ok().body(response);

    }
    
    @GetMapping("/options/planItem")
    public ResponseEntity<List<PlanItemComboDto>> getPlanItemOptionsByConferenceId(
        @RequestParam(value = "conferenceId") Long conferenceId
    ) {

        List<PlanItemComboDto> response = proposalEvaluationService.getPlanItemOptionsByConferenceId(conferenceId);

        return ResponseEntity.ok().body(response);

    }
    
    @GetMapping("/options/planItemArea")
    public ResponseEntity<List<PlanItemComboDto>> getPlanItemAreaOptionsByConferenceId(
        @RequestParam(value = "conferenceId") Long conferenceId
    ) {

        List<PlanItemComboDto> response = proposalEvaluationService.getPlanItemAreaOptionsByConferenceId(conferenceId);

        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/options/budgetOptions")
    public ResponseEntity<List<BudgetOptionsDto>> fetchBudgetOptions() {

        List<BudgetOptionsDto> response = proposalEvaluationService.fetchDataFromPentahoAPI();

        return ResponseEntity.ok().body(response);
    
    }

    @GetMapping("/options/configuration")
    public ResponseEntity<DomainConfigurationDto> getDomainConfiguration(
        @RequestParam(value = "conferenceId") Long conferenceId
    ) {

        DomainConfigurationDto response = proposalEvaluationService.getDomainConfiguration(conferenceId);

        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/isCommentEvaluated")
    public ResponseEntity<Boolean> checkIsCommentEvaluated(
        @RequestHeader(name = "Authorization") String token,
        @RequestParam(value = "commentId") Long commentId
    ) {

        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
            return ResponseEntity.status(401).body(null);
        }

        return ResponseEntity.ok().body(proposalEvaluationService.checkIsCommentEvaluated(commentId));

    }

}
