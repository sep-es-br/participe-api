package br.gov.es.participe.controller.dto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.text.ParseException;

import br.gov.es.participe.model.Evaluation;


public class EvaluationConfigurationDto {

    private String beginDate;
    private String endDate;
    private String displayMode;
    private String evaluationDisplayStatus;


    public EvaluationConfigurationDto(Evaluation evaluation) {
        setBeginDate(evaluation.getBeginDate());
        setEndDate(evaluation.getEndDate());
        setDisplayMode(evaluation.getDisplayMode()); 
        setEvaluationDisplayStatus(evaluation.getEvaluationDisplayStatus());
    }

    public EvaluationConfigurationDto() {

    }

    public Date getBeginDate() throws ParseException {
        if (beginDate != null && !beginDate.isEmpty()) {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(beginDate);
        }
        return null;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public void setBeginDate(Date beginDate) {
        if (beginDate != null) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            this.beginDate = dateFormat.format(beginDate);
            return;
        }
        this.beginDate = null;
    }

    public Date getEndDate() throws ParseException {
        if (endDate != null && !endDate.isEmpty()) {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(endDate);
        }
        return null;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setEndDate(Date endDate) {
        if (endDate != null) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            this.endDate = dateFormat.format(endDate);
            return;
        }
        this.endDate = null;
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

}