package br.gov.es.participe.controller.dto;

import br.gov.es.participe.util.domain.DisplayModeType;
import br.gov.es.participe.util.domain.StatusConferenceType;

import java.text.ParseException;
//import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
//import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings({ "unused" })
public class ConferenceParamDto {

  private static final String formatDate = "dd/MM/yyyy HH:mm:ss";
  static Logger log = Logger.getLogger(ConferenceParamDto.class.getName());
  private Long id;
  private String beginDate;
  private String endDate;
  private String offset;
  private String name;
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
  private boolean segmentation;
  private List<Long> targetedByItems;
  private DisplayModeType displayMode;
  private StatusConferenceType displayStatusConference;
  private String preOpeningText;
  private String postClosureText;
  private List<HowItWorkStepDto> howItWork;
  private String externalLinksMenuLabel;
  private List<ExternalLinksDto> externalLinks;
  private List<FileDto> backgroundImages;
  private String serverName;
  private boolean defaultServerConference;
  private Boolean showStatistics;
  private Boolean showCalendar;
  private Boolean showStatisticsPanel ;
  private Boolean showExternalLinks;
  private ResearchConfigurationParamDto researchConfiguration;

  public ConferenceParamDto() {
  }

  public ConferenceParamDto(ConferenceDto conferenceDto) {
    id = conferenceDto.getId();
    name = conferenceDto.getName();
    beginDate = conferenceDto.getBeginDate();
    endDate = conferenceDto.getEndDate();
    description = conferenceDto.getDescription();
    plan = new PlanParamDto(conferenceDto.getPlan());
    titleAuthentication = conferenceDto.getTitleAuthentication();
    subtitleAuthentication = conferenceDto.getSubtitleAuthentication();
    titleParticipation = conferenceDto.getTitleParticipation();
    subtitleParticipation = conferenceDto.getSubtitleParticipation();
    titleRegionalization = conferenceDto.getTitleRegionalization();
    subtitleRegionalization = conferenceDto.getSubtitleRegionalization();
    fileParticipation = conferenceDto.getFileParticipation();
    fileAuthentication = conferenceDto.getFileAuthentication();
    localityType = conferenceDto.getLocalityType();
    meeting = conferenceDto.getMeeting();
    selfDeclaration = conferenceDto.getSelfDeclaration();
    moderators = conferenceDto.getModerators();
    segmentation = conferenceDto.getSegmentation();
    targetedByItems = conferenceDto.getTargetedByItems();
    displayMode = conferenceDto.getDisplayMode();
    displayStatusConference = conferenceDto.getDisplayStatusConference();
    preOpeningText = conferenceDto.getPreOpeningText();
    postClosureText = conferenceDto.getPostClosureText();
    howItWork = conferenceDto.getHowItWork();
    externalLinks = conferenceDto.getExternalLinks();
    backgroundImages = conferenceDto.getBackgroundImages();
    serverName = conferenceDto.getServerName();
    defaultServerConference = conferenceDto.getDefaultServerConference();
    showStatistics = conferenceDto.getShowStatistics();
    showCalendar = conferenceDto.getShowCalendar();
    showExternalLinks = conferenceDto.getShowExternalLinks();
    showStatisticsPanel  = conferenceDto.getShowStatisticsPanel ();
    researchConfiguration = new ResearchConfigurationParamDto(conferenceDto.getResearchConfiguration());
  }

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

  public Date getBeginDate() throws ParseException {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    final LocalDateTime parse = LocalDateTime.parse(beginDate, formatter);
    return Date.from(parse.atZone(ZoneId.systemDefault()).toInstant());
  }

  public void setBeginDate(String beginDate) {
    this.beginDate = beginDate;
  }

  public String getOffset() {
    return offset;
  }

  public void setOffset(String offset) {
    this.offset = offset;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getEndDate() throws ParseException {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    final LocalDateTime parse = LocalDateTime.parse(endDate, formatter);

    return Date.from(parse.atZone(ZoneId.systemDefault()).toInstant());
  }

  public void setEndDate(String endDate) {
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

  public Boolean getShowStatisticsPanel () {
    return showStatisticsPanel ;
  }

  public void setShowStatisticsPanel (Boolean showStatisticsPanel ) {
    this.showStatisticsPanel  = showStatisticsPanel ;
  }

  public Boolean getShowExternalLinks() {
    return showExternalLinks;
  }

  public void setShowExternalLinks(Boolean showExternalLinks) {
    this.showExternalLinks = showExternalLinks;
  }

  public ResearchConfigurationParamDto getResearchConfiguration() {
    return researchConfiguration;
  }

  public void setResearchConfiguration(ResearchConfigurationParamDto researchConfiguration) {
    this.researchConfiguration = researchConfiguration;
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

}
