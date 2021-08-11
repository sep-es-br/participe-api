package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.IsAuthenticatedBy;

public class AuthenticationProfileDto {
  private String idByAuth;
  private String loginName;
  private String loginEmail;
  private String authenticationType;

  public AuthenticationProfileDto() {
  }

  public AuthenticationProfileDto(IsAuthenticatedBy isAuthenticatedBy) {
    this.idByAuth = isAuthenticatedBy.getIdByAuth();
    this.loginName = isAuthenticatedBy.getName();
    this.authenticationType = isAuthenticatedBy.getAuthType();
    this.loginEmail = isAuthenticatedBy.getEmail();
  }

  public String getIdByAuth() {
    return idByAuth;
  }

  public String getLoginName() {
    return loginName;
  }

  public String getAuthenticationType() {
    return authenticationType;
  }

  public String getLoginEmail() {
    return loginEmail;
  }
}
