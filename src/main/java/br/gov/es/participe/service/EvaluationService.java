package br.gov.es.participe.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import br.gov.es.participe.model.Evaluation;
import br.gov.es.participe.repository.EvaluationRepository;

@Service
public class EvaluationService {

    @Autowired
    private EvaluationRepository evaluationRepository;

    public void save(Evaluation evaluation) {
        evaluationRepository.save(evaluation);
    }

    public Optional<Evaluation> findByIdConference(Long idConference) {
        return Optional.ofNullable(evaluationRepository.findByConferenceId(idConference));
    }
    
}
