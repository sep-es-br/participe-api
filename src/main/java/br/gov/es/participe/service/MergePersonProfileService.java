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

    Person personToUpdate = this.personService.find(personId);

    List<CheckedInAt> checkedInAt = this.mergeCheckedInMeeting(personIdToRemove, personToUpdate);
    this.mergeLogin(personIdToRemove, personToUpdate);
    this.mergeAttends(personIdToRemove, personToUpdate);
    this.mergeSelfDeclaration(personIdToRemove, personToUpdate);
    this.mergeCommentsLikedBy(personIdToRemove, personToUpdate);
    List<IsAuthenticatedBy> isAuthenticatedBy = this.mergeIsAuthenticatedBy(personIdToRemove, personToUpdate);

    this.personRepository.deleteById(personIdToRemove);

    this.isAuthenticatedByRepository.saveAll(isAuthenticatedBy);
    this.checkedInAtRepository.saveAll(checkedInAt);

    return new PersonParamDto(personToUpdate);
  }

  @Transactional
  private void mergeCommentsLikedBy(Long personIdToRemove, Person personToUpdate) {

    List<Comment> commentLikedByToMerge = this.commentRepository.findAllCommentsLikedByPerson(personIdToRemove);

    if (commentLikedByToMerge.isEmpty())
      return;

    List<Comment> commentLikedByToMergeUpdate = this.commentRepository
        .findAllCommentsLikedByPerson(personToUpdate.getId());

    commentLikedByToMerge.forEach(comment -> comment.getPersonLiked().add(personToUpdate));

    commentLikedByToMergeUpdate.addAll(commentLikedByToMerge);

    this.commentRepository.saveAll(commentLikedByToMergeUpdate);

  }

  @Transactional
  private void mergeLogin(Long personIdToRemove, Person personToUpdate) {
    List<Login> loginToMerge = loginRepository.findAllByPerson(personIdToRemove);

    if (loginToMerge.isEmpty())
      return;

    loginToMerge.forEach(login -> login.setPerson(personToUpdate));

    this.loginRepository.saveAll(loginToMerge);
  }

  private List<CheckedInAt> mergeCheckedInMeeting(Long personIdToRemove, Person person) {
    List<CheckedInAt> checkedInAtToMerge = this.meetingRepository.findAllPersonCheckedIn(personIdToRemove);

    if (checkedInAtToMerge.isEmpty())
      return Collections.emptyList();

    List<CheckedInAt> checkedInAtRelationshipUpdated = checkedInAtToMerge
        .stream()
        .map(checkedInAt -> {
          CheckedInAt newCheckedInAt = new CheckedInAt();
          newCheckedInAt.setTime(checkedInAt.getTime());
          newCheckedInAt.setMeeting(checkedInAt.getMeeting());
          newCheckedInAt.setPerson(person);
          return newCheckedInAt;
        }).collect(Collectors.toList());

    return checkedInAtRelationshipUpdated;
  }

  private List<IsAuthenticatedBy> mergeIsAuthenticatedBy(Long personIdToRemove, Person person) {
    List<IsAuthenticatedBy> authenticatedByToMerge = this.isAuthenticatedByRepository
        .findAllByIdPerson(personIdToRemove);

    if (authenticatedByToMerge.isEmpty())
      return Collections.emptyList();

    List<IsAuthenticatedBy> authenticatedByToUpdate = this.isAuthenticatedByRepository
        .findAllByIdPerson(person.getId());

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

    return authenticatedByRelationshipUpdated;
  }

  @Transactional
  private void mergeSelfDeclaration(Long personIdToRemove, Person person) {
    List<SelfDeclaration> selfDeclarationsToMerge = selfDeclarationRepository.findAllByIdPerson(personIdToRemove);

    if (selfDeclarationsToMerge.isEmpty())
      return;

    List<SelfDeclaration> selfDeclarationsUpdate = selfDeclarationRepository.findAllByIdPerson(person.getId());

    selfDeclarationsToMerge.forEach(selfDeclaration -> selfDeclaration.setPerson(person));

    selfDeclarationsUpdate.addAll(selfDeclarationsToMerge);

    this.selfDeclarationRepository.saveAll(selfDeclarationsUpdate);

  }
  
  @Transactional
  private void mergeAttends(Long personIdToRemove, Person personToUpdate) {
    List<Attend> attendsToMerge = this.attendRepository.findAllAttendByIdPerson(personIdToRemove);

    if (attendsToMerge.isEmpty())
      return;

    List<Attend> attendsToUpdate = this.attendRepository.findAllAttendByIdPerson(personToUpdate.getId());

    attendsToMerge.forEach(attend -> attend.setPersonMadeBy(personToUpdate));

    attendsToUpdate.addAll(attendsToMerge);

    this.attendRepository.saveAll(attendsToUpdate);
  }
}
