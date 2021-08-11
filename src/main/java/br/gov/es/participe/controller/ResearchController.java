package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.*;
import br.gov.es.participe.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(value = "/research", produces = MediaType.APPLICATION_JSON_VALUE)
public class ResearchController {

    @Autowired
    private ResearchService researchService;

    @GetMapping("/{idConference}")
    public ResponseEntity<ResearchConfigurationDto> findResearch(@PathVariable Long idConference){
        Research research = researchService.findByIdConference(idConference).orElse(null);
        return ResponseEntity.status(200).body(research != null ? new ResearchConfigurationDto(research) : null);
    }
}
