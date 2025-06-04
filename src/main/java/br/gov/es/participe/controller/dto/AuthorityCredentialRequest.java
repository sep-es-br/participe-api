/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.controller.dto;

/**
 *
 * @author desenvolvimento
 */
public class AuthorityCredentialRequest {
    private Long madeBy;
    private String representedByCpf;
    private String representedByEmail;
    private String representedByName;
    private String representedBySub;
    private Long meetingId;
    private String organization;
    private String role;
    private String email;
    private Long localityId;

    public String getRepresentedBySub() {
        return representedBySub;
    }

    public void setRepresentedBySub(String representedBySub) {
        this.representedBySub = representedBySub;
    }

    
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getLocalityId() {
        return localityId;
    }

    public void setLocalityId(Long localityId) {
        this.localityId = localityId;
    }
    
    

    public Long getMadeBy() {
        return madeBy;
    }

    public void setMadeBy(Long madeBy) {
        this.madeBy = madeBy;
    }

    public String getRepresentedByEmail() {
        return representedByEmail;
    }

    public void setRepresentedByEmail(String representedByEmail) {
        this.representedByEmail = representedByEmail;
    }

    public String getRepresentedByCpf() {
        return representedByCpf;
    }

    public void setRepresentedByCpf(String representedByCpf) {
        this.representedByCpf = representedByCpf;
    }

    public String getRepresentedByName() {
        return representedByName;
    }

    public void setRepresentedByName(String representedByName) {
        this.representedByName = representedByName;
    }
    
    

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }
    
    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
}
