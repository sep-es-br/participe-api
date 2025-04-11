package br.gov.es.participe.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.participe.controller.dto.DomainDto;
import br.gov.es.participe.controller.dto.DomainParamDto;
import br.gov.es.participe.model.Domain;
import br.gov.es.participe.service.DomainService;
import br.gov.es.participe.service.PersonService;

@RestController
@CrossOrigin
@RequestMapping(value = "/domains")
public class DomainController {

    @Autowired
    private DomainService domainService;

    @Autowired
    private PersonService personService;

    @GetMapping
    @SuppressWarnings({ "rawtypes" })
    public ResponseEntity index(
            @RequestParam(value = "query", required = false) String query) {

        List<Domain> domains = domainService.findAll(query);
        List<DomainDto> response = new ArrayList<>();

        domains.forEach(domain -> response.add(new DomainDto(domain, true)));

        return ResponseEntity.status(200).body(response);
    }

    @Transactional
    @PostMapping
    @SuppressWarnings({ "rawtypes" })
    public ResponseEntity store(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody DomainParamDto domainParamDto) {
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
            return ResponseEntity.status(401).body(null);
        }
        Domain domain = new Domain(domainParamDto);
        DomainDto response = new DomainDto(domainService.save(domain), true);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    @SuppressWarnings({ "rawtypes" })
    public ResponseEntity show(@PathVariable Long id) {
        DomainDto response = new DomainDto(domainService.find(id), true);
        return ResponseEntity.status(200).body(response);
    }

    @Transactional
    @PutMapping("/{id}")
    @SuppressWarnings({ "rawtypes" })
    public ResponseEntity update(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable Long id, @RequestBody DomainParamDto domainUpdateDto) {
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
            return ResponseEntity.status(401).body(null);
        }
        domainUpdateDto.setId(id);
        Domain domain = new Domain(domainUpdateDto);
        DomainDto response = new DomainDto(domainService.save(domain), true);
        return ResponseEntity.status(200).body(response);
    }


    @Transactional
    @DeleteMapping("/{id}")
    @SuppressWarnings({ "rawtypes" })
    public ResponseEntity destroy(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable Long id) {
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
            return ResponseEntity.status(401).body(null);
        }
        domainService.delete(id);
        return ResponseEntity.status(200).build();
    }
     
}
