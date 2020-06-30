package br.gov.es.participe.controller.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ConferenceParamDto {

    private Long id;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date beginDate;
    private String name;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date endDate;
    private String description;
    private PlanParamDto plan;
    private String titleAuthentication;
    private String subtitleAuthentication;
    private String titleParticipation;
    private String subtitleParticipation;
    private String titleRegionalization;
    private String subtitleRegionalization;
    private FileDto fileParticipation;
    private FileDto fileAuthentication;
    private LocalityTypeDto localityType;
    private List<MeetingDto> meeting;
    private List<SelfDeclarationDto> selfDeclaration;
    private List<PersonDto> moderators;

    public List<SelfDeclarationDto> getSelfDeclaration() {
        return selfDeclaration;
    }

    public void setSelfDeclaration(List<SelfDeclarationDto> selfDeclaration) {
        this.selfDeclaration = selfDeclaration;
    }

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

    public String getTitleAuthentication() {
        return titleAuthentication;
    }

    public void setTitleAuthentication(String titleAuthentication) {
        this.titleAuthentication = titleAuthentication;
    }

    public String getSubtitleAuthentication() {
        return subtitleAuthentication;
    }

    public void setSubtitleAuthentication(String subtitleAuthentication) {
        this.subtitleAuthentication = subtitleAuthentication;
    }

    public String getTitleParticipation() {
        return titleParticipation;
    }

    public void setTitleParticipation(String titleParticipation) {
        this.titleParticipation = titleParticipation;
    }

    public String getSubtitleParticipation() {
        return subtitleParticipation;
    }

    public void setSubtitleParticipation(String subtitleParticipation) {
        this.subtitleParticipation = subtitleParticipation;
    }

    public String getTitleRegionalization() {
        return titleRegionalization;
    }

    public void setTitleRegionalization(String titleRegionalization) {
        this.titleRegionalization = titleRegionalization;
    }

    public String getSubtitleRegionalization() {
        return subtitleRegionalization;
    }

    public void setSubtitleRegionalization(String subtitleRegionalization) {
        this.subtitleRegionalization = subtitleRegionalization;
    }

    public FileDto getFileParticipation() {
        return fileParticipation;
    }

    public void setFileParticipation(FileDto fileParticipation) {
        this.fileParticipation = fileParticipation;
    }

    public FileDto getFileAuthentication() {
        return fileAuthentication;
    }

    public void setFileAuthentication(FileDto fileAuthentication) {
        this.fileAuthentication = fileAuthentication;
    }

    public LocalityTypeDto getLocalityType() {
        return localityType;
    }

    public void setLocalityType(LocalityTypeDto localityType) {
        this.localityType = localityType;
    }

    public List<MeetingDto> getMeeting() {
        return meeting;
    }

    public void setMeeting(List<MeetingDto> meeting) {
        this.meeting = meeting;
    }

    public List<PersonDto> getModerators() {
        return moderators;
    }

    public void setModerators(List<PersonDto> moderators) {
        this.moderators = moderators;
    }
}
