package br.gov.es.participe.controller;

import static br.gov.es.participe.util.domain.CommentTypeType.PROPOSAL;
import static br.gov.es.participe.util.domain.CommentFromType.REMOTE;
import static br.gov.es.participe.util.domain.CommentStatusType.ALL;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.participe.controller.dto.ConferenceDto;
import br.gov.es.participe.controller.dto.LeanLocalityResultDto;
import br.gov.es.participe.controller.dto.LeanPlanItemResultDto;
import br.gov.es.participe.controller.dto.LocalityDto;
import br.gov.es.participe.controller.dto.ModerationFilterDto;
import br.gov.es.participe.controller.dto.ModerationParamDto;
import br.gov.es.participe.controller.dto.ModerationResultDto;
import br.gov.es.participe.controller.dto.PlanDto;
import br.gov.es.participe.model.Comment;
import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Plan;
import br.gov.es.participe.service.CommentService;
import br.gov.es.participe.service.ConferenceService;
import br.gov.es.participe.service.LocalityService;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.service.PlanItemService;
import br.gov.es.participe.service.PlanService;
import br.gov.es.participe.service.TokenService;
import br.gov.es.participe.util.domain.CommentFromType;
import br.gov.es.participe.util.domain.CommentStatusType;
import br.gov.es.participe.util.domain.CommentTypeType;
import br.gov.es.participe.util.domain.TokenType;

@RestController
@CrossOrigin
@RequestMapping(value = "/moderation")
public class ModerationController {

  @Autowired
  private CommentService commentService;

  @Autowired
  private ConferenceService conferenceService;

  @Autowired
  private TokenService tokenService;

  @Autowired
  private LocalityService localityService;

  @Autowired
  private PlanService planService;

  @Autowired
  private PlanItemService planItemService;

  @Autowired
  private PersonService personService;

  @GetMapping
  public ResponseEntity<List<ModerationResultDto>> findAllCommentsByStatus(
      @RequestHeader(name = "Authorization") String token,
      @RequestParam Long conferenceId,
      @RequestParam(value = "status", required = false, defaultValue = "") String status,
      @RequestParam(value = "from", required = false, defaultValue = "") String from,
      @RequestParam(value = "text", required = false, defaultValue = "") String text,
      @RequestParam(value = "localityIds", required = false) Long[] localityIds,
      @RequestParam(value = "planItemIds", required = false) Long[] planItemIds,
      @RequestParam(value = "structureItemIds", required = false) Long[] structureItemIds,
      @RequestParam(value = "initalDate", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date initialDate,
      @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date endDate) {

    if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
      return ResponseEntity.status(401).body(null);
    }

    Long[] emptyList = {};
    CommentStatusType commStatus = ALL;
    CommentTypeType commType = PROPOSAL;
    CommentFromType commFrom = REMOTE;
    Long idModerator = tokenService.getPersonId(token.substring(7), TokenType.AUTHENTICATION);
    ModerationFilterDto moderationFilterDto = new ModerationFilterDto();
    moderationFilterDto.setIdModerator(idModerator);
    moderationFilterDto.setConferenceId(conferenceId);
    moderationFilterDto.setStatus(commStatus.getLeanNameByCompleteName(status));
    moderationFilterDto.setFrom(commFrom.getLeanNameByCompleteName(from));
    moderationFilterDto.setText(text);
    moderationFilterDto.setLocalityIds(localityIds != null ? localityIds : emptyList);
    moderationFilterDto.setPlanItemIds(planItemIds != null ? planItemIds : emptyList);
    moderationFilterDto.setStructureItemIds(structureItemIds != null ? structureItemIds : emptyList);
    moderationFilterDto.setInitialDate(initialDate);
    moderationFilterDto.setEndDate(endDate);
    List<ModerationResultDto> response = commentService.findAllByStatus(moderationFilterDto);

    response.forEach(c -> {
      c.setStatus(commStatus.getCompleteNameFromLeanName(c.getStatus()));
      c.setType(commType.getCompleteNameFromLeanName(c.getType()));
      c.setFrom(commFrom.getCompleteNameFromLeanName(c.getFrom()));
    });

    return ResponseEntity.status(200).body(response);
  }

