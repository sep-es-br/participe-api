package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.PersonParamDto;
import br.gov.es.participe.model.Attend;
import br.gov.es.participe.model.CheckedInAt;
import br.gov.es.participe.model.Comment;
import br.gov.es.participe.model.IsAuthenticatedBy;
import br.gov.es.participe.model.Login;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.SelfDeclaration;
import br.gov.es.participe.repository.AttendRepository;
import br.gov.es.participe.repository.AuthServiceRepository;
import br.gov.es.participe.repository.CheckedInAtRepository;
import br.gov.es.participe.repository.CommentRepository;
import br.gov.es.participe.repository.IsAuthenticatedByRepository;
import br.gov.es.participe.repository.LoginRepository;
import br.gov.es.participe.repository.MeetingRepository;
import br.gov.es.participe.repository.PersonRepository;
import br.gov.es.participe.repository.SelfDeclarationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("unused")
public class MergePersonProfileService {

  private final PersonService personService;
  private final PersonRepository personRepository;
  private final SelfDeclarationRepository selfDeclarationRepository;
  private final LoginRepository loginRepository;
  private final AttendRepository attendRepository;
  private final AuthServiceRepository authServiceRepository;
  private final CheckedInAtRepository checkedInAtRepository;
  private final CommentRepository commentRepository;
  private final IsAuthenticatedByRepository isAuthenticatedByRepository;
  private final MeetingRepository meetingRepository;
  private static final Logger log = LoggerFactory.getLogger(MergePersonProfileService.class);

  @Autowired
  public MergePersonProfileService(
      PersonRepository personRepository,
      PersonService personService,
      SelfDeclarationRepository selfDeclarationRepository,
      LoginRepository loginRepository,
      AttendRepository attendRepository,
      AuthServiceRepository authServiceRepository,
      CheckedInAtRepository checkedInAtRepository,
      CommentRepository commentRepository,
      IsAuthenticatedByRepository isAuthenticatedByRepository,
      MeetingRepository meetingRepository) {
    this.personRepository = personRepository;
    this.personService = personService;
    this.loginRepository = loginRepository;
    this.attendRepository = attendRepository;
    this.authServiceRepository = authServiceRepository;
    this.checkedInAtRepository = checkedInAtRepository;
    this.commentRepository = commentRepository;
    this.isAuthenticatedByRepository = isAuthenticatedByRepository;
    this.selfDeclarationRepository = selfDeclarationRepository;
    this.meetingRepository = meetingRepository;
  }

  public PersonParamDto merge(Long personIdToRemove, Long personId) {
    log.info("Iniciando mescla dos dados da personId={} na personId={}", personIdToRemove, personId);
    Person personToUpdate = this.personService.find(personId);

    List<CheckedInAt> checkedInAt = this.mergeCheckedInMeeting(personIdToRemove, personToUpdate);
    this.mergeLogin(personIdToRemove, personToUpdate);
    this.mergeAttends(personIdToRemove, personToUpdate);
    this.mergeSelfDeclaration(personIdToRemove, personToUpdate);
    this.mergeCommentsLikedBy(personIdToRemove, personToUpdate);
    List<IsAuthenticatedBy> isAuthenticatedBy = this.mergeIsAuthenticatedBy(personIdToRemove, personToUpdate);

    log.info("Removendo registro da personId={}", personIdToRemove);
    this.personRepository.deleteById(personIdToRemove);

    log.info("Persistindo {} registros de IsAuthenticatedBy relacionados a personId={}", isAuthenticatedBy.size(), personId);
    this.isAuthenticatedByRepository.saveAll(isAuthenticatedBy);
    log.info("Persistindo {} registros de CheckedInAt relacionados a personId={}", checkedInAt.size(), personId);
    this.checkedInAtRepository.saveAll(checkedInAt);

    return new PersonParamDto(personToUpdate);
  }

