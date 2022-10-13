package br.gov.es.participe.model;

import java.io.Serializable;

import org.springframework.data.neo4j.core.schema.Node;

@Node
public class Channel extends Entity implements Serializable {

    private String name;

    private String url;

    public Channel() {

    }

    public Channel(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
