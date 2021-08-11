package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.*;
import br.gov.es.participe.util.domain.DisplayModeType;
import br.gov.es.participe.util.domain.ResearchDisplayStatusType;

import java.text.*;
import java.util.Date;

public class ResearchConfigurationDto {

    private final String format = "dd/MM/yyyy HH:mm:ss";
    private String beginDate;
    private String endDate;
    private DisplayModeType displayModeResearch;
    private ResearchDisplayStatusType researchDisplayStatus;
    private String researchLink;
    private String estimatedTimeResearch;

    public ResearchConfigurationDto() {}

    public ResearchConfigurationDto(Research research) {
        setBeginDate(research.getBeginDate());
        setEndDate(research.getEndDate());
        setDisplayModeResearch(research.getModeType());
        setResearchDisplayStatus(research.getStatusType());
        setResearchLink(research.getLink());
        setEstimatedTimeResearch(research.getEstimatedTime());
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public void setBeginDate(Date beginDate) {
        if (beginDate != null) {
            DateFormat dateFormat = new SimpleDateFormat(format);
            this.beginDate = dateFormat.format(beginDate);
            return;
        }
        this.beginDate = null;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public void setEndDate(Date endDate) {
        if (endDate != null) {
            DateFormat dateFormat = new SimpleDateFormat(format);
            this.endDate = dateFormat.format(endDate);
            return;
        }
        this.endDate = null;
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