  private void mergeCommentsLikedBy(Long personIdToRemove, Person personToUpdate) {

    List<Comment> commentLikedByToMerge = this.commentRepository.findAllCommentsLikedByPerson(personIdToRemove);

    log.info("Foram encontrados {} registros de Comment relacionados a personId={} para serem mesclados na personId={}",
      commentLikedByToMerge.size(),
      personIdToRemove,
      personToUpdate.getId()
    );

    if (commentLikedByToMerge.isEmpty())
      return;

    List<Comment> commentLikedByToMergeUpdate = this.commentRepository
        .findAllCommentsLikedByPerson(personToUpdate.getId());

    log.info(
      "Foram encontrados {} registros já existentes de Comment relacionados a personId={}",
      commentLikedByToMergeUpdate.size(),
      personToUpdate.getId()
    );

    commentLikedByToMerge.forEach(comment -> {
      comment.getPersonLiked().add(personToUpdate);
      log.info(
        "Mesclando commentId={} da personId={} na personId={}",
        comment.getId(),
        personIdToRemove,
        personToUpdate.getId()
      );
    });

    commentLikedByToMergeUpdate.addAll(commentLikedByToMerge);

    log.info("Persistindo {} registros de Comment relacionados a personId={}", commentLikedByToMergeUpdate.size(), personToUpdate.getId());

    this.commentRepository.saveAll(commentLikedByToMergeUpdate);

  }

  private void mergeLogin(Long personIdToRemove, Person personToUpdate) {
    List<Login> loginToMerge = loginRepository.findAllByPerson(personIdToRemove);

    log.info("Foram encontrados {} registros de Login relacionados a personId={} para serem mesclados na personId={}",
      loginToMerge.size(),
      personIdToRemove,
      personToUpdate.getId()
    );

    if (loginToMerge.isEmpty())
      return;

    loginToMerge.forEach(login -> {
      log.info(
        "Mesclando loginId={} da personId={} na personId={}",
        login.getId(),
        personIdToRemove,
        personToUpdate.getId()
      );
      login.setPerson(personToUpdate);
    });

    log.info("Persistindo {} registros de Login relacionados a personId={}", loginToMerge.size(), personToUpdate.getId());
    this.loginRepository.saveAll(loginToMerge);
  }

  private List<CheckedInAt> mergeCheckedInMeeting(Long personIdToRemove, Person person) {
    List<CheckedInAt> checkedInAtToMerge = this.meetingRepository.findAllPersonCheckedIn(personIdToRemove);

    log.info("Foram encontrados {} registros de CheckedInAt relacionados a personId={} para serem mesclados na personId={}",
      checkedInAtToMerge.size(),
      personIdToRemove,
      person.getId()
    );

    if (checkedInAtToMerge.isEmpty())
      return Collections.emptyList();

    List<CheckedInAt> checkedInAtRelationshipUpdated = checkedInAtToMerge.stream()
        .map(checkedInAt -> {
          CheckedInAt newCheckedInAt = new CheckedInAt();
          newCheckedInAt.setTime(checkedInAt.getTime());
          newCheckedInAt.setMeeting(checkedInAt.getMeeting());
          newCheckedInAt.setPerson(person);
          log.info(
            "Criando novo registro de CheckedInAt para a personId={} e meetingId={}",
            person.getId(),
            checkedInAt.getMeeting().getId()
          );
          return newCheckedInAt;
        }).collect(Collectors.toList());

    return checkedInAtRelationshipUpdated;
  }

