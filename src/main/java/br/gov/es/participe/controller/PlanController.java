package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.PlanDto;
import br.gov.es.participe.model.Plan;
import br.gov.es.participe.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/plans", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlanController {

    @Autowired
    private PlanService planService;

    @GetMapping
    public ResponseEntity index(@RequestParam(value = "query", required = false) String query) {
        List<Plan> plans = planService.findAll(query);
        List<PlanDto> response = new ArrayList<>();

        plans.forEach(plan -> response.add(new PlanDto(plan, true)));

        return ResponseEntity.status(200).body(response);
    }

    @PostMapping
    public ResponseEntity store(@RequestBody PlanDto planDto) {
        Plan plan = new Plan(planDto);
        PlanDto response = new PlanDto(planService.save(plan), true);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity show(@PathVariable Long id) {
        PlanDto response = new PlanDto(planService.find(id), true);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Long id, @RequestBody PlanDto planDto) {
        planDto.setId(id);
        Plan plan = new Plan(planDto);
        PlanDto response = new PlanDto(planService.save(plan), true);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity destroy(@PathVariable Long id) {
        planService.delete(id);
        return ResponseEntity.status(200).build();
    }
}
