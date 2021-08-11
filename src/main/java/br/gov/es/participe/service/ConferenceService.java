package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.AttendRepository;
import br.gov.es.participe.repository.ConferenceRepository;
import br.gov.es.participe.util.ParticipeUtils;
import br.gov.es.participe.util.domain.StatusConferenceType;
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
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class ConferenceService {

  static Logger log = Logger.getLogger(ConferenceService.class.getName());

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
  private StructureService structureService;

  @Autowired
  private ParticipeUtils participeUtils;

  public void generateAuthenticationScreen(
    Long id,
    AuthenticationScreenDto auth,
    UriComponentsBuilder uriComponentsBuilder
  ) {
    conferenceRepository.findByIdFull(id).ifPresent(conference -> {
      auth.setStatus(conference.getStatusType());
      auth.setTitleAuthentication(conference.getTitleAuthentication());
      auth.setSubtitleAuthentication(conference.getSubtitleAuthentication());
      auth.setFileAuthentication(new FileDto(conference.getFileAuthentication()));

      File backGroundImage = fileService.findRandomackGroundImage(id);
      auth.setBackgroundImageUrl(
        backGroundImage != null ? new FileDto(conference.getFileAuthentication()) : null);

      String url = uriComponentsBuilder.path("/files/").build().toUri().toString();
      auth.getFileAuthentication().setUrl(url + conference.getFileAuthentication().getId());
      if(backGroundImage != null) {
        auth.getBackgroundImageUrl().setUrl(url + backGroundImage.getId());
      }

      Plan plan = planService.findByConference(conference.getId());
      if(plan.getlocalitytype() != null) {
        auth.setLocalityType(plan.getlocalitytype().getName());
      }
      auth.setBeginDate(conference.getBeginDate());
      auth.setEndDate(conference.getEndDate());
      auth.setProposal(commentService.countCommentByConference(id));
      auth.setHighlights(highlightService.countHighlightByConference(id));
      auth.setParticipations(attendRepository.countParticipationByConference(conference.getId()));
      auth.setNumberOfLocalities(localityService.countLocalitiesParticipation(conference.getId()));
    });
  }

  public void generateCardScreen(CardScreenDto response, Long id) {
    Conference conference = find(id);
    Plan plan = planService.find(conference.getPlan().getId());

    if(plan.getlocalitytype() != null) {
      response.setRegionalizable(plan.getlocalitytype().getName());
    }
    response.setTitle(conference.getTitleRegionalization());
    response.setSubtitle(conference.getSubtitleRegionalization());
  }

  public boolean validate(String name, Long id) {
    return conferenceRepository.validateName(name, id) == null;
  }

  public List<Conference> findAll(String name, Long plan, Integer month, Integer year) {
    List<Conference> conferences = new ArrayList<>();

    conferenceRepository.findAllByQuery(name, plan, month, year).iterator().forEachRemaining(conferences::add);
    for(Conference conference : conferences) {
      boolean deleteConference = true;
      if(conference.getMeeting() != null) {
        for(Meeting m : conference.getMeeting()) {
          m.setConference(null);
        }
      }
      if(conference.getPlan() != null) {
        List<PlanItem> planItens = planItemService.findAllByIdPlan(conference.getPlan().getId());
        for(PlanItem item : planItens) {
          if(item.getAttends() != null) {
            deleteConference = false;
          }
        }
      }
      conference.setHasAttend(deleteConference);
    }

    return conferences;
  }

  public List<Conference> findByPlan(Long id) {
    return conferenceRepository.findByPlan(id);
  }

  public List<ConferenceDto> findAllActives(Long idPerson, Boolean activeConferences) {
    Person person = personService.find(idPerson);
    final boolean adm = person.getRoles() != null && person.getRoles().contains("Administrator");
    List<ConferenceDto> conferences = new ArrayList<>();
    conferenceRepository.findAllActives(new Date(), activeConferences || !adm).forEach(conference -> {
      if(adm || (conference.getModerators() != null
                 && conference.getModerators().stream().anyMatch(m -> idPerson.equals(m.getId())))) {
        ConferenceDto dto = new ConferenceDto(conference);
        dto.setPlan(null);
        dto.setLocalityType(null);
        dto.setFileAuthentication(null);
        dto.setFileParticipation(null);
        if(adm) {
          Date begin = getDate(dto.getBeginDate());
          Date end = getDate(dto.getEndDate());
          dto.setIsActive(participeUtils.isActive(begin, end));
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
      log.throwing(ConferenceParamDto.class.getName(), "getDate", e);
    }
    return null;
  }

  @Transactional
  public ConferenceDto update(Long conferenceId, ConferenceParamDto conferenceParamDto) throws ParseException {
    Conference conferenceStored = this.conferenceRepository
      .findById(conferenceId)
      .orElseThrow(
        () -> new IllegalStateException("This conference not exist.")
      );

    conferenceStored.update(conferenceParamDto);

    Conference conferenceUpdated = this.save(conferenceStored, conferenceParamDto);

    return ConferenceDto.createConferenceDtoWithoutMeeting(conferenceUpdated);
  }

  @Transactional
  public Conference save(Conference conference, ConferenceParamDto param) throws ParseException {
    validateConference(param);
    // clearAttributes(conference);
    loadAttributes(conference);

    if(conference.getModerators() != null && !conference.getModerators().isEmpty()) {
      HashSet<Person> moderators = new HashSet<>();
      conference.getModerators().forEach(p -> {
        Optional<Person> find = personService.findByContactEmail(p.getContactEmail());
        moderators.add(find.orElseGet(() -> personService.save(p, true)));
      });
      conference.setModerators(moderators);
    }

    conference.setName(param.getName().trim().replaceAll(" +", " "));
    loadAttributesFromParam(conference, param);
    conferenceRepository.save(conference);
    return conference;
  }

  private void loadAttributesFromParam(Conference conference, ConferenceParamDto param) throws ParseException {
    loadResearch(conference, param);
    loadSegmentation(conference, param);
    loadServe(conference, param);
    loadExternalLinks(conference, param);
    loadBackGroundImages(conference, param);
    loadTopics(conference, param);
  }

  private void loadResearch(Conference conference, ConferenceParamDto param) throws ParseException {
    Research research = conference.getId() == null ? new Research()
      : researchService.findByIdConference(conference.getId()).orElse(new Research());

    research.setBeginDate(param.getResearchConfiguration().getBeginDate());
    research.setEndDate(param.getResearchConfiguration().getEndDate());
    research.setLink(param.getResearchConfiguration().getResearchLink());
    research.setEstimatedTime(param.getResearchConfiguration().getEstimatedTimeResearch());
    research.setDisplayMode(String.format("%s %s", param.getResearchConfiguration().getDisplayModeResearch().name(),
                                          param.getResearchConfiguration().getResearchDisplayStatus().name()
    ));
    research.setStatusType(param.getResearchConfiguration().getResearchDisplayStatus());
    research.setConference(conference);
    researchService.save(research);
  }

  private void loadSegmentation(Conference conference, ConferenceParamDto param) {
    List<StructureItem> itens = new ArrayList<>();
    if(param.getSegmentation()) {
      itens = structureItemService.findByIds(param.getTargetedByItems());
    }
    conference.setStructureItems(new HashSet<>(itens));
  }

  private void loadServe(Conference conference, ConferenceParamDto param) {
    PortalServer portalServer = portalServerService.findByUrl(param.getServerName())
      .orElse(new PortalServer(param.getServerName()));

    conference.setServer(portalServer);

    if(param.getDefaultServerConference()) {
      portalServer.getConferences().forEach(con -> con.setDefaultServer(null));
      portalServer.setConference(null);
      conference.setDefaultServer(portalServer);
    } else {
      if (conference.getDefaultServer() != null) {
        conference.setDefaultServer(null);
      }
      if (portalServer.getConference() != null && param.getId() != null
          && param.getId().equals(portalServer.getConference().getId())) {
        portalServer.setConference(null);
      }
    }
    portalServerService.save(portalServer);
  }

  private void loadBackGroundImages(Conference conference, ConferenceParamDto param) {
    List<File> files = fileService.findAllBackGroundImageFromConference(conference.getId());
    if(param.getBackgroundImages() != null && !param.getBackgroundImages().isEmpty()) {
      List<File> listFiles = param.getBackgroundImages().stream()
        .map(f -> new File(f).setConferenceBackGround(conference)).collect(Collectors.toList());
      if(!listFiles.isEmpty()) {
        fileService.saveAll(listFiles);
      }

      files.stream().map(File::getId).filter(id -> listFiles.stream().noneMatch(file -> id.equals(file.getId())))
        .forEach(fileService::delete);
    }
  }

  private void loadTopics(Conference conference, ConferenceParamDto param) {
    List<Topic> topics = topicService.findAllByConference(conference.getId());
    if(param.getHowItWork() != null && !param.getHowItWork().isEmpty()) {

      List<Topic> listToAdd = param.getHowItWork().stream()
        .map(work -> new Topic(work).setConferenceTopic(conference)).collect(Collectors.toList());

      if(!listToAdd.isEmpty()) {
        topicService.saveAll(listToAdd);
      }

      List<Topic> listToRemove = topics.stream().filter(
          filter -> param.getHowItWork().stream().noneMatch(work -> filter.getId().equals(work.getId())))
        .collect(Collectors.toList());
      if(!listToRemove.isEmpty()) {
        topicService.deleteAll(listToRemove);
      }
    }
    else {
      topicService.deleteAllByConference(conference);
    }
  }

  private void loadExternalLinks(Conference conference, ConferenceParamDto param) {
    if(param.getExternalLinks() != null && !param.getExternalLinks().isEmpty()) {

      List<String> urlsToSaveOrEdit = param.getExternalLinks().stream()
        .map(ExternalLinksDto::getUrl)
        .distinct()
        .collect(Collectors.toList());

      List<ExternalContent> externalContents = externalContentService.findExternalContentsByUrls(urlsToSaveOrEdit);

      List<IsLinkedBy> linked = isLinkedByService.findByExternaContentUrlAndConferenceId(conference.getId());

      List<ExternalLinksDto> externalContentsToAdd = findNewExternalContent(param.getExternalLinks(), linked);

      if(!externalContentsToAdd.isEmpty()) {
        createAllExternalContents(externalContentsToAdd, externalContents);
      }
      clearOldLinks(param.getExternalLinks(), linked);
      updateLinks(param.getExternalLinks(), linked, externalContents);
      createNewLinks(param.getExternalLinks(), linked, externalContents, conference);

      isLinkedByService.saveAll(linked);
    }
    else {
      removeAllLinkedFromConference(conference);
    }
  }

  private void clearOldLinks(List<ExternalLinksDto> externalLinks, List<IsLinkedBy> linked) {
    List<IsLinkedBy> linksToDelete = linked.stream()
      .filter(link -> externalLinks.stream().noneMatch(f -> link.getId().equals(f.getId())))
      .collect(Collectors.toList());
    linked.removeAll(linksToDelete);
    isLinkedByService.deleteAll(linksToDelete);
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
    isLinkedByService.deleteAllByConference(conference);
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
    externalContentService.saveAll(externalContentsToSave);
    externalContents.addAll(externalContentsToSave);
  }

  private void clearAttributes(Conference conference) {
    if(conference.getId() != null) {
      if(conference.getPlan() != null && conference.getPlan().getId() != null) {
        Conference conference1 = find(conference.getId());
        conference1.setPlan(null);
        conferenceRepository.save(conference1);
      }
      if(conference.getFileAuthentication() != null && conference.getFileAuthentication().getId() != null) {
        Conference conference1 = find(conference.getId());
        conference1.setFileAuthentication(null);
        conferenceRepository.save(conference1);
      }
      if(conference.getFileParticipation() != null && conference.getFileParticipation().getId() != null) {
        Conference conference1 = find(conference.getId());
        conference1.setFileParticipation(null);
        conferenceRepository.save(conference1);
      }
      if(conference.getLocalityType() != null && conference.getLocalityType().getId() != null) {
        Conference conference1 = find(conference.getId());
        conference1.setLocalityType(null);
        conferenceRepository.save(conference1);
      }
    }
  }

  private void loadAttributes(Conference conference) {
    if(conference.getPlan() != null && conference.getPlan().getId() != null) {
      conference.setPlan(planService.find(conference.getPlan().getId()));
    }

    if(conference.getFileAuthentication() != null && conference.getFileAuthentication().getId() != null) {
      conference.setFileAuthentication(fileService.find(conference.getFileAuthentication().getId()));
    }

    if(conference.getFileParticipation() != null && conference.getFileParticipation().getId() != null) {
      conference.setFileParticipation(fileService.find(conference.getFileParticipation().getId()));
    }
    if(conference.getLocalityType() != null && conference.getLocalityType().getId() != null) {
      conference.setLocalityType(localityTypeService.find(conference.getLocalityType().getId()));
    }
  }

  private void validateConference(ConferenceParamDto conference) {
    if(conference.getPlan() == null || conference.getPlan().getId() == null) {
      throw new IllegalArgumentException("Plan is required");
    }

    if(conference.getFileAuthentication() == null || conference.getFileAuthentication().getId() == null) {
      throw new IllegalArgumentException("Authentication Image is required");
    }

    if(conference.getFileParticipation() == null || conference.getFileParticipation().getId() == null) {
      throw new IllegalArgumentException("Participation Image is required");
    }

    Conference c = conferenceRepository.findByNameIgnoreCase(conference.getName());
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

  public Conference find(Long id) {
    return conferenceRepository.findByIdFull(id)
      .orElseThrow(() -> new IllegalArgumentException("Conference not found: " + id));
  }

  public void loadOtherAttributes(ConferenceDto conference) {
    List<IsLinkedBy> linked = isLinkedByService.findByExternaContentUrlAndConferenceId(conference.getId());
    conference.setExternalLinks(linked.stream().map(ExternalLinksDto::new).collect(Collectors.toList()));

    portalServerService.findByIdConference(conference.getId()).ifPresent(p -> {
      conference.setDefaultServerConference(
        p.getConference() != null && conference.getId().equals(p.getConference().getId()));
      conference.setServerName(p.getUrl());
    });

    if(conference.getHowItWork() == null || conference.getHowItWork().isEmpty()) {
      List<Topic> topics = topicService.findAllByConference(conference.getId());
      conference.setHowItWork(topics.stream().map(HowItWorkStepDto::new)
                                .sorted(Comparator.comparing(HowItWorkStepDto::getOrder)).collect(Collectors.toList()));
    }

    if(conference.getBackgroundImages() == null || conference.getBackgroundImages().isEmpty()) {
      List<File> files = fileService.findAllBackGroundImageFromConference(conference.getId());
      conference.setBackgroundImages(files.stream().map(FileDto::new).collect(Collectors.toList()));
    }

    if(conference.getResearchConfiguration() == null) {
      conference.setResearchConfiguration(researchService.findByIdConference(conference.getId())
                                            .map(ResearchConfigurationDto::new).orElse(null));
    }
  }

  @Transactional
  public Boolean delete(Long id) {
    boolean deleteConference = true;
    Conference conference = find(id);

    if(conference.getPlan() != null && conference.getPlan().getId() != null) {
      List<PlanItem> planItens = planItemService.findAllByIdPlan(conference.getPlan().getId());
      if(planItens != null && !planItens.isEmpty()) {
        for(PlanItem item : planItens) {
          if(item.getAttends() != null) {
            deleteConference = false;
          }
        }
      }
    }
    if(deleteConference) {
      conferenceRepository.delete(conference);
    }

    return deleteConference;
  }

  public List<PersonDto> findModeratorsByConferenceId(Long id) {
    List<PersonDto> persons = new ArrayList<>();
    conferenceRepository.findModeratorsById(id).forEach(p -> persons.add(new PersonDto(p)));
    return persons;
  }

  public Integer countSelfDeclarationById(Long id) {
    return conferenceRepository.countSelfDeclarationById(id);
  }

  public List<Conference> findAllWithMeetings(Date date, Long idPerson) {
    List<Conference> conferences = new ArrayList<>();
    conferenceRepository.findAllWithMeeting(date, idPerson).iterator().forEachRemaining(conferences::add);

    removeUnusableFields(conferences);

    return conferences;
  }

  public List<Conference> findAllWithPresentialMeetings(Date date, Long idPerson) {
    List<Conference> conferences = new ArrayList<>(conferenceRepository
                                                     .findAllWithPresentialMeeting(date, idPerson)
    );

    removeUnusableFields(conferences);

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

  public ConferenceRegionalizationDto conferenceContainsRegionalizationStructure(Long idConference) {

    Boolean regionalization = this.structureService.conferenceContainsRegionalizationStructure(idConference);

    return new ConferenceRegionalizationDto(regionalization);
  }

  public void updateAutomaticConference() {
    List<Conference> conferences = conferenceRepository.findAllAutomatic();
    if(conferences != null) {
      conferences.forEach(this::checkConferenceStatus);
      this.conferenceRepository.saveAll(conferences);
    }
  }

  private void checkConferenceStatus(Conference conference) {
    try {
      StatusConferenceType status;

      if(participeUtils.isPreOpening(conference.getBeginDate())) {
        status = StatusConferenceType.PRE_OPENING;
      }
      else if(participeUtils.isPosClosure(conference.getEndDate())) {
        status = StatusConferenceType.POST_CLOSURE;
      }
      else {
        status = StatusConferenceType.OPEN;
      }

      conference.setStatusType(status);
    }
    catch(Exception e) {
      log.throwing(Conference.class.getName(), "updateAutomaticConference", e);
    }
  }

  public String validateDefaultConference(String serverName, Long idConference) {
    Optional<PortalServer> portalOptional = portalServerService.findByUrl(serverName);
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
    Conference conference = find(id);
    if(conference.getBeginDate() != null) {
      LocalDateTime date = conference.getBeginDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
      List<Integer> dateInformattions = Arrays.asList(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(),
                                                      date.getHour(), date.getMinute(), date.getSecond()
      );
      response.setDate(dateInformattions);
    }
    response.setText(conference.getPreOpening());
    return response;
  }

  public PrePosConferenceDto getPosOpeningScreen(Long id) {
    PrePosConferenceDto response = new PrePosConferenceDto();
    response.setText(conferenceRepository.findPostClosureByIdConference(id));
    return response;
  }

  public PortalServer getPortalServerDefault(String url) {
    return portalServerService.findByUrl(url).orElse(null);
  }
}
