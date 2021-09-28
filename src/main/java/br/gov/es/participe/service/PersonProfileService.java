package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.AuthenticationProfileDto;
import br.gov.es.participe.controller.dto.LocalityInfoDto;
import br.gov.es.participe.controller.dto.PersonProfileEmailsDto;
import br.gov.es.participe.controller.dto.PersonProfileSearchDto;
import br.gov.es.participe.controller.dto.PersonProfileUpdateDto;
import br.gov.es.participe.controller.dto.RelationshipAuthServiceAuxiliaryDto;
import br.gov.es.participe.controller.dto.SelfDeclarationParamDto;
import br.gov.es.participe.model.AuthService;
import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.IsAuthenticatedBy;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.SelfDeclaration;
import br.gov.es.participe.repository.AuthServiceRepository;
import br.gov.es.participe.repository.IsAuthenticatedByRepository;
import br.gov.es.participe.repository.LocalityRepository;
import br.gov.es.participe.repository.PersonRepository;
import br.gov.es.participe.repository.SelfDeclarationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.gov.es.participe.controller.dto.RelationshipAuthServiceAuxiliaryDto.RelationshipAuthServiceAuxiliaryDtoBuilder;
import static java.util.Arrays.asList;

@Service
public class PersonProfileService {

  private final PersonService personService;
  private final PersonRepository personRepository;
  private final SelfDeclarationRepository selfDeclarationRepository;
  private final LocalityRepository localityRepository;
  private final IsAuthenticatedByRepository isAuthenticatedByRepository;
  private final AuthServiceRepository authServiceRepository;
  private final ConferenceService conferenceService;

  private static final String VALID_CHARACTERS = "[A-Za-z0-9]+";

  private static final String PARTICIPE_EMAIL = "participeEmail";
  private static final String PARTICIPE_CPF = "participeCpf";
  private static final List<String> INTERNAL_LOGIN_TYPE =
    asList(PARTICIPE_EMAIL, PARTICIPE_CPF);

  @Autowired
  public PersonProfileService(
    PersonService personService,
    PersonRepository personRepository,
    SelfDeclarationRepository selfDeclarationRepository,
    LocalityRepository localityRepository,
    IsAuthenticatedByRepository isAuthenticatedByRepository,
    AuthServiceRepository authServiceRepository,
    ConferenceService conferenceService
  ) {
    this.personService = personService;
    this.personRepository = personRepository;
    this.selfDeclarationRepository = selfDeclarationRepository;
    this.localityRepository = localityRepository;
    this.isAuthenticatedByRepository = isAuthenticatedByRepository;
    this.authServiceRepository = authServiceRepository;
    this.conferenceService = conferenceService;
  }

  public PersonProfileSearchDto findById(Long idPerson, Long idConference) {
    Person person = personService.find(idPerson);

    List<IsAuthenticatedBy> isAuthenticatedBy = findIsAuthenticatedById(idPerson);

    LocalityInfoDto localityDto = personRepository.findLocalityByPersonAndConference(
      idConference,
      idPerson
    );

    Optional<SelfDeclaration> selfDeclarationOptional = findSelfDeclaration(idPerson, idConference);

    SelfDeclaration selfDeclaration;

    if(!selfDeclarationOptional.isPresent()) {
      selfDeclaration = new SelfDeclaration(new SelfDeclarationParamDto(
        idConference,
        localityDto.getLocalityId(),
        idPerson
      ));
      selfDeclaration.setReceiveInformational(true);
      selfDeclarationRepository.save(selfDeclaration);
    }
    else {
      selfDeclaration = selfDeclarationOptional.get();
    }

    return new PersonProfileSearchDto(
      person,
      localityDto,
      isAuthenticatedBy.stream()
        .map(AuthenticationProfileDto::new)
        .collect(Collectors.toList()),
      selfDeclaration.getReceiveInformational()
    );
  }

  public List<PersonProfileEmailsDto> findPersonEmail(Long personId) {
    return this.personRepository.findPersonEmails(personId);
  }

  public PersonProfileSearchDto updatePersonProfile(PersonProfileUpdateDto personDto) {

    if(personDto.getId() == null) {
      throw new IllegalStateException("Person id must be informed.");
    }

    List<IsAuthenticatedBy> authentications = this.findIsAuthenticatedById(personDto.getId());

    hasSocialLoginRemoved(personDto.getAuthentications(), authentications);

    Person personToUpdate = personService.find(personDto.getId());

    this.updatePerson(personToUpdate, personDto);

    LocalityInfoDto locality = this.updateSelfDeclaration(
      personToUpdate,
      personDto.getConferenceId(),
      personDto.getLocalityId(),
      personDto.getReceiveInformational()
    );

    this.updateAuthentications(
      personToUpdate,
      personDto.getAuthentications(),
      personDto.getConferenceId(),
      new UpdatePasswordDto(personDto)
    );

    List<AuthenticationProfileDto> authenticationsDto =
      this.findIsAuthenticatedById(personDto.getId())
        .stream()
        .map(AuthenticationProfileDto::new)
        .collect(Collectors.toList());

    return new PersonProfileSearchDto(
      personToUpdate,
      locality,
      authenticationsDto,
      personDto.getReceiveInformational()
    );
  }

