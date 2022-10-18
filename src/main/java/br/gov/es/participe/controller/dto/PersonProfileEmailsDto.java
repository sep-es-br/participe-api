package br.gov.es.participe.controller.dto;

import br.gov.es.participe.util.interfaces.QueryResult;

@QueryResult
public class PersonProfileEmailsDto {

  private final String email;

  public PersonProfileEmailsDto(String email) {
    this.email = email;
  }

  public String getEmail() {
    return email;
  }
}
