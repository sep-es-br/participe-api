package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.*;

public class ExternalLinksDto {
    private Long id;
    private String label;
    private String url;

    public ExternalLinksDto(){}

    public ExternalLinksDto(IsLinkedBy link){
        setId(link.getId());
        setLabel(link.getLabel());
        setUrl(link.getExternalContent().getUrl());
    }

    public ExternalLinksDto(Long id, String label, String url) {
        setId(id);
        setLabel(label);
        setUrl(url);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
