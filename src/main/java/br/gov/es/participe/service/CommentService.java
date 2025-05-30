package br.gov.es.participe.service;

import static br.gov.es.participe.enumerator.TypeMeetingEnum.PRESENCIAL;
import static br.gov.es.participe.enumerator.TypeMeetingEnum.PRESENCIAL_VIRTUAL;
import static br.gov.es.participe.util.domain.CommentStatusType.ALL;
import static br.gov.es.participe.util.domain.CommentTypeType.PROPOSAL;
import static br.gov.es.participe.util.domain.CommentFromType.REMOTE;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.hibernate.validator.internal.util.stereotypes.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import br.gov.es.participe.controller.dto.ModerationFilterDto;
import br.gov.es.participe.controller.dto.ModerationParamDto;
import br.gov.es.participe.controller.dto.ModerationResultDto;
import br.gov.es.participe.controller.dto.ModerationStructure;
import br.gov.es.participe.controller.dto.PlanDto;
import br.gov.es.participe.controller.dto.PlanItemDto;
import br.gov.es.participe.controller.dto.ProposalDto;
import br.gov.es.participe.controller.dto.ProposalsDto;
import br.gov.es.participe.controller.dto.StructureItemDto;
import br.gov.es.participe.model.CheckedInAt;
import br.gov.es.participe.model.Comment;
import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.Highlight;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Meeting;
import br.gov.es.participe.model.ModeratedBy;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.Plan;
import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.model.SelfDeclaration;
import br.gov.es.participe.model.StructureItem;
import br.gov.es.participe.repository.CommentRepository;
import br.gov.es.participe.repository.ModeratedByRepository;
import br.gov.es.participe.util.StringUtils;
import br.gov.es.participe.util.domain.CommentFromType;
import br.gov.es.participe.util.domain.CommentStatusType;
import br.gov.es.participe.util.domain.CommentTypeType;

@Service
public class CommentService {

  private static final String ADMINISTRATOR = "Administrator";

  @Lazy
  @Autowired
  private PersonService personService;

  @Autowired
  private CommentRepository commentRepository;

  @Lazy
  @Autowired
  private PlanService planService;

  @Lazy
  @Autowired
  private PlanItemService planItemService;

  @Lazy
  @Autowired
  private MeetingService meetingService;

  @Lazy
  @Autowired
  private HighlightService highlightService;

  @Lazy
  @Autowired
  private LocalityService localityService;

  @Lazy
  @Autowired
  private ConferenceService conferenceService;

  @Autowired
  private ModeratedByRepository moderatedByRepository;

  private static final Logger log = LoggerFactory.getLogger(CommentService.class);

  public List<Comment> findAll(Long idPerson, Long idConference) {
    List<Comment> comments = new ArrayList<>();
    commentRepository
        .findByIdPerson(idPerson, idConference)
        .iterator()
        .forEachRemaining(comments::add);

    return comments;
  }

