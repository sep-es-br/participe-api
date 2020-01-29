package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.Domain;
import br.gov.es.participe.model.Locality;

import java.util.ArrayList;
import java.util.List;

public class LocalityDto {

    private Long id;
    private String name;
    private LocalityTypeDto type;
    private List<DomainDto> domains;
    private List<LocalityDto> parents;
    private List<LocalityDto> children;

    public LocalityDto() {
    }

    public LocalityDto(Locality locality, Domain parentDomain, boolean loadChildren) {
        if (locality == null) return;
        if (parentDomain != null && !locality.getDomains().contains(parentDomain)) return;

        id = locality.getId();
        name = locality.getName();
        type = new LocalityTypeDto(locality.getType());
        if (!locality.getDomains().isEmpty()) {
            domains = new ArrayList<>();
            locality.getDomains().forEach(domain -> domains.add(new DomainDto(domain, false)));
        }

        if (!locality.getParents().isEmpty()) {
            parents = new ArrayList<>();
            locality.getParents().forEach(parent -> parents.add(new LocalityDto(parent, parentDomain, false)));
        }

        if (loadChildren && !locality.getChildren().isEmpty()) {
            children = new ArrayList<>();
            locality.getChildren().forEach(child -> {
                LocalityDto childLocalityDto = new LocalityDto(child, parentDomain, true);
                if (childLocalityDto.getId() != null) {
                    children.add(childLocalityDto);
                }
            });
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

    public LocalityTypeDto getType() {
        return type;
    }

    public void setType(LocalityTypeDto type) {
        this.type = type;
    }

    public List<DomainDto> getDomains() {
        return domains;
    }

    public void setDomains(List<DomainDto> domains) {
        this.domains = domains;
    }

    public List<LocalityDto> getParents() {
        return parents;
    }

    public void setParents(List<LocalityDto> parents) {
        this.parents = parents;
    }

    public List<LocalityDto> getChildren() {
        return children;
    }

    public void setChildren(List<LocalityDto> children) {
        this.children = children;
    }
}
