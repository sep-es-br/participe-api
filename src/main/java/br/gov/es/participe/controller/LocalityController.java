package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.CardScreenDto;
import br.gov.es.participe.controller.dto.ComplementLocalityDto;
import br.gov.es.participe.controller.dto.LocalityDto;
import br.gov.es.participe.controller.dto.LocalityParamDto;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.service.ConferenceService;
import br.gov.es.participe.service.LocalityService;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/localities")
public class LocalityController {

    @Autowired
    private LocalityService localityService;

    @Autowired
    private ConferenceService conferenceService;

    @Autowired
    private PersonService personService;

    @SuppressWarnings("rawtypes")
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

    @SuppressWarnings("rawtypes")
    @GetMapping("/domain/{id}")
    public ResponseEntity findByDomain(@PathVariable Long id) {
        List<Locality> localities = localityService.findByDomain(id);
        localities.removeIf(locality -> !(locality.getParents() == null || locality.getParents().isEmpty()));
        List<LocalityDto> response = new ArrayList<>();
        localities.forEach(locality -> response.add(new LocalityDto(locality, null, true, true)));

        return ResponseEntity.status(200).body(response);
    }

    @SuppressWarnings("rawtypes")
    @GetMapping("/conference/{id}")
    public ResponseEntity findByIdConference(@PathVariable Long id) {
        List<Locality> localities = localityService.findByIdConference(id);

        List<LocalityDto> localitiesDto = new ArrayList<>();
        localities.forEach(locality -> {
            LocalityDto dto = new LocalityDto(locality, null, true, false);
            if (dto.getChildren() != null && !dto.getChildren().isEmpty()) {
                StringUtils stringUtils = new StringUtils();
                dto.getChildren().sort(Comparator.comparing(c -> stringUtils.replaceSpecialCharacters(c.getName())));
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

    @SuppressWarnings("rawtypes")
    @GetMapping("/complement/{idConference}")
    public ResponseEntity findLocalitiesToComplement(@PathVariable Long idConference) {
        List<Locality> localities = localityService.findLocalitiesToComplement(idConference, true);
        List<LocalityDto> localitiesDto = new ArrayList<>();
        localities.forEach(locality -> localitiesDto.add(new LocalityDto(locality, null, true, false)));

        ComplementLocalityDto response = new ComplementLocalityDto();
        response.setLocalities(localitiesDto);
        if (!localities.isEmpty()) {
            Locality type = localityService.find(localities.get(0).getId());
            response.setNameType(type.getType().getName());
        }
        return ResponseEntity.status(200).body(response);
    }

    @Transactional
    @PostMapping
    @SuppressWarnings("rawtypes")
    public ResponseEntity store(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody LocalityParamDto localityParamDto) {
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
            return ResponseEntity.status(401).body(null);
        }
        Locality locality = new Locality(localityParamDto);
        LocalityDto response = new LocalityDto(localityService.create(locality), null, true, true);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    @SuppressWarnings("rawtypes")
    public ResponseEntity show(@PathVariable Long id) {
        LocalityDto response = new LocalityDto(localityService.find(id), null, true, true);
        return ResponseEntity.status(200).body(response);
    }

    @Transactional
    @PutMapping("/{id}")
    @SuppressWarnings("rawtypes")
    public ResponseEntity update(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable Long id,
            @RequestBody LocalityParamDto localityParamDto) {
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
            return ResponseEntity.status(401).body(null);
        }
        LocalityDto response = new LocalityDto(localityService.update(id, localityParamDto), null, true, true);
        return ResponseEntity.status(200).body(response);
    }


    @Transactional
    @DeleteMapping("/{idLocality}/domain/{idDomain}")
    @SuppressWarnings("rawtypes")
    public ResponseEntity destroy(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable Long idLocality,
            @PathVariable Long idDomain) {
        if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
            return ResponseEntity.status(401).body(null);
        }
        localityService.delete(idLocality, idDomain);
        return ResponseEntity.status(200).build();
    }
    
}
