package br.gov.es.participe.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.participe.controller.dto.EvaluatorParamDto;
import br.gov.es.participe.exception.EvaluationSectionsNotFoundException;
import br.gov.es.participe.exception.ParticipeServiceException;
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

        if (evaluator.isEmpty()) {
            Evaluator newEvaluator = new Evaluator(evaluatorParamDto);
            evaluatorRepository.save(newEvaluator);

            return newEvaluator;
        } else {
            throw new ParticipeServiceException("Já existe uma entidade avaliadora com o guid desta organização.");
        }
    }

    public Evaluator updateEvaluator(EvaluatorParamDto evaluatorParamDto, Long evalSectId) {

        Evaluator evaluator = evaluatorRepository.findById(evalSectId).orElseThrow(() -> new EvaluationSectionsNotFoundException(evalSectId));

        evaluator.setSections(evaluatorParamDto.getSectionsGuid());
        evaluator.setServers(evaluatorParamDto.getServersGuid());

        evaluatorRepository.save(evaluator);

        return evaluator;
    }

    public void deleteEvaluator(Long evalSectId) {

        evaluatorRepository.deleteById(evalSectId);

    }
}
