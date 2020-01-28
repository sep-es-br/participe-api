package br.gov.es.participe.util.dto;

import br.gov.es.participe.controller.dto.LocalityDto;
import br.gov.es.participe.model.Locality;

import java.util.Set;

public class PlaceDto {

    private Long id;
    private String name;
    private LocalityDto locality;
    private PlaceDto parent;
    private Set<PlaceDto> children;

    public PlaceDto() {
    }

    public PlaceDto(Locality place, boolean loadChildren) {
//        id = place.getId();
//        name = place.getName();
//        if (place.getLocality() != null) {
//            this.locality = new LocalityDto(place.getLocality());
//        }
//        if (!place.getChildren().isEmpty()) {
//            place.getChildren().forEach(child -> {
//                children.add(new PlaceDto(child, loadChildren));
//            });
//        }
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

    public LocalityDto getLocality() {
        return locality;
    }

    public void setLocality(LocalityDto locality) {
        this.locality = locality;
    }

    public PlaceDto getParent() {
        return parent;
    }

    public void setParent(PlaceDto parent) {
        this.parent = parent;
    }

    public Set<PlaceDto> getChildren() {
        return children;
    }

    public void setChildren(Set<PlaceDto> children) {
        this.children = children;
    }
}
