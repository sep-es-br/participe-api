package br.gov.es.participe.model;

import java.io.Serializable;
import java.util.Date;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateString;

@NodeEntity
public class Evaluation extends Entity implements Serializable {
    
    @DateString
    private Date beginDate;

    @DateString
    private Date endDate;

    private String displayMode;

    private String evaluationDisplayStatus;

    @Relationship(type = "APPLIES_TO")
    private Conference conference;


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

    public String getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(String displayMode) {
        this.displayMode = displayMode;
    }

    public String getEvaluationDisplayStatus() {
        return evaluationDisplayStatus;
    }

    public void setEvaluationDisplayStatus(String evaluationDisplayStatus) {
        this.evaluationDisplayStatus = evaluationDisplayStatus;
    }

    public Conference getConference() {
        return conference;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }

    
}