  private List<IsAuthenticatedBy> findIsAuthenticatedById(Long id) {
    return this.isAuthenticatedByRepository.findAllByIdPerson(id);
  }

  private void updatePerson(Person personToUpdate, PersonProfileUpdateDto dto) {
    personToUpdate.setName(dto.getName().trim());
    personToUpdate.setContactEmail(dto.getContactEmail().trim());
    if(personToUpdate.getActive() == null) {
      personToUpdate.setActive(true);
    }
    if(!dto.getTelephone().trim().isEmpty() && dto.getTelephone() != null) {
      personToUpdate.setTelephone(dto.getTelephone().trim());
    }
  }

  public LocalityInfoDto updateSelfDeclaration(
    Person person,
    Long conferenceId,
    Long localityId,
    Boolean receiveInformational
  ) {
    SelfDeclaration selfDeclaration = findSelfDeclaration(
      person.getId(),
      conferenceId
    ).orElseGet(() -> {
      Conference conference = conferenceService.find(conferenceId);
      SelfDeclaration sd = new SelfDeclaration();
      sd.setPerson(person);
      sd.setConference(conference);
      return sd;
    });

    Locality newLocality =
      this.localityRepository
        .findById(localityId)
        .orElseThrow(
          () -> new IllegalArgumentException("Locality not found.")
        );
    selfDeclaration.setReceiveInformational(receiveInformational);
    selfDeclaration.setLocality(newLocality);

    this.selfDeclarationRepository.save(selfDeclaration);

    return new LocalityInfoDto(newLocality);
  }

  private Optional<SelfDeclaration> findSelfDeclaration(Long personId, Long conferenceId) {
    return Optional.ofNullable(this.selfDeclarationRepository.findByConferenceIdAndPersonId(
      conferenceId,
      personId
    ));
  }

  private void updateAuthentications(
    Person person,
    List<AuthenticationProfileDto> authenticationDtos,
    Long conferenceId,
    UpdatePasswordDto updatePasswordDto
  ) {
    List<String> storedLoginNames =
      this.findIsAuthenticatedById(person.getId())
        .stream()
        .map(auth -> auth.getAuthService().getServer())
        .collect(Collectors.toList());

    List<IsAuthenticatedBy> isAuthenticatedBySaved = new ArrayList<>();

    for(AuthenticationProfileDto dto : authenticationDtos) {

      if(!storedLoginNames.contains(dto.getLoginName())) {
        IsAuthenticatedBy isAuthenticatedBy = this.createSocialLogin(person, dto, updatePasswordDto, conferenceId);
        isAuthenticatedBySaved.add(isAuthenticatedBy);
        continue;
      }
      if(INTERNAL_LOGIN_TYPE.contains(dto.getAuthenticationType())) {
        IsAuthenticatedBy authenticatedBy =
          this.findIsAuthenticatedById(person.getId()).stream()
            .filter(auth -> INTERNAL_LOGIN_TYPE.contains(auth.getAuthType()))
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
        this.updateInternalLogin(dto, updatePasswordDto, authenticatedBy);
        isAuthenticatedBySaved.add(authenticatedBy);
      }
    }

    this.isAuthenticatedByRepository.saveAll(isAuthenticatedBySaved);
  }

  private void hasSocialLoginRemoved(List<AuthenticationProfileDto> authenticationDtos, List<IsAuthenticatedBy> authentications) {
    List<IsAuthenticatedBy> removedAuthentications =
      authentications.stream().filter((IsAuthenticatedBy auth) -> {
        List<String> authenticationDtoNames = authenticationDtos.stream()
          .map(AuthenticationProfileDto::getLoginName)
          .collect(Collectors.toList());
        return !authenticationDtoNames.contains(auth.getAuthService().getServer());
      }).collect(Collectors.toList());

    if(removedAuthentications.isEmpty()) return;

    for(IsAuthenticatedBy auth : removedAuthentications) {
      AuthService authService = auth.getAuthService();
      this.isAuthenticatedByRepository.deleteById(auth.getId());
      this.authServiceRepository.deleteById(authService.getId());
    }
  }

