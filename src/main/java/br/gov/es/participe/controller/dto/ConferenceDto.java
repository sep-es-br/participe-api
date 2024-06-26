package br.gov.es.participe.controller.dto;


import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.StructureItem;
import br.gov.es.participe.util.domain.DisplayModeType;
import br.gov.es.participe.util.domain.StatusConferenceType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ConferenceDto {

  private Long id;
  private String name;
  private Boolean hasAttend;
  private String description;
  private String titleAuthentication;
  private String subtitleAuthentication;
  private String titleParticipation;
  private String subtitleParticipation;
  private String titleRegionalization;
  private String subtitleRegionalization;
  private Boolean isActive;

  private PlanDto plan;
  private LocalityTypeDto localityType;
  private List<MeetingDto> meeting;
  private FileDto fileParticipation;
  private FileDto fileAuthentication;
  private FileDto fileFooter;

  private List<SelfDeclarationDto> selfDeclaration;
  private List<PersonDto> moderators;

  private String beginDate;
  private String endDate;

  private Boolean segmentation;
  private List<Long> targetedByItems;
  private DisplayModeType displayMode;
  private StatusConferenceType displayStatusConference;
  private String preOpeningText;
  private String postClosureText;
  private List<HowItWorkStepDto> howItWork;
  private String externalLinksMenuLabel;
  private List<ExternalLinksDto> externalLinks;
  private List<FileDto> backgroundImages;
  private List<FileDto> calendarImages;
  private String serverName;
  private boolean defaultServerConference;
  private Boolean showStatistics;
  private Boolean showCalendar;
  private Boolean showStatisticsPanel;
  private Boolean showProposalsPanel;
  private Boolean showExternalLinks;

  private ResearchConfigurationDto researchConfiguration;

  private ConferenceColorDto customProperties;

  private EvaluationConfigurationDto evaluationConfiguration;

  public ConferenceDto() {
  }

  public ConferenceDto(Conference conference) {
    if (conference == null) return;

    this.displayMode = conference.getModeType();
    this.displayStatusConference = conference.getStatusType();
    this.preOpeningText = conference.getPreOpening();
    this.postClosureText = conference.getPostClosure();
    this.externalLinksMenuLabel = conference.getMenuLabel();
    this.segmentation = !conference.getStructureItems().isEmpty();

    this.targetedByItems = conference.getStructureItems().stream().map(StructureItem::getId).collect(Collectors.toList());

    if (conference.getTopics() != null && !conference.getTopics().isEmpty()) {
      this.howItWork = new ArrayList<>();
      conference.getTopics().forEach(topic ->
          this.howItWork.add(new HowItWorkStepDto(topic)));
    }

    if (conference.getBackgroundImages() != null && !conference.getBackgroundImages().isEmpty()) {
      this.backgroundImages = new ArrayList<>();
      conference.getBackgroundImages().forEach(file -> this.backgroundImages.add(new FileDto(file)));
    }

    if (conference.getCalendarImages() != null && !conference.getCalendarImages().isEmpty()) {
      this.calendarImages = new ArrayList<>();
      conference.getCalendarImages().forEach(file -> this.calendarImages.add(new FileDto(file)));
    }

    this.researchConfiguration = conference.getResearch() != null ? new ResearchConfigurationDto(conference.getResearch()) : null;

    this.evaluationConfiguration = conference.getEvaluation() != null ? new EvaluationConfigurationDto(conference.getEvaluation()) : null;

    this.description = conference.getDescription();
    this.id = conference.getId();
    this.name = conference.getName();
    this.hasAttend = conference.getHasAttend();
    setBeginDate(conference.getBeginDate());
    setEndDate(conference.getEndDate());
    this.plan = new PlanDto(conference.getPlan(), false);
    this.titleAuthentication = conference.getTitleAuthentication();
    this.titleParticipation = conference.getTitleParticipation();
    this.titleRegionalization = conference.getTitleRegionalization();
    this.subtitleAuthentication = conference.getSubtitleAuthentication();
    this.subtitleParticipation = conference.getSubtitleParticipation();
    this.subtitleRegionalization = conference.getSubtitleRegionalization();

    this.showCalendar = (conference.getShowCalendar() != null )
      ? conference.getShowCalendar()
      : true;

    this.showStatistics = (conference.getShowStatistics() != null )
      ? conference.getShowStatistics()
      : true;

    this.showStatisticsPanel  = (conference.getShowStatisticsPanel() != null )
      ? conference.getShowStatisticsPanel()
      : true;

    this.showProposalsPanel  = (conference.getShowProposalsPanel() != null )
      ? conference.getShowProposalsPanel()
      : true;

    this.showExternalLinks  = (conference.getShowExternalLinks() != null )
      ? conference.getShowExternalLinks()
      : true;

    this.fileParticipation = (conference.getFileParticipation() != null)
      ? new FileDto(conference.getFileParticipation())
      : null;
    
      this.fileFooter = (conference.getFileFooter() != null)
      ? new FileDto(conference.getFileFooter())
      : null;

    this.fileAuthentication = (conference.getFileAuthentication() != null)
      ? new FileDto(conference.getFileAuthentication())
      : null;

    this.localityType = new LocalityTypeDto(conference.getLocalityType());

    if (conference.getSelfDeclaration() != null && !conference.getSelfDeclaration().isEmpty()) {
      this.selfDeclaration = new ArrayList<>();
      conference.getSelfDeclaration().forEach(self -> new SelfDeclarationDto(self, true));
    }

    if (conference.getMeeting() != null && !conference.getMeeting().isEmpty()) {
      this.meeting = new ArrayList<>();
      conference.getMeeting().forEach(meet -> this.meeting.add(new MeetingDto(meet, false)));
    }

    if (conference.getModerators() != null && !conference.getModerators().isEmpty()) {
      this.moderators = new ArrayList<>();
      conference.getModerators().forEach(moderator -> this.moderators.add(new PersonDto(moderator)));
    }
  }

  public static ConferenceDto createConferenceDtoWithoutMeeting(Conference conference) {
    ConferenceDto dto = new ConferenceDto(conference);
    dto.setMeeting(null);
    return dto;
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

  public Boolean getHasAttend() {
    return hasAttend;
  }

  public void setHasAttend(Boolean hasAttend) {
    this.hasAttend = hasAttend;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public PlanDto getPlan() {
    return plan;
  }

  public void setPlan(PlanDto plan) {
    this.plan = plan;
  }

  public String getBeginDate() {
    return beginDate;
  }

  public void setBeginDate(String beginDate) {
    this.beginDate = beginDate;
  }

  public void setBeginDate(Date beginDate) {
    if (beginDate == null) {
      this.beginDate = null;
      return;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    LocalDateTime localDateTime = beginDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

    this.beginDate = localDateTime.format(formatter);
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public void setEndDate(Date endDate) {
    if (endDate == null) {
      this.endDate = null;
      return;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    LocalDateTime localDateTime = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    this.endDate = localDateTime.format(formatter);
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

  public List<MeetingDto> getMeeting() {
    return meeting;
  }

  public void setMeeting(List<MeetingDto> meeting) {
    this.meeting = meeting;
  }

  public LocalityTypeDto getLocalityType() {
    return localityType;
  }

  public void setLocalityType(LocalityTypeDto locality) {
    this.localityType = locality;
  }

  public FileDto getFileAuthentication() {
    return fileAuthentication;
  }

  public void setFileAuthentication(FileDto fileAuthentication) {
    this.fileAuthentication = fileAuthentication;
  }

  public List<SelfDeclarationDto> getSelfDeclaration() {
    return selfDeclaration;
  }

  public void setSelfDeclaration(List<SelfDeclarationDto> selfDeclaration) {
    this.selfDeclaration = selfDeclaration;
  }

  public List<PersonDto> getModerators() {
    return moderators;
  }

  public void setModerators(List<PersonDto> moderators) {
    this.moderators = moderators;
  }

  public Boolean getSegmentation() {
    return segmentation;
  }

  public void setSegmentation(Boolean segmentation) {
    this.segmentation = segmentation;
  }

  public List<Long> getTargetedByItems() {
    return targetedByItems;
  }

  public void setTargetedByItems(List<Long> targetedByItems) {
    this.targetedByItems = targetedByItems;
  }

  public DisplayModeType getDisplayMode() {
    return displayMode;
  }

  public void setDisplayMode(DisplayModeType displayMode) {
    this.displayMode = displayMode;
  }

  public StatusConferenceType getDisplayStatusConference() {
    return displayStatusConference;
  }

  public void setDisplayStatusConference(StatusConferenceType displayStatusConference) {
    this.displayStatusConference = displayStatusConference;
  }

  public String getPreOpeningText() {
    return preOpeningText;
  }

  public void setPreOpeningText(String preOpeningText) {
    this.preOpeningText = preOpeningText;
  }

  public String getPostClosureText() {
    return postClosureText;
  }

  public void setPostClosureText(String postClosureText) {
    this.postClosureText = postClosureText;
  }

  public List<HowItWorkStepDto> getHowItWork() {
    return howItWork;
  }

  public void setHowItWork(List<HowItWorkStepDto> howItWork) {
    this.howItWork = howItWork;
  }

  public String getExternalLinksMenuLabel() {
    return externalLinksMenuLabel;
  }

  public void setExternalLinksMenuLabel(String externalLinksMenuLabel) {
    this.externalLinksMenuLabel = externalLinksMenuLabel;
  }

  public List<ExternalLinksDto> getExternalLinks() {
    return externalLinks;
  }

  public void setExternalLinks(List<ExternalLinksDto> externalLinks) {
    this.externalLinks = externalLinks;
  }

  public List<FileDto> getBackgroundImages() {
    return backgroundImages;
  }

  public void setBackgroundImages(List<FileDto> backgroundImages) {
    this.backgroundImages = backgroundImages;
  }

  public List<FileDto> getCalendarImages() {
    return calendarImages;
  }

  public void setCalendarImages(List<FileDto> calendarImages) {
    this.calendarImages = calendarImages;
  }

  public String getServerName() {
    return serverName;
  }

  public void setServerName(String serverName) {
    this.serverName = serverName;
  }

  public Boolean getDefaultServerConference() {
    return defaultServerConference;
  }

  public void setDefaultServerConference(Boolean defaultServerConference) {
    this.defaultServerConference = defaultServerConference;
  }

  public Boolean getShowCalendar() {
    return showCalendar;
  }

  public void setShowCalendar(Boolean showCalendar){
    this.showCalendar = showCalendar;
  }

  public Boolean getShowStatistics() {
    return showStatistics;
  }

  public void setShowStatistics(Boolean showStatistics){
    this.showStatistics = showStatistics;
  }

  public Boolean getShowStatisticsPanel () {
    return showStatisticsPanel ;
  }

  public void setShowStatisticsPanel (Boolean showStatisticsPanel ) {
    this.showStatisticsPanel  = showStatisticsPanel ;
  }

  public Boolean getShowProposalsPanel() {
    return showProposalsPanel;
  }

  public void setShowProposalsPanel(Boolean showProposalsPanel) {
    this.showProposalsPanel = showProposalsPanel;
  }

  public Boolean getShowExternalLinks() {
    return showExternalLinks;
  }

  public void setShowExternalLinks(Boolean showExternalLinks) {
    this.showExternalLinks = showExternalLinks;
  }

  public ResearchConfigurationDto getResearchConfiguration() {
    return researchConfiguration;
  }

  public void setResearchConfiguration(ResearchConfigurationDto researchConfiguration) {
    this.researchConfiguration = researchConfiguration;
  }

  public ConferenceColorDto getCustomProperties() {
    return customProperties;
  }

  public void setCustomProperties(ConferenceColorDto customProperties) {
    this.customProperties = customProperties;
  }

  public EvaluationConfigurationDto getEvaluationConfiguration() {
    return evaluationConfiguration;
  }

  public void setEvaluationConfiguration(EvaluationConfigurationDto evaluationConfiguration) {
    this.evaluationConfiguration = evaluationConfiguration;
  }

  public FileDto getFileFooter() {
    return fileFooter;
  }

  public void setFileFooter(FileDto fileFooter) {
    this.fileFooter = fileFooter;
  }

}
