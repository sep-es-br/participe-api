package br.gov.es.participe.service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.participe.controller.dto.ProposalEvaluationDto;
import br.gov.es.participe.model.Comment;
import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.model.ProposalEvaluation;
import br.gov.es.participe.repository.CommentRepository;
import br.gov.es.participe.repository.PlanItemRepository;
import br.gov.es.participe.repository.ProposalEvaluationRepository;

@Service
public class ProposalEvaluationService {
    

    @Autowired
    private ProposalEvaluationRepository propEvalRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PlanItemRepository planItemRepository;

    private final static Logger log = LoggerFactory.getLogger(ProposalEvaluationService.class);

    // public ProposalEvaluation createProposalEvaluationFromComment(Comment comment) {

    //     Boolean existsByCommentId = propEvalRepository.existsByCommentId(comment.getId());

    //     if(existsByCommentId == true){
    //         return null;
    //     }

    //     ProposalEvaluation proposalEvaluation = new ProposalEvaluation(comment);

    //     Optional<PlanItem> planItemArea = planItemRepository.findFatherPlanItem(comment.getPlanItem().getId());

    //     if(planItemArea.isPresent() == true){
    //         proposalEvaluation.setPlanItemArea(planItemArea.get());
    //     }

    //     propEvalRepository.save(proposalEvaluation);

    //     return proposalEvaluation;
    // }

    public List<ProposalEvaluationDto> listProposalEvaluationsByConferenceId(Long conferenceId) {

        List<ProposalEvaluationDto> proposalEvaluationList = propEvalRepository.findAllByConferenceId(conferenceId);

        // List<ProposalEvaluationDto> proposalEvaluationList = new ArrayList<ProposalEvaluationDto>();

        // proposalEvaluationResultList.forEach((propEval) -> proposalEvaluationList.add(new ProposalEvaluationDto(propEval)));

        return proposalEvaluationList;

    }
}
