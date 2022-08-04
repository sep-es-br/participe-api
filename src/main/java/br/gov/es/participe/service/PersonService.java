package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.*;
import br.gov.es.participe.util.domain.ProfileType;
import br.gov.es.participe.util.domain.TokenType;
import br.gov.es.participe.util.dto.MessageDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@SuppressWarnings({ "unused" })
public class PersonService {

  private static final String PERSON_ERROR_NOT_FOUND = "person.error.not-found";
  private static final String PERSON_ERROR_CONFERENCE_NOT_SPECIFIED = "person.error.conference-not-specified";
  private static final String PERSON_ERROR_SELFDECLARATION_NOT_SPECIFIED = "person.error.selfdeclaration-not-specified";
  private static final String PERSON_ERROR_CPF_NOT_INFORMED = "person.error.cpf-not-informed";
  private static final String PERSON_ERROR_INVALID_PASS = "person.error.invalid-password";
  private static final String PERSON_ERROR_PASS_NOT_MATCHING = "person.error.password-not-matching";
  private static final String PERSON_ERROR_EMAILS_NOT_MATCHING = "person.error.emails-not-matching";
  private static final String PERSON_ERROR_CPF_ALREADY_STORED = "person.error.cpf-already-stored";
  private static final String PERSON_ERROR_EMAIL_ALREADY_STORED = "person.error.email-already-stored";
  private static final String PERSON_ERROR_EMAIL_NOT_INFORMED = "person.error.email-not-informed";
  private static final String PERSON_ERROR_PASS_LACKING_CHARACTERS = "person.error.password-lacking-characters";
  private static final String PERSON_ERROR_MEETING_ID_NOT_SPECIFIED = "person.error.meeting-id-not-specified";
  private static final String PARTICIPE = "Participe";
  private static final String SERVER = "Participe";
  private static final String ACESSOCIDADAO = "AcessoCidadao";
  private static final String OAUTH = "oauth";
  private static final String TITLE = "title";
  private static final String SUBTITLE = "subtitle";
  private static final String NAME = "name";
  private static final String PASS_PARAM = "password";
  private static final String EMAIL_RESPONSE_MESSAGE = " - Confirme seu Email";
  private static final String VALID_CHARACTERS = "[A-Za-z0-9]+";

  @Autowired
  private PersonRepository personRepository;

  @Autowired
  private SelfDeclarationService selfDeclarationService;

  @Autowired
  private IsAuthenticatedByRepository isAuthenticatedByRepository;

  @Autowired
  private AuthServiceRepository authServiceRepository;

  @Autowired
  private AttendRepository attendRepository;

  @Autowired
  private EmailService emailService;

  @Autowired
  private ConferenceService conferenceService;

  @Autowired
  private LoginRepository loginRepository;

  @Autowired
  private TokenService tokenService;

  @Autowired
  private AcessoCidadaoService acessoCidadaoService;

 
  

  public Boolean forgotPassword(String email, Long conferenceId, String server) {
    Optional<Person> person = this.havePersonWithLoginEmail(email, server, null);

    if (!person.isPresent()) {
      return false;
    }

    Conference conference = conferenceService.find(conferenceId);
    String password = generateTemporaryPassword();

    List<IsAuthenticatedBy> auth = isAuthenticatedByRepository.findAllByIdPerson(person.get().getId());

    for (IsAuthenticatedBy a : auth) {
      if (a.getName().equals(server)) {
        a.setPassword(password);
        a.setTemporaryPassword(true);
        isAuthenticatedByRepository.save(a);
        break;
      }
    }

    HashMap<String, String> model = new HashMap<>();

    model.put(TITLE, conference.getTitleAuthentication());
    model.put(SUBTITLE, conference.getSubtitleAuthentication());
    model.put(NAME, person.get().getName());
    model.put(PASS_PARAM, password);

    sendEmail(person.get().getContactEmail(), conference.getTitleAuthentication() + EMAIL_RESPONSE_MESSAGE, model);

    return true;
  }

  public Optional<Person> havePersonWithLoginEmail(String email, String server, String cpf) {
    return this.personRepository.havePersonWithLoginEmail(email, server, cpf);
  }

