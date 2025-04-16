package br.gov.es.participe.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.gov.es.participe.controller.dto.BudgetOptionsDto;
import br.gov.es.participe.controller.dto.DomainConfigurationDto;
import br.gov.es.participe.controller.dto.EvaluatorRoleDto;
import br.gov.es.participe.controller.dto.LocalityInfoDto;
import br.gov.es.participe.controller.dto.PlanItemComboDto;
import br.gov.es.participe.controller.dto.ProposalEvaluationCommentResultDto;
import br.gov.es.participe.controller.dto.ProposalEvaluationDto;
import br.gov.es.participe.controller.dto.ProposalEvaluationRequestDto;
import br.gov.es.participe.controller.dto.ProposalEvaluationResponseDto;

public interface ProposalEvaluationServiceInterface {

    String checkIsPersonEvaluator(Long personId);

    Page<ProposalEvaluationCommentResultDto> findAllCommentsForEvaluation(
        Boolean evaluationStatus, 
        Long localityId, 
        Long planItemAreaId, 
        Long planItemId,
        List<String> organizationGuid, 
        Boolean loaIncluded, 
        String commentText, 
        Long conferenceId, 
        Pageable pageable
    );

    ProposalEvaluationResponseDto getProposalEvaluationData(Long proposalId, String guid);

    ProposalEvaluationResponseDto createProposalEvaluation(ProposalEvaluationRequestDto proposalEvaluationRequestDto);

    void deleteProposalEvaluation(ProposalEvaluationRequestDto proposalEvaluationRequestDto);

    List<LocalityInfoDto> getLocalityOptionsByConferenceId(Long conferenceId);

    List<PlanItemComboDto> getPlanItemOptionsByConferenceId(Long conferenceId);

    List<PlanItemComboDto> getPlanItemAreaOptionsByConferenceId(Long conferenceId);

    Boolean checkIsCommentEvaluated(Long commentId);

    List<BudgetOptionsDto> fetchDataFromPentahoAPI();

    DomainConfigurationDto getDomainConfiguration(Long conferenceId);

    ByteArrayInputStream jasperXlsx(
        Boolean evaluationStatus,
        Long localityId,
        Long planItemAreaId,
        Long planItemId,
        List<String> organizationGuid,
        Boolean loaIncluded,
        String commentText,
        Long conferenceId
    );

    List<ProposalEvaluationDto> listProposalEvaluationsByConferenceId(Long conferenceId);
}
