package br.gov.es.participe.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.gov.es.participe.controller.dto.DomainConfigurationDto;
import br.gov.es.participe.controller.dto.EvaluatorRoleDto;
import br.gov.es.participe.controller.dto.LocalityInfoDto;
import br.gov.es.participe.controller.dto.PlanItemComboDto;
import br.gov.es.participe.controller.dto.ProposalEvaluationRequestDto;
import br.gov.es.participe.controller.dto.ProposalEvaluationResponseDto;
import br.gov.es.participe.controller.dto.ProposalEvaluationResultDto;
import br.gov.es.participe.model.Comment;
import br.gov.es.participe.model.Evaluates;
import br.gov.es.participe.model.Evaluation;
import br.gov.es.participe.model.IsAuthenticatedBy;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.repository.ProposalEvaluationRepository;

@Service
public class ProposalEvaluationService {
    
    @Autowired
    private ProposalEvaluationRepository proposalEvaluationRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private AcessoCidadaoService acessoCidadaoService;

    @Autowired
    private EvaluatorsService evaluatorsService;

    public String checkIsPersonEvaluator(Long personId) throws IOException {

        IsAuthenticatedBy authRelationship = personService.getIsAuthenticatedBy(personId, "AcessoCidadao");

        EvaluatorRoleDto evaluatorRoleDto = new EvaluatorRoleDto(null, null, null);

        try {
            evaluatorRoleDto = acessoCidadaoService.findRoleFromAcessoCidadaoAPIByAgentePublicoSub(authRelationship.getIdByAuth());

        } catch (IOException e) {
            e.printStackTrace();
        }

        String roleGuid = evaluatorRoleDto.getGuid();
        String sectionGuid = evaluatorRoleDto.getLotacao();

        return evaluatorsService.findOrganizationGuidBySectionOrRole(sectionGuid, roleGuid);

        /* 
        1 - Chamar PersonService pra pegar o nó Person com id = personId
        2 - Chamar algum service (AuthService? AcessoCidadaoService?) pra pegar a propriedade 
            idByAuth do relacionamento IS_AUTHENTICATED_BY entre Person e AuthService (server = 'AcessoCidadao')
        3 - Com idByAuth (que é o sub do agente publico), percorrer nós Role chamando EvaluatorsService até achar algum
        |-> Se sim, retorna true
        |-> Se não:
            3.1 - Percorrer nós Section chamando EvaluatorsService até achar algum cujo papel.lotacaoGuid = section.guid
            |-> Se sim, retorna true
            |-> Se não, throw erro 403 
        */

    }

    public Page<ProposalEvaluationResultDto> findAllCommentsForEvaluation(
        Boolean evaluationStatus, 
        Long localityId, 
        Long planItemAreaId, 
        Long planItemId,
        String organizationGuid, 
        Boolean loaIncluded, 
        String commentText, 
        Long conferenceId, 
        Pageable pageable
    ) {

        Page<ProposalEvaluationResultDto> commentsForEvaluation = proposalEvaluationRepository.findAllCommentsForEvaluation(
            evaluationStatus, 
            localityId, 
            planItemAreaId, 
            planItemId,
            organizationGuid, 
            loaIncluded, 
            commentText, 
            conferenceId, 
            pageable);

        return commentsForEvaluation;
        
    }

    public ProposalEvaluationResponseDto getProposalEvaluationData(Long proposalId) {

        Optional<Evaluates> evaluatesRelationship = proposalEvaluationRepository.getEvaluatesRelationshipDataByCommentId(proposalId);

        if(evaluatesRelationship.isPresent()){
            return new ProposalEvaluationResponseDto(evaluatesRelationship.get());
        } else {
            return new ProposalEvaluationResponseDto();
        }

    }

