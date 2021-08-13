package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.Person;

public class SigninDto {

  private PersonDto person;

  private String type;

  private String token;

  private String refreshToken;

  private boolean isCompleted;

  private boolean isTemporaryPassword;

  private String authServiceName;

  public SigninDto(Person person, String authServiceName, String token, String refreshToken) {
    this.type = "Bearer";
    this.person = new PersonDto(person);
    this.token = token;
    this.refreshToken = refreshToken;
    this.isCompleted = isAllFieldsCompleted(person);
    this.authServiceName = authServiceName;
  }

  public PersonDto getPerson() {
    return person;
  }

  public void setPerson(PersonDto person) {
    this.person = person;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public boolean isCompleted() {
    return isCompleted;
  }

  public void setCompleted(boolean completed) {
    isCompleted = completed;
  }

  private boolean isAllFieldsCompleted(Person person) {
    if(person != null) {
      if(person.getAccessToken() == null) return false;
      if(person.getContactEmail() == null) return false;
      if(person.getSelfDeclaretions() == null) return false;
      if(person.getSelfDeclaretions().isEmpty()) return false;

      return person.getName() != null;
    }
    else {
      return false;
    }
  }

  public boolean isTemporaryPassword() {
    return isTemporaryPassword;
  }

  public void setTemporaryPassword(boolean isTemporaryPassword) {
    this.isTemporaryPassword = isTemporaryPassword;
  }

  public String getAuthServiceName() {
    return authServiceName;
  }

  public void setAuthServiceName(String authServiceName) {
    this.authServiceName = authServiceName;
  }
}
