package br.gov.es.participe.controller.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.gov.es.participe.model.Conference;

public class ConferenceDto {

    private Long id;
    private String name;
    private String description;
    private PlanDto plan;
//    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    @JsonFormat(pattern="dd/MM/yyyy")
    private Date beginDate;
    @JsonFormat(pattern="dd/MM/yyyy")
    private Date endDate;


    public ConferenceDto() {
    }

    public ConferenceDto(Conference conference) {
        if (conference == null) return;

        this.description = conference.getDescription();
        this.id = conference.getId();
        this.name = conference.getName();
        this.beginDate = conference.getBeginDate();
        this.plan = new PlanDto(conference.getPlan(), false);
        this.endDate = conference.getEndDate();
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

    public PlanDto getPlan() {
        return plan;
    }

    public void setPlan(PlanDto plan) {
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
