package br.gov.es.participe.model;

import java.util.Date;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateString;

import br.gov.es.participe.controller.dto.ConferenceDto;

@NodeEntity
public class Conference extends Entity {

    private String name;

    private String description;

    @Relationship(type = "APPLIES_TO", direction = Relationship.INCOMING)
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

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
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