package br.gov.es.participe.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ConferenceParamDto {

    private Long id;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date beginDate;
    private String name;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date endDate;
    private String description;
    private PlanParamDto plan;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
