package br.gov.es.participe.controller.dto;

import br.gov.es.participe.util.interfaces.QueryResult;

import java.util.Date;

@QueryResult
public class PersonMeetingDto {
    private Long personId;
    private String name;
    private String email;
    private String telephone;
    private String locality;
    private String superLocality;
    private Long superLocalityId;
    private String regionalizable;
    private Boolean checkedIn;
    private Date checkedInDate;
    private String cpf;
    private Boolean isAuthTypeCpf;

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getRegionalizable() {
        return regionalizable;
    }

    public void setRegionalizable(String regionalizable) {
        this.regionalizable = regionalizable;
    }

    public Boolean getCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(Boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public Date getCheckedInDate() {
        return checkedInDate;
    }

    public void setCheckedInDate(Date checkedInDate) {
        this.checkedInDate = checkedInDate;
    }

    public String getSuperLocality() {
        return superLocality;
    }

    public void setSuperLocality(String superLocality) {
        this.superLocality = superLocality;
    }

    public Long getSuperLocalityId() {
        return superLocalityId;
    }

    public void setSuperLocalityId(Long superLocalityId) {
        this.superLocalityId = superLocalityId;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Boolean getAuthTypeCpf() {
        return isAuthTypeCpf;
    }

    public void setAuthTypeCpf(Boolean authTypeCpf) {
        isAuthTypeCpf = authTypeCpf;
    }
}
