package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.PlanItemDto;
import br.gov.es.participe.controller.dto.StructureItemDto;
import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.service.PlanItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/plan-items", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlanItemController {

    @Autowired
    private PlanItemService planItemService;

    @GetMapping
    public ResponseEntity index(@RequestParam(value = "query", required = false) String query) {
        List<PlanItem> planItems;
        if (query != null && !query.isEmpty()) {
            planItems = planItemService.search(query);
        } else {
            planItems = planItemService.findAll();
        }

        List<PlanItemDto> response = new ArrayList<>();
        planItems.forEach(planItem -> response.add(new PlanItemDto(planItem, null, true)));

        return ResponseEntity.status(200).body(response);
    }

    @PostMapping
    public ResponseEntity store(@RequestBody PlanItemDto planItemDto) {
        PlanItem planItem = new PlanItem(planItemDto);
        planItem = planItemService.save(planItem);
        PlanItemDto response = new PlanItemDto(planItem, null, false);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity show(@PathVariable Long id) {
        PlanItemDto response = new PlanItemDto(planItemService.find(id), null, true);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Long id, @RequestBody PlanItemDto planItemDto) {
        PlanItem planItem = new PlanItem(planItemDto);
        PlanItemDto response = new PlanItemDto(planItemService.save(planItem), null, true);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity destroy(@PathVariable Long id) {
        planItemService.delete(id);
        return ResponseEntity.status(200).build();
    }
}
