package br.gov.es.participe.controller.dto;

import br.gov.es.participe.util.domain.StatusConferenceType;

import java.util.Date;

public class AuthenticationScreenDto {

  private String localityType;
  private String titleAuthentication;
  private String subtitleAuthentication;

  private Boolean showStatistics;
  private Boolean showCalendar;
  private Boolean showStatisticsPanel ;


  private FileDto fileAuthentication;

  private Integer proposal;
  private Integer highlights;
  private Integer participations;
  private Integer numberOfLocalities;

  private StatusConferenceType status;
  private FileDto backgroundImageUrl;

  private Date beginDate;
  private Date endDate;

  public AuthenticationScreenDto() {
    setProposal(0);
    setHighlights(0);
  }

  public String getLocalityType() {
    return localityType;
  }

  public void setLocalityType(String localityType) {
    this.localityType = localityType;
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

  public FileDto getFileAuthentication() {
    return fileAuthentication;
  }

  public void setFileAuthentication(FileDto fileAuthentication) {
    this.fileAuthentication = fileAuthentication;
  }

  public Boolean getShowStatistics() {
    return showStatistics;
  }

  public void setShowStatistics(Boolean showStatistics) {
    this.showStatistics = showStatistics;
  }

  
  public Boolean getShowCalendar() {
    return showCalendar;
  }
  
  public void setShowCalendar(Boolean showCalendar) {
    this.showCalendar = showCalendar;
  }
  
  public Boolean getShowStatisticsPanel (){
    return showStatisticsPanel ;
  }

  public void setShowStatisticsPanel (Boolean showStatisticsPanel ){
    this.showStatisticsPanel  = showStatisticsPanel ;
  }
  
  public Integer getProposal() {
    return proposal;
  }

  public void setProposal(Integer proposal) {
    this.proposal = proposal;
  }

  public Integer getHighlights() {
    return highlights;
  }

  public void setHighlights(Integer highlights) {
    this.highlights = highlights;
  }

  public Integer getParticipations() {
    return participations;
  }

  public void setParticipations(Integer participations) {
    this.participations = participations;
  }

  public Integer getNumberOfLocalities() {
    return numberOfLocalities;
  }

  public void setNumberOfLocalities(Integer numberOfLocalities) {
    this.numberOfLocalities = numberOfLocalities;
  }

  public StatusConferenceType getStatus() {
    return status;
  }

  public void setStatus(StatusConferenceType status) {
    this.status = status;
  }

  public FileDto getBackgroundImageUrl() {
    return this.backgroundImageUrl;
  }

  public void setBackgroundImageUrl(FileDto backgroundImageUrl) {
    this.backgroundImageUrl = backgroundImageUrl;
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