  public ProposalsDto listProposal(Long idConference, Long idPerson, Integer pageNumber, String text, String status,
      Long[] localityIds, Long[] planItemIds) {
    List<ProposalDto> proposals = new ArrayList<>();
    ProposalsDto screen = new ProposalsDto();
    Plan plan = planService.findByConferenceWithPlanItem(idConference);
    Page<Comment> comments = findAllCommentsByConference(idConference, pageNumber, text, status, localityIds,
        planItemIds);

    screen.setTotalPages(comments.getTotalPages());

    ProposalDto proposal;
    for (Comment comment : comments.getContent()) {
      List<PlanItemDto> itens = new ArrayList<>();
      proposal = new ProposalDto();
      proposal.setTime(comment.getTime().toString());
      proposal.setCommentid(comment.getId());
      proposal.setComment(comment.getText());
      if (comment.getLocality() != null) {
        if(comment.getLocality().getParents() != null && !comment.getLocality().getParents().isEmpty()) {
            proposal.setLocalityMicro(comment.getLocality().getParents().iterator().next().getName());
            proposal.setLocalityName(comment.getLocality().getName());
            proposal.setLocalityTypeName(comment.getLocality().getType().getName());
        } else {
            proposal.setLocalityMicro(comment.getLocality().getName());
            proposal.setLocalityTypeName(comment.getLocality().getType().getName());
            proposal.setLocalityName("Toda Microrregião");
        }
      }
      PlanItem planItem = getPlanItemDto(plan.getItems(), comment.getPlanItem().getId());
      listPlanItem(planItem, itens);
      proposal.setPlanItens(itens);

      Person personMadeBy = comment.getPersonMadeBy();
      Set<Person> personLiked = comment.getPersonLiked();
      if (personMadeBy == null) {
        continue;
      }
      proposal.setPersonName(personMadeBy.getName());
      proposal.setPersonId(personMadeBy.getId());
      if (personLiked == null || personLiked.isEmpty()) {
        proposal.setLikes(null);
      } else {
        proposal.setLikes(personLiked.size());
        setIsLiked(proposal, personLiked, idPerson);
      }

      SelfDeclaration selfDeclaration = comment.getPersonMadeBy().getSelfDeclaretions() != null
          ? comment.getPersonMadeBy()
              .getSelfDeclaretions()
              .stream()
              .filter(self -> self != null && self.getConference() != null)
              .filter(self -> self.getConference().getId().equals(idConference))
              .findFirst()
              .orElse(null)
          : null;
      if (selfDeclaration != null && selfDeclaration.getLocality() != null) {
        proposal.setLocalityPerson(selfDeclaration.getLocality().getName());
      }

      proposals.add(proposal);
    }
    screen.setProposals(proposals);
    return screen;
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

  private void setIsLiked(ProposalDto proposal, Set<Person> personLiked, Long idPerson) {
    proposal.setIsLiked(false);
    for (Person person : personLiked) {
      if (person.getId().compareTo(idPerson) == 0) {
        proposal.setIsLiked(true);
      }
    }
  }

  private void listPlanItem(PlanItem planItem, List<PlanItemDto> items) {
    if (planItem == null)
      return;

    PlanItemDto plani = new PlanItemDto(planItem, true);

    if (planItem.getParent() != null) {
      listPlanItem(planItem.getParent(), items);
    }

    items.add(plani);
  }

  public Integer countCommentByConference(Long id) {
    return commentRepository.countCommentByConference(id);
  }


  public Comment save(Comment comment, Long idPerson, Boolean usePlanItem) {

    if(comment.getConference() == null){
      throw new IllegalArgumentException("Conference cannot be null:comment");
    }
    if(comment.getLocality() == null){
      throw new IllegalArgumentException("Locality cannot be null:comment");
    }

    log.info("Consultando nós relacionados ao commentId={} feito por personId={}",
      comment.getId(),
      comment.getPersonMadeBy().getId()
    );
    Meeting meeting = loadMeeting(comment);
    Person person = loadPerson(comment, idPerson);
    Locality locality = loadLocality(comment);
    PlanItem planItem = loadPlanItem(comment, usePlanItem);
    Conference conference = loadConference(comment);

    comment.setConference(conference);
    comment.setMeeting(meeting);
    comment.setPlanItem(planItem);
    comment.setLocality(locality);

    if (comment.getType() == null || comment.getType().isEmpty()) {
      comment.setType("prop");
    }
    Date date = new Date();

    comment.setTime(date);

    Optional<Person> personParticipating = personService
        .findPersonIfParticipatingOnMeetingPresentially(person.getId(), date, conference.getId());

    if (personParticipating.isPresent()) {
      log.info("A personId={} está participando presencialmente da meeting relacionada a conferenceId={}",
              person.getId(),
              conference.getId()
      );
      Meeting meetingPresentially = this.meetingService
          .findCheckedInMeetingsByPerson(personParticipating.get().getId())
          .stream()
          .map(CheckedInAt::getMeeting)
          .filter(m -> isTodayInMeetingPeriod(date, m) && isPresentialMeeting(m))
          .findFirst()
          .orElse(null);

      log.info("Alterando meeting relacionado ao commentId={} para a meetingId={}",
               comment.getId(),
               Optional.ofNullable(meetingPresentially).map(Meeting::getId).orElse(null)
      );
      comment.setMeeting(meetingPresentially);
      comment.setFrom("pres");
    } else {
      comment.setFrom("rem");
    }

    if (comment.getStatus() == null) {
      comment.setStatus("pen");
    }
    if (comment.getModerated() == null) {
      comment.setModerated(false);
    }

    Comment response = commentRepository.save(comment);
    log.info("commentId={} persistido com sucesso", comment.getId());

    log.info(
      "Procurando um highlight com os parâmetros personId={}, planItemId={}, conferenceId={} e localityId={}",
      person.getId(),
      planItem.getId(),
      comment.getConference().getId(),
      Optional.ofNullable(locality).map(Locality::getId).orElse(null)
    );
    Highlight highlight = highlightService.find(
        person.getId(),
        planItem.getId(),
        comment.getConference().getId(),
        locality != null ? locality.getId() : null
    );

    if (highlight == null) {
      log.info("Não foi encontrado um Highlight relacionado ao commentId={}", comment.getId());

      highlight = new Highlight();
      highlight.setFrom(comment.getFrom());
      highlight.setMeeting(comment.getMeeting());
      highlight.setPlanItem(planItem);
      highlight.setLocality(locality);
      highlight.setPersonMadeBy(person);
      highlight.setConference(conference);

      log.info(
        "Criando um novo highlight com os parâmetros from={}, meetingId={}, planItemId={}, localityId={}, personId={}, conferenceId={} extraídos do commentId={}",
        comment.getFrom(),
        Optional.ofNullable(comment.getMeeting()).map(Meeting::getId).orElse(null),
        planItem.getId(),
        Optional.ofNullable(locality).map(Locality::getId).orElse(null),
        person.getId(),
        conference.getId(),
        comment.getId()
      );

      highlightService.save(highlight, comment.getFrom());
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
    if (comment.getMeeting() != null) {
      log.info("Consultando a entidade Meeting relacionado ao commentId={} com meetingId={}", comment.getId(), comment.getMeeting().getId());
      meeting = meetingService.find(comment.getMeeting().getId());
    }
    log.info("Encontrou uma meeting relacionada ao commentId={} ? meeting != null = {}", comment.getId(), Objects.nonNull(meeting));
    return meeting;
  }

  public Conference loadConference(Comment comment) {
    Conference conference = null;
    if (comment.getConference() != null && comment.getConference().getId() != null) {
      log.info("Consultando a entidade Conference relacionada ao commentId={} com conferenceId={}", comment.getId(), comment.getPlanItem().getId());
      conference = conferenceService.find(comment.getConference().getId());
    }
    log.info("Encontrou uma conference relacionada ao commentId={} ? conference != null = {}", comment.getId(), Objects.nonNull(conference));
    return conference;
  }

  private PlanItem loadPlanItem(Comment comment, Boolean usePlanItem) {
    PlanItem planItem;
    log.info("Consultando a entidade PlanItem relacionada ao commentId={} com planItemId={}", comment.getId(), comment.getPlanItem().getId());
    if (usePlanItem) {
      log.info("Flag usePlanItem=true, realizando consulta do planItemId={} relacionado ao commentId={}", comment.getPlanItem().getId(), comment.getId());
      planItem = planItemService.find(comment.getPlanItem().getId());
    } else {
      log.info("Flag usePlanItem=false, realizando consulta ao pai do planItemId={} relacionado ao commentId={}", comment.getPlanItem().getId(), comment.getId());
      planItem = planItemService.findFatherPlanItem(comment.getPlanItem().getId());
    }
    log.info("Encontrou uma planItem relacionada ao commentId={} ? planItem != null = {}", comment.getId(), Objects.nonNull(planItem));
    return planItem;
  }

  private Locality loadLocality(Comment comment) {
    Long localityId = comment.getLocality() != null ? comment.getLocality().getId() : null;

    Locality locality = null;
    if (localityId != null) {
      log.info("Consultando a entidade Locality relacionada ao commentId={} com localityId={}", comment.getId(), comment.getLocality().getId());
      locality = localityService.find(comment.getLocality().getId());
    }
    log.info("Encontrou uma locality relacionada ao commentId={} ? locality != null = {}", comment.getId(), Objects.nonNull(locality));
    return locality;
  }

  private Person loadPerson(Comment comment, Long idPerson) {
    Person person;
    if (idPerson == null) {
      log.info(
        "Parâmetro idPerson não foi informado utilizando personId={} contido no relacionamento PersonMadeBy do commentId={}",
        comment.getPersonMadeBy().getId(),
        comment.getId()
      );
      log.info("Consultando a entidade Person relacionado ao commentId={} com personId={}", comment.getId(), comment.getPersonMadeBy().getId());
      person = personService.find(comment.getPersonMadeBy().getId());
      comment.setPersonMadeBy(person);
    } else {
      log.info("Consultando a entidade Person relacionado ao commentId={} com personId={}", comment.getId(), comment.getPersonMadeBy().getId());
      person = personService.find(idPerson);
    }
    log.info("Encontrou uma person relacionada ao commentId={} ? person != null = {}", comment.getId(), Objects.nonNull(person));
    return person;
  }

  public void deleteAllByIdPerson(Long id) {
    log.info("Consultando comments relacionados a personId={}", id);
    List<Comment> comments = commentRepository.findByIdPerson(id, null);
    log.info("Foram encontrados {} comments relacionados a personId={}", comments.size(), id);
    commentRepository.deleteAll(comments);
    log.info("Apagando {} comments relacionados a personId={}", comments.size(), id);
  }

  public Comment find(Long commentId) {
    return commentRepository
        .findById(commentId)
        .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));
  }

