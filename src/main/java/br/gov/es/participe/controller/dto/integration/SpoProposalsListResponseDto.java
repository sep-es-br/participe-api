/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.controller.dto.integration;

import java.util.Date;
import org.neo4j.ogm.annotation.typeconversion.DateString;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 *
 * @author gean.carneiro
 */
@QueryResult
public class SpoProposalsListResponseDto {
    
    private String syncHash;
    private String proposalText;
    private String areaName;
    private String budgetUnitId;
    private String budgetUnitName;
    private String microrregion;
    
    @DateString
    private Date date;

    public String getSyncHash() {
        return syncHash;
    }

    public void setSyncHash(String syncHash) {
        this.syncHash = syncHash;
    }

    public String getProposalText() {
        return proposalText;
    }

    public void setProposalText(String proposalText) {
        this.proposalText = proposalText;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getBudgetUnitId() {
        return budgetUnitId;
    }

    public void setBudgetUnitId(String budgetUnitId) {
        this.budgetUnitId = budgetUnitId;
    }

    public String getBudgetUnitName() {
        return budgetUnitName;
    }

    public void setBudgetUnitName(String budgetUnitName) {
        this.budgetUnitName = budgetUnitName;
    }

    public String getMicrorregion() {
        return microrregion;
    }

    public void setMicrorregion(String microrregion) {
        this.microrregion = microrregion;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    
    
}
