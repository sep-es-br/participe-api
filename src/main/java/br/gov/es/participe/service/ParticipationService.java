package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.AttendRepository;
import br.gov.es.participe.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ParticipationService {

  private static final String FILES = "/files/";

  @Autowired
  private ConferenceService conferenceService;

  @Autowired
  private PlanService planService;

  @Autowired
  private CommentService commentService;

  @Autowired
  private PlanItemService planItemService;

  @Autowired
  private StructureService structureService;

  @Autowired
  private StructureItemService structureItemService;

  @Autowired
  private HighlightService highlightService;

  @Autowired
  private AttendRepository attendRepository;

  @Autowired
  private SelfDeclarationService selfDeclarationService;

  @Autowired
  private ResearchService researchService;

  @Autowired
  private PersonService personService;

  private static final Logger log = LoggerFactory.getLogger(ParticipationService.class);

  public BodyParticipationDto bodyChildren(Long idPlanItem,
  Long idLocality,
  Long idConference,
  Long idPerson,
  String text,
  UriComponentsBuilder uriComponentsBuilder){
    StructureItem structureItem = null;
    
    List<PlanItemDto> itens = new ArrayList<>();
    
    PlanItem planI = planItemService.findByPlanItemChildren(idPlanItem,idLocality);
    Set<PlanItem> planItems = planI.getChildren();

    if (planItems != null) {
      itens.addAll(getListPlanItemDto(planItems, text, idPerson, idConference, idLocality));
      if (!itens.isEmpty()) {
        structureItem = structureItemService.findByIdPlanItem(itens.get(0).getId());
      }
    } else if (planI != null) {
        structureItem = structureItemService.findChild(planI.getStructureItem().getId());
    }

    StructureItemDto structureItemDto = new StructureItemDto(structureItem, null, false, false);

    structureItemDto.setParent(null);
    structureItemDto.setLocality(null);
    structureItemDto.setStructure(null);

    LinkParentDto link = new LinkParentDto();

    if (structureItem != null && structureItem.getLink() != null && planI != null) {
      link.setIdParent(planI.getId());
      link.setTextLink(structureItem.getLink());
      structureItemDto.setLink(null);
      structureItemDto.setParentLink(link);
    }

    String url = uriComponentsBuilder.path(FILES).build().toUri().toString();
    itens.forEach(item -> {
      if (structureItemDto.getLogo() && item.getFile() != null && item.getFile().getId() != null) {
        item.setImage(url + item.getFile().getId());
        item.setFile(null);
      }
    });

    BodyParticipationDto body = new BodyParticipationDto();
    body.setItens(itens);
    body.setStructureitem(structureItemDto);

    return body;

  }

  public BodyParticipationDto body(
      Long idPlanItem,
      Long idLocality,
      Long idConference,
      Long idPerson,
      String text,
      UriComponentsBuilder uriComponentsBuilder) {
    Conference conference = conferenceService.find(idConference);
    Plan plan;
    PlanItem planI = null;
    Set<PlanItem> planItems;
    StructureItem structureItem = null;
    List<PlanItemDto> itens = new ArrayList<>();

    if (idPlanItem == null) {
      plan = planService.findFilesById(conference.getPlan().getId());
      planItems = plan.getItems();
    } else {
      planI = planItemService.findByIdWithLocalities(idPlanItem);
      planItems = planI.getChildren();
    }

    if (planItems != null) {
      itens.addAll(getListPlanItemDto(planItems, text, idPerson, idConference, idLocality));
      if (!itens.isEmpty()) {
        structureItem = structureItemService.findByIdPlanItem(itens.get(0).getId());
      }
    } else {
      if (planI != null) {
        structureItem = structureItemService.findChild(planI.getStructureItem().getId());
      }
    }

    StructureItemDto structureItemDto = new StructureItemDto(structureItem, null, false, false);

    structureItemDto.setParent(null);
    structureItemDto.setLocality(null);
    structureItemDto.setStructure(null);

    LinkParentDto link = new LinkParentDto();

    if (structureItem != null && structureItem.getLink() != null && planI != null) {
      link.setIdParent(planI.getId());
      link.setTextLink(structureItem.getLink());
      structureItemDto.setLink(null);
      structureItemDto.setParentLink(link);
    }

    String url = uriComponentsBuilder.path(FILES).build().toUri().toString();
    itens.forEach(item -> {
      if (structureItemDto.getLogo() && item.getFile() != null && item.getFile().getId() != null) {
        item.setImage(url + item.getFile().getId());
        item.setFile(null);
      }
    });

    BodyParticipationDto body = new BodyParticipationDto();
    body.setItens(itens);
    body.setStructureitem(structureItemDto);

    return body;
  }

  private List<PlanItemDto> getListPlanItemDto(Set<PlanItem> planItems, String text, Long idPerson, Long idConference,
      Long idLocality) {
    List<PlanItemDto> itens = new ArrayList<>();
    if (planItems != null) {
      if (!planItems.isEmpty() && text != null && !text.isEmpty()) {
        planItems = planItems
            .stream()
            .filter(planItem -> isMatchingText(planItem, text))
            .collect(Collectors.toSet());
      }

      for (PlanItem planItem : planItems) {
        PlanItemDto planItemDto = generatePlanItemDtoFront(planItem, idPerson, idConference, idLocality);
        if (planItem.getLocalities() == null) {
          itens.add(planItemDto);
        } else {
          if (planItem.getLocalities().stream().anyMatch(l -> l.getId().equals(idLocality))) {
            itens.add(planItemDto);
          }
        }
      }
    }
    return itens;
  }

  private boolean isMatchingText(PlanItem planItem, String text) {
    if (isNameOrDescription(planItem, text)) {
      return true;
    }
    PlanItem planItem1 = planItemService.findByIdWithLocalities(planItem.getId());
    if (planItem1 != null && planItem1.getChildren() != null && !planItem1.getChildren().isEmpty()) {
      return planItem1.getChildren().stream().anyMatch(pi -> isNameOrDescription(pi, text));
    }
    return false;
  }

  private boolean isNameOrDescription(PlanItem planItem, String text) {
    StringUtils stringUtils = new StringUtils();
    return (planItem.getName() != null
        && !planItem.getName().isEmpty() && stringUtils.compareIfAContainsB(planItem.getName(), text))
        || (planItem.getDescription() != null && !planItem.getDescription().isEmpty()
            && stringUtils.compareIfAContainsB(planItem.getDescription(), text));
  }

  public PlanItemDto generatePlanItemDtoFront(PlanItem planItem, Long idPerson, Long idConference, Long idLocality) {
    PlanItemDto planItemDto = new PlanItemDto(planItem, null, false);

    if (planItemDto.getStructureItem().getId() == null) {
      planItemDto.setStructureItem(null);
    }

    planItemDto.setParent(null);
    planItemDto.setLocalities(null);

    List<Comment> comments = commentService.find(idPerson, planItem.getId(), idConference, idLocality);

    if (comments != null && !comments.isEmpty()) {
      List<CommentDto> commentsDto = new ArrayList<>();
      planItemDto.setCommentsMade(comments.size());
      comments.forEach(comment -> commentsDto.add(new CommentDto(comment, true)));
      planItemDto.setComments(commentsDto);
    } else {
      planItemDto.setCommentsMade(0);
    }

    List<Highlight> high = highlightService.findAll(
        idPerson,
        planItem.getId(),
        idConference,
        idLocality);
    planItemDto.setVotes(high != null && !high.isEmpty());
    return planItemDto;
  }

  public PortalHeader header(Long idPerson, Long id, UriComponentsBuilder uriComponentsBuilder) {
    PortalHeader header = new PortalHeader();
    Conference conference = conferenceService.find(id);
    SelfDeclaration self = selfDeclarationService.findByPersonAndConference(idPerson, id);
    researchService.findByIdConference(id)
        .ifPresent(research -> header.setResearch(new ResearchConfigurationDto(research)));

    header.setTitle(conference.getTitleParticipation());
    header.setSubtitle(conference.getSubtitleParticipation());
    header.setAnswerSurvey(self != null && self.getAnswerSurvey());

    String url = uriComponentsBuilder.path(FILES).build().toUri().toString();
    if (conference.getFileParticipation() != null) {
      header.setImage(url + conference.getFileParticipation().getId());
    }

    return header;
  }

  public Map<String, String> webHeaderImage(Long id, UriComponentsBuilder uriComponentsBuilder) {
    Map<String, String> image = new HashMap<>();
    Conference conference = conferenceService.find(id);

    String url = uriComponentsBuilder.path(FILES).build().toUri().toString();
    if (conference.getFileParticipation() != null) {
      image.put("image", url + conference.getFileParticipation().getId());
    }

    return image;
  }

  public Map<String, String> footerImage (Long id, UriComponentsBuilder uriComponentsBuilder) {
    Map<String, String> footerImage = new HashMap<>();
    
    Conference conference = conferenceService.find(id);

    String url = uriComponentsBuilder.path(FILES).build().toUri().toString();
    if (conference.getFileFooter() != null) {
      footerImage.put("footerImage", url + conference.getFileFooter().getId());
    }

    return footerImage;
  }

  public ConferenceDto getConferenceDto(Long id, UriComponentsBuilder uriComponentsBuilder) {
    Conference conference = conferenceService.find(id);
    ConferenceDto conferenceDto = new ConferenceDto(conference);
    if (conference.getPlan() != null && conference.getPlan().getId() != null) {
      Plan plan = planService.find(conference.getPlan().getId());
      PlanDto planDto = new PlanDto(plan, true);
      if (plan.getStructure() != null && plan.getStructure().getId() != null) {
        Structure structure = structureService.find(plan.getStructure().getId());
        StructureDto structureDto = new StructureDto(structure, true);
        planDto.setStructure(structureDto);
      }
      conferenceDto.setPlan(planDto);
    }

    String url = uriComponentsBuilder.path(FILES).build().toUri().toString();
    if (conferenceDto.getFileAuthentication() != null && conferenceDto.getFileAuthentication().getId() != null) {
      conferenceDto.getFileAuthentication().setUrl(url + conferenceDto.getFileAuthentication().getId());
    }
    if (conferenceDto.getFileParticipation() != null && conferenceDto.getFileParticipation().getId() != null) {
      conferenceDto.getFileParticipation().setUrl(url + conferenceDto.getFileParticipation().getId());
    }

    return conferenceDto;
  }

  public ParticipationsDto findAll(Long idPerson, String text, Long idConference, Pageable pageable) {
    ParticipationsDto dto = new ParticipationsDto();
    Plan plan = planService.findByConferenceWithPlanItem(idConference);
    Page<ParticipationDto> participations = attendRepository.findByIdConferenceAndIdPersonAndText(idPerson,
        idConference, text,
        pageable);
    for (ParticipationDto participation : participations) {
      PlanItem planItem = getPlanItemDto(plan.getItems(), participation.getPlanItem().getId());

      setPlanItens(participation, planItem);

      setLocality(participation);

      participation.setPlanItem(null);

      participation.setHighlight(participation.getText() == null);

      setLikedQuantity(participation);

      participation.setPersonLiked(null);
    }
    dto.setTotalPages(participations.getTotalPages());
    dto.setParticipations(participations.getContent());

    return dto;
  }

  private void setLikedQuantity(ParticipationDto participation) {
    if (!participation.isHighlight()) {
      participation.setQtdLiked(participation.getPersonLiked().size());
    }
  }

  private void setLocality(ParticipationDto participation) {
    if (participation.getLocality() != null) {
      participation.setLocalityDto(new LocalityDto(participation.getLocality()));
      if (participation.getLocalityType() != null) {
        participation.getLocalityDto().setType(new LocalityTypeDto(participation.getLocalityType()));
        participation.setLocalityType(null);
      }
      participation.setLocality(null);
    }
  }

  private void setPlanItens(ParticipationDto participation, PlanItem planItem) {
    if (planItem != null) {
      List<PlanItemDto> itens = new ArrayList<>();
      loadPlanItem(planItem, itens);
      participation.setPlanItems(itens);
    }
  }

  private void loadPlanItem(PlanItem planItem, List<PlanItemDto> itens) {
    PlanItemDto planItemDto = new PlanItemDto(planItem, true);
    planItemDto.setDescription(planItem.getDescription());

    if (planItem.getParent() != null) {
      loadPlanItem(planItem.getParent(), itens);
    }

    itens.add(planItemDto);
  }

  private PlanItem getPlanItemDto(Set<PlanItem> itens, Long id) {
    for (PlanItem item : itens) {
      if (item.getId().equals(id)) {
        return item;
      }
      if (item.getChildren() != null && !item.getChildren().isEmpty()) {
        PlanItem planItem = getPlanItemDto(item.getChildren(), id);
        if (planItem != null) {
          return planItem;
        }
      }
    }
    return null;
  }

  public void setSurvey(Boolean answerSurvey, Long idPerson, Long idConference) {
    SelfDeclaration self = selfDeclarationService.findByPersonAndConference(idPerson, idConference);

    log.info(
      "Alterando answerSurvey para answerSurvey={} da SelfDeclaration selfDeclarationId={} relacionados a personId={} e conferenceId={}",
      answerSurvey,
      self.getId(),
      idPerson,
      idConference
    );

    self.setAnswerSurvey(answerSurvey);

    selfDeclarationService.save(self);
    log.info(
      "SelfDeclaration alterada com sucesso selfDeclarationId={} relacionados a personId={} e conferenceId={}",
      self.getId(),
      idPerson,
      idConference
    );
  }

  @SuppressWarnings("unused")
  private void verifyPersonConsistence(Long idPerson, SelfDeclaration self) {
    if (self.getPerson() == null) {
      Person person = personService.find(idPerson);
      self.setPerson(person);
    }
  }
}
