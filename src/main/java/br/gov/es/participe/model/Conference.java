package br.gov.es.participe.model;

import java.util.Date;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateString;

import br.gov.es.participe.controller.dto.ConferenceDto;
import br.gov.es.participe.controller.dto.ConferenceParamDto;

@NodeEntity
public class Conference extends Entity {

    private String name;

    private String description;

    @Relationship(type = "TARGETS")
    private Plan plan;

    @DateString
    private Date beginDate;

    @DateString
    private Date endDate;

    public Conference() {
    }

    public Conference(ConferenceDto conferenceDto) {
        if (conferenceDto == null) return;

        setId(conferenceDto.getId());
        this.name = conferenceDto.getName();
        this.description = conferenceDto.getDescription();
        if(conferenceDto.getPlan() != null && conferenceDto.getPlan().getId() != null){
            this.plan = new Plan(conferenceDto.getPlan());
        }
        this.beginDate = conferenceDto.getBeginDate();
        this.endDate = conferenceDto.getEndDate();
    }

    public Conference(ConferenceParamDto conferenceParamDto) {
        if (conferenceParamDto == null) return;

        setId(conferenceParamDto.getId());
        this.description = conferenceParamDto.getDescription();
        if(conferenceParamDto.getPlan() != null && conferenceParamDto.getPlan().getId() != null){
            this.plan = new Plan(conferenceParamDto.getPlan());
        }
        this.beginDate = conferenceParamDto.getBeginDate();
        this.name = conferenceParamDto.getName();
        this.endDate = conferenceParamDto.getEndDate();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
