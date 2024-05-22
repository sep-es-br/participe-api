package br.gov.es.participe.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.participe.controller.dto.EvaluatorDto;
import br.gov.es.participe.controller.dto.EvaluatorOrganizationDto;
import br.gov.es.participe.controller.dto.EvaluatorParamDto;
import br.gov.es.participe.controller.dto.EvaluatorSectionDto;
import br.gov.es.participe.controller.dto.EvaluatorServerDto;
import br.gov.es.participe.model.Evaluator;
import br.gov.es.participe.service.AcessoCidadaoService;
import br.gov.es.participe.service.EvaluatorsService;
import br.gov.es.participe.service.PersonService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@CrossOrigin
@RequestMapping("/evaluators")
public class EvaluatorsController {
    
    @Autowired
    private PersonService personService;

    @Autowired
    private AcessoCidadaoService acessoCidadaoService;

    @Autowired
    private EvaluatorsService evaluatorsService;

    // @GetMapping
    // public ResponseEntity<List<EvaluatorDto>> listEvaluators(
    //     @RequestHeader(name = "Authorization") String token
    // ) {
    //     if (!(personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" }))) {
    //         return ResponseEntity.status(401).body(null);
    //     }

    //     List<EvaluatorDto> response = new ArrayList<EvaluatorDto>();

    //     evaluatorsService.findAllEvaluators().iterator().forEachRemaining((evaluator) -> {
    //         EvaluatorDto evaluatorDto = new EvaluatorDto(evaluator);
    //         response.add(evaluatorDto);
    //     });

    //     return ResponseEntity.status(200).body(response);
    // }

    @PostMapping
    public ResponseEntity<?> createEvaluator(
        @RequestHeader(name = "Authorization") String token,
        @RequestBody @Valid EvaluatorParamDto evaluatorParamDto
    ) {
        if (!(personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" }))) {
            return ResponseEntity.status(401).body(null);
        }

        EvaluatorDto response = evaluatorsService.saveEvaluator(evaluatorParamDto);

        // EvaluatorDto response = new EvaluatorDto(newEvaluator);

        return ResponseEntity.status(201).body(response);
    
    }

    // @PutMapping("/{evalSectId}")
    // public ResponseEntity<EvaluatorDto> updateEvaluator(
    //     @RequestHeader(name = "Authorization") String token,
    //     @RequestBody @Valid EvaluatorParamDto evaluatorParamDto,
    //     @PathVariable(name = "evalSectId") Long evalSectId
    // ) {

    //     if (!(personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" }))) {
    //         return ResponseEntity.status(401).body(null);
    //     }

    //     Evaluator updatedEvaluator = evaluatorsService.updateEvaluator(evaluatorParamDto, evalSectId);

    //     EvaluatorDto response = new EvaluatorDto(updatedEvaluator);

    //     return ResponseEntity.status(200).body(response);
    // }

    @DeleteMapping("/{evalSectId}")
    public ResponseEntity<String> deleteEvaluator(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "evalSectId") Long evalSectId
    ) {
        if (!(personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" }))) {
            return ResponseEntity.status(401).body(null);
        }

        evaluatorsService.deleteEvaluator(evalSectId);

        return ResponseEntity.status(200).body("Entidade avaliadora excluída com sucesso.");
    }

    @GetMapping("/organizations")
    public ResponseEntity<List<EvaluatorOrganizationDto>> listOrganizations(@RequestHeader(name = "Authorization") String token) throws IOException  {

        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
            return ResponseEntity.status(401).body(null);
          }

        List<EvaluatorOrganizationDto> response = acessoCidadaoService.findForOrganizations();

        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/sections/{orgGuid}")
    public ResponseEntity<List<EvaluatorSectionDto>> listSections(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(value = "orgGuid") String orgGuid) throws IOException  {
        
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
            return ResponseEntity.status(401).body(null);
          }

        List<EvaluatorSectionDto> response = acessoCidadaoService.findListOfOrganizationUnits(orgGuid);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/servers/{unitGuid}")
    public ResponseEntity<List<EvaluatorServerDto>> listServers(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(value = "unitGuid") String unitGuid) throws IOException  {
        
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
            return ResponseEntity.status(401).body(null);
          }

        List<EvaluatorServerDto> response = acessoCidadaoService.findUnitRoles(unitGuid);

        return ResponseEntity.ok().body(response);
    }
}