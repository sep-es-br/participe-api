package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.ModerationFilterDto;
import br.gov.es.participe.controller.dto.ModerationParamDto;
import br.gov.es.participe.controller.dto.ModerationResultDto;
import br.gov.es.participe.controller.dto.ModerationStructure;
import br.gov.es.participe.controller.dto.PlanDto;
import br.gov.es.participe.controller.dto.PlanItemDto;
import br.gov.es.participe.controller.dto.ProposalDto;
import br.gov.es.participe.controller.dto.ProposalsDto;
import br.gov.es.participe.controller.dto.StructureItemDto;
import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.CommentRepository;
import br.gov.es.participe.repository.ModeratedByRepository;
import br.gov.es.participe.util.StringUtils;
import br.gov.es.participe.util.domain.CommentStatusType;
import br.gov.es.participe.util.domain.CommentTypeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static br.gov.es.participe.enumerator.TypeMeetingEnum.PRESENCIAL;
import static br.gov.es.participe.enumerator.TypeMeetingEnum.PRESENCIAL_VIRTUAL;
import static br.gov.es.participe.util.domain.CommentStatusType.ALL;
import static br.gov.es.participe.util.domain.CommentTypeType.REMOTE;

@Service
public class CommentService {

  private static final String ADMINISTRATOR = "Administrator";

  @Autowired
  private PersonService personService;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private PlanService planService;

  @Autowired
  private PlanItemService planItemService;

  @Autowired
  private MeetingService meetingService;

  @Autowired
  private HighlightService highlightService;

  @Autowired
  private LocalityService localityService;

  @Autowired
  private ConferenceService conferenceService;

  @Autowired
  private SelfDeclarationService selfDeclarationService;

  @Autowired
  private LocalityTypeService localityTypeService;

  @Autowired
  private ModeratedByRepository moderatedByRepository;

  public List<Comment> findAll(Long idPerson, Long idConference) {
    List<Comment> comments = new ArrayList<>();
    commentRepository
      .findByIdPerson(idPerson, idConference)
      .iterator()
      .forEachRemaining(comments::add);

    return comments;
  }

  public ProposalsDto listProposal(Long idConference, Long idPerson, Integer pageNumber, String text, String status, Long[] localityIds, Long[] planItemIds) {
    List<ProposalDto> proposals = new ArrayList<>();
    ProposalsDto screen = new ProposalsDto();
    Plan plan = planService.findByConferenceWithPlanItem(idConference);
    Page<Comment> comments = findAllCommentsByConference(idConference, pageNumber, text, status, localityIds, planItemIds);

    screen.setTotalPages(comments.getTotalPages());

    ProposalDto proposal;
    for(Comment comment : comments.getContent()) {
      List<PlanItemDto> itens = new ArrayList<>();
      proposal = new ProposalDto();
      proposal.setTime(comment.getTime().toString());
      proposal.setCommentid(comment.getId());
      proposal.setComment(comment.getText());
      if(comment.getLocality() != null) {
        proposal.setLocalityName(comment.getLocality().getName());
        proposal.setLocalityTypeName(comment.getLocality().getType().getName());
      }
      PlanItem planItem = getPlanItemDto(plan.getItems(), comment.getPlanItem().getId());
      listPlanItem(planItem, itens);
      proposal.setPlanItens(itens);

      Person personMadeBy = comment.getPersonMadeBy();
      Set<Person> personLiked = comment.getPersonLiked();
      if(personMadeBy == null) {
        continue;
      }
      proposal.setPersonName(personMadeBy.getName());
      if(personLiked == null || personLiked.isEmpty()) {
        proposal.setLikes(null);
      }
      else {
        proposal.setLikes(personLiked.size());
        setIsLiked(proposal, personLiked, idPerson);
      }

      SelfDeclaration selfDeclaration = comment.getPersonMadeBy().getSelfDeclaretions() != null ? comment.getPersonMadeBy()
        .getSelfDeclaretions()
        .stream()
        .filter(self -> self != null && self.getConference() != null)
        .filter(self -> self.getConference().getId().equals(idConference))
        .findFirst()
        .orElse(null) : null;
      if(selfDeclaration != null && selfDeclaration.getLocality() != null) {
        proposal.setLocalityPerson(selfDeclaration.getLocality().getName());
      }

      proposals.add(proposal);
    }
    screen.setProposals(proposals);
    return screen;
  }

  private PlanItem getPlanItemDto(Set<PlanItem> itens, Long id) {
    for(PlanItem item : itens) {
      if(item.getId().equals(id)) {
        return item;
      }
      if(item.getChildren() != null && !item.getChildren().isEmpty()) {
        PlanItem planItem = getPlanItemDto(item.getChildren(), id);
        if(planItem != null) {
          return planItem;
        }
      }
    }
    return null;
  }

  private void setIsLiked(ProposalDto proposal, Set<Person> personLiked, Long idPerson) {
    proposal.setIsLiked(false);
    for(Person person : personLiked) {
      if(person.getId().compareTo(idPerson) == 0) {
        proposal.setIsLiked(true);
      }
    }
  }

  private void listPlanItem(PlanItem planItem, List<PlanItemDto> items) {
    PlanItemDto plani = new PlanItemDto(planItem, true);

    if(planItem.getParent() != null) {
      listPlanItem(planItem.getParent(), items);
    }

    items.add(plani);
  }

  public Integer countCommentByConference(Long id) {
    return commentRepository.countCommentByConference(id);
  }

  @Transactional
  public Comment save(Comment comment, Long idPerson, String from, Boolean usePlanItem) {

    Meeting meeting = loadMeeting(comment);
    Person person = loadPerson(comment, idPerson);
    Locality locality = loadLocality(comment);
    PlanItem planItem = loadPlanItem(comment, from, usePlanItem);
    Conference conference = loadConference(comment);

    comment.setConference(conference);
    comment.setMeeting(meeting);
    comment.setPlanItem(planItem);
    comment.setLocality(locality);
    if(comment.getClassification() == null || comment.getClassification().isEmpty()) {
      comment.setClassification("proposal");
    }
    Date date = new Date();

    comment.setTime(date);

    Optional<Person> personParticipating = personService
      .findPersonIfParticipatingOnMeetingPresentially(person.getId(), date);

    if(personParticipating.isPresent()) {

      Meeting meetingPresentially = this.meetingService
        .findCheckedInMeetingsByPerson(personParticipating.get().getId())
        .stream()
        .map(CheckedInAt::getMeeting)
        .filter(m -> isTodayInMeetingPeriod(date, m) && isPresentialMeeting(m))
        .findFirst()
        .orElse(null);

      comment.setMeeting(meetingPresentially);
      comment.setType("pre");
    }
    else {
      comment.setType("com");
    }

    if(comment.getStatus() == null) {
      comment.setStatus("pen");
    }
    if(comment.getModerated() == null) {
      comment.setModerated(false);
    }

    Comment response = commentRepository.save(comment);
    Highlight highlight = highlightService.find(
      person.getId(),
      planItem.getId(),
      comment.getConference().getId(),
      locality != null ? locality.getId() : null
    );

    if(highlight == null) {
      highlight = new Highlight();
      highlight.setFrom(from);
      highlight.setMeeting(meeting);
      highlight.setPlanItem(planItem);
      highlight.setLocality(locality);
      highlight.setPersonMadeBy(person);
      highlight.setConference(conference);

      highlightService.save(highlight, from);
    }
    return response;
  }

  private boolean isPresentialMeeting(Meeting m) {
    return PRESENCIAL.equals(m.getTypeMeetingEnum()) || PRESENCIAL_VIRTUAL.equals(m.getTypeMeetingEnum());
  }

  private boolean isTodayInMeetingPeriod(Date date, Meeting m) {
    return date.after(m.getBeginDate()) && date.before(m.getEndDate());
  }

  private Meeting loadMeeting(Comment comment) {
    Meeting meeting = null;
    if(comment.getMeeting() != null) {
      meeting = meetingService.find(comment.getMeeting().getId());
    }
    return meeting;
  }

  private Conference loadConference(Comment comment) {
    Conference conference = null;
    if(comment.getConference() != null && comment.getConference().getId() != null) {
      conference = conferenceService.find(comment.getConference().getId());
    }
    return conference;
  }

  private PlanItem loadPlanItem(Comment comment, String from, Boolean usePlanItem) {
    PlanItem planItem;
    if(usePlanItem) {
      planItem = planItemService.find(comment.getPlanItem().getId());
    }
    else {
      planItem = planItemService.findFatherPlanItem(comment.getPlanItem().getId());
    }

    if(from != null) {
      comment.setFrom(from);
    }
    return planItem;
  }

  private Locality loadLocality(Comment comment) {
    Long localityId = comment.getLocality() != null ? comment.getLocality().getId() : null;

    Locality locality = null;
    if(localityId != null) {
      locality = localityService.find(comment.getLocality().getId());
    }
    return locality;
  }

  private Person loadPerson(Comment comment, Long idPerson) {
    Person person = null;
    if(idPerson == null) {
      person = personService.find(comment.getPersonMadeBy().getId());
      comment.setPersonMadeBy(person);
    }
    else {
      person = personService.find(idPerson);
    }
    return person;
  }

  @Transactional
  public void deleteAllByIdPerson(Long id) {
    List<Comment> comments = commentRepository.findByIdPerson(id, null);

    for(Comment comment : comments) {
      commentRepository.delete(comment);
    }
  }

  public Comment find(Long commentId) {
    return commentRepository
      .findById(commentId)
      .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));
  }

  public List<Comment> find(Long idPerson, Long idPlanItem, Long idConference, Long idLocality) {
    return commentRepository.findByIdPersonAndIdPlanItemAndIdConferenceAndIdLocality(idPerson, idPlanItem, idConference, idLocality);
  }

  public Page<Comment> findAllCommentsByConference(Long idConference, Integer pageNumber, String text, String status, Long[] localityIds, Long[] planItemIds) {
    Pageable page = PageRequest.of(pageNumber, 30);
    return commentRepository.findAllCommentsByConference(idConference, status, text, localityIds, planItemIds, page);
  }

  public List<ModerationResultDto> findAllByStatus(ModerationFilterDto moderationFilterDto) {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    StringUtils stringUtils = new StringUtils();
    Calendar endDatePlus1 = Calendar.getInstance();
    if(moderationFilterDto.getEndDate() != null) {
      endDatePlus1.setTime(moderationFilterDto.getEndDate());
      endDatePlus1.add(Calendar.DATE, 1);
    }

    Person moderator = personService.find(moderationFilterDto.getIdModerator());

    List<ModerationResultDto> response = commentRepository
      .findAllByStatus(moderationFilterDto.getStatus(), moderationFilterDto.getType(), moderationFilterDto.getLocalityIds(),
                       moderationFilterDto.getPlanItemIds(), moderationFilterDto.getConferenceId(),
                       moderationFilterDto.getStructureItemIds()
      )
      .stream()
      .filter(comentario -> {
        Date date = null;
        try {
          date = formatter.parse(comentario
                                   .getTime());
        }
        catch(ParseException e) {
          return true;
        }

        return (moderationFilterDto.getInitialDate() == null || date.after(moderationFilterDto.getInitialDate())) &&
               (moderationFilterDto.getEndDate() == null || date.before(endDatePlus1.getTime()));
      })
      .filter(comentario -> {
        String texto = comentario.getText().toLowerCase();
        String citizenName = comentario.getCitizenName().toLowerCase();
        String compareText = stringUtils.replaceSpecialCharacters(moderationFilterDto.getText());
        texto = stringUtils.replaceSpecialCharacters(texto);
        citizenName = stringUtils.replaceSpecialCharacters(citizenName);

        return texto.contains(compareText) || citizenName.contains(compareText);
      })
      .collect(Collectors.toList());
    final boolean adm = moderator.getRoles() != null && moderator.getRoles().contains(ADMINISTRATOR);
    response.forEach(c -> {
      if(adm || (c.getModerated() != null && c.getModerated())) {
        c.setDisableModerate(false);
      }
      else if(c.getModeratorId() == null) {
        c.setDisableModerate(false);
      }
      else {
        c.setDisableModerate(!c.getModeratorId().equals(moderator.getId()));
      }
    });
    return response;
  }

  public ModerationResultDto findModerationResultById(Long idComment, Long conferenceId) {
    CommentStatusType commStatus = ALL;
    CommentTypeType commType = REMOTE;
    ModerationResultDto response = commentRepository.findModerationResultById(idComment, conferenceId);

    if(response == null) {
      return null;
    }

    response.setStatus(commStatus.getCompleteNameFromLeanName(response.getStatus()));
    response.setType(commType.getCompleteNameFromLeanName(response.getType()));

    List<ModerationStructure> modStructure = new ArrayList<>();
    List<PlanItem> planItems = new ArrayList<>();
    List<StructureItem> structureItems = new ArrayList<>();
    commentRepository.findModerationPlanItemsByCommentId(idComment)
      .iterator()
      .forEachRemaining(planItems::add);
    commentRepository.findModerationStructureItemsByCommentId(idComment)
      .iterator()
      .forEachRemaining(structureItems::add);

    planItems.forEach(planItem -> modStructure.add(new ModerationStructure(planItem)));

    int index = 0;
    for(StructureItem structureItem : structureItems) {
      modStructure.get(index).setStructureItemWithEntity(structureItem);
      index++;
    }

    Collections.reverse(modStructure);
    response.setCommentStructure(modStructure);

    return response;
  }

  public PlanDto findTreeViewByCommentId(Long idComment) {
    Comment comment = commentRepository.findById(idComment).orElse(null);
    if(comment != null) {
      PlanItem planItem = planItemService.find(comment.getPlanItem().getId());
      if(planItem != null) {
        Plan plan = planService.findByPlanItem(planItem.getId());
        if(plan != null) {
          return clearPlan(new PlanDto(plan, true));
        }
      }

    }
    return null;
  }

  private PlanDto clearPlan(PlanDto planDto) {
    planDto.setDomain(null);
    planDto.setStructure(null);
    planDto.setItems(clearPlanItem(planDto.getItems()));
    return planDto;
  }

  private List<PlanItemDto> clearPlanItem(Collection<PlanItemDto> items) {
    List<PlanItemDto> set = new ArrayList<>();
    if(items != null && !items.isEmpty()) {
      for(PlanItemDto i : items) {
        PlanItemDto item = new PlanItemDto();
        item.setId(i.getId());
        item.setName(i.getName());
        if(i.getStructureItem() != null) {
          StructureItemDto structure = new StructureItemDto();
          structure.setId(i.getStructureItem().getId());
          structure.setName(i.getStructureItem().getName());
          structure.setComments(i.getStructureItem().getComments());
          item.setStructureItem(structure);
        }
        if(i.getChildren() != null && !i.getChildren().isEmpty()) {
          item.setChildren(clearPlanItem(i.getChildren()));
        }
        set.add(item);
      }
    }
    return set;
  }

  public Comment findPersonLiked(Long idComment) {
    return commentRepository.findPersonLiked(idComment);
  }

  @Transactional
  public Comment begin(Comment comment, Long idModerator) {
    Person moderator = personService.find(idModerator);
    final boolean adm = moderator.getRoles() != null && moderator.getRoles().contains(ADMINISTRATOR);
    ModeratedBy moderatedBy = moderatedByRepository.findByComment(comment);
    if(moderatedBy != null) {
      if(!moderatedBy.getPerson().getId().equals(moderator.getId())) {
        if(moderatedBy.getFinish() != null && !moderatedBy.getFinish() && !adm) {
          throw new IllegalArgumentException("moderation.comment.error.moderator");
        }

        moderatedByRepository.delete(moderatedBy);
        moderatedBy = new ModeratedBy(false, new Date(), comment, moderator);

      }
      moderatedBy.setFinish(false);
      moderatedBy.setTime(new Date());
      moderatedBy = moderatedByRepository.save(moderatedBy);
      return moderatedBy.getComment();
    }
    else {
      moderatedBy = new ModeratedBy(false, new Date(), comment, moderator);
      moderatedBy = moderatedByRepository.save(moderatedBy);
      return moderatedBy.getComment();
    }

  }

  @Transactional
  public Comment end(Comment comment, Long idModerator) {
    Person moderator = personService.find(idModerator);
    ModeratedBy moderatedBy = moderatedByRepository.findByComment(comment);
    if(moderatedBy != null && moderatedBy.getPerson().getId().equals(moderator.getId())) {
      moderatedBy.setFinish(true);
      moderatedBy.setTime(new Date());
      moderatedBy = moderatedByRepository.save(moderatedBy);
      return moderatedBy.getComment();
    }
    return comment;
  }

  @Transactional
  public Comment update(Comment comment, ModerationParamDto moderationParamDto, Long idModerator) {
    Person moderator = personService.find(idModerator);
    ModeratedBy moderatedBy = moderatedByRepository.findByComment(comment);
    if(moderatedBy == null) {
      moderatedBy = new ModeratedBy(true, new Date(), comment, moderator);
    }
    final boolean adm = moderator.getRoles() != null && moderator.getRoles().contains(ADMINISTRATOR);
    if(!adm && (moderatedBy.getFinish() != null && !moderatedBy.getFinish()) && !moderatedBy.getPerson().getId().equals(
      moderator.getId())) {
      throw new IllegalArgumentException("moderation.error.moderator");
    }
    if(comment == null) {
      throw new IllegalArgumentException("No comment found for given id.");
    }

    validateComment(moderationParamDto, comment);
    loadComment(comment, moderationParamDto);
    Locality locality = null;
    PlanItem planItem = null;
    if(moderationParamDto.getLocality() != null) {
      locality = localityService.find(moderationParamDto.getLocality());
    }

    if(moderationParamDto.getPlanItem() != null) {
      planItem = planItemService.find(moderationParamDto.getPlanItem());
    }

    if(locality != null) {
      comment.setLocality(locality);
    }
    if(planItem != null) {
      comment.setPlanItem(planItem);
    }

    comment.setTime(new Date());

    moderatedBy.setFinish(true);
    moderatedByRepository.save(moderatedBy);
    return commentRepository.save(comment);
  }

  private Comment loadComment(Comment comment, ModerationParamDto moderationParamDto) {
    if(moderationParamDto.getLocality() != null) {
      comment.setLocality(null);
      comment = commentRepository.save(comment);
    }

    if(moderationParamDto.getPlanItem() != null) {
      comment.setPlanItem(null);
      comment = commentRepository.save(comment);
    }

    if(moderationParamDto.getClassification() != null) {
      comment.setClassification(moderationParamDto.getClassification());
    }
    if(moderationParamDto.getText() != null) {
      comment.setText(moderationParamDto.getText());
    }
    if(moderationParamDto.getStatus() != null) {
      CommentStatusType status = Arrays.asList(CommentStatusType.values()).stream().filter(
        s -> s.completeName.equals(moderationParamDto.getStatus())).findFirst().orElse(null);
      if(status != null) {
        comment.setStatus(status.leanName);
      }
    }
    if(moderationParamDto.getType() != null) {
      CommentTypeType type = Arrays.asList(CommentTypeType.values()).stream().filter(
        s -> s.completeName.equals(moderationParamDto.getType())).findFirst().orElse(null);
      if(type != null) {
        comment.setType(type.leanName);
      }
    }

    return comment;
  }

  private void validateComment(ModerationParamDto moderationParamDto, Comment comment) {
    if(!moderationParamDto.getId().equals(comment.getId())) {
      throw new IllegalArgumentException("Mismatch between param ids.");
    }

    if(moderationParamDto.getText() != null && moderationParamDto.getText().isEmpty()) {
      throw new IllegalArgumentException("New text cannot be empty string.");
    }

    if(moderationParamDto.getStatus() != null && (moderationParamDto.getStatus().isEmpty()
                                                  || Arrays.asList(CommentStatusType.values()).stream().noneMatch(
      s -> s.completeName.equals(moderationParamDto.getStatus())))) {
      throw new IllegalArgumentException("Invalid status.");
    }

    if(moderationParamDto.getType() != null && (moderationParamDto.getType().isEmpty()
                                                || Arrays.asList(CommentTypeType.values()).stream().noneMatch(
      s -> s.completeName.equals(moderationParamDto.getType())))) {
      throw new IllegalArgumentException("Invalid type.");
    }
    if(comment.getClassification() != null &&
       (!comment.getClassification().equalsIgnoreCase("comment") && !comment.getClassification().equalsIgnoreCase("proposal"))) {
      throw new IllegalArgumentException("Invalid classification.");
    }
  }
}
