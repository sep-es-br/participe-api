package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.AttendRepository;
import br.gov.es.participe.repository.ConferenceRepository;
import br.gov.es.participe.util.ParticipeUtils;
import br.gov.es.participe.util.domain.StatusConferenceType;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConferenceService {


  @Autowired
  private ConferenceRepository conferenceRepository;

  @Autowired
  private PlanService planService;

  @Autowired
  private FileService fileService;

  @Autowired
  private LocalityTypeService localityTypeService;

  @Autowired
  private PlanItemService planItemService;

  @Autowired
  private CommentService commentService;

  @Autowired
  private HighlightService highlightService;

  @Autowired
  private AttendRepository attendRepository;

  @Autowired
  private LocalityService localityService;

  @Autowired
  private PersonService personService;

  @Autowired
  private ExternalContentService externalContentService;

  @Autowired
  private IsLinkedByService isLinkedByService;

  @Autowired
  private TopicService topicService;

  @Autowired
  private PortalServerService portalServerService;

  @Autowired
  private StructureItemService structureItemService;

  @Autowired
  private ResearchService researchService;

  @Autowired
  private EvaluationService evaluationService;
  
  @Autowired
  private StructureService structureService;

  @Autowired
  private ParticipeUtils participeUtils;
  @Autowired
  private ConferenceColorService conferenceColorService;

  private static final org.slf4j.Logger log = LoggerFactory.getLogger(ConferenceService.class);

  public void generateAuthenticationScreen(
    Long id,
    AuthenticationScreenDto auth,
    UriComponentsBuilder uriComponentsBuilder
  ) {
    this.conferenceRepository.findByIdFull(id).ifPresent(conference -> {
      auth.setStatus(conference.getStatusType());
      auth.setTitleAuthentication(conference.getTitleAuthentication());
      auth.setSubtitleAuthentication(conference.getSubtitleAuthentication());
      auth.setFileAuthentication(new FileDto(conference.getFileAuthentication()));
      auth.setShowStatistics(conference.getShowStatistics());
      auth.setShowCalendar(conference.getShowCalendar());
      auth.setShowStatisticsPanel (conference.getShowStatisticsPanel());
      auth.setShowProposalsPanel(conference.getShowProposalsPanel());
      auth.setShowExternalLinks(conference.getShowExternalLinks());

      File backGroundImage = this.fileService.findRandomackGroundImage(id);
      auth.setBackgroundImageUrl(
        backGroundImage != null ? new FileDto(conference.getFileAuthentication()) : null);

      String url = uriComponentsBuilder.path("/files/").build().toUri().toString();

      if (conference.getFileAuthentication() != null) {
        auth.getFileAuthentication().setUrl(url + conference.getFileAuthentication().getId());
      }

      if(backGroundImage != null) {
        auth.getBackgroundImageUrl().setUrl(url + backGroundImage.getId());
      }

      File calendarImage = this.fileService.findRandomCalendarImage(id);
      auth.setCalendarImageUrl( calendarImage != null ? new FileDto(conference.getFileAuthentication()) : null);
      if(calendarImage  != null) {
        auth.getCalendarImageUrl().setUrl(url + calendarImage.getId());
      }

      Plan plan = this.planService.findByConference(conference.getId());
      if(plan.getlocalitytype() != null) {
        auth.setLocalityType(plan.getlocalitytype().getName());
      }
      auth.setBeginDate(conference.getBeginDate());
      auth.setEndDate(conference.getEndDate());
      //auth.setProposal(this.commentService.countCommentByConference(id));
      //auth.setHighlights(this.highlightService.countHighlightByConference(id));
      //auth.setParticipations(this.attendRepository.countParticipationByConference(conference.getId()));
      //auth.setNumberOfLocalities(this.localityService.countLocalitiesParticipation(conference.getId()));
      auth.setProposal(attendRepository.countCommentAllOriginsByConference(id));
      auth.setHighlights(highlightService.countHighlightAllOriginsByConference(id));
      auth.setParticipations(this.attendRepository.countParticipationAllOriginsByConference(conference.getId()));
      auth.setNumberOfLocalities(attendRepository.countLocalityAllOriginsByConference(conference.getId()));
    });
  }

  public void generateCardScreen(CardScreenDto response, Long id) {
    Conference conference = this.find(id);
    Plan plan = this.planService.find(conference.getPlan().getId());

    if(plan.getlocalitytype() != null) {
      response.setRegionalizable(plan.getlocalitytype().getName());
    }
    response.setTitle(conference.getTitleRegionalization());
    response.setSubtitle(conference.getSubtitleRegionalization());
  }

  public Conference find(Long id) {
    return this.conferenceRepository.findByIdFull(id)
      .orElseThrow(() -> new IllegalArgumentException("Conference not found: " + id));
  }

  public boolean validate(String name, Long id) {
    return this.conferenceRepository.validateName(name, id) == null;
  }

  public List<Conference> findAll(String name, Long plan, Integer month, Integer year) {
    List<Conference> conferences = new ArrayList<>();

    this.conferenceRepository.findAllByQuery(name, plan, month, year).iterator().forEachRemaining(conferences::add);
    for(Conference conference : conferences) {
      boolean deleteConference = true;
      if(conference.getMeeting() != null) {
        for(Meeting m : conference.getMeeting()) {
          m.setConference(null);
        }
      }
      if(conference.getPlan() != null) {
        List<PlanItem> planItens = this.planItemService.findAllByIdPlan(conference.getPlan().getId());
        for(PlanItem item : planItens) {
          if(item.getAttends() != null) {
            deleteConference = false;
            break;
          }
        }
      }
      conference.setHasAttend(deleteConference);
    }

    return conferences;
  }

  public List<Conference> findByPlan(Long id) {
    return this.conferenceRepository.findByPlan(id);
  }

  public List<ConferenceDto> findAllActives(Long idPerson, Boolean activeConferences) {
    Person person = this.personService.find(idPerson);
    final boolean adm = person.getRoles() != null && person.getRoles().contains("Administrator");
    List<ConferenceDto> conferences = new ArrayList<>();
    this.conferenceRepository.findAllActives(new Date(), activeConferences || !adm).forEach(conference -> {
      Evaluation evaluation = conference.getId() == null ? new Evaluation()
      : this.evaluationService.findByIdConference(conference.getId()).orElse(null);
      
      conference.setEvaluation(evaluation);
      if(adm || (conference.getModerators() != null
                 && conference.getModerators().stream().anyMatch(m -> idPerson.equals(m.getId())))) {
        ConferenceDto dto = new ConferenceDto(conference);
        dto.setPlan(null);
        dto.setLocalityType(null);
        dto.setFileAuthentication(null);
        dto.setFileParticipation(null);
        if(adm) {
          Date begin = this.getDate(dto.getBeginDate());
          Date end = this.getDate(dto.getEndDate());
          dto.setIsActive(this.participeUtils.isActive(begin, end));
        }
        else {
          dto.setIsActive(true);
        }
        conferences.add(dto);
      }
    });
    return conferences;
  }

  private Date getDate(String date) {
    try {
      if(date == null) {
        return null;
      }
      return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(date);
    }
    catch(ParseException e) {
      log.error("Erro ao converter Date", e);
    }
    return null;
  }


  public ConferenceDto update(Long conferenceId, ConferenceParamDto conferenceParamDto) throws ParseException {


    if (conferenceParamDto.getName()== null)  {
      throw new IllegalArgumentException("Update: Conference object is null");
    }

    Conference conferenceStored = this.conferenceRepository.findById(conferenceId)
      .orElseThrow(() -> new IllegalStateException("This conference not exist."));

      ConferenceColor conferenceColor = conferenceColorService.findByConferenceColor(conferenceId);
      if(conferenceColor != null){
        conferenceColorService.update(conferenceColor, conferenceParamDto.getCustomProperties());
      }else{
        conferenceColorService.save(conferenceStored, conferenceParamDto.getCustomProperties());
      }

    conferenceStored.update(conferenceParamDto);

    Conference conferenceUpdated = this.save(conferenceStored, conferenceParamDto);

    return ConferenceDto.createConferenceDtoWithoutMeeting(conferenceUpdated);
  }


  public Conference save(Conference conference, ConferenceParamDto param) throws ParseException {

    if (param.getName()== null)  {
      throw new IllegalArgumentException("Create: Conference object is null");
    }

    this.validateConference(param);
    // clearAttributes(conference);
    this.loadAttributes(conference);

    if(param.getModerators() != null) {
      HashSet<Person> moderators = new HashSet<>();
      param.getModerators().forEach(p -> {
        log.info("Consultando moderator com loginEmail={} relacionado a conferenceId={}", p.getContactEmail(), conference.getId());
        Optional<Person> find = this.personService.findByLoginEmail(p.getContactEmail());
        final var moderator = find.orElseGet(() -> this.personService.save(new Person(p), true));
        log.info("Relacionando moderatorId={} a conferenceId={}", moderator.getId(), conference.getId());
        moderators.add(moderator);
      });
      conference.setModerators(moderators);
    }

    conference.setName(param.getName().trim().replaceAll(" +", " "));

    this.loadAttributesFromParam(conference, param);

    log.info("Persistindo conferenceId={}", conference.getId());
    this.conferenceRepository.save(conference);

    return conference;
  }

  private void loadAttributesFromParam(Conference conference, ConferenceParamDto param) throws ParseException {
    this.loadEvaluation(conference, param);
    this.loadResearch(conference, param);
    this.loadSegmentation(conference, param);
    this.loadServe(conference, param);
    this.loadExternalLinks(conference, param);
    this.loadBackGroundImages(conference, param);
    this.loadCalendarImages(conference, param);
    this.loadTopics(conference, param);
    if (param.getFileAuthentication() != null) {
      conference.setFileAuthentication(this.fileService.find(param.getFileAuthentication().getId()));
    }
    if (param.getFileParticipation() != null) {
      conference.setFileParticipation(this.fileService.find(param.getFileParticipation().getId()));
    }
    if (param.getFileFooter() != null) {
      conference.setFileFooter(this.fileService.find(param.getFileFooter().getId()));
    }
  }

  private void loadEvaluation(Conference conference, ConferenceParamDto param) throws ParseException{
    Evaluation evaluation = conference.getId() == null ? new Evaluation()
      : this.evaluationService.findByIdConference(conference.getId()).orElse(new Evaluation());

    evaluation.setBeginDate(param.getEvaluationConfiguration().getBeginDate());
    evaluation.setEndDate(param.getEvaluationConfiguration().getEndDate());
    evaluation.setDisplayMode(param.getEvaluationConfiguration().getDisplayMode());
    evaluation.setEvaluationDisplayStatus(param.getEvaluationConfiguration().getEvaluationDisplayStatus());
    evaluation.setConference(conference);
    this.evaluationService.save(evaluation);
    log.info("Evaluation relacionado a conferenceId={} criado com sucesso com researchId={}", conference.getId(), evaluation.getId());
  }

  private void loadResearch(Conference conference, ConferenceParamDto param) throws ParseException {
    Research research = conference.getId() == null ? new Research()
      : this.researchService.findByIdConference(conference.getId()).orElse(new Research());

    research.setBeginDate(param.getResearchConfiguration().getBeginDate());
    research.setEndDate(param.getResearchConfiguration().getEndDate());
    research.setLink(param.getResearchConfiguration().getResearchLink());
    research.setEstimatedTime(param.getResearchConfiguration().getEstimatedTimeResearch());
    research.setDisplayMode(String.format("%s %s", param.getResearchConfiguration().getDisplayModeResearch().name(),
                                          param.getResearchConfiguration().getResearchDisplayStatus().name()
    ));
    research.setStatusType(param.getResearchConfiguration().getResearchDisplayStatus());
    research.setConference(conference);
    this.researchService.save(research);
    log.info("Research relacionado a conferenceId={} criado com sucesso com researchId={}", conference.getId(), research.getId());
  }

  private void loadSegmentation(Conference conference, ConferenceParamDto param) {
    List<StructureItem> itens = new ArrayList<>();
    if(param.getSegmentation()) {
      itens = this.structureItemService.findByIds(param.getTargetedByItems());
    }
    conference.setStructureItems(new HashSet<>(itens));
  }

  private void loadServe(Conference conference, ConferenceParamDto param) {
    PortalServer portalServer = this.portalServerService.findByUrl(param.getServerName())
      .orElse(new PortalServer(param.getServerName()));

    conference.setServer(portalServer);
    portalServer.getConferences().add(conference);

    if(param.getDefaultServerConference()) {
      portalServer.getConferences().forEach(con -> con.setDefaultServer(null));
      portalServer.setConference(null);
      conference.setDefaultServer(portalServer);
    }
    else {
      if(conference.getDefaultServer() != null) {
        conference.setDefaultServer(null);
      }
      if(portalServer.getConference() != null && param.getId() != null
         && param.getId().equals(portalServer.getConference().getId())) {
        portalServer.setConference(null);
      }
    }
    this.portalServerService.save(portalServer);
    log.info("PortalServer relacionado a conferenceId={} criado com sucesso com portalServerId={}", conference.getId(), portalServer.getId());
  }

  private void loadBackGroundImages(Conference conference, ConferenceParamDto param) {
    List<File> files = this.fileService.findAllBackGroundImageFromConference(conference.getId());
    if(param.getBackgroundImages() != null && !param.getBackgroundImages().isEmpty()) {

      log.info("Foram informados {} backgroundImages relacionadas a conferenceId={}",
              param.getBackgroundImages().size(),
              conference.getId()
      );

      List<File> filesToSave = param.getBackgroundImages().stream()
        .map(f -> new File(f).setConferenceBackGround(conference)).collect(Collectors.toList());

      log.info("Foram encontrados {} backgroundImages para serem adicionadas relacionadas a conferenceId={}", filesToSave.size(), conference.getId());

      if(!filesToSave.isEmpty()) {
        this.fileService.saveAll(filesToSave);
      }

      final var filesToDelete = files.stream()
              .map(File::getId)
              .filter(id -> filesToSave.stream().noneMatch(file -> id.equals(file.getId())))
              .collect(Collectors.toList());

      log.info("Foram encontrados {} backgroundImages para serem removidas relacionadas a conferenceId={}", filesToDelete.size(), conference.getId());

      filesToDelete.forEach(this.fileService::delete);
    }
    else if (files != null && !files.isEmpty()){
      log.info("Não foi encontrado nenhuma backgroundImage, removendo todas relacionadas a conferenceId={}", conference.getId());
      files.forEach(file -> fileService.delete(file.getId()));
    }
  }

  private void loadCalendarImages(Conference conference, ConferenceParamDto param) {
    List<File> files = this.fileService.findAllCalendarImageFromConference(conference.getId());
    if(param.getCalendarImages() != null && !param.getCalendarImages().isEmpty()) {

      log.info("Foram informados {} calendarImages relacionadas a conferenceId={}",
              param.getCalendarImages().size(),
              conference.getId()
      );

      List<File> filesToSave = param.getCalendarImages().stream()
        .map(f -> new File(f).setConferenceCalendarImage(conference)).collect(Collectors.toList());

      log.info("Foram encontrados {} getCalendarImages para serem adicionadas relacionadas a conferenceId={}", filesToSave.size(), conference.getId());

      if(!filesToSave.isEmpty()) {
        this.fileService.saveAll(filesToSave);
      }

      final var filesToDelete = files.stream()
              .map(File::getId)
              .filter(id -> filesToSave.stream().noneMatch(file -> id.equals(file.getId())))
              .collect(Collectors.toList());

      log.info("Foram encontrados {} getCalendarImages para serem removidas relacionadas a conferenceId={}", filesToDelete.size(), conference.getId());

      filesToDelete.forEach(this.fileService::delete);
    }
    else if (files != null && !files.isEmpty()){
      log.info("Não foi encontrado nenhuma getCalendarImages, removendo todas relacionadas a conferenceId={}", conference.getId());
      files.forEach(file -> fileService.delete(file.getId()));
    }
  }

  private void loadTopics(Conference conference, ConferenceParamDto param) {
    List<Topic> topics = this.topicService.findAllByConference(conference.getId());
    if(param.getHowItWork() != null && !param.getHowItWork().isEmpty()) {

      log.info("Foram encontrados {} topics relacionados a conferenceId={}", topics.size(), conference.getId());

      List<Topic> listToAdd = param.getHowItWork().stream()
        .map(work -> new Topic(work).setConferenceTopic(conference)).collect(Collectors.toList());
      log.info("Foi encontrado {} topics para serem adicionados", listToAdd.size());
      if(!listToAdd.isEmpty()) {
        this.topicService.saveAll(listToAdd);
      }

      List<Topic> listToRemove = topics.stream().filter(
          filter -> param.getHowItWork().stream().noneMatch(work -> filter.getId().equals(work.getId())))
        .collect(Collectors.toList());
      log.info("Foi encontrado {} topics para serem adicionados", listToAdd.size());
      if(!listToRemove.isEmpty()) {
        this.topicService.deleteAll(listToRemove);
      }
    }
    else {
      log.info("Nenhum Topic informado, removendo todos os Topics relacionados a conferenceId={}", conference.getId());
      this.topicService.deleteAllByConference(conference);
    }
  }

  private void loadExternalLinks(Conference conference, ConferenceParamDto param) {
    if(param.getExternalLinks() != null && !param.getExternalLinks().isEmpty()) {

      log.info("Foram informados {} ExternalLinks relacionados a conferenceId={}",
              param.getExternalLinks().size(),
              conference.getId()
      );

      List<String> urlsToSaveOrEdit = param.getExternalLinks().stream()
        .map(ExternalLinksDto::getUrl)
        .distinct()
        .collect(Collectors.toList());

      List<ExternalContent> externalContents = this.externalContentService.findExternalContentsByUrls(urlsToSaveOrEdit);

      List<IsLinkedBy> linked = this.isLinkedByService.findByExternaContentUrlAndConferenceId(conference.getId());

      List<ExternalLinksDto> externalContentsToAdd = this.findNewExternalContent(param.getExternalLinks(), linked);

      if(!externalContentsToAdd.isEmpty()) {
        this.createAllExternalContents(externalContentsToAdd, externalContents);
      }
      this.clearOldLinks(param.getExternalLinks(), linked);
      this.updateLinks(param.getExternalLinks(), linked, externalContents);
      this.createNewLinks(param.getExternalLinks(), linked, externalContents, conference);

      log.info("Criando {} ExternalContents relacionados a conferenceId={}", linked.size(), conference.getId());
      this.isLinkedByService.saveAll(linked);
    }
    else {
      log.info("Nenhum ExternalContent informado, removendo todos relacionamentos IsLinkedBy relacionados a conferenceId={}", conference.getId());
      this.removeAllLinkedFromConference(conference);
    }
  }

  private void clearOldLinks(List<ExternalLinksDto> externalLinks, List<IsLinkedBy> linked) {
    List<IsLinkedBy> linksToDelete = linked.stream()
      .filter(link -> externalLinks.stream().noneMatch(f -> link.getId().equals(f.getId())))
      .collect(Collectors.toList());
    linked.removeAll(linksToDelete);
    this.isLinkedByService.deleteAll(linksToDelete);
  }

  private void createNewLinks(
    List<ExternalLinksDto> externalLinks,
    List<IsLinkedBy> linked,
    List<ExternalContent> externalContents,
    Conference conference
  ) {
    externalLinks.forEach(external -> {
      if(external.getId() == null) {

        IsLinkedBy link = new IsLinkedBy();

        link.setLabel(external.getLabel());
        link.setConference(conference);

        externalContents.stream()
          .filter(f -> f.getUrl().equals(external.getUrl()))
          .findFirst()
          .ifPresent(link::setExternalContent);

        linked.add(link);
      }
    });
  }

  private void updateLinks(
    List<ExternalLinksDto> externalLinks,
    List<IsLinkedBy> linked,
    List<ExternalContent> externalContents
  ) {
    externalLinks.forEach(external -> {

      Optional<IsLinkedBy> linkOptional = linked.stream().filter(f -> f.getId().equals(external.getId())).findFirst();

      if(linkOptional.isPresent()) {
        IsLinkedBy link = linkOptional.get();

        link.setLabel(external.getLabel());

        externalContents.stream()
          .filter(f -> f.getUrl().equals(external.getUrl()))
          .findFirst()
          .ifPresent(externalContent -> link.getExternalContent().setUrl(external.getUrl()));
      }
    });
  }

  private List<ExternalLinksDto> findNewExternalContent(
    List<ExternalLinksDto> externalLinks,
    List<IsLinkedBy> linked
  ) {
    return externalLinks.stream()
      .filter(filter -> linked.stream()
        .noneMatch(external -> external.getExternalContent().getUrl().equals(filter.getUrl()))
      ).collect(Collectors.toList());
  }

  private void removeAllLinkedFromConference(Conference conference) {
    this.isLinkedByService.deleteAllByConference(conference);
  }

  private void createAllExternalContents(List<ExternalLinksDto> linked, List<ExternalContent> externalContents) {
    List<ExternalContent> externalContentsToSave = new ArrayList<>();
    Set<String> urls = new HashSet<>();
    linked.forEach(external -> {
      Optional<ExternalContent> externalContentOptional = externalContents.stream()
        .filter(link -> link.getUrl().equals(external.getUrl())).findFirst();
      ExternalContent externalContent = externalContentOptional.orElseGet(() -> new ExternalContent(external));
      if(externalContent.getId() == null && urls.add(externalContent.getUrl())) {
        externalContentsToSave.add(externalContent);
      }
    });
    this.externalContentService.saveAll(externalContentsToSave);
    externalContents.addAll(externalContentsToSave);
  }

  private void loadAttributes(Conference conference) {
    if(conference.getPlan() != null && conference.getPlan().getId() != null) {
      log.info("Consultando planId={} relacionado a conferenceId={}", conference.getPlan().getId(), conference.getId());
      conference.setPlan(this.planService.find(conference.getPlan().getId()));
    }

    if(conference.getFileAuthentication() != null && conference.getFileAuthentication().getId() != null) {
      log.info("Consultando fileAuthenticationId={} relacionado a conferenceId={}", conference.getFileAuthentication().getId(), conference.getId());
      conference.setFileAuthentication(this.fileService.find(conference.getFileAuthentication().getId()));
    }

    if(conference.getFileParticipation() != null && conference.getFileParticipation().getId() != null) {
      log.info("Consultando fileParticipationId={} relacionado a conferenceId={}", conference.getFileParticipation().getId(), conference.getId());
      conference.setFileParticipation(this.fileService.find(conference.getFileParticipation().getId()));
    }
    if(conference.getLocalityType() != null && conference.getLocalityType().getId() != null) {
      log.info("Consultando localityTypeId={} relacionado a conferenceId={}", conference.getLocalityType().getId(), conference.getId());
      conference.setLocalityType(this.localityTypeService.find(conference.getLocalityType().getId()));
    }
  }

  private void validateConference(ConferenceParamDto conference) {
    if(conference.getPlan() == null || conference.getPlan().getId() == null) {
      throw new IllegalArgumentException("Plan is required");
    }

    Conference c = this.conferenceRepository.findByNameIgnoreCase(conference.getName());
    if(c != null) {
      if(conference.getId() != null) {
        if(!conference.getId().equals(c.getId())) {
          throw new IllegalArgumentException("This name already exists");
        }
      }
      else {
        throw new IllegalArgumentException("This name already exists");
      }
    }
  }

  private void clearAttributes(Conference conference) {
    if(conference.getId() != null) {
      if(conference.getPlan() != null && conference.getPlan().getId() != null) {
        Conference conference1 = this.find(conference.getId());
        conference1.setPlan(null);
        this.conferenceRepository.save(conference1);
      }
      if(conference.getFileAuthentication() != null && conference.getFileAuthentication().getId() != null) {
        Conference conference1 = this.find(conference.getId());
        conference1.setFileAuthentication(null);
        this.conferenceRepository.save(conference1);
      }
      if(conference.getFileParticipation() != null && conference.getFileParticipation().getId() != null) {
        Conference conference1 = this.find(conference.getId());
        conference1.setFileParticipation(null);
        this.conferenceRepository.save(conference1);
      }
      if(conference.getLocalityType() != null && conference.getLocalityType().getId() != null) {
        Conference conference1 = this.find(conference.getId());
        conference1.setLocalityType(null);
        this.conferenceRepository.save(conference1);
      }
    }
  }

  public void loadOtherAttributes(ConferenceDto conference) {
    List<IsLinkedBy> linked = this.isLinkedByService.findByExternaContentUrlAndConferenceId(conference.getId());
    conference.setExternalLinks(linked.stream().map(ExternalLinksDto::new).collect(Collectors.toList()));

    this.portalServerService.findByIdConference(conference.getId()).ifPresent(p -> {
      conference.setDefaultServerConference(
        p.getConference() != null && conference.getId().equals(p.getConference().getId()));
      conference.setServerName(p.getUrl());
    });

    if(conference.getHowItWork() == null || conference.getHowItWork().isEmpty()) {
      List<Topic> topics = this.topicService.findAllByConference(conference.getId());
      conference.setHowItWork(topics.stream().map(HowItWorkStepDto::new)
                                .sorted(Comparator.comparing(HowItWorkStepDto::getOrder)).collect(Collectors.toList()));
    }

    if(conference.getBackgroundImages() == null || conference.getBackgroundImages().isEmpty()) {
      List<File> files = this.fileService.findAllBackGroundImageFromConference(conference.getId());
      conference.setBackgroundImages(files.stream().map(FileDto::new).collect(Collectors.toList()));
    }

    if(conference.getCalendarImages() == null || conference.getCalendarImages().isEmpty()) {
      List<File> files = this.fileService.findAllCalendarImageFromConference(conference.getId());
      conference.setCalendarImages(files.stream().map(FileDto::new).collect(Collectors.toList()));
    }

    if(conference.getResearchConfiguration() == null) {
      conference.setResearchConfiguration(this.researchService.findByIdConference(conference.getId())
                                            .map(ResearchConfigurationDto::new).orElse(null));
    }
    
    if(conference.getEvaluationConfiguration() == null) {
      conference.setEvaluationConfiguration(this.evaluationService.findByIdConference(conference.getId())
                                            .map(EvaluationConfigurationDto::new).orElse(null));
    }

    if(conference.getCustomProperties() == null){
      ConferenceColor cores = this.conferenceColorService.findByConferenceColor(conference.getId());
      if(cores != null){
        ConferenceColorDto coresDTO = new ConferenceColorDto(cores);
        conference.setCustomProperties(coresDTO);
      }
    }
  }


  public Boolean delete(Long id) {
    boolean deleteConference = true;
    log.info("Iniciando remoção da conferenceId={}", id);
    Conference conference = this.find(id);

    log.info("conferenceId={} encontrada", id);

    if(conference.getPlan() != null && conference.getPlan().getId() != null) {
      List<PlanItem> planItens = this.planItemService.findAllByIdPlan(conference.getPlan().getId());
      if(planItens != null && !planItens.isEmpty()) {
        for(PlanItem item : planItens) {
          if(item.getAttends() != null) {
            deleteConference = false;
            break;
          }
        }
      }
    }
    if(deleteConference) {
      log.info(
        "Não foi encontrado nenhum Attend relacionado ao planId={} e conferenceId={}",
        Optional.ofNullable(conference.getPlan()).map(Plan::getId).orElse(null),
        conference.getId()
      );
      this.conferenceRepository.delete(conference);
    }
    log.info("conferenceId={} foi removido? {}", conference.getId(), deleteConference);
    return deleteConference;
  }

  public List<PersonDto> findModeratorsByConferenceId(Long id) {
    List<PersonDto> persons = new ArrayList<>();
    this.conferenceRepository.findModeratorsById(id).forEach(p -> persons.add(new PersonDto(p)));
    return persons;
  }

  public Integer countSelfDeclarationById(Long id) {
    return this.conferenceRepository.countSelfDeclarationById(id);
  }

  public List<Conference> findAllWithMeetings(Date date, Long idPerson) {
    List<Conference> conferences = new ArrayList<>();
    this.conferenceRepository.findAllWithMeeting(date, idPerson).iterator().forEachRemaining(conferences::add);

    this.removeUnusableFields(conferences);

    return conferences;
  }

  private void removeUnusableFields(List<Conference> conferences) {
    for(Conference conference : conferences) {
      conference.setTitleAuthentication(null);
      conference.setSubtitleAuthentication(null);
      conference.setTitleParticipation(null);
      conference.setSubtitleParticipation(null);
      conference.setTitleRegionalization(null);
      conference.setSubtitleRegionalization(null);
      conference.setPlan(null);
      conference.setLocalityType(null);

      for(Meeting meeting : conference.getMeeting()) {
        meeting.setReceptionists(null);
      }
    }
  }

  public List<Conference> findAllOpenWithPresentialMeetings4Admins() {
    List<Conference> conferences = new ArrayList<>(this.conferenceRepository.findAllOpenWithPresentialMeeting4Admins());

    this.removeUnusableFields(conferences);

    return conferences;
  }

  public List<Conference> findAllOpenWithPresentialMeetings4Receptionists(Date date, Long idPerson) {
    List<Conference> conferences = new ArrayList<>(this.conferenceRepository.findAllOpenWithPresentialMeeting4Receptionists(date, idPerson));

    this.removeUnusableFields(conferences);

    return conferences;
  }

  public ConferenceRegionalizationDto conferenceContainsRegionalizationStructure(Long idConference) {
    Boolean regionalization = this.structureService.conferenceContainsRegionalizationStructure(idConference);
    return new ConferenceRegionalizationDto(regionalization);
  }

  @Transactional
  public void updateAutomaticConference() {
    List<Conference> conferences = this.conferenceRepository.findAllAutomatic();
    if(conferences != null) {
      conferences.forEach(this::checkConferenceStatus);
      this.conferenceRepository.saveAll(conferences);
    }
  }

  private void checkConferenceStatus(Conference conference) {
    try {
      StatusConferenceType status;

      final Date beginDate = conference.getBeginDate();
      if(this.isPreOpening(beginDate)) {
        status = StatusConferenceType.PRE_OPENING;
      }
      else {
        final Date endDate = conference.getEndDate();
        if(this.isPosClosure(endDate)) {
          status = StatusConferenceType.POST_CLOSURE;
        }
        else {
          status = StatusConferenceType.OPEN;
        }
      }

      conference.setStatusType(status);
    }
    catch(Exception e) {
      log.info("Ocorreu um erro ao verificar o status da conferenceId={}", conference.getId());
    }
  }

  public boolean isPosClosure(Date endDate) {
    final Date now = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    return endDate != null && now.after(endDate);
  }

  public boolean isPreOpening(Date beginDate) {
    final Date now = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    return beginDate != null && now.before(beginDate);
  }

  public String validateDefaultConference(String serverName, Long idConference) {
    Optional<PortalServer> portalOptional = this.portalServerService.findByUrl(serverName);
    if(portalOptional.isPresent()) {
      PortalServer portal = portalOptional.get();
      if(portal.getConference() != null
         && (idConference == null || !idConference.equals(portal.getConference().getId()))) {
        return portal.getConference().getName();
      }
    }
    return null;
  }

  public PrePosConferenceDto getPreOpeningScreen(Long id) {
    PrePosConferenceDto response = new PrePosConferenceDto();
    Conference conference = this.find(id);

    if(conference.getBeginDate() != null) {
      LocalDateTime date = conference.getBeginDate()
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime();
      List<Integer> dateInformattions = Arrays.asList(
        date.getYear(),
        date.getMonthValue() - 1,
        date.getDayOfMonth(),
        date.getHour(),
        date.getMinute(),
        date.getSecond()
      );
      response.setDate(dateInformattions);
    }
    response.setText(conference.getPreOpening());
    return response;
  }

  public PrePosConferenceDto getPosOpeningScreen(Long id) {
    PrePosConferenceDto response = new PrePosConferenceDto();
    response.setText(this.conferenceRepository.findPostClosureByIdConference(id));
    return response;
  }

  public PortalServer getPortalServerDefault(String url) {
    return this.portalServerService.findByUrl(url).orElse(null);
  }
}
