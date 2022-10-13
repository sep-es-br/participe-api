package br.gov.es.participe.model;

import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.DateString;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Date;

@Node
public class Login extends Entity {

  @Relationship(type = "USING")
  private AuthService authService;

  @Relationship(type = "TO")
  private Conference conference;

  @Relationship(type = "MADE", direction = Relationship.Direction.INCOMING)
  private Person person;

  @DateString
  private Date time;

  public Login() {
  }

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
