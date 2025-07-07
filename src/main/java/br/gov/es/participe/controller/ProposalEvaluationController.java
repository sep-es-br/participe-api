package br.gov.es.participe.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.service.ConferenceService;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.service.ProposalEvaluationService;
import br.gov.es.participe.util.domain.BudgetPlan;
import java.util.Arrays;

@RestController
@CrossOrigin
@RequestMapping("/proposal-evaluation")
public class ProposalEvaluationController {

    @Autowired
    private ProposalEvaluationService proposalEvaluationService;

    @Autowired
    private PersonService personService;

    @Autowired
    private ConferenceService conferenceService;


    @GetMapping("/is-evaluator/{personId}")
    public ResponseEntity<String> checkIsPersonEvaluator(@PathVariable(name = "personId") Long personId) throws IOException {
        String response = proposalEvaluationService.checkIsPersonEvaluator(personId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProposalEvaluationCommentResultDto>> listProposalEvaluationsByConference(
        @RequestParam(value = "evaluationStatus", required = false, defaultValue = "") Boolean evaluationStatus,
        @RequestParam(value = "localityId", required = false, defaultValue = "") Long localityId,
        @RequestParam(value = "planItemAreaId", required = false, defaultValue = "") Long planItemAreaId,
        @RequestParam(value = "planItemId", required = false, defaultValue = "") Long planItemId,
        @RequestParam(value = "organizationGuid", required = false, defaultValue = "") List<String> organizationGuid,
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
            pageable
        );
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{proposalId}")
    public ResponseEntity<ProposalEvaluationResponseDto> getProposalEvaluationData(
        @PathVariable(name = "proposalId") Long proposalId,
        @RequestParam(value = "guid", required = false, defaultValue = "") String guid
    ) {
        ProposalEvaluationResponseDto response = proposalEvaluationService.getProposalEvaluationData(proposalId, guid);
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
        if (!personService.hasOneOfTheRoles(token, new String[]{"Administrator", "Moderator"})) {
            return ResponseEntity.status(401).body(null);
        }
        return ResponseEntity.ok().body(proposalEvaluationService.checkIsCommentEvaluated(commentId));
    }
    
    @GetMapping("/budgetPlanList")
    public ResponseEntity<List<BudgetPlan>> getBudgetPlanList(
    ) {

       List<BudgetPlan> result = proposalEvaluationService.fetchBudgetPlanFromBI();
        
        // List<BudgetPlan> result = Arrays.asList(
        //         new BudgetPlan("000004", "CAPS DE MARATAÍZES IMPLANTADO"),
        //         new BudgetPlan("000005", "CENTRAL DE REGULAÇÃO ESTADUAL DE ACESSO A CONSULTAS, EXAMES E INTERNAÇÕES IMPLANTADA"),
        //         new BudgetPlan("000006", "CENTRAL SAMU 192 NORTE IMPLANTADO - SÃO MATEUS"),
        //         new BudgetPlan("000007", "CENTRAL SAMU 192 SUL IMPLANTADO - CACHOEIRO DE ITAPEMIRIM")
        // );
        
        
        
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/conferences")
    public ResponseEntity<List<ConferenceDto>> findConferencesActives(
        @RequestHeader(name = "Authorization") String token,
        @RequestParam(name = "activeConferences", required = false, defaultValue = "false") Boolean activeConferences
    ) {
        List<ConferenceDto> conferences = conferenceService.findAllActivesEvaluation(activeConferences);
        return ResponseEntity.status(200).body(conferences);
    }

    @GetMapping("/proposalEvaluationXlsx")
    public ResponseEntity<InputStreamResource> findproposalEvaluationXml(
        @RequestParam(value = "evaluationStatus", required = false, defaultValue = "") Boolean evaluationStatus,
        @RequestParam(value = "localityId", required = false, defaultValue = "") Long localityId,
        @RequestParam(value = "planItemAreaId", required = false, defaultValue = "") Long planItemAreaId,
        @RequestParam(value = "planItemId", required = false, defaultValue = "") Long planItemId,
        @RequestParam(value = "organizationGuid", required = false, defaultValue = "") List<String> organizationGuid,
        @RequestParam(value = "loaIncluded", required = false, defaultValue = "") Boolean loaIncluded,
        @RequestParam(value = "commentText", required = false, defaultValue = "") String commentText,
        @RequestParam(value = "conferenceId", required = true) Long conferenceId
    ) {
        ByteArrayInputStream response = proposalEvaluationService.jasperXlsx(
            evaluationStatus,
            localityId,
            planItemAreaId,
            planItemId,
            organizationGuid,
            loaIncluded,
            commentText,
            conferenceId
        );
        return new ResponseEntity<>(new InputStreamResource(response), HttpStatus.OK);
    }
}