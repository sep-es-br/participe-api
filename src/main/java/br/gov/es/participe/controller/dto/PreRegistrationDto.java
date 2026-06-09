package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.PreRegistration;
import java.util.*;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class PreRegistrationDto {
    private Long id;
    private MeetingDto meeting;
    private PersonDto person;
    private Date checkInDate;
    private Date preRegistrationDate;
        
    private OptionOrganization organization;
    private String role;
    private String email;
    private Long localityId;
    
    private Boolean isAuthority;
    private String authoritySub;
    
    private String qrcode;

    public PreRegistrationDto() {
    }

    public PreRegistrationDto(PreRegistration preRegistration) {
        if (preRegistration == null) return;

        this.id = preRegistration.getId();
        this.meeting = new MeetingDto(preRegistration.getMeeting(),false);
        this.person = new PersonDto(preRegistration.getPerson());
        this.checkInDate = preRegistration.getCheckIn();
        this.preRegistrationDate = preRegistration.getPreRegistration();
    }

    public PreRegistrationDto(PreRegistration preRegistration, String email, Long localityId, String sub, byte[] qrcode) {
        if (preRegistration == null) return;

        this.id = preRegistration.getMadeBy().getId();
        this.meeting = new MeetingDto(preRegistration.getMeeting(),false);
        this.person = new PersonDto(preRegistration.getPerson());
        this.checkInDate = preRegistration.getCheckIn();
        this.preRegistrationDate = preRegistration.getPreRegistration();
        this.qrcode = Base64.getEncoder().encodeToString(qrcode);
        
        OptionOrganization optOrg = new OptionOrganization();
        optOrg.setGuid(preRegistration.getOrganizationGuid());
        optOrg.setName(preRegistration.getOrganization());
        optOrg.setShortName(preRegistration.getOrganizationShort());
        this.organization = optOrg;
        this.role = preRegistration.getRole();
        this.email = email;
        this.localityId = localityId;
        this.authoritySub = sub;
        
        this.isAuthority = Optional.ofNullable(preRegistration.getIsAuthority()).orElse(false);
        
    }

    public String getAuthoritySub() {
        return authoritySub;
    }

    public void setAuthoritySub(String authoritySub) {
        this.authoritySub = authoritySub;
    }

    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PersonDto getPerson() {
        return person;
    }

    public void setPersonId(PersonDto person) {
        this.person = person;
    }

    public MeetingDto getMeeting() {
        return meeting;
    }

    public void setMeetingId(MeetingDto meeting) {
        this.meeting = meeting;
    }

    public Date getCheckIn() {
        return checkInDate;
    }

    public void setCheckIn(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Boolean getIsAuthority() {
        return isAuthority;
    }

    public void setIsAuthority(Boolean isAuthority) {
        this.isAuthority = isAuthority;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public OptionOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(OptionOrganization organization) {
        this.organization = organization;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getLocalityId() {
        return localityId;
    }

    public void setLocalityId(Long localityId) {
        this.localityId = localityId;
    }
    
    

    public Date getPreRegistration() {
        return preRegistrationDate;
    }

    public void setPreRegistration(Date preRegistrationDate) {
        this.preRegistrationDate = preRegistrationDate;
    }

    public String getQRCode() {
        return qrcode;
    }

    public void setQRCode(String qrcode) {
        this.qrcode = qrcode;
    }
    

}
