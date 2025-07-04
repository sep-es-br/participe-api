package br.gov.es.participe.controller.dto;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Date;
import java.util.List;

@QueryResult
public class PersonMeetingDto {
    private Long personId;
    private Long checkInId;
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
    private List<String> authName;
    private Boolean isAuthority;
    private Boolean isAnnounced;
    private Boolean toAnnounce;
    private String role;
    private String organization;
    private String sub;

    public Long getCheckInId() {
        return checkInId;
    }

    public void setCheckInId(Long checkInId) {
        this.checkInId = checkInId;
    }

    public Boolean getIsAuthTypeCpf() {
        return isAuthTypeCpf;
    }

    public void setIsAuthTypeCpf(Boolean isAuthTypeCpf) {
        this.isAuthTypeCpf = isAuthTypeCpf;
    }

    public Boolean getIsAnnounced() {
        return isAnnounced;
    }

    public void setIsAnnounced(Boolean isAnnounced) {
        this.isAnnounced = isAnnounced;
    }

    public Boolean getToAnnounce() {
        return toAnnounce;
    }

    public void setToAnnounce(Boolean toAnnounce) {
        this.toAnnounce = toAnnounce;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    
    
    public Boolean getIsAuthority() {
        return isAuthority;
    }

    public void setIsAuthority(Boolean isAuthority) {
        this.isAuthority = isAuthority;
    }

    public List<String> getAuthName() {
        return authName;
    }

    public void setAuthName(List<String> authName) {
        this.authName = authName;
    }

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

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }
}
