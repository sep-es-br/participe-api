package br.gov.es.participe.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

import br.gov.es.participe.controller.dto.PlanItemDto;
import br.gov.es.participe.controller.dto.PlanItemParamDto;
import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.service.PlanItemService;

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
    public ResponseEntity store(@RequestBody PlanItemParamDto planItemParamDto) {
        PlanItem planItem = new PlanItem(planItemParamDto);
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
    public ResponseEntity update(@PathVariable Long id, @RequestBody PlanItemParamDto planItemParamDto) {
        PlanItem planItem = new PlanItem(planItemParamDto);
        PlanItemDto response = new PlanItemDto(planItemService.save(planItem), null, true);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity destroy(@PathVariable Long id) {
        planItemService.delete(id);
        return ResponseEntity.status(200).build();
    }
}
