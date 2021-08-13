package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.StructureItemDto;
import br.gov.es.participe.model.StructureItem;
import br.gov.es.participe.service.StructureItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping(value = "/structure-items")
public class StructureItemController {

    @Autowired
    private StructureItemService structureItemService;

    @GetMapping
    public ResponseEntity index(@RequestParam(value = "query", required = false) String query) {
        List<StructureItem> structureItems;
        if (query != null && !query.isEmpty()) {
            structureItems = structureItemService.search(query);
        } else {
            structureItems = structureItemService.findAll();
        }

        List<StructureItemDto> response = new ArrayList<>();
        structureItems.forEach(structureItem -> response.add(new StructureItemDto(structureItem, null, true, true)));

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("list")
    public ResponseEntity listByStructure(@RequestParam(value = "id") Long idStructure) {
        List<StructureItem> structureItems = structureItemService.findByStructure(idStructure);
        List<StructureItemDto> response = null;
        if (structureItems != null && !structureItems.isEmpty()) {
            response = structureItems.stream().map(StructureItemDto::new).collect(Collectors.toList());
        }
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping
    public ResponseEntity store(@RequestBody StructureItemDto structureItemDto) {
        StructureItem structureItem = new StructureItem(structureItemDto);
        StructureItemDto response = new StructureItemDto(structureItemService.create(structureItem), null, true, true);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity show(@PathVariable Long id) {
        StructureItemDto response = new StructureItemDto(structureItemService.find(id), null, true, true);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Long id, @RequestBody StructureItemDto structureItemDto) {
        StructureItemDto response = new StructureItemDto(structureItemService.update(id, structureItemDto), null, true, true);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity destroy(@PathVariable Long id) {
        structureItemService.delete(id);
        return ResponseEntity.status(200).build();
    }

}
