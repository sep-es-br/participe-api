package br.gov.es.participe.controller.dto;

import br.gov.es.participe.util.domain.DisplayModeType;
import br.gov.es.participe.util.domain.ResearchDisplayStatusType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class ResearchConfigurationParamDto {

    static Logger log = Logger.getLogger(ResearchConfigurationParamDto.class.getName());

    private String beginDate;
    private String endDate;
    private DisplayModeType displayModeResearch;
    private ResearchDisplayStatusType researchDisplayStatus;
    private String researchLink;
    private String estimatedTimeResearch;

    public ResearchConfigurationParamDto() {
    }

    public ResearchConfigurationParamDto(ResearchConfigurationDto researchConfiguration) {
        beginDate = researchConfiguration.getBeginDate();
        endDate = researchConfiguration.getEndDate();
        displayModeResearch = researchConfiguration.getDisplayModeResearch();
        researchDisplayStatus = researchConfiguration.getResearchDisplayStatus();
        researchLink = researchConfiguration.getResearchLink();
        estimatedTimeResearch = researchConfiguration.getEstimatedTimeResearch();
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

    public Date getEndDate() throws ParseException {
        if (endDate != null && !endDate.isEmpty()) {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(endDate);
        }
        return null;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public DisplayModeType getDisplayModeResearch() {
        return displayModeResearch;
    }

    public void setDisplayModeResearch(DisplayModeType displayModeResearch) {
        this.displayModeResearch = displayModeResearch;
    }

    public ResearchDisplayStatusType getResearchDisplayStatus() {
        return researchDisplayStatus;
    }

    public void setResearchDisplayStatus(ResearchDisplayStatusType researchDisplayStatus) {
        this.researchDisplayStatus = researchDisplayStatus;
    }

    public String getResearchLink() {
        return researchLink;
    }

    public void setResearchLink(String researchLink) {
        this.researchLink = researchLink;
    }

    public String getEstimatedTimeResearch() {
        return estimatedTimeResearch;
    }

    public void setEstimatedTimeResearch(String estimatedTimeResearch) {
        this.estimatedTimeResearch = estimatedTimeResearch;
    }
}
