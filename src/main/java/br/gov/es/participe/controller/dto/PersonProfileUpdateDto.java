package br.gov.es.participe.controller.dto;


import java.util.List;

public class PersonProfileUpdateDto {

  private Long id;
  private String name;
  private String telephone;
  private String contactEmail;
  private Long localityId;
  private Boolean receiveInformational;

  private List<AuthenticationProfileDto> authentications;
  private Long conferenceId;

  private String newPassword;
  private String confirmNewPassword;

  public PersonProfileUpdateDto() {
  }

  public PersonProfileUpdateDto(Long id, String name, String telephone, String contactEmail,
                                Long localityId, Boolean receiveInformational, List<AuthenticationProfileDto> authentications,
                                Long conferenceId, String newPassword, String confirmNewPassword
  ) {
    this.id = id;
    this.name = name;
    this.telephone = telephone;
    this.contactEmail = contactEmail;
    this.localityId = localityId;
    this.receiveInformational = receiveInformational;
    this.authentications = authentications;
    this.conferenceId = conferenceId;
    this.newPassword = newPassword;
    this.confirmNewPassword = confirmNewPassword;
  }

  public String getName() {
    return name;
  }

  public String getTelephone() {
    return telephone;
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public Long getLocalityId() {
    return localityId;
  }

  public Boolean getReceiveInformational() {
    return receiveInformational;
  }

  public List<AuthenticationProfileDto> getAuthentications() {
    return authentications;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public void setLocalityId(Long localityId) {
    this.localityId = localityId;
  }

  public void setReceiveInformational(Boolean receiveInformational) {
    this.receiveInformational = receiveInformational;
  }

  public void setAuthentications(List<AuthenticationProfileDto> authentications) {
    this.authentications = authentications;
  }

  public Long getConferenceId() {
    return conferenceId;
  }

  public void setConferenceId(Long conferenceId) {
    this.conferenceId = conferenceId;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public String getConfirmNewPassword() {
    return confirmNewPassword;
  }

  public void setConfirmNewPassword(String confirmNewPassword) {
    this.confirmNewPassword = confirmNewPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
}
