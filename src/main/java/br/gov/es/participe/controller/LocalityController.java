package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.CardScreenDto;
import br.gov.es.participe.controller.dto.ComplementLocalityDto;
import br.gov.es.participe.controller.dto.LocalityDto;
import br.gov.es.participe.controller.dto.LocalityParamDto;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.service.ConferenceService;
import br.gov.es.participe.service.LocalityService;
import br.gov.es.participe.util.ParticipeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/localities")
public class LocalityController {

    @Autowired
    private LocalityService localityService;
    
    @Autowired
    private ConferenceService conferenceService;

    @GetMapping
    public ResponseEntity index(@RequestParam(value = "query", required = false) String query,
                                @RequestParam(value = "type", required = false) Long type) {
        List<Locality> localities;
        if (query != null && !query.isEmpty() && type != null) {
            localities = localityService.search(query, type);
        } else {
            localities = localityService.findAll();
        }

        List<LocalityDto> response = new ArrayList<>();
        localities.forEach(locality -> response.add(new LocalityDto(locality, null, true, true)));

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/domain/{id}")
    public ResponseEntity findByDomain(@PathVariable Long id) {
        List<Locality> localities = localityService.findByDomain(id);
        localities.removeIf(locality -> !(locality.getParents() == null || locality.getParents().isEmpty()));
        List<LocalityDto> response = new ArrayList<>();
        localities.forEach(locality -> response.add(new LocalityDto(locality, null, true, true)));

        return ResponseEntity.status(200).body(response);
    }
    
    @GetMapping("/conference/{id}")
    public ResponseEntity findByIdConference(@PathVariable Long id) {
    	List<Locality> localities = localityService.findByIdConference(id);
    	
    	List<LocalityDto> localitiesDto = new ArrayList<>();
    	localities.forEach(locality -> {
    		LocalityDto dto = new LocalityDto(locality, null, true, false);
    		if(dto.getChildren() != null && !dto.getChildren().isEmpty()) {
    			dto.getChildren().sort((c1, c2) -> c1.getName().trim().compareTo(c2.getName().trim()));
    			dto.setMapSplit(new ArrayList<>());
    			dto.getChildren().forEach(loc -> {
    				String name = loc.getName().trim().toLowerCase();
    				name = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    				name = name.replace(" ", "-");
    				dto.getMapSplit().add(name);
    			});
    		}
    		localitiesDto.add(dto);
		});
    	
    	CardScreenDto response = new CardScreenDto();
    	conferenceService.generateCardScreen(response, id);
    	
    	response.setLocalities(localitiesDto);
    	
    	return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/complement/{idConference}")
    public ResponseEntity findLocalitiesToComplement(@PathVariable Long idConference) {
    	List<Locality> localities = localityService.findLocalitiesToComplement(idConference, true);
    	List<LocalityDto> localitiesDto = new ArrayList<>();
    	localities.forEach(locality -> localitiesDto.add(new LocalityDto(locality, null, true, false)));
    	
    	ComplementLocalityDto response = new ComplementLocalityDto();
    	response.setLocalities(localitiesDto);
    	if(localities != null && !localities.isEmpty()) {
    		Locality type = localityService.find(localities.get(0).getId());
    		response.setNameType(type.getType().getName());
    	}
    	 
    	return ResponseEntity.status(200).body(response);
    }

    @PostMapping
    public ResponseEntity store(@RequestBody LocalityParamDto localityParamDto) {
        Locality locality = new Locality(localityParamDto);
        LocalityDto response = new LocalityDto(localityService.create(locality), null, true, true);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity show(@PathVariable Long id) {
        LocalityDto response = new LocalityDto(localityService.find(id), null, true, true);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Long id, @RequestBody LocalityParamDto localityParamDto) {
        LocalityDto response = new LocalityDto(localityService.update(id, localityParamDto.getName()), null, true, true);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{idLocality}/domain/{idDomain}")
    public ResponseEntity destroy(@PathVariable Long idLocality, @PathVariable Long idDomain) {
        localityService.delete(idLocality, idDomain);
        return ResponseEntity.status(200).build();
    }
}
