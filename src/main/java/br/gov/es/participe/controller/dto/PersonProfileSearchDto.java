package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.Person;

import java.util.List;

public class PersonProfileSearchDto {

  private final String name;
  private final String contactEmail;
  private final Long localityId;
  private final String telephone;
  private final List<AuthenticationProfileDto> authentications;
  private final Boolean receiveInformational;

  public PersonProfileSearchDto(
    Person person,
    LocalityInfoDto locality,
    List<AuthenticationProfileDto> authentications,
    Boolean receiveInformational
  ) {
    this.name = person.getName();
    this.contactEmail = person.getContactEmail();
    this.localityId = locality.getLocalityId();
    this.telephone = person.getTelephone();
    this.authentications = authentications;
    this.receiveInformational = receiveInformational;
  }

  public String getName() {
    return name;
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public Long getLocalityId() {
    return localityId;
  }

  public String getTelephone() {
    return telephone;
  }

  public List<AuthenticationProfileDto> getAuthentications() {
    return authentications;
  }

  public Boolean getReceiveInformational() {
    return receiveInformational;
  }
}
