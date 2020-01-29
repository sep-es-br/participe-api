package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.Domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DomainDto {

    private Long id;
    private String name;
    private List<LocalityDto> localities;

    public DomainDto() {
    }

    public DomainDto(Domain domain, boolean loadLocalities) {
        if (domain == null) return;

        this.id = domain.getId();
        this.name = domain.getName();

        if (loadLocalities && !domain.getLocalities().isEmpty()) {
            localities = new ArrayList<>();
            domain.getLocalities()
                    .stream()
                    .filter(locality -> locality.getParents().isEmpty())
                    .collect(Collectors.toList())
                    .forEach(locality -> localities.add(new LocalityDto(locality, domain, true)));
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LocalityDto> getLocalities() {
        return localities;
    }

    public void setLocalities(List<LocalityDto> localities) {
        this.localities = localities;
    }
}
