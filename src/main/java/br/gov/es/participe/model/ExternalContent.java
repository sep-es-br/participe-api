package br.gov.es.participe.model;

import br.gov.es.participe.controller.dto.*;
import org.springframework.data.neo4j.core.schema.Node;

import java.io.Serializable;

@Node
public class ExternalContent extends Entity implements Serializable {
    private String url;

    public ExternalContent() {}

    public ExternalContent(ExternalLinksDto link) {
        this.url = link.getUrl();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        if (url != null ) {
            this.url = url.trim();
        }
    }
}
