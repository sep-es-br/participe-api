package br.gov.es.participe.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.participe.controller.dto.StructureDto;
import br.gov.es.participe.model.Structure;
import br.gov.es.participe.service.StructureService;

@RestController
@CrossOrigin
@RequestMapping(value = "/structures")
public class StructureController {

    @Autowired
    private StructureService structureService;

    @GetMapping
    public ResponseEntity index(@RequestParam(value = "query", required = false) String query) {
        List<Structure> structures = structureService.findAll(query);
        List<StructureDto> response = new ArrayList<>();

        structures.forEach(structure -> response.add(new StructureDto(structure, true)));

        return ResponseEntity.status(200).body(response);
    }

    @PostMapping
    public ResponseEntity store(@RequestBody StructureDto structureDto) {
        Structure structure = new Structure(structureDto);
        StructureDto response = new StructureDto(structureService.save(structure), true);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity show(@PathVariable Long id) {
        StructureDto response = new StructureDto(structureService.find(id), true);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Long id, @RequestBody StructureDto structureDto) {
        structureDto.setId(id);
        Structure structure = new Structure(structureDto);
        StructureDto response = new StructureDto(structureService.save(structure), true);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity destroy(@PathVariable Long id) {
        structureService.delete(id);
        return ResponseEntity.status(200).build();
    }

}
