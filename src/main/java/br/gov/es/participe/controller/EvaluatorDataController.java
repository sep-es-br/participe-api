package br.gov.es.participe.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.participe.controller.dto.EvaluatorOrganizationDto;
import br.gov.es.participe.controller.dto.EvaluatorSectionDto;
import br.gov.es.participe.controller.dto.EvaluatorServerDto;
import br.gov.es.participe.service.AcessoCidadaoService;
import br.gov.es.participe.service.PersonService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@CrossOrigin
@RequestMapping(value = "/evaluator-data")
public class EvaluatorDataController {
    
    @Autowired
    private AcessoCidadaoService acessoCidadaoService;

    @Autowired
    private PersonService personService;

    @GetMapping("/organizations")
    public ResponseEntity<List<EvaluatorOrganizationDto>> listOrganizations(@RequestHeader(name = "Authorization") String token) throws IOException  {

        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
            return ResponseEntity.status(401).body(null);
          }

        List<EvaluatorOrganizationDto> response = acessoCidadaoService.findForOrganizations();

        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/sections")
    public ResponseEntity<List<EvaluatorSectionDto>> listSections(@RequestHeader(name = "Authorization") String token,
    @RequestParam(value = "orgGuid") String orgGuid) throws IOException  {
        
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
            return ResponseEntity.status(401).body(null);
          }

        List<EvaluatorSectionDto> response = acessoCidadaoService.findListOfOrganizationUnits(orgGuid);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/servers")
    public ResponseEntity<List<EvaluatorServerDto>> listServers(@RequestHeader(name = "Authorization") String token,
    @RequestParam(value = "unitGuid") String unitGuid) throws IOException  {
        
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
            return ResponseEntity.status(401).body(null);
          }

        List<EvaluatorServerDto> response = acessoCidadaoService.findUnitRoles(unitGuid);

        return ResponseEntity.ok().body(response);
    }

    
    
}
