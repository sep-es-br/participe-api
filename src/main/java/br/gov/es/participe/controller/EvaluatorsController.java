package br.gov.es.participe.controller;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.participe.controller.dto.EvaluatorOrganizationDto;
import br.gov.es.participe.controller.dto.EvaluatorRequestDto;
import br.gov.es.participe.controller.dto.EvaluatorResponseDto;
import br.gov.es.participe.controller.dto.EvaluatorSectionDto;
import br.gov.es.participe.controller.dto.EvaluatorsNamesRequestDto;
import br.gov.es.participe.controller.dto.EvaluatorsNamesResponseDto;
import br.gov.es.participe.controller.dto.EvaluatorRoleDto;
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

    private static String[] personRoles = { "Administrator", "Moderator" };
    
    @Autowired
    private PersonService personService;

    @Autowired
    private AcessoCidadaoService acessoCidadaoService;

    @Autowired
    private EvaluatorsService evaluatorsService;

    @GetMapping
    public ResponseEntity<Page<EvaluatorResponseDto>> listEvaluators(
        @RequestHeader(name = "Authorization") String token,
        @RequestParam(value = "searchOrganization", required = false, defaultValue = "") String orgGuidFilter,
        @RequestParam(value = "searchSection", required = false, defaultValue = "") String sectionGuidFilter,
        @RequestParam(value = "searchRole", required = false, defaultValue = "") String roleGuidFilter,
        Pageable pageable
    ) {

        if (!personService.hasOneOfTheRoles(token, personRoles)) {
            return ResponseEntity.status(401).body(null);
        }

        Page<EvaluatorResponseDto> response = evaluatorsService.findAllEvaluators(
            orgGuidFilter, 
            sectionGuidFilter, 
            roleGuidFilter, 
            pageable
        );

        return ResponseEntity.status(200).body(response);

    }

    @PostMapping
    public ResponseEntity<EvaluatorResponseDto> createEvaluator(
        @RequestHeader(name = "Authorization") String token,
        @RequestBody @Valid EvaluatorRequestDto evaluatorRequestDto
    ) {

        if (!personService.hasOneOfTheRoles(token, personRoles)) {
            return ResponseEntity.status(401).body(null);
        }

        EvaluatorResponseDto response = evaluatorsService.saveEvaluator(evaluatorRequestDto);

        return ResponseEntity.status(201).body(response);
    
    }

    @PutMapping("/{evaluatorId}")
    public ResponseEntity<EvaluatorResponseDto> updateEvaluator(
        @RequestHeader(name = "Authorization") String token,
        @RequestBody @Valid EvaluatorRequestDto evaluatorRequestDto,
        @PathVariable(name = "evaluatorId") Long evaluatorId
    ) {

        if (!personService.hasOneOfTheRoles(token, personRoles)) {
            return ResponseEntity.status(401).body(null);
        }

        EvaluatorResponseDto response = evaluatorsService.updateEvaluator(evaluatorRequestDto, evaluatorId);

        return ResponseEntity.status(200).body(response);

    }

    @DeleteMapping("/{evaluatorId}")
    public ResponseEntity<String> deleteEvaluator(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "evaluatorId") Long evaluatorId
    ) {

        if (!personService.hasOneOfTheRoles(token, personRoles)) {
            return ResponseEntity.status(401).body(null);
        }

        evaluatorsService.deleteEvaluator(evaluatorId);

        return ResponseEntity.status(200).body("Avaliador excluído com sucesso.");

    }

    @GetMapping("/organizations")
    public ResponseEntity<List<EvaluatorOrganizationDto>> listOrganizations(
        @RequestHeader(name = "Authorization") String token
    ) throws IOException {

        List<EvaluatorOrganizationDto> response = acessoCidadaoService.findOrganizationsFromOrganogramaAPI();

        return ResponseEntity.ok().body(response);

    }


    @GetMapping("/sections/{orgGuid}")
    public ResponseEntity<List<EvaluatorSectionDto>> listSections(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(value = "orgGuid") String orgGuid
    ) throws IOException {
        
        if (!personService.hasOneOfTheRoles(token, personRoles)) {
            return ResponseEntity.status(401).body(null);
          }

        List<EvaluatorSectionDto> response = acessoCidadaoService.findSectionsFromOrganogramaAPI(orgGuid);

        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/roles/{unitGuid}")
    public ResponseEntity<List<EvaluatorRoleDto>> listRoles(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(value = "unitGuid") String unitGuid
    ) throws IOException {
        
        if (!personService.hasOneOfTheRoles(token, personRoles)) {
            return ResponseEntity.status(401).body(null);
          }

        List<EvaluatorRoleDto> response = acessoCidadaoService.findRolesFromAcessoCidadaoAPI(unitGuid);

        return ResponseEntity.ok().body(response);

    }

    @PostMapping("/names")
    public ResponseEntity<EvaluatorsNamesResponseDto> getNamesFromGuidLists(
        @RequestHeader(name = "Authorization") String token,
        @RequestBody EvaluatorsNamesRequestDto evaluatorsNamesRequestDto
    ) throws IOException {

        if (!personService.hasOneOfTheRoles(token, personRoles)) {
            return ResponseEntity.status(401).body(null);
        }

        EvaluatorsNamesResponseDto response = evaluatorsService.mapGuidstoNames(evaluatorsNamesRequestDto);

        return ResponseEntity.ok().body(response);
        
    }
}
