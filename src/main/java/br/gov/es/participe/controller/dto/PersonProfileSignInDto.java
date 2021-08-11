package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.Person;

public class PersonProfileSignInDto {

  private final String name;
  private final String loginName;
  private final String loginEmail;
  private final String loginId;
  private final String loginType;

  private final Boolean hasRelatedRecord;
  private final Long personIdAlreadyRelated;

  public PersonProfileSignInDto(
    Person person,
    String loginName,
    String loginEmail,
    String loginId,
    String loginType
  ) {
    this(person, loginName, loginEmail, loginId, loginType, false, null);
  }

  public PersonProfileSignInDto(
    Person person,
    String loginName,
    String loginEmail,
    String loginId,
    String loginType,
    Boolean hasRelatedRecord,
    Long personIdAlreadyRelated
  ) {
    this.name = person.getName();
    this.loginName = loginName;
    this.loginEmail = loginEmail;
    this.loginId = loginId;
    this.loginType = loginType;
    this.hasRelatedRecord = hasRelatedRecord;
    this.personIdAlreadyRelated = personIdAlreadyRelated;
  }

  public String getName() {
    return name;
  }

  public String getLoginName() {
    return loginName;
  }

  public String getLoginEmail() {
    return loginEmail;
  }

  public String getLoginId() {
    return loginId;
  }

  public String getLoginType() {
    return loginType;
  }

  public Boolean getHasRelatedRecord() {
    return hasRelatedRecord;
  }

  public Long getPersonIdAlreadyRelated() {
    return personIdAlreadyRelated;
  }
}
