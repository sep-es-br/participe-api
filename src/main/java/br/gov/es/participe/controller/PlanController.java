package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.PlanDto;
import br.gov.es.participe.controller.dto.PlanParamDto;
import br.gov.es.participe.model.Plan;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/plans", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlanController {

    @Autowired
    private PlanService planService;

    @Autowired
    private PersonService personService;

    @GetMapping
    @SuppressWarnings("rawtypes")
    public ResponseEntity index(@RequestParam(value = "query", required = false) String query) {
        List<Plan> plans = planService.findAll(query);
        List<PlanDto> response = new ArrayList<>();

        plans.forEach(plan -> response.add(new PlanDto(plan, true)));

        return ResponseEntity.status(200).body(response);
    }

    @Transactional
    @PostMapping
    @SuppressWarnings("rawtypes")
    public ResponseEntity store(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody PlanParamDto planParamDto) {
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
            return ResponseEntity.status(401).body(null);
        }
        Plan plan = new Plan(planParamDto);
        PlanDto response = new PlanDto(planService.save(plan), true);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    @SuppressWarnings("rawtypes")
    public ResponseEntity show(@PathVariable Long id) {
        PlanDto response = new PlanDto(planService.find(id), true);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    @SuppressWarnings("rawtypes")
    public ResponseEntity update(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable Long id,
            @RequestBody PlanParamDto planParamDto) {
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
            return ResponseEntity.status(401).body(null);
        }
        planParamDto.setId(id);
        Plan plan = new Plan(planParamDto);
        PlanDto response = new PlanDto(planService.save(plan), true);
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
        planService.delete(id);
        return ResponseEntity.status(200).build();
    }
    */
}
