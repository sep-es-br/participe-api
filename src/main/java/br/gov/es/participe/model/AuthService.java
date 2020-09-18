package br.gov.es.participe.model;

import java.io.Serializable;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;

@NodeEntity
public class AuthService extends Entity implements Serializable {

    private String server;

    private String serverId;
    
    private Integer numberOfAccesses;
    
    @JsonIgnore
    @Relationship(type = "IS_AUTHENTICATED_BY", direction = Relationship.INCOMING)
    private Person person;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

	public Integer getNumberOfAccesses() {
		return numberOfAccesses;
	}

	public void setNumberOfAccesses(Integer numberOfAccesses) {
		this.numberOfAccesses = numberOfAccesses;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}
