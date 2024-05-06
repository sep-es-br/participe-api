package br.gov.es.participe.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.participe.controller.dto.EvaluationSectionsDto;
import br.gov.es.participe.model.Organization;
import br.gov.es.participe.service.EvaluationSectionsService;
import br.gov.es.participe.service.PersonService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@CrossOrigin
@RequestMapping(value = "/evaluation-sections")
public class EvaluatorSectionsController {
    
    @Autowired
    private EvaluationSectionsService evaluationSectionsService;

    @Autowired
    private PersonService personService;

    @GetMapping
    public ResponseEntity<List<EvaluationSectionsDto>> listEvaluators(
        @RequestHeader(name = "Authorization") String token
    ) {

        // Adicionar "Evaluator" / "Avaliador"?
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
	    	return ResponseEntity.status(401).body(null);
	    }

        List<Organization> organizationsList = evaluationSectionsService.listEvaluators();

        List<EvaluationSectionsDto> response = new ArrayList<>();

        organizationsList.iterator().forEachRemaining((organization) -> {
            EvaluationSectionsDto newEvalSect = new EvaluationSectionsDto(organization);
            response.add(newEvalSect);
        });

        return ResponseEntity.status(200).body(response);
    }

    @Transactional
    @PostMapping
    public ResponseEntity<EvaluationSectionsDto> createEvaluationSection(
        @RequestHeader(name = "Authorization") String token,
        @RequestBody EvaluationSectionsDto evaluationSectionDto
    )  {

        // Adicionar "Evaluator" / "Avaliador"?
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
	    	return ResponseEntity.status(401).body(null);
	    }

        Organization newOrganization = evaluationSectionsService.createEvaluationSection(evaluationSectionDto);

        EvaluationSectionsDto response = new EvaluationSectionsDto(newOrganization);

        return ResponseEntity.status(201).body(response);

    }
    
    

}
