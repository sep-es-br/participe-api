package br.gov.es.participe.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.participe.controller.dto.EvaluatorOrganizationDto;
import br.gov.es.participe.controller.dto.EvaluatorRequestDto;
import br.gov.es.participe.controller.dto.EvaluatorResponseDto;
import br.gov.es.participe.controller.dto.EvaluatorSectionDto;
import br.gov.es.participe.controller.dto.EvaluatorsNamesRequestDto;
import br.gov.es.participe.controller.dto.EvaluatorRoleDto;
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

    @GetMapping
    public ResponseEntity<List<EvaluatorResponseDto>> listEvaluators(
        @RequestHeader(name = "Authorization") String token
    ) {
        if (!(personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" }))) {
            return ResponseEntity.status(401).body(null);
        }

        List<EvaluatorResponseDto> response = evaluatorsService.findAllEvaluators();

        return ResponseEntity.status(200).body(response);
    }

    @PostMapping
    public ResponseEntity<EvaluatorResponseDto> createEvaluator(
        @RequestHeader(name = "Authorization") String token,
        @RequestBody @Valid EvaluatorRequestDto evaluatorRequestDto
    ) {
        if (!(personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" }))) {
            return ResponseEntity.status(401).body(null);
        }

        EvaluatorResponseDto response = evaluatorsService.saveEvaluator(evaluatorRequestDto);

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

        List<EvaluatorOrganizationDto> response = acessoCidadaoService.findOrganizationsFromOrganogramaAPI();

        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/sections/{orgGuid}")
    public ResponseEntity<List<EvaluatorSectionDto>> listSections(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(value = "orgGuid") String orgGuid) throws IOException  {
        
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
            return ResponseEntity.status(401).body(null);
          }

        List<EvaluatorSectionDto> response = acessoCidadaoService.findSectionsFromOrganogramaAPI(orgGuid);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/roles/{unitGuid}")
    public ResponseEntity<List<EvaluatorRoleDto>> listRoles(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(value = "unitGuid") String unitGuid) throws IOException  {
        
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
            return ResponseEntity.status(401).body(null);
          }

        List<EvaluatorRoleDto> response = acessoCidadaoService.findRolesFromAcessoCidadaoAPI(unitGuid);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/names")
    public ResponseEntity<Map<String, String>> getNamesFromGuidLists(
        @RequestHeader(name = "Authorization") String token,
        @RequestBody EvaluatorsNamesRequestDto evaluatorsNamesRequestDto
    ) throws IOException {

        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
            return ResponseEntity.status(401).body(null);
        }

        List<EvaluatorOrganizationDto> organizationsList = acessoCidadaoService.findOrganizationsFromOrganogramaAPI();

        Map<String, String> organizationsNamesList = organizationsList.stream()
            .filter((org) -> evaluatorsNamesRequestDto.getOrganizationsGuidList().contains(org.getGuid()))
            .collect(Collectors.toUnmodifiableMap((org) -> org.getGuid(), (org) -> org.getName()));

        List<EvaluatorSectionDto> sectionsList = new ArrayList<EvaluatorSectionDto>();
        
        evaluatorsNamesRequestDto.getOrganizationsGuidList().iterator().forEachRemaining((orgGuid) -> 
            {
                try {
                    acessoCidadaoService.findSectionsFromOrganogramaAPI(orgGuid).forEach(sectionsList::add);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        );

        Map<String, String> sectionsNamesList = sectionsList.stream()
            .filter((section) -> evaluatorsNamesRequestDto.getSectionsGuidList().contains(section.getGuid()))
            .collect(Collectors.toUnmodifiableMap((section) -> section.getGuid(), (section) -> section.getName()));

        List<EvaluatorRoleDto> rolesList = new ArrayList<EvaluatorRoleDto>();

        evaluatorsNamesRequestDto.getSectionsGuidList().iterator().forEachRemaining((unitGuid) -> 
            {
                try {
                    acessoCidadaoService.findRolesFromAcessoCidadaoAPI(unitGuid).forEach(rolesList::add);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        );

        Map<String, String> rolesNamesList = rolesList.stream()
            .filter((role) -> evaluatorsNamesRequestDto.getRolesGuidList().contains(role.getGuid()))
            .collect(Collectors.toUnmodifiableMap((role) -> role.getGuid(), (role) -> role.getName()));

        Map<String, String> response = new HashMap<String, String>();

        response.putAll(organizationsNamesList);
        response.putAll(sectionsNamesList);
        response.putAll(rolesNamesList);

        return ResponseEntity.ok().body(response);

    }
}