    public ProposalEvaluationResponseDto createProposalEvaluation(ProposalEvaluationRequestDto proposalEvaluationRequestDto) {
        
        Person person = personService.find(proposalEvaluationRequestDto.getPersonId());

        Comment proposal = commentService.find(proposalEvaluationRequestDto.getProposalId());

        Evaluates newEvaluatesRelationship = new Evaluates(proposalEvaluationRequestDto);

        newEvaluatesRelationship.setPerson(person);
        newEvaluatesRelationship.setComment(proposal);
        newEvaluatesRelationship.setCreatedAt(new Date());

        proposalEvaluationRepository.save(newEvaluatesRelationship);

        return new ProposalEvaluationResponseDto(newEvaluatesRelationship);
    
    }

    public ProposalEvaluationResponseDto updateProposalEvaluation(Long evaluationId, ProposalEvaluationRequestDto proposalEvaluationRequestDto) {

        Evaluates evaluatesRelationship = proposalEvaluationRepository.findById(evaluationId)
            .orElseThrow();

        if(evaluatesRelationship.getPerson().getId() != proposalEvaluationRequestDto.getPersonId()){
            Person person = personService.find(proposalEvaluationRequestDto.getPersonId());
            evaluatesRelationship.setPerson(person);
        }

        if(evaluatesRelationship.getComment().getId() != proposalEvaluationRequestDto.getProposalId()){
            Comment proposal = commentService.find(proposalEvaluationRequestDto.getProposalId());
            evaluatesRelationship.setComment(proposal);
        }

        evaluatesRelationship.setIncludedInNextYearLOA(proposalEvaluationRequestDto.getIncludedInNextYearLOA());
        evaluatesRelationship.setBudgetUnitId(proposalEvaluationRequestDto.getBudgetUnitId());
        evaluatesRelationship.setBudgetActionId(proposalEvaluationRequestDto.getBudgetActionId());
        evaluatesRelationship.setBudgetPlan(proposalEvaluationRequestDto.getBudgetPlan());
        evaluatesRelationship.setReason(proposalEvaluationRequestDto.getReason());
        evaluatesRelationship.setRepresenting(proposalEvaluationRequestDto.getRepresenting());

        evaluatesRelationship.setUpdatedAt(new Date());

        proposalEvaluationRepository.save(evaluatesRelationship);

        return new ProposalEvaluationResponseDto(evaluatesRelationship);

    }

    public List<LocalityInfoDto> getLocalityOptionsByConferenceId(Long conferenceId) {
        
        List<LocalityInfoDto> localityInfoDtoList = new ArrayList<LocalityInfoDto>();

        List<Locality> localitiesList = proposalEvaluationRepository.getLocalityOptionsByConferenceId(conferenceId);

        localitiesList.iterator().forEachRemaining((locality) -> {
            localityInfoDtoList.add(new LocalityInfoDto(locality));
        });

        return localityInfoDtoList;

    }

    public List<PlanItemComboDto> getPlanItemOptionsByConferenceId(Long conferenceId) {

        List<PlanItemComboDto> planItemComboDtoList = new ArrayList<PlanItemComboDto>();
        
        List<PlanItem> planItemsList = proposalEvaluationRepository.getPlanItemOptionsByConferenceId(conferenceId);

        planItemsList.iterator().forEachRemaining((planItem) -> {
            planItemComboDtoList.add(new PlanItemComboDto(planItem.getId(), planItem.getName()));
        });

        return planItemComboDtoList;
    
    }

    public List<PlanItemComboDto> getPlanItemAreaOptionsByConferenceId(Long conferenceId) {

        List<PlanItemComboDto> planItemComboDtoList = new ArrayList<PlanItemComboDto>();

        List<PlanItem> planItemsAreaList = proposalEvaluationRepository.getPlanItemAreaOptionsByConferenceId(conferenceId);

        planItemsAreaList.iterator().forEachRemaining((planItemArea) -> {
            planItemComboDtoList.add(new PlanItemComboDto(planItemArea.getId(), planItemArea.getName()));
        });

        return planItemComboDtoList;

    }

    public DomainConfigurationDto getDomainConfiguration(Long conferenceId) {
        return proposalEvaluationRepository.getDomainConfiguration(conferenceId);
    }

}
