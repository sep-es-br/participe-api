package br.gov.es.participe.controller;

import java.util.ArrayList;
import java.util.List;

import br.gov.es.participe.controller.dto.LocalityDto;
import br.gov.es.participe.controller.dto.LocalityParamDto;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.service.LocalityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(value = "/localities")
public class LocalityController {

    @Autowired
    private LocalityService localityService;

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
        localities.forEach(locality -> response.add(new LocalityDto(locality, null, true)));

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/domain/{id}")
    public ResponseEntity findByDomain(@PathVariable Long id) {
        List<Locality> localities = localityService.findByDomain(id);
        localities.removeIf(locality -> !(locality.getParents() == null || locality.getParents().isEmpty()));
        List<LocalityDto> response = new ArrayList<>();
        localities.forEach(locality -> response.add(new LocalityDto(locality, null, true)));

        return ResponseEntity.status(200).body(response);
    }

    @PostMapping
    public ResponseEntity store(@RequestBody LocalityParamDto localityParamDto) {
        Locality locality = new Locality(localityParamDto);
        LocalityDto response = new LocalityDto(localityService.create(locality), null, true);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity show(@PathVariable Long id) {
        LocalityDto response = new LocalityDto(localityService.find(id), null, true);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Long id, @RequestBody LocalityParamDto localityParamDto) {
        LocalityDto response = new LocalityDto(localityService.update(id, localityParamDto.getName()), null, true);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{idLocality}/domain/{idDomain}")
    public ResponseEntity destroy(@PathVariable Long idLocality, @PathVariable Long idDomain) {
        localityService.delete(idLocality, idDomain);
        return ResponseEntity.status(200).build();
    }
}