  public SigninDto authenticate(PersonParamDto user, String server, Long conferenceId) {
    String userLogin = user.getLogin().contains("@") ? user.getLogin() : user.getLogin() + "@cpf";

    Optional<Person> optionalPerson = this.havePersonWithLoginEmail(userLogin, SERVER, null);

    if (!optionalPerson.isPresent()) {
      return null;
    }

    final Person person = optionalPerson.get();

    if (person.getActive() != null && !person.getActive()) {
      return new SigninDto(person, SERVER, null, null);
    }

    String authenticationToken = tokenService.generateToken(person, TokenType.AUTHENTICATION);
    String refreshToken = tokenService.generateToken(person, TokenType.REFRESH);

    person.setAccessToken(authenticationToken);

    SigninDto signInDto = new SigninDto(person, SERVER, authenticationToken, refreshToken);
    signInDto.setCompleted(true);

    List<IsAuthenticatedBy> auth = isAuthenticatedByRepository.findAllByIdPerson(person.getId());

    for (IsAuthenticatedBy a : auth) {
      if (a.getName().equals(server)) {
        if (!user.getPassword().equals(a.getPassword())) {
          return null;
        }
        signInDto.setTemporaryPassword(a.getTemporaryPassword());
      }
    }

    createRelationshipWithAuthService(
        new RelationshipAuthServiceAuxiliaryDto.RelationshipAuthServiceAuxiliaryDtoBuilder(person)
            .password(user.getPassword())
            .server(server)
            .serverId(person.getId().toString())
            .conferenceId(conferenceId)
            .resetPassword(false)
            .makeLogin(true)
            .build());

    return signInDto;
  }

  @Transactional
  public Person save(Person person, boolean isLike) {
    if (!isLike) {
      String loginEmail = person.getContactEmail();
      Person personBD = personRepository.findPersonByParticipeAuthServiceEmailOrCpf(loginEmail, person.getCpf());

      if (personBD != null) {
        return personBD;
      }
    }

    return personRepository.save(person);
  }

  @Transactional
  public Person createRelationshipWithAuthService(
      RelationshipAuthServiceAuxiliaryDto relationshipAuthServiceAuxiliaryDto) {
    return createRelationshipWithAuthService(relationshipAuthServiceAuxiliaryDto, true);
  }

  public Person createRelationshipWithAuthServiceWithoutPersist(
      RelationshipAuthServiceAuxiliaryDto relationshipAuthServiceAuxiliaryDto) {
    return createRelationshipWithAuthService(relationshipAuthServiceAuxiliaryDto, false);
  }

  private Person createRelationshipWithAuthService(
      RelationshipAuthServiceAuxiliaryDto relationshipAuthServiceAuxiliaryDto,
      Boolean persistRelationship) {
    Person person = relationshipAuthServiceAuxiliaryDto.getPerson();
    Long conferenceId = relationshipAuthServiceAuxiliaryDto.getConferenceId();
    String server = relationshipAuthServiceAuxiliaryDto.getServer();
    String serverId = relationshipAuthServiceAuxiliaryDto.getServerId();
    Boolean makeLogin = relationshipAuthServiceAuxiliaryDto.getMakeLogin();
    String password = relationshipAuthServiceAuxiliaryDto.getPassword();
    String typeAuthentication = relationshipAuthServiceAuxiliaryDto.getTypeAuthentication();
    Boolean resetPassword = relationshipAuthServiceAuxiliaryDto.getResetPassword();

    AuthService authService = null;
    Conference conference = null;
    boolean newAuthService = true;

    if (person.getId() == null) {
      person = personRepository.save(person);
    } else {
      loadSelfDeclaration(person, conferenceId);
    }

    IsAuthenticatedBy authenticatedBy = getIsAuthenticatedBy(person.getId(), server);

    if (authenticatedBy != null) {
      authService = authenticatedBy.getAuthService();
      newAuthService = false;
    }

    if (conferenceId != null) {
      conference = conferenceService.find(conferenceId);
    }

    if (newAuthService) {
      authService = new AuthService();
      authService.setServer(server);
      authService.setServerId(serverId);
      if (makeLogin) {
        authService.setNumberOfAccesses(1);
      } else {
        authService.setNumberOfAccesses(0);
      }
      if (persistRelationship) {
        authService = authServiceRepository.save(authService);
      }
      authenticatedBy = createAuthenticatedBy(
          server,
          person,
          conference,
          password,
          authService,
          typeAuthentication);
    } else {
      if (authService.getNumberOfAccesses() == null) {
        authService.setNumberOfAccesses(0);
      }
      authService
          .setNumberOfAccesses(makeLogin ? authService.getNumberOfAccesses() + 1 : authService.getNumberOfAccesses());
      if (persistRelationship) {
        authServiceRepository.save(authService);
      }
      loadAuthenticatedBy(
          authenticatedBy,
          person,
          serverId,
          password,
          conferenceId,
          typeAuthentication);
    }

    this.verifyResetPasswordCondition(
        resetPassword,
        newAuthService,
        person,
        conference,
        authenticatedBy);

    this.verifyMakeLoginCondition(person, makeLogin, authService, conferenceId);

    if (persistRelationship) {
      isAuthenticatedByRepository.save(authenticatedBy, 0);
    }
    return person;
  }

