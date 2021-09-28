package br.gov.es.participe.model;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import java.util.Date;

@RelationshipEntity(type = "IS_AUTHENTICATED_BY")
public class IsAuthenticatedBy extends Entity {

  private String idByAuth;
  private String name;
  private String authType;
  private String email;
  private String password;

  @Property(name = "temporary_password")
  private Boolean temporaryPassword;

  @DateLong
  @Property(name = "password_time")
  private Date passwordTime;

  @StartNode
  private Person person;

  @EndNode
  private AuthService authService;

  public IsAuthenticatedBy() {
  }

  public IsAuthenticatedBy(String name, String authType, String password,
                           Boolean temporaryPassword, Date passwordTime, Person person, AuthService authService) {

    this.idByAuth = authService.getServerId();
    this.name = name;
    this.authType = authType;
    this.email = person.getContactEmail();
    this.password = password;
    this.temporaryPassword = temporaryPassword;
    this.passwordTime = passwordTime;
    this.person = person;
    this.authService = authService;
  }

  public IsAuthenticatedBy copyWithoutRelationshipOf() {
    IsAuthenticatedBy newAuthenticatedBy = new IsAuthenticatedBy();

    newAuthenticatedBy.setIdByAuth(this.getIdByAuth());
    newAuthenticatedBy.setAuthType(this.getAuthType());
    newAuthenticatedBy.setName(this.getName());
    newAuthenticatedBy.setEmail(this.getEmail());
    newAuthenticatedBy.setPassword(this.getPassword());
    newAuthenticatedBy.setTemporaryPassword(this.getTemporaryPassword());
    newAuthenticatedBy.setPasswordTime(this.getPasswordTime());

    return newAuthenticatedBy;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAuthType() {
    return authType;
  }

  public void setAuthType(String authType) {
    this.authType = authType;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Boolean getTemporaryPassword() {
    return temporaryPassword;
  }

  public void setTemporaryPassword(Boolean temporaryPassword) {
    this.temporaryPassword = temporaryPassword;
  }

  public Date getPasswordTime() {
    return passwordTime;
  }

  public void setPasswordTime(Date passwordTime) {
    this.passwordTime = passwordTime;
  }

  public String getIdByAuth() {
    return idByAuth;
  }

  public void setIdByAuth(String idByAuth) {
    this.idByAuth = idByAuth;
  }

  public Person getPerson() {
    return person;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  public AuthService getAuthService() {
    return authService;
  }

  public void setAuthService(AuthService authService) {
    this.authService = authService;
  }
}
