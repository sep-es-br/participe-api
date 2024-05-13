package br.gov.es.participe.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.participe.controller.dto.EvaluatorDto;
import br.gov.es.participe.controller.dto.EvaluatorParamDto;
import br.gov.es.participe.model.Evaluator;
import br.gov.es.participe.service.EvaluatorService;
import br.gov.es.participe.service.PersonService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@CrossOrigin
@RequestMapping("/evaluation-sections")
public class EvaluationSectionsController {
    
    @Autowired
    private PersonService personService;

    @Autowired
    private EvaluatorService evaluatorService;

    @GetMapping
    public ResponseEntity<List<EvaluatorDto>> listEvaluators(
        @RequestHeader(name = "Authorization") String token
    ) {
        if (!(personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" }))) {
            return ResponseEntity.status(401).body(null);
        }

        List<EvaluatorDto> response = new ArrayList<EvaluatorDto>();

        evaluatorService.findAllEvaluators().iterator().forEachRemaining((evaluator) -> {
            EvaluatorDto evaluatorDto = new EvaluatorDto(evaluator);
            response.add(evaluatorDto);
        });

        return ResponseEntity.status(200).body(response);
    }

    @PostMapping
    public ResponseEntity<?> createEvaluator(
        @RequestHeader(name = "Authorization") String token,
        @RequestBody @Valid EvaluatorParamDto evaluatorParamDto
    ) {
        if (!(personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" }))) {
            return ResponseEntity.status(401).body(null);
        }

        Evaluator newEvaluator = evaluatorService.saveEvaluator(evaluatorParamDto);

        if(newEvaluator == null) {
            // return ResponseEntity.status(400).body(null);
            return ResponseEntity.status(400).body("Já existe uma entidade avaliadora com o guid desta organização.");
        }

        EvaluatorDto response = new EvaluatorDto(newEvaluator);

        return ResponseEntity.status(201).body(response);
    
    }
}
