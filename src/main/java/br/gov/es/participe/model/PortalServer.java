package br.gov.es.participe.model;

import org.neo4j.ogm.annotation.*;

import java.io.*;
import java.util.*;

public class PortalServer extends Entity implements Serializable {
    private String url;

    @Relationship(type = "HOSTS", direction = Relationship.OUTGOING)
    private Set<Conference> conferences;

    @Relationship(type = "IS_DEFAULT", direction = Relationship.UNDIRECTED)
    private Conference conference;

    public PortalServer() {}

    public PortalServer(String url) {
        this.url = url;
    }

    public Set<Conference> getConferences() {
        if (conferences == null) {
            conferences = new HashSet<>();
        }
        return conferences;
    }

    public void setConferences(Set<Conference> conferences) {
        this.conferences = conferences;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Conference getConference() {
        return conference;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }
}
