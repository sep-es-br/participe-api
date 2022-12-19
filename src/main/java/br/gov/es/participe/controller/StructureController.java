package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.StructureDto;
import br.gov.es.participe.controller.dto.StructureParamDto;
import br.gov.es.participe.model.Structure;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.service.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/structures")
public class StructureController {

    @Autowired
    private StructureService structureService;

    @Autowired
    private PersonService personService;

    @GetMapping
    @SuppressWarnings("rawtypes")
    public ResponseEntity index(@RequestParam(value = "query", required = false) String query) {
        List<Structure> structures = structureService.findAll(query);
        List<StructureDto> response = new ArrayList<>();

        structures.forEach(structure -> response.add(new StructureDto(structure, true)));

        return ResponseEntity.status(200).body(response);
    }

    @Transactional
    @PostMapping
    @SuppressWarnings("rawtypes")
    public ResponseEntity store(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody StructureParamDto structureParamDto) {
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
            return ResponseEntity.status(401).body(null);
        }
        Structure structure = new Structure(structureParamDto);
        StructureDto response = new StructureDto(structureService.save(structure), true);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    @SuppressWarnings("rawtypes")
    public ResponseEntity show(@PathVariable Long id) {
        StructureDto response = new StructureDto(structureService.find(id), true);
        return ResponseEntity.status(200).body(response);
    }


    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<StructureDto> update(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable Long id,
            @RequestBody StructureParamDto structureParamDto) {
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
            return ResponseEntity.status(401).body(null);
        }
        StructureDto response = new StructureDto(structureService.update(structureParamDto, id), true);
        return ResponseEntity.status(200).body(response);
    }
/*
    @DeleteMapping("/{id}")
    @SuppressWarnings("rawtypes")
    public ResponseEntity destroy(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable Long id) {
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
            return ResponseEntity.status(401).body(null);
        }
        structureService.delete(id);
        return ResponseEntity.status(200).build();
    }
     */

}