  private void verifyResetPasswordCondition(Boolean resetPassword, Boolean newAuthService, Person person,
      Conference conference, IsAuthenticatedBy authenticatedBy) {
    if (resetPassword && !newAuthService) {
      resetPassword(person, conference, authenticatedBy);
    }
  }

  private void verifyMakeLoginCondition(Person person, Boolean makeLogin, AuthService authService, Long conferenceId) {
    if (makeLogin) {
      createLogin(person, authService, conferenceId);
    }
  }

  private void resetPassword(Person person, Conference conference, IsAuthenticatedBy authenticatedBy) {
    Date passwordTime = expirationTime((long) 24);
    String password = generateTemporaryPassword();
    authenticatedBy.setPassword(password);
    authenticatedBy.setPasswordTime(passwordTime);

    sendConfirmationEmail(person, conference, password);
  }

  private IsAuthenticatedBy createAuthenticatedBy(
      String server,
      Person person,
      Conference conference,
      String password,
      AuthService authService,
      String typeAuthentication) {
    IsAuthenticatedBy authenticatedBy;
    if (server.equalsIgnoreCase(PARTICIPE)) {
      if (typeAuthentication != null && typeAuthentication.equals("cpf")) {
        authenticatedBy = new IsAuthenticatedBy(PARTICIPE, "participeCpf", password,
            false, null, person, authService);
      } else {
        Date passwordTime = expirationTime((long) 24);

        password = generateTemporaryPassword();

        authenticatedBy = new IsAuthenticatedBy(PARTICIPE, "participeEmail", password, true,
            passwordTime, person, authService);

        sendConfirmationEmail(person, conference, password);
      }
    } else {
      authenticatedBy = new IsAuthenticatedBy(server, "oauth", null, false, null, person,
          authService);
    }
    return authenticatedBy;
  }

  private void sendConfirmationEmail(Person person, Conference conference, String password) {
    HashMap<String, String> model = new HashMap<>();
    String titleAuthentication = "";
    if (conference != null) {
      titleAuthentication = conference.getTitleAuthentication();
      model.put(TITLE, conference.getTitleAuthentication());
      model.put(SUBTITLE, conference.getSubtitleAuthentication());
    }
    model.put(NAME, person.getName());
    model.put(PASS_PARAM, password);
    sendEmail(person.getContactEmail(), titleAuthentication + EMAIL_RESPONSE_MESSAGE, model);
  }

  private void loadSelfDeclaration(Person person, Long conferenceId) {
    if (conferenceId != null) {
      SelfDeclaration sd = selfDeclarationService.findByPersonAndConference(person.getId(), conferenceId);
      if (sd != null) {
        person.addSelfDeclaration(sd);
      }
    }
  }

  @Transactional
  private void createLogin(Person person, AuthService authService, Long conferenceId) {
    if (conferenceId != null) {
      Conference conference = conferenceService.find(conferenceId);
      Login login = new Login(person, authService, conference);
      loginRepository.save(login);
    }
  }

  private void loadAuthenticatedBy(
      IsAuthenticatedBy authenticatedBy,
      Person person,
      String server,
      String password,
      Long conferenceId,
      String typeAuthentication) {
    if (authenticatedBy.getPassword() != null && !authenticatedBy.getPassword().equals(password)) {
      authenticatedBy.setPassword(password);
    }
    if (authenticatedBy.getEmail() == null) {
      authenticatedBy.setEmail(person.getContactEmail());
    }
    if (authenticatedBy.getName() == null) {
      authenticatedBy.setName(server);
    }
    if (conferenceId == null) {
      authenticatedBy.setTemporaryPassword(false);
    }

    if (authenticatedBy.getAuthType() == null) {
      if (server.equalsIgnoreCase(PARTICIPE)) {
        if (typeAuthentication != null && typeAuthentication.equals("cpf")) {
          authenticatedBy.setAuthType("participeCpf");
        } else {
          authenticatedBy.setAuthType("participeEmail");
        }
      } else {
        authenticatedBy.setAuthType("oauth");
      }
    }
  }

  private IsAuthenticatedBy getIsAuthenticatedBy(Long personId, String server) {
    List<IsAuthenticatedBy> relationships = isAuthenticatedByRepository.findAllByIdPerson(personId);

    if (relationships != null && !relationships.isEmpty()) {
      for (IsAuthenticatedBy relationship : relationships)
        if (relationship.getAuthService().getServer().equalsIgnoreCase(server)) {
          return relationship;
        }
    }
    return null;
  }