  public List<Comment> find(Long idPerson, Long idPlanItem, Long idConference, Long idLocality) {
    return commentRepository.findByIdPersonAndIdPlanItemAndIdConferenceAndIdLocality(idPerson, idPlanItem, idConference,
        idLocality);
  }

  public Page<Comment> findAllCommentsByConference(Long idConference, Integer pageNumber, String text, String status,
      Long[] localityIds, Long[] planItemIds) {
    Pageable page = PageRequest.of(pageNumber, 30);
    return commentRepository.findAllCommentsByConference(idConference, status, text, localityIds, planItemIds, page);
  }

  private Page<ModerationResultDto> filterCommentsByPage(List<ModerationResultDto> commentList, Integer page, Integer size) {
    if (commentList.isEmpty()) {
      return new PageImpl<>(commentList);
    }
    
    Pageable pageRequest = PageRequest.of(page, size);

    int start = (int) pageRequest.getOffset();
    int end = Math.min((start + pageRequest.getPageSize()), commentList.size());

    List<ModerationResultDto> content = commentList.subList(start, end);

    return new PageImpl<>(content, pageRequest, commentList.size());
  }

  public Page<ModerationResultDto> findAllByStatus(ModerationFilterDto moderationFilterDto, Integer pageNumber, Integer rowsPerPage) {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    StringUtils stringUtils = new StringUtils();
    Calendar endDatePlus1 = Calendar.getInstance();
    if (moderationFilterDto.getEndDate() != null) {
      endDatePlus1.setTime(moderationFilterDto.getEndDate());
      endDatePlus1.add(Calendar.DATE, 1);
    }

    Person moderator = personService.find(moderationFilterDto.getIdModerator());

    List<ModerationResultDto> response = commentRepository
        .findAllByStatus(moderationFilterDto.getStatus(), moderationFilterDto.getFrom(),
            moderationFilterDto.getLocalityIds(),
            moderationFilterDto.getPlanItemIds(), moderationFilterDto.getConferenceId(),
            moderationFilterDto.getStructureItemIds())
        .stream()
        .filter(comentario -> {
          Date date;
          try {
            date = formatter.parse(comentario
                .getTime());
          } catch (ParseException e) {
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
      if (adm || (c.getModerated() != null && c.getModerated())) {
        c.setDisableModerate(false);
      } else if (c.getModeratorId() == null) {
        c.setDisableModerate(false);
      } else {
        c.setDisableModerate(!c.getModeratorId().equals(moderator.getId()));
      }
    });

    Page<ModerationResultDto> pagedResponse = filterCommentsByPage(response, pageNumber, rowsPerPage);

    return pagedResponse;
  }

  public ModerationResultDto findModerationResultById(Long idComment, Long conferenceId) {
    ModerationResultDto response = commentRepository.findModerationResultById(idComment, conferenceId);

    if (response == null) {
      return null;
    }

    response.setStatus(ALL.getCompleteNameFromLeanName(response.getStatus()));
    response.setType(PROPOSAL.getCompleteNameFromLeanName(response.getType()));
    response.setFrom(REMOTE.getCompleteNameFromLeanName(response.getFrom()));

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
    for (StructureItem structureItem : structureItems) {
      modStructure.get(index).setStructureItemWithEntity(structureItem);
      index++;
    }

    Collections.reverse(modStructure);
    response.setCommentStructure(modStructure);

    return response;
  }

  public PlanDto findTreeViewByCommentId(Long idComment) {
    Comment comment = commentRepository.findById(idComment).orElse(null);
    if (comment != null) {
      PlanItem planItem = planItemService.find(comment.getPlanItem().getId());
      if (planItem != null) {
        Plan plan = planService.findByPlanItem(planItem.getId());
        if (plan != null) {
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
    if (items != null && !items.isEmpty()) {
      for (PlanItemDto i : items) {
        PlanItemDto item = new PlanItemDto();
        item.setId(i.getId());
        item.setName(i.getName());
        if (i.getStructureItem() != null) {
          StructureItemDto structure = new StructureItemDto();
          structure.setId(i.getStructureItem().getId());
          structure.setName(i.getStructureItem().getName());
          structure.setComments(i.getStructureItem().getComments());
          item.setStructureItem(structure);
        }
        if (i.getChildren() != null && !i.getChildren().isEmpty()) {
          item.setChildren(clearPlanItem(i.getChildren()));
        }
        set.add(item);
      }
    }
    Collections.sort(set);
    return set;
  }

  public Comment findPersonLiked(Long idComment) {
    return commentRepository.findPersonLiked(idComment);
  }

  /*

  @Transactional(isolation=Isolation.READ_COMMITTED)
  public Comment begin(Comment comment, Long idModerator) {
    Person moderator = personService.find(idModerator);
    final boolean adm = moderator.getRoles() != null && moderator.getRoles().contains(ADMINISTRATOR);
    ModeratedBy moderatedBy = moderatedByRepository.findByComment(comment);
    if (moderatedBy != null) {
      if (!moderatedBy.getPerson().getId().equals(moderator.getId())) {
        if (moderatedBy.getFinish() != null && !moderatedBy.getFinish() && !adm) {
          throw new IllegalArgumentException("moderation.comment.error.moderator");
        }

        moderatedByRepository.delete(moderatedBy);
        moderatedBy = new ModeratedBy(false, new Date(), comment, moderator);

      }
      moderatedBy.setFinish(false);
      moderatedBy.setTime(new Date());
    } else {
      moderatedBy = new ModeratedBy(false, new Date(), comment, moderator);
    }
    moderatedBy = moderatedByRepository.save(moderatedBy);
    return moderatedBy.getComment();

  }
 */


  public ModerationResultDto begin(Long idComment, Long idModerator) {

      Comment comment = find(idComment);
      Person moderator = personService.find(idModerator);
      final boolean adm = moderator.getRoles() != null && moderator.getRoles().contains(ADMINISTRATOR);
      ModeratedBy moderatedBy = moderatedByRepository.findByComment(comment);
      if (moderatedBy != null) {
        if (!moderatedBy.getPerson().getId().equals(moderator.getId())) {
          if (moderatedBy.getFinish() != null && !moderatedBy.getFinish() && !adm) {
            throw new IllegalArgumentException("moderation.comment.error.moderator");
          }
          log.info(
            "Removendo moderatedBy moderatedById={} relacionado ao commentId={} moderatorId={}",
            moderatedBy.getId(),
            comment.getId(),
            moderatedBy.getPerson().getId()
          );
          moderatedByRepository.delete(moderatedBy);
          moderatedBy = new ModeratedBy(false, new Date(), comment, moderator);
        }
        moderatedBy.setFinish(false);
        moderatedBy.setTime(new Date());
      } else {
        moderatedBy = new ModeratedBy(false, new Date(), comment, moderator);
      }

      moderatedBy = moderatedByRepository.save(moderatedBy);
      log.info(
        "Criado novo moderatedBy moderatedById={} relacionado ao commentId={} moderatorId={}",
        moderatedBy.getId(),
        comment.getId(),
        moderatedBy.getPerson().getId()
      );

      return findModerationResultById(comment.getId(),comment.getConference().getId());

  }


  //@Transactional
  public Comment end(Comment comment, Long idModerator) {
    Person moderator = personService.find(idModerator);
    ModeratedBy moderatedBy = moderatedByRepository.findByComment(comment);
    if (moderatedBy != null && moderatedBy.getPerson().getId().equals(moderator.getId())) {
      moderatedBy.setFinish(true);
      moderatedBy.setTime(new Date());
      log.info(
        "Alterando moderatedBy moderatedById={} do commentId={} e moderatorId={} para finish=true",
        moderatedBy.getId(),
        moderatedBy.getComment().getId(),
        moderator.getId()
      );
      moderatedBy = moderatedByRepository.save(moderatedBy);
      return moderatedBy.getComment();
    }
    log.info("O comment informado commentId={} não está relacionado ao moderatorId={} informado", comment.getId(), idModerator);
    return comment;
  }

  //@Transactional
  public Comment update(Comment comment, ModerationParamDto moderationParamDto, Long idModerator) {

    if (comment == null) {
      throw new IllegalArgumentException("No comment found for given id.");
    }

    Conference conferenceDB =conferenceService.find(comment.getConference().getId());

    Long prevPlanItemID = (comment.getPlanItem() == null) ? -1 : comment.getPlanItem().getId();
    Long prevLocalityID = (comment.getLocality() == null) ? -1 : comment.getLocality().getId();

    String prevStatus = (comment.getStatus() == null) ? "" : comment.getStatus();
    CommentStatusType moderationParamStatus = null;
    if (moderationParamDto.getStatus() != null) {
      moderationParamStatus = Arrays.stream(CommentStatusType.values()).filter(
          s -> s.completeName.equals(moderationParamDto.getStatus())).findFirst().orElse(null);
    }
    Person moderator = personService.find(idModerator);
    ModeratedBy moderatedBy = moderatedByRepository.findByComment(comment);
    if (moderatedBy == null) {
      moderatedBy = new ModeratedBy(true, new Date(), comment, moderator);
    }
    final boolean adm = moderator.getRoles() != null && moderator.getRoles().contains(ADMINISTRATOR);
    if (!adm && (moderatedBy.getFinish() != null && !moderatedBy.getFinish())
        && !moderatedBy.getPerson().getId().equals(
            moderator.getId())) {
      throw new IllegalArgumentException("moderation.error.moderator");
    }

    validateComment(moderationParamDto, comment);
    loadComment(comment, moderationParamDto);
    Locality locality = null;
    PlanItem planItem = null;
    if (moderationParamDto.getLocality() != null) {
      locality = localityService.find(moderationParamDto.getLocality());
    }

    if (moderationParamDto.getPlanItem() != null) {
      planItem = planItemService.find(moderationParamDto.getPlanItem());
    }

    if(locality != null){
        comment.setLocality(locality);
    }
    
    
    
    if (planItem != null) {
      comment.setPlanItem(planItem);
    }
    comment.setTime(new Date());

    moderatedBy.setFinish(true);
    moderatedByRepository.save(moderatedBy);
    log.info(
      "Criado ModeratedBy moderatedById={} relacionado ao commentId={} e moderatorId={}",
      moderatedBy.getId(),
      moderatedBy.getComment().getId(),
      moderatedBy.getPerson().getId()
    );

    if ((moderationParamDto.getLocality() != null && !prevLocalityID.equals(moderationParamDto.getLocality()))
        || (moderationParamDto.getPlanItem() != null && !prevPlanItemID.equals(moderationParamDto.getPlanItem()))
        || (moderationParamStatus != null && !prevStatus.equals(moderationParamStatus.leanName))) {

      // Try to remove previous highlight
      Highlight highlightDB = highlightService.find(
          comment.getPersonMadeBy().getId(),
          prevPlanItemID,
          comment.getConference().getId(),
          prevLocalityID
      );

      if (highlightDB != null) {
        highlightService.removeHighlight(highlightDB);
      }

      // If this comment is not going to or staying in trash
      if (!comment.getStatus().equals("rem")) {
        // Try to remove previous highlight
        Highlight newHighlight = new Highlight();
        newHighlight.setConference(comment.getConference());
        newHighlight.setFrom(comment.getFrom());
        newHighlight.setLocality(comment.getLocality());
        newHighlight.setMeeting(comment.getMeeting());
        newHighlight.setPersonMadeBy(comment.getPersonMadeBy());
        newHighlight.setPlanItem(comment.getPlanItem());
        newHighlight.setTime(new Date());
        log.info(
          "Comment com status != rem, criando um novo highlight com os parâmetros conferenceId={}, from={}, localityId={}, meetingId={}, personMadeById={}, planItemId={}",
          Optional.ofNullable(newHighlight.getConference()).map(Conference::getId).orElse(null),
          newHighlight.getFrom(),
          Optional.ofNullable(newHighlight.getLocality()).map(Locality::getId).orElse(null),
          Optional.ofNullable(newHighlight.getMeeting()).map(Meeting::getId).orElse(null),
          Optional.ofNullable(newHighlight.getPersonMadeBy()).map(Person::getId).orElse(null),
          Optional.ofNullable(newHighlight.getPlanItem()).map(PlanItem::getId).orElse(null)
        );
        highlightService.save(newHighlight, newHighlight.getFrom());
      }

    }
    comment.setConference(conferenceDB);
    final var commentCreated = commentRepository.save(comment);
    log.info(
      "Criando um novo comment com os parâmetros commentId={}, conferenceId={}, moderatorId={}, localityId={}, meetingId={}, personMadeById={} planItemId={}",
      commentCreated.getId(),
      Optional.ofNullable(commentCreated.getConference()).map(Conference::getId).orElse(null),
      Optional.ofNullable(commentCreated.getModerator()).map(Person::getId).orElse(null),
      Optional.ofNullable(commentCreated.getLocality()).map(Locality::getId).orElse(null),
      Optional.ofNullable(commentCreated.getMeeting()).map(Meeting::getId).orElse(null),
      Optional.ofNullable(commentCreated.getPersonMadeBy()).map(Person::getId).orElse(null),
      Optional.ofNullable(commentCreated.getPlanItem()).map(PlanItem::getId).orElse(null)
    );
    return commentCreated;
  }

  private Comment loadComment(Comment comment, ModerationParamDto moderationParamDto) {
    if (moderationParamDto.getLocality() != null) {
      comment.setLocality(null);
      comment = commentRepository.save(comment);
    }

    comment.setDuplicated(moderationParamDto.getDuplicated());

    if (moderationParamDto.getPlanItem() != null) {
      comment.setPlanItem(null);
      comment = commentRepository.save(comment);
    }

    if (moderationParamDto.getClassification() != null) {
      comment.setClassification(moderationParamDto.getClassification());
    }
    if (moderationParamDto.getText() != null) {
      comment.setText(moderationParamDto.getText());
    }
    if (moderationParamDto.getStatus() != null) {
      CommentStatusType status = Arrays.stream(CommentStatusType.values()).filter(
          s -> s.completeName.equals(moderationParamDto.getStatus())).findFirst().orElse(null);
      if (status != null) {
        comment.setStatus(status.leanName);
      }
    }
    if (moderationParamDto.getType() != null) {
      CommentTypeType type = Arrays.stream(CommentTypeType.values()).filter(
          s -> s.completeName.equals(moderationParamDto.getType())).findFirst().orElse(null);
      if (type != null) {
        comment.setType(type.leanName);
      }
    }

    if (moderationParamDto.getFrom() != null) {
      CommentFromType from = Arrays.stream(CommentFromType.values()).filter(
          s -> s.completeName.equals(moderationParamDto.getFrom())).findFirst().orElse(null);
      if (from != null) {
        comment.setFrom(from.leanName);
      }
    }

    return comment;
  }

  private void validateComment(ModerationParamDto moderationParamDto, Comment comment) {
    if (!moderationParamDto.getId().equals(comment.getId())) {
      throw new IllegalArgumentException("Mismatch between param ids.");
    }

    if (moderationParamDto.getText() != null && moderationParamDto.getText().isEmpty()) {
      throw new IllegalArgumentException("New text cannot be empty string.");
    }

    if (moderationParamDto.getStatus() != null && (moderationParamDto.getStatus().isEmpty()
        || Arrays.stream(CommentStatusType.values()).noneMatch(
            s -> s.completeName.equals(moderationParamDto.getStatus())))) {
      throw new IllegalArgumentException("Invalid status.");
    }

    if (moderationParamDto.getType() != null && (moderationParamDto.getType().isEmpty()
        || Arrays.stream(CommentTypeType.values()).noneMatch(
            s -> s.completeName.equals(moderationParamDto.getType())))) {
      throw new IllegalArgumentException("Invalid type.");
    }

    if (moderationParamDto.getFrom() != null && (moderationParamDto.getFrom().isEmpty()
        || Arrays.stream(CommentFromType.values()).noneMatch(
            s -> s.completeName.equals(moderationParamDto.getFrom())))) {
      throw new IllegalArgumentException("Invalid from.");
    }

    if (comment.getClassification() != null &&
        (!comment.getClassification().equalsIgnoreCase("comment")
            && !comment.getClassification().equalsIgnoreCase("proposal"))) {
      throw new IllegalArgumentException("Invalid classification.");
    }
  }
}