  private List<IsAuthenticatedBy> mergeIsAuthenticatedBy(Long personIdToRemove, Person person) {
    List<IsAuthenticatedBy> authenticatedByToMerge = this.isAuthenticatedByRepository
        .findAllByIdPerson(personIdToRemove);

    log.info(
      "Foram encontrados {} registros de IsAuthenticatedBy relacionados a personId={} para serem mesclados na personId={}",
      authenticatedByToMerge.size(),
      personIdToRemove,
      person.getId()
    );

    if (authenticatedByToMerge.isEmpty())
      return Collections.emptyList();

    List<IsAuthenticatedBy> authenticatedByToUpdate = this.isAuthenticatedByRepository
        .findAllByIdPerson(person.getId());

    log.info(
      "Foram encontrados {} registros já existentes de IsAuthenticatedBy relacionados a personId={}",
      authenticatedByToMerge.size(),
      person.getId()
    );

    List<IsAuthenticatedBy> authenticatedByRelationshipUpdated = authenticatedByToMerge
        .stream()
        .filter(isAuthenticatedBy -> {
          boolean notHaveTheseAuthType = authenticatedByToUpdate
              .stream()
              .map(IsAuthenticatedBy::getName)
              .noneMatch(name -> name.equalsIgnoreCase(isAuthenticatedBy.getName()));
          return notHaveTheseAuthType;
        })
        .map(authenticatedBy -> {
          IsAuthenticatedBy newAuthenticatedBy = authenticatedBy.copyWithoutRelationshipOf();
          newAuthenticatedBy.setPerson(person);
          newAuthenticatedBy.setAuthService(authenticatedBy.getAuthService());
          return newAuthenticatedBy;
        }).collect(Collectors.toList());

    log.info("Encontrado {} registros de IsAuthenticatedBy mescláveis relacionados a personId={}", authenticatedByRelationshipUpdated.size(), person.getId());

    return authenticatedByRelationshipUpdated;
  }

  private void mergeSelfDeclaration(Long personIdToRemove, Person person) {
    List<SelfDeclaration> selfDeclarationsToMerge = selfDeclarationRepository.findAllByIdPerson(personIdToRemove);

    log.info("Foram encontrados {} registros de SelfDeclaration relacionados a personId={} para serem mesclados na personId={}",
      selfDeclarationsToMerge.size(),
      personIdToRemove,
      person.getId()
    );

    if (selfDeclarationsToMerge.isEmpty())
      return;

    List<SelfDeclaration> selfDeclarationsUpdate = selfDeclarationRepository.findAllByIdPerson(person.getId());

    selfDeclarationsToMerge.forEach(selfDeclaration -> {
      selfDeclaration.setPerson(person);
      log.info(
        "Alterando registro de SelfDeclaration selfDeclarationId={} de personId={} para a personId={}",
        selfDeclaration.getId(),
        personIdToRemove,
        person.getId()
      );
    });

    selfDeclarationsUpdate.addAll(selfDeclarationsToMerge);

    log.info("Persistindo {} registros de SelfDeclaration relacionados a personId={}", selfDeclarationsUpdate.size(), person.getId());

    this.selfDeclarationRepository.saveAll(selfDeclarationsUpdate);
  }

  private void mergeAttends(Long personIdToRemove, Person personToUpdate) {
    List<Attend> attendsToMerge = this.attendRepository.findAllAttendByIdPerson(personIdToRemove);

    log.info(
      "Foram encontrados {} registros de Attend relacionados a personId={} para serem mesclados na personId={}",
      attendsToMerge.size(),
      personIdToRemove,
      personToUpdate.getId()
    );

    if (attendsToMerge.isEmpty())
      return;

    List<Attend> attendsToUpdate = this.attendRepository.findAllAttendByIdPerson(personToUpdate.getId());
    log.info(
       "Foram encontrados {} registros já existentes de Attend relacionados a personId={}",
       attendsToMerge.size(),
       personToUpdate.getId()
    );

    attendsToMerge.forEach(attend -> {
      log.info(
        "Mesclando attendId={} da personId={} na personId={}",
        attend.getId(),
        personIdToRemove,
        personToUpdate.getId()
      );
      attend.setPersonMadeBy(personToUpdate);
    });
    attendsToUpdate.addAll(attendsToMerge);

    log.info("Persistindo {} registros de Attend relacionados a personId={}", attendsToUpdate.size(), personToUpdate.getId());

    this.attendRepository.saveAll(attendsToUpdate);
  }
}
