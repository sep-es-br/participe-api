package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.LocalityTypeDto;
import br.gov.es.participe.model.LocalityType;
import br.gov.es.participe.service.LocalityTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/locality-types")
public class LocalityTypeController {

    @Autowired
    private LocalityTypeService localityTypeService;

    @GetMapping
    @SuppressWarnings("rawtypes")
    public ResponseEntity index() {
        List<LocalityType> localityTypes = localityTypeService.findAll();
        List<LocalityTypeDto> response = new ArrayList<>();

        localityTypes.forEach(localityType -> response.add(new LocalityTypeDto(localityType)));

        return ResponseEntity.status(200).body(response);
    }

}
