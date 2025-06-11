package br.gov.es.participe.controller.dto;

import java.util.Date;
import org.springframework.data.neo4j.annotation.QueryResult;

import org.neo4j.ogm.annotation.typeconversion.DateString;

@QueryResult
public class AuthorityMeetingDto {
    private Long idPerson;
    private Long idCheckIn;
    
    @DateString
    private Date checkInTime;
    
    private Long idLocality;
    
    private Boolean announced;
    private String name;
    private String role;
    private String organization;
    private String organizationShort;

    public String getOrganizationShort() {
        return organizationShort;
    }

    public void setOrganizationShort(String organizationShort) {
        this.organizationShort = organizationShort;
    }
    
    public Long getIdPerson() {
        return idPerson;
    }

    public Long getIdLocality() {
        return idLocality;
    }

    public void setIdLocality(Long localityId) {
        this.idLocality = localityId;
    }
    
    

    public void setIdPerson(Long idPerson) {
        this.idPerson = idPerson;
    }

    public Long getIdCheckIn() {
        return idCheckIn;
    }

    public void setIdCheckIn(Long idCheckIn) {
        this.idCheckIn = idCheckIn;
    }

    public Date getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Date checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Boolean getAnnounced() {
        return announced;
    }

    public void setAnnounced(Boolean announced) {
        this.announced = announced;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    
    
}
