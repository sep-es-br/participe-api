package br.gov.es.participe.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.participe.controller.dto.EvaluationSectionsDto;
import br.gov.es.participe.model.Organization;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.repository.EvaluationSectionsRepository;

@Service
public class EvaluationSectionsService {

    @Autowired
    private EvaluationSectionsRepository evaluationSectionsRepository;

    // private static final Logger log = LoggerFactory.getLogger(EvaluationSectionsService.class);

    public List<Organization> listEvaluators() {
        List<Organization> evalSectList = new ArrayList<Organization>();
        
        evaluationSectionsRepository.findAll().iterator().forEachRemaining(evalSectList::add);

        return evalSectList;
    }

    public Organization createEvaluationSection(EvaluationSectionsDto evaluationSectionsDto) {
        Organization newOrganization = new Organization(evaluationSectionsDto);

        Set<Person> evaluators = new HashSet<Person>();

        evaluationSectionsDto.getServersNames().iterator().forEachRemaining((serverName) -> {
            // log.info(serverName);

            // Não retorna por que servidor mockado tem mais informações do que precisa
            // expect: serverName = "Max Emanuel Flores Evangelista Calderaro"
            // result: serverName = "MAX EMANUEL FLORES EVANGELISTA CALDERARO CHEFE GRUPO DE PLANEJAMENTO E ORCAMENTO QCE-05 - GPO - SEP - GOVES"
            
            Person evaluator = evaluationSectionsRepository.findPersonByName(serverName);
            evaluators.add(evaluator);
        });

        newOrganization.setEvaluators(evaluators);

        evaluationSectionsRepository.save(newOrganization);

        return newOrganization;
    }
}