  @Transactional
  public Person complement(Person person, SelfDeclaration selfDeclaration) {
    Person personBD = this.havePersonWithLoginEmail(person.getContactEmail(), null, null).orElse(null);

    if (personBD == null) {
      personBD = personRepository.save(person);
    } else {
      personBD.setTelephone(person.getTelephone());
      personRepository.save(personBD);
    }

    if (selfDeclaration.getPerson() == null) {
      selfDeclaration.setPerson(personBD);
    } else {
      selfDeclaration.getPerson().setId(personBD.getId());
    }

    selfDeclarationService.save(selfDeclaration);

    return find(personBD.getId());
  }

  public Person findRelationships(Long id) {
    return personRepository.findRelationships(id);
  }

  public Person find(Long id) {
    return personRepository.findById(id).orElseThrow(
        () -> new IllegalArgumentException(PERSON_ERROR_NOT_FOUND));
  }

  public PersonKeepCitizenDto findCitizenById(Long personId, Long conferenceId) {
    Person person = Optional.ofNullable(find(personId))
        .orElseThrow(() -> new IllegalArgumentException(PERSON_ERROR_NOT_FOUND));

    List<LoginAccessDto> loginAccessDtos = personRepository.findAccessByPerson(conferenceId, personId);

    if (loginAccessDtos == null) {
      loginAccessDtos = new ArrayList<>();
      loginAccessDtos.add(new LoginAccessDto(SERVER, 0L));
    }

    SelfDeclaration selfDeclaration = this.selfDeclarationService.findByPersonAndConference(personId, conferenceId);

    final Boolean receiveInformational = Optional.ofNullable(selfDeclaration)
        .map(SelfDeclaration::getReceiveInformational).orElse(null);
    PersonKeepCitizenDto personCitizen = getPersonKeepCitizenDto(person, receiveInformational);

    personCitizen.setAutentication(loginAccessDtos);

    long total = loginAccessDtos.stream().mapToLong(LoginAccessDto::getAcesses).sum();
    personCitizen.setNumberOfAcesses(total);

    LocalityInfoDto recentLocality = personRepository.findLocalityByPersonAndConference(conferenceId, personId);

    if (recentLocality != null) {
      personCitizen.setLocalityId(recentLocality.getLocalityId());
      personCitizen.setLocalityName(recentLocality.getLocalityName());
    }

    isAuthenticatedByRepository.findAllByIdPerson(personId)
        .stream()
        .filter(a -> a.getName().equals(SERVER))
        .map(IsAuthenticatedBy::getPassword)
        .forEach(personCitizen::setPassword);

    personCitizen.setActive(person.getActive() == null || person.getActive());

    return personCitizen;
  }

  private PersonKeepCitizenDto getPersonKeepCitizenDto(Person person, Boolean receiveInformational) {
    PersonKeepCitizenDto personCitizen = new PersonKeepCitizenDto();
    personCitizen.setId(person.getId());
    personCitizen.setCpf(person.getCpf());
    personCitizen.setName(person.getName());
    personCitizen.setEmail(person.getContactEmail());
    personCitizen.setTelephone(person.getTelephone());
    personCitizen.setReceiveInformational(
        receiveInformational != null ? receiveInformational : true);
    personCitizen.setTypeAuthentication("mail");
    if (person.getCpf() != null && person.getContactEmail() != null
        && person.getContactEmail().startsWith(person.getCpf())) {
      personCitizen.setTypeAuthentication("cpf");
    }
    return personCitizen;
  }

  public List<Person> findAll() {
    List<Person> persons = new ArrayList<>();

    personRepository.findAll()
        .iterator()
        .forEachRemaining(persons::add);

    return persons;
  }

  @Transactional
  public void delete(Long id) {
    Person person = findRelationships(id);
    List<IsAuthenticatedBy> relationships = isAuthenticatedByRepository.findAllByIdPerson(
        person.getId());

    if (person.getSelfDeclaretions() != null && !person.getSelfDeclaretions().isEmpty()) {
      for (SelfDeclaration self : person.getSelfDeclaretions())
        selfDeclarationService.delete(self.getId());
    }

    if (relationships != null && !relationships.isEmpty()) {
      for (IsAuthenticatedBy relationship : relationships) {
        authServiceRepository.delete(relationship.getAuthService());
        isAuthenticatedByRepository.delete(relationship);
      }
    }

    List<Attend> atts = attendRepository.findAllAttendByIdPerson(person.getId());

    if (atts != null) {
      attendRepository.deleteAll(atts);
    }

    List<Login> logins = loginRepository.findAllByPerson(id);
    if (logins != null) {
      loginRepository.deleteAll(logins);
    }
    personRepository.delete(person);
  }

  public boolean validate(String email, String cpf, String server) {
    return !personRepository.havePersonWithLoginEmail(email, server, cpf).isPresent();
  }

