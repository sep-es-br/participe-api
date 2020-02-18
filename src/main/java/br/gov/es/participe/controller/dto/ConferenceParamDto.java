package br.gov.es.participe.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ConferenceParamDto {

    private Long id;
    private String name;
    private String description;
    private PlanParamDto plan;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date beginDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date endDate;

    public ConferenceParamDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PlanParamDto getPlan() {
        return plan;
    }

    public void setPlan(PlanParamDto plan) {
        this.plan = plan;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