  public IsAuthenticatedBy createSocialLogin(
    Person person,
    AuthenticationProfileDto dto,
    UpdatePasswordDto updatePasswordDto,
    Long conferenceId
  ) {
    RelationshipAuthServiceAuxiliaryDtoBuilder builder =
      new RelationshipAuthServiceAuxiliaryDtoBuilder(person)
        .server(dto.getLoginName())
        .conferenceId(conferenceId)
        .resetPassword(false)
        .typeAuthentication(dto.getAuthenticationType())
        .makeLogin(true);

    if(INTERNAL_LOGIN_TYPE.contains(dto.getAuthenticationType())) {
      builder.serverId(person.getId().toString());

      if(updatePasswordDto.validate()) {
        builder.password(updatePasswordDto.password);
      }
    }
    else {
      builder.serverId(dto.getIdByAuth());
    }

    AuthService authService = this.createAuthServiceRelationship(builder.build());

    return this.createIsAuthenticatedBy(builder.build(), authService, dto.getLoginEmail());
  }

  public IsAuthenticatedBy createIsAuthenticatedBy(
    RelationshipAuthServiceAuxiliaryDto relationshipDto,
    AuthService authService,
    String email
  ) {

    IsAuthenticatedBy isAuthenticatedBy =
      this.isAuthenticatedByRepository.findAuthenticatedByWithPersonAndAuthService(
        relationshipDto.getPerson().getId(),
        authService.getId()
      ).orElse(new IsAuthenticatedBy());

    if(isAuthenticatedBy.getId() == null && isAuthenticatedBy.getPerson() == null) {
      isAuthenticatedBy.setPerson(relationshipDto.getPerson());
    }
    if(isAuthenticatedBy.getId() == null && isAuthenticatedBy.getAuthService() == null) {
      isAuthenticatedBy.setAuthService(authService);
    }

    isAuthenticatedBy.setEmail(email);
    isAuthenticatedBy.setPassword(relationshipDto.getPassword());
    isAuthenticatedBy.setTemporaryPassword(false);
    isAuthenticatedBy.setPasswordTime(null);
    isAuthenticatedBy.setAuthType(relationshipDto.getTypeAuthentication());
    isAuthenticatedBy.setIdByAuth(relationshipDto.getServerId());
    isAuthenticatedBy.setName(relationshipDto.getServer());

    return isAuthenticatedBy;
  }

  public AuthService createAuthServiceRelationship(RelationshipAuthServiceAuxiliaryDto relationshipDto) {

    AuthService authService = new AuthService();

    authService.setServer(relationshipDto.getServer());
    authService.setServerId(relationshipDto.getServerId());
    authService.setNumberOfAccesses(0);

    authService = this.authServiceRepository.save(authService);

    return authService;
  }

  private void updateInternalLogin(AuthenticationProfileDto dto, UpdatePasswordDto updatePasswordDto, IsAuthenticatedBy authenticatedBy) {

    if(!updatePasswordDto.havePasswordToUpdate()) return;

    if(updatePasswordDto.validate()) {
      authenticatedBy.setPassword(updatePasswordDto.password);
    }

    if(!dto.getLoginEmail().contains("@")) {
      throw new IllegalStateException("The email is not valid");
    }

    if((isLoginByCpf(dto) && PARTICIPE_EMAIL.equals(dto.getAuthenticationType())) ||
       (PARTICIPE_CPF.equals(dto.getAuthenticationType()) && !isLoginByCpf(dto))
    ) {
      throw new IllegalArgumentException("Authentication type does not match login.");
    }

    if(!dto.getAuthenticationType().equalsIgnoreCase(authenticatedBy.getAuthType())) {
      if(isLoginByCpf(dto)) {
        authenticatedBy.setEmail(dto.getLoginEmail());
        authenticatedBy.setAuthType(PARTICIPE_CPF);
      }
      else {
        authenticatedBy.setEmail(dto.getLoginEmail());
        authenticatedBy.setAuthType(PARTICIPE_EMAIL);
      }
    }
    else {
      authenticatedBy.setEmail(dto.getLoginEmail());
    }
  }

  private boolean isLoginByCpf(AuthenticationProfileDto dto) {
    return dto.getLoginEmail().contains("@cpf");
  }

  private static class UpdatePasswordDto {
    final String password;
    final String confirmPassword;

    public UpdatePasswordDto(PersonProfileUpdateDto dto) {
      this.password = dto.getNewPassword();
      this.confirmPassword = dto.getConfirmNewPassword();
    }

    public boolean havePasswordToUpdate() {
      return password != null && confirmPassword != null;
    }

    public boolean validate() {
      if(this.havePasswordToUpdate()) {
        if(!this.password.equals(this.confirmPassword)) {
          throw new IllegalStateException("The password and your confirmation don't match!");
        }
        int size = this.password.length();

        if(size < 6 || !this.password.matches(VALID_CHARACTERS)) {
          throw new IllegalStateException("Password is not valid");
        }
        return true;
      }
      return false;
    }
  }
}