  public Person likesComments(Long idPerson) {
    return personRepository.likesComments(idPerson);
  }

  public Person findPersonMadeByIdComment(Long idComment) {
    return personRepository.findPersonMadeByIdComment(idComment);
  }

  public List<Person> findPersonLikedByIdComment(Long idComment) {
    return personRepository.findPersonLikedByIdComment(idComment);
  }

  public Optional <Person> findByContactEmail(String email) {
    return personRepository.findByContactEmail(email);
  }

  public Optional <Person> findByCpf(String cpf) {
    return personRepository.findByCpf(cpf);
  }

  public Optional<Person> findByLoginEmail(String email) {
    return personRepository.findByLoginEmail(email);
  }

  private Date expirationTime(Long hours) {
    Date in = new Date();
    LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault()).plusHours(hours);
    return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
  }

  private String generateTemporaryPassword() {
    String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
    String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
    String numbers = RandomStringUtils.randomNumeric(2);
    String totalChars = RandomStringUtils.randomAlphanumeric(2);
    String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
        .concat(numbers)
        .concat(totalChars);
    List<Character> pwdChars = combinedChars.chars()
        .mapToObj(c -> (char) c)
        .collect(Collectors.toList());
    Collections.shuffle(pwdChars);

    return pwdChars.stream()
        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
        .toString();
  }

  private void sendEmail(String to, String title, HashMap<String, String> data) {
    emailService.sendEmail(to, title, data);
  }

  @Transactional
  public Page<PersonKeepCitizenDto> listKeepCitizen(String name, String email, String authentication, Boolean active,
      List<Long> locality, Long conferenceId, Pageable page) {
    if (conferenceId == null) {
      throw new IllegalArgumentException(PERSON_ERROR_CONFERENCE_NOT_SPECIFIED);
    }

    Page<PersonKeepCitizenDto> response = personRepository
        .findPersonKeepCitizen(name, email, authentication,
            active, locality, page);

    response.forEach(element -> {
      List<LoginAccessDto> loginAccessDtos = personRepository.findAccessByPerson(
          conferenceId,
          element.getId());
      if (loginAccessDtos == null || loginAccessDtos.isEmpty()) {
        loginAccessDtos = new ArrayList<>();
        loginAccessDtos.add(new LoginAccessDto(SERVER, 0L));
      }
      element.setAutentication(loginAccessDtos);

      long total = 0;
      for (LoginAccessDto loginDto : loginAccessDtos) {
        total = total + loginDto.getAcesses();
      }
      element.setNumberOfAcesses(total);

      LocalityInfoDto recentLocality = personRepository.findLocalityByPersonAndConference(
          conferenceId, element.getId());
      if (recentLocality != null) {
        element.setLocalityId(recentLocality.getLocalityId());
        element.setLocalityName(recentLocality.getLocalityName());
      }
    });

    return response;
  }

  public Person storePersonOperator(PersonParamDto personParam, String profile) throws IOException {
    final boolean emailsIncompativeis = (personParam.getContactEmail() != null && personParam.getConfirmEmail() != null)
        &&
        !personParam.getContactEmail().equals(personParam.getConfirmEmail());

    if (emailsIncompativeis) {
      MessageDto msg = new MessageDto();

      msg.setMessage("E-mails Incompatíveis");
      msg.setCode(422);

      return null;
    }

    ProfileType profileType = (profile.equalsIgnoreCase("Administrator")) ? ProfileType.ADMINISTRATOR
        : (profile.equalsIgnoreCase("Moderator")) ? ProfileType.MODERATOR
            : (profile.equalsIgnoreCase("Recepcionist")) ? ProfileType.RECEPCIONIST : null;

    List<PersonDto> personList = acessoCidadaoService.listPersonsByPerfil(profileType, "",
        personParam.getContactEmail());

    if (personList == null || personList.size() == 0) {

      return null;

    }

    Person person = this.save(new Person(personParam, false), false);

    this.createRelationshipWithAuthService(
        new RelationshipAuthServiceAuxiliaryDto.RelationshipAuthServiceAuxiliaryDtoBuilder(person)
            .password(null)
            .server(ACESSOCIDADAO)
            .serverId(personList.get(0).getSub())
            .conferenceId(null)
            .resetPassword(false)
            .makeLogin(false)
            .typeAuthentication(OAUTH)
            .build());

    return person;

  }

  public ResponseEntity<?> storePerson(PersonParamDto personParam,Boolean makeLogin) {
   
    if (personParam.getId() == null ){
      if(personParam.getCpf() != null){
       Optional <Person> personCpf = findByCpf(personParam.getCpf());
        if(personCpf.isPresent()){
          String nome= "Usuário já cadastrado com CPF: "+personCpf.get().getName();
          throw new IllegalArgumentException(nome);
        }
      }

      if(personParam.getContactEmail() != null){
       Optional <Person> personEmail = findByContactEmail(personParam.getContactEmail());
       
        if(personEmail.isPresent()){
          String nome= "Usuário já cadastrado com e-mail: "+personEmail.get().getName();
          throw new IllegalArgumentException(nome);
        }
      }
    }
    
    final boolean emailsIncompativeis = (personParam.getContactEmail() != null && personParam.getConfirmEmail() != null)
        &&
        !personParam.getContactEmail().equals(personParam.getConfirmEmail());

    if (emailsIncompativeis) {
      MessageDto msg = new MessageDto();

      msg.setMessage("E-mails Incompatíveis");
      msg.setCode(422);

      return ResponseEntity.status(422).body(msg);
    }

    boolean notHaveParticipeLoginInThisConference = this.validate(personParam.getContactEmail(), null, SERVER);
    Boolean typeAuthenticationCpf = storePersonValidation(personParam);

    if (notHaveParticipeLoginInThisConference) {

      Person person = createParticipeLogin(personParam, makeLogin, typeAuthenticationCpf);
      return ResponseEntity.status(200).body(new PersonDto(person));
    }

    MessageDto msg = new MessageDto();

    if (personParam.getTypeAuthentication() != null && personParam.getTypeAuthentication().equals("cpf")) {
      msg.setMessage("O CPF informado já existe para outro usuário");
    } else {
      msg.setMessage("E-mail já cadastrado");
    }

    msg.setCode(400);

    return ResponseEntity.status(400).body(msg);
  }


  private Person createParticipeLogin(PersonParamDto personParam, Boolean makeLogin, Boolean typeAuthenticationCpf) {
    Person person = this.save(new Person(personParam, typeAuthenticationCpf), false);

    final Long id = person.getId();
    final SelfDeclarationParamDto paramSelfDeclaration = personParam.getSelfDeclaration();
    final Long conference = paramSelfDeclaration.getConference();

    Objects.requireNonNull(paramSelfDeclaration, PERSON_ERROR_SELFDECLARATION_NOT_SPECIFIED);
    Objects.requireNonNull(conference, "Conference id must be not null");
    Objects.requireNonNull(paramSelfDeclaration.getLocality(), "Locality id must be not null");
    Objects.requireNonNull(id, "Person id must be not null");

    final SelfDeclaration declaration = this.selfDeclarationService.findByPersonAndConference(id, conference);
    Optional<SelfDeclaration> selfDeclarationOptional = Optional.ofNullable(declaration);

    SelfDeclaration selfDeclaration;

    if (selfDeclarationOptional.isPresent()) {
      selfDeclaration = this.selfDeclarationService.updateLocality(selfDeclarationOptional.get(),
          paramSelfDeclaration.getLocality());
    } else {
      selfDeclaration = new SelfDeclaration(conference, paramSelfDeclaration.getLocality(), id);
      selfDeclaration.setReceiveInformational(
          personParam.isReceiveInformational() != null && personParam.isReceiveInformational());
      selfDeclaration.setAnswerSurvey(false);
      selfDeclaration = this.selfDeclarationService.save(selfDeclaration);
    }

    Objects.requireNonNull(selfDeclaration.getId(), "Failed to create or update self declaration");

    this.createRelationshipWithAuthService(
        new RelationshipAuthServiceAuxiliaryDto.RelationshipAuthServiceAuxiliaryDtoBuilder(person)
            .password(personParam.getPassword())
            .server(SERVER)
            .serverId(id.toString())
            .conferenceId(selfDeclaration.getConference().getId())
            .resetPassword(false)
            .makeLogin(makeLogin)
            .typeAuthentication(personParam.getTypeAuthentication())
            .build());
    return person;
  }

  private Boolean storePersonValidation(PersonParamDto personParam) {
    if (personParam.getTypeAuthentication() != null && personParam.getTypeAuthentication().equals("cpf")) {
      typeAuthenticationCpfValidation(personParam);
      personParam.setContactEmail(personParam.getCpf() + "@cpf");
      personParam.setConfirmEmail(personParam.getCpf() + "@cpf");
      return true;
    }

    return false;
  }

  public ResponseEntity<?> updatePerson(PersonParamDto personParam, Boolean makeLogin) {
    this.verifyTypeAuthentication(personParam);

    Person person = this.find(personParam.getId());

    if (!personParam.getContactEmail().equals(personParam.getConfirmEmail())) {
      throw new IllegalArgumentException(PERSON_ERROR_EMAILS_NOT_MATCHING);
    }

    final boolean cpfAlreadyStored = personParam.getTypeAuthentication() != null
        && personParam.getTypeAuthentication().equals("cpf");
    if (!person.getContactEmail().equals(personParam.getContactEmail()) &&
        !this.validate(personParam.getContactEmail(), null, SERVER)) {
      MessageDto msg = new MessageDto();

      if (cpfAlreadyStored) {
        msg.setMessage(PERSON_ERROR_CPF_ALREADY_STORED);
      } else {
        msg.setMessage(PERSON_ERROR_EMAIL_ALREADY_STORED);
      }
      msg.setCode(400);

      return ResponseEntity.status(400).body(msg);
    }

    person.setCpf(personParam.getCpf());

    if (cpfAlreadyStored) {
      person.setContactEmail(personParam.getCpf() + "@cpf");
    } else {
      person.setContactEmail(personParam.getConfirmEmail());
    }

    person.setActive(personParam.getActive());
    person.setTelephone(personParam.getTelephone());
    person.setName(personParam.getName());

    SelfDeclaration sd = selfDeclarationService
        .findByPersonAndConference(person.getId(), personParam.getSelfDeclaration().getConference());

    if (sd == null) {
      Person personSdCreation = new Person();
      Conference conferenceSdCreation = new Conference();
      Locality localitySdCreation = new Locality();

      personSdCreation.setId(person.getId());
      conferenceSdCreation.setId(personParam.getSelfDeclaration().getConference());
      localitySdCreation.setId(personParam.getSelfDeclaration().getLocality());
      SelfDeclaration selfDeclaration = new SelfDeclaration(conferenceSdCreation,
          localitySdCreation, personSdCreation);
      selfDeclaration.setReceiveInformational(personParam.isReceiveInformational());
      selfDeclaration.setAnswerSurvey(false);
      sd = selfDeclarationService.save(selfDeclaration);
    } else if (!personParam.getSelfDeclaration().getLocality().equals(sd.getLocality().getId())) {
      sd.setReceiveInformational(personParam.isReceiveInformational());
      sd = selfDeclarationService.updateLocality(
          sd,
          personParam.getSelfDeclaration().getLocality());
    } else {
      sd.setReceiveInformational(personParam.isReceiveInformational());
      sd = this.selfDeclarationService.save(sd);
    }

    PersonDto response = new PersonDto(
        person,
        sd.getReceiveInformational());

    this.createRelationshipWithAuthService(
        new RelationshipAuthServiceAuxiliaryDto.RelationshipAuthServiceAuxiliaryDtoBuilder(person)
            .password(personParam.getPassword())
            .server(SERVER)
            .serverId(person.getId().toString())
            .conferenceId(personParam.getSelfDeclaration().getConference())
            .resetPassword(personParam.isResetPassword())
            .makeLogin(makeLogin)
            .typeAuthentication(personParam.getTypeAuthentication())
            .build());

    return ResponseEntity.status(200).body(response);
  }

  private void verifyTypeAuthentication(PersonParamDto personParam) {
    if (personParam.getTypeAuthentication() != null) {
      switch (personParam.getTypeAuthentication()) {
        case "mail":
          this.typeAuthenticationEmailValidation(personParam);
          break;
        case "cpf":
          this.typeAuthenticationCpfValidation(personParam);
          personParam.setContactEmail(personParam.getCpf() + "@cpf");
          personParam.setConfirmEmail(personParam.getCpf() + "@cpf");
          break;
        default:
          break;
      }
    }
  }

  private void typeAuthenticationEmailValidation(PersonParamDto personParam) {
    if (personParam.getContactEmail() == null || personParam.getConfirmEmail().isEmpty()) {
      throw new IllegalArgumentException(PERSON_ERROR_EMAIL_NOT_INFORMED);
    }
  }

  private void typeAuthenticationCpfValidation(PersonParamDto personParam) {
    if (personParam.getCpf() == null || personParam.getCpf().isEmpty()) {
      throw new IllegalArgumentException(PERSON_ERROR_CPF_NOT_INFORMED);
    }
    int size = personParam.getPassword() != null ? personParam.getPassword().length() : 0;
    if (personParam.getPassword() == null || size < 6 || !personParam.getPassword().matches(VALID_CHARACTERS)) {
      throw new IllegalArgumentException(PERSON_ERROR_PASS_LACKING_CHARACTERS);
    }
    if (personParam.getConfirmPassword() == null
        || !personParam.getPassword().equals(personParam.getConfirmPassword())) {
      throw new IllegalArgumentException(PERSON_ERROR_PASS_NOT_MATCHING);
    }
  }

  public ResponseEntity<?> updatePerson(String token, PersonParamDto personParam, Boolean makeLogin) {
    String[] keys = token.split(" ");
    Long idPerson = tokenService.getPersonId(keys[1], TokenType.AUTHENTICATION);

    if (personParam.getConfirmPassword().equals(personParam.getPassword())) {
      int size = personParam.getPassword().length();
      if (size >= 6 && personParam.getPassword().matches(VALID_CHARACTERS)) {
        Person person = this.find(idPerson);
        PersonDto response = new PersonDto(person);

        this.createRelationshipWithAuthService(
            new RelationshipAuthServiceAuxiliaryDto.RelationshipAuthServiceAuxiliaryDtoBuilder(
                person)
                .password(personParam.getPassword())
                .server(SERVER)
                .serverId(idPerson.toString())
                .resetPassword(false)
                .makeLogin(makeLogin)
                .typeAuthentication(
                    personParam.getTypeAuthentication())
                .build());

        return ResponseEntity.status(200).body(response);
      }
      MessageDto msg = new MessageDto();
      msg.setMessage(PERSON_ERROR_PASS_LACKING_CHARACTERS);
      msg.setCode(403);
      return ResponseEntity.status(403).body(msg);
    }
    MessageDto msg = new MessageDto();
    msg.setMessage(PERSON_ERROR_PASS_NOT_MATCHING);
    msg.setCode(422);
    return ResponseEntity.status(422).body(msg);
  }

  public Page<PersonMeetingDto> findPersonForMeeting(Long meetingId, String name, Pageable pageable) {
    if (meetingId == null) {
      throw new IllegalArgumentException(PERSON_ERROR_MEETING_ID_NOT_SPECIFIED);
    }

    Page<PersonMeetingDto> personMeetingDtoPage = personRepository.findPersonForMeeting(meetingId, name, pageable);

    personMeetingDtoPage.forEach(element -> {
      element.setAuthTypeCpf(element.getEmail().endsWith("@cpf"));

      LocalityRegionalizableDto localityInfo = personRepository.findMostRecentLocality(element.getPersonId(),
          meetingId);

      if (localityInfo == null) {
        localityInfo = personRepository.findLocalityIfThereIsNoLogin(element.getPersonId(), meetingId);
      }

      if (localityInfo != null) {
        element.setLocality(localityInfo.getLocality());
        element.setRegionalizable(localityInfo.getRegionalizable());
        element.setSuperLocality(localityInfo.getSuperLocality());
        element.setSuperLocalityId(localityInfo.getSuperLocalityId());
      }
    });

    return personMeetingDtoPage;
  }

  public Page<PersonMeetingDto> findPersonsCheckedInOnMeeting(Long meetingId, List<Long> localities, String name,
      Pageable pageable) {
    if (meetingId == null) {
      throw new IllegalArgumentException(PERSON_ERROR_MEETING_ID_NOT_SPECIFIED);
    }

    Page<PersonMeetingDto> personMeetingDtoPage = personRepository.findPersonsCheckedInOnMeeting(
        meetingId,
        localities, name, pageable);

    personMeetingDtoPage.forEach(element -> {
      LocalityRegionalizableDto localityInfo;
      localityInfo = personRepository.findMostRecentLocality(
          element.getPersonId(),
          meetingId);
      if (localityInfo == null) {
        localityInfo = personRepository.findLocalityIfThereIsNoLogin(
            element.getPersonId(),
            meetingId);
      }
      if (localityInfo != null) {
        element.setLocality(localityInfo.getLocality());
        element.setRegionalizable(localityInfo.getRegionalizable());
        element.setSuperLocalityId(localityInfo.getSuperLocalityId());
        element.setSuperLocality(localityInfo.getSuperLocality());
      }
    });

    return personMeetingDtoPage;
  }

  public Long findPeopleQuantityOnMeeting(Long meetingId) {
    return personRepository.findPeopleQuantityOnMeeting(meetingId);
  }

  public Optional<Person> findPersonIfParticipatingOnMeetingPresentially(Long personId, Date date, Long conferenceId) {
    return personRepository.findPersonIfParticipatingOnMeetingPresentially(personId, date, conferenceId);
  }

  public Boolean hasOneOfTheRoles(String token, String[] roles) {
    Person person = this.getPerson(token);
    Boolean ret = false;
    for (int i = 0; i < roles.length; i++) {
      ret |= (person.getRoles() != null && !person.getRoles().isEmpty() && person.getRoles().contains(roles[i]));
    }
    return ret;
  }

  public Person getPerson(String token) {
    String[] keys = token.split(" ");
    Long idPerson = tokenService.getPersonId(keys[1], TokenType.AUTHENTICATION);
    return this.find(idPerson);
  }

}