  @GetMapping("/{idComment}")
  public ResponseEntity<ModerationResultDto> findModerationResultById(
      @RequestHeader(name = "Authorization") String token,
      @PathVariable Long idComment,
      @RequestParam Long conferenceId) {

    if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
      return ResponseEntity.status(401).body(null);
    }
    ModerationResultDto response = commentService
        .findModerationResultById(idComment, conferenceId);
    if (response == null) {
      return ResponseEntity.status(200).body(new ModerationResultDto());
    }
    return ResponseEntity.status(200).body(response);
  }

  @GetMapping("/treeView/{idComment}")
  public ResponseEntity<PlanDto> findPlanByCommentId(
      @RequestHeader(name = "Authorization") String token,
      @PathVariable Long idComment) {

    if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
      return ResponseEntity.status(401).body(null);
    }
    PlanDto response = commentService
        .findTreeViewByCommentId(idComment);
    return ResponseEntity.status(200).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ModerationResultDto> update(@PathVariable Long id,
      @RequestHeader(name = "Authorization") String token,
      @RequestBody ModerationParamDto moderationParamDto) {

    if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
      return ResponseEntity.status(401).body(null);
    }
    if (moderationParamDto == null) {
      return ResponseEntity.status(400).body(null);
    }
    Long idPerson = tokenService.getPersonId(token.substring(7), TokenType.AUTHENTICATION);
    Comment comment = commentService.find(id);
    commentService.update(comment, moderationParamDto, idPerson);
    ModerationResultDto moderation = commentService.findModerationResultById(comment.getId(),
        comment.getConference().getId());
    return ResponseEntity.status(200).body(moderation);
  }

  @PutMapping("/begin/{id}")
  public ResponseEntity<ModerationResultDto> begin(
      @PathVariable Long id,
      @RequestHeader(name = "Authorization") String token) {

    if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
      return ResponseEntity.status(401).body(null);
    }
    Long idPerson = tokenService.getPersonId(token.substring(7), TokenType.AUTHENTICATION);
    Comment comment = commentService.find(id);
    commentService.begin(comment, idPerson);
    ModerationResultDto moderation = commentService.findModerationResultById(comment.getId(),
        comment.getConference().getId());
    return ResponseEntity.status(200).body(moderation);
  }

  @PutMapping("/end/{id}")
  public ResponseEntity<ModerationResultDto> end(
      @PathVariable Long id,
      @RequestHeader(name = "Authorization") String token) {

    if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
      return ResponseEntity.status(401).body(null);
    }
    Long idPerson = tokenService.getPersonId(token.substring(7), TokenType.AUTHENTICATION);
    Comment comment = commentService.find(id);
    commentService.end(comment, idPerson);
    ModerationResultDto moderation = commentService.findModerationResultById(comment.getId(),
        comment.getConference().getId());
    return ResponseEntity.status(200).body(moderation);
  }

  @GetMapping("/conferences")
  public ResponseEntity<List<ConferenceDto>> findConferencesActives(
      @RequestHeader(name = "Authorization") String token,
      @RequestParam(name = "activeConferences", required = false, defaultValue = "false") Boolean activeConferences) {

    if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
      return ResponseEntity.status(401).body(null);
    }
    Long idPerson = tokenService.getPersonId(token.substring(7), TokenType.AUTHENTICATION);
    List<ConferenceDto> conferences = conferenceService.findAllActives(idPerson, activeConferences);
    return ResponseEntity.status(200).body(conferences);
  }

  @GetMapping("/localities/conference/{id}")
  public ResponseEntity<LeanLocalityResultDto> findLocByIdConference(
      @RequestHeader(name = "Authorization") String token,
      @PathVariable Long id) {

    if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
      return ResponseEntity.status(401).body(null);
    }

    List<Locality> localities = localityService.findByIdConference(id);

    List<LocalityDto> localitiesDto = new ArrayList<>();
    localities.forEach(locality -> {
      LocalityDto dto = new LocalityDto(locality, null, false, false);
      localitiesDto.add(dto);
    });

    LeanLocalityResultDto response = new LeanLocalityResultDto();
    response.setLocalities(localitiesDto);

    Conference conference = conferenceService.find(id);
    Plan plan = planService.find(conference.getPlan().getId());
    if (plan.getlocalitytype() != null) {
      response.setRegionalizable(plan.getlocalitytype().getName());
    }

    return ResponseEntity.status(200).body(response);
  }

  @GetMapping("plan-items/conference/{id}")
  public ResponseEntity<LeanPlanItemResultDto> findPlanItemsByConference(
      @RequestHeader(name = "Authorization") String token,
      @PathVariable Long id) {

    if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
      return ResponseEntity.status(401).body(null);
    }
    LeanPlanItemResultDto response = planItemService.findPlanItemsByConference(id);
    return ResponseEntity.status(200).body(response);
  }
}