package br.gov.es.participe.controller.dto;

import org.springframework.data.neo4j.annotation.QueryResult;

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
