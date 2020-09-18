package br.gov.es.participe.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateString;

import java.util.Date;

@NodeEntity
public class Login extends Entity {
	
	@Relationship(type = "USING")
	private AuthService authService;
	
	@Relationship(type = "TO")
	private Conference conference;
	
	@Relationship(type = "MADE", direction = Relationship.INCOMING)
	private Person person;

    @DateString
    private Date time;
    
    public Login() {}

	public Login(Person person, AuthService authService, Conference conference) {
		this.person = person;
		this.authService = authService;
		this.conference = conference;
		this.time = new Date();
	}

	public AuthService getAuthService() {
		return authService;
	}

	public void setAuthService(AuthService authService) {
		this.authService = authService;
	}

	public Conference getConference() {
		return conference;
	}

	public void setConference(Conference conference) {
		this.conference = conference;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
}
