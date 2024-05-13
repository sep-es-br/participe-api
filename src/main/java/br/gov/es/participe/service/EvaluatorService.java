package br.gov.es.participe.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.participe.controller.dto.EvaluatorParamDto;
import br.gov.es.participe.model.Evaluator;
import br.gov.es.participe.repository.EvaluatorRepository;

@Service
public class EvaluatorService {
    
    @Autowired
    private EvaluatorRepository evaluatorRepository;

    private static final Logger log = LoggerFactory.getLogger(EvaluatorService.class);

    public List<Evaluator> findAllEvaluators() {

        List<Evaluator> evaluatorsList = new ArrayList<Evaluator>();

        evaluatorRepository.findAll().iterator().forEachRemaining(evaluatorsList::add);

        return evaluatorsList;

    }

    public Evaluator saveEvaluator(EvaluatorParamDto evaluatorParamDto) {

        Optional<Evaluator> evaluator = evaluatorRepository.findByOrganizationGuid(evaluatorParamDto.getOrganizationGuid());

        if (!evaluator.isPresent()) {
            Evaluator newEvaluator = new Evaluator(evaluatorParamDto);
            evaluatorRepository.save(newEvaluator);

            return newEvaluator;
        } else {
            return null;
        }
    }
}
