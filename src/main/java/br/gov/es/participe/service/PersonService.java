package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.*;
import br.gov.es.participe.util.domain.ProfileType;
import br.gov.es.participe.util.domain.TokenType;
import br.gov.es.participe.util.dto.MessageDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;

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

  private static final Logger log = LoggerFactory.getLogger(PersonService.class);

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

  @Autowired
  private PreRegistrationService preRegistrationService;

  @Autowired
  private CheckedInAtRepository checkedInAtRepository;

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
    log.info("Iniciando autenticação personId={}, login={}, server={} conferenceId={} ", user.getId(), user.getLogin(), server, conferenceId);
    String userLogin = user.getLogin().contains("@") ? user.getLogin() : user.getLogin() + "@cpf";


    log.info("Consultando person pelo login de email email={} no server={}, conferenceId={}", userLogin, SERVER, conferenceId);
    Optional<Person> optionalPerson = this.havePersonWithLoginEmail(userLogin, SERVER, null);

    if (!optionalPerson.isPresent()) {
      log.info("Person email={} não encontrada no server={} e conferenceId={}", userLogin, server, conferenceId);
      return null;
    }

    final Person person = optionalPerson.get();

    if (person.getActive() != null && !person.getActive()) {
      log.info("Person personId={} com active={}, criando resposta...", person.getId(), person.getActive());
      final var response = new SigninDto(person, SERVER, null, null);
      log.info(
        "Resposta criada personId={}, conferenceId={} server={}, active={}, completed={}",
        person.getId(),
        conferenceId,
        SERVER,
        person.getActive(),
        response.isCompleted()
      );
      return response;
    }

    log.info(
      "Person personId={} encontrada com active={}, gerado token e refreshToken...",
      person.getId(),
      person.getActive()
    );
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

  public Person save(Person person, boolean isLike) {
    log.info("Criando person contactEmail={}, isLike={}", person.getContactEmail(), isLike);
    if (!isLike) {
      String loginEmail = person.getContactEmail();
      Person personBD = personRepository.findPersonByParticipeAuthServiceEmailOrCpf(loginEmail, person.getCpf());

      if (personBD != null) {
        log.info("Person contactEmail={} já registrada com personId={}", loginEmail, personBD.getId());
        return personBD;
      }
    }

    return personRepository.save(person);
  }

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
    log.info(
      "Criando relacionamento com AuthService com os parâmetros {} persistRelationship={}",
      relationshipAuthServiceAuxiliaryDto.toString(),
      persistRelationship
    );
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
      log.info(
        "Person não foi criada personId={}, name={}, conferenceId={}, server={}",
        person.getId(),
        person.getName(),
        conferenceId,
        server
      );
      person = personRepository.save(person);
      log.info("Person criada com sucesso personId={}", person.getId());
    } else {
      log.info("Person personId={} informada, carregando selfDeclarations da conferenceId={}", person.getId(), conferenceId);
      loadSelfDeclaration(person, conferenceId);
    }

    IsAuthenticatedBy authenticatedBy = getIsAuthenticatedBy(person.getId(), server);

    if (authenticatedBy != null) {
      authenticatedBy.setEmail(person.getContactEmail());
      authService = authenticatedBy.getAuthService();
      newAuthService = false;
    }

    if (conferenceId != null) {
      log.info("Conference com conferenceId={} foi informada, buscando nó", conferenceId);
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
        log.info(
          "Criado AuthService authServiceId={} com atributos server={}, serverId={}, numberOfAccesses={}",
          authService.getId(),
          authService.getServer(),
          authService.getServerId(),
          authService.getNumberOfAccesses()
        );
      }
      authenticatedBy = createAuthenticatedBy(
          server,
          person,
          conference,
          password,
          authService,
          typeAuthentication
      );
    } else {
      if (authService.getNumberOfAccesses() == null) {
        authService.setNumberOfAccesses(0);
      }
      authService
          .setNumberOfAccesses(makeLogin ? authService.getNumberOfAccesses() + 1 : authService.getNumberOfAccesses());
      if (persistRelationship) {
        log.info("Alterando numberOfAccesses do authServiceId={} para numberOfAccesses={}",
          authService.getId(),
          authService.getNumberOfAccesses()
        );
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
      log.info("Alterações no relacionamento isAuthenticatedById={} persistidas com sucesso", authenticatedBy.getId());
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
    log.info("Verificando flag makeLogin={} para a personId={}, authService={}, conferenceId={}",
             makeLogin,
             person.getId(),
             authService.getId(),
             conferenceId
    );
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
    // TODO: verificar possível problema, se conferenceId == null a selfDeclaration nunca será adicionada à Person
    if (conferenceId == null) {
      log.info(
        "Não foi informado uma Conference para consultar a SelfDeclaration da personId={}",
        person.getId()
      );
    }
    log.info("Carregando SelfDeclaration person personId={} com conferenceId={}", person.getId(), conferenceId);
    SelfDeclaration sd = selfDeclarationService.findByPersonAndConference(person.getId(), conferenceId);
    if (sd != null) {
      log.info("SelfDeclaration com selfDeclarationId={} encontrada para a conferenceId={} e personId={}, adicionando à personId={}",
              sd.getId(),
              conferenceId,
              person.getId(),
              person.getId()
      );
      person.addSelfDeclaration(sd);
    }
  }

  private void createLogin(Person person, AuthService authService, Long conferenceId) {
    log.info("Iniciando criação de um registro de Login para personId={}, authServiceId={}, server={}, conferenceId={}",
             person.getId(),
             authService.getId(),
             authService.getServer(),
             conferenceId
    );
    if (conferenceId == null) {
      log.info(
        "Não foi informado uma Conference para a criação do Login com os parâmetros personId={}, authServiceId={}, server={} retornando...",
        person.getId(),
        authService.getId(),
        authService.getServer()
      );
      return;
    }
    Conference conference = conferenceService.find(conferenceId);
    Login login = new Login(person, authService, conference);
    loginRepository.save(login);
    log.info(
      "Registro de login loginId={} criado com parâmetros personId={}, authServiceId={} e conferenceId={}",
      login.getId(),
      login.getPerson().getId(),
      login.getAuthService().getId(),
      login.getConference().getId()
    );
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
    log.info("IsAuthenticatedBy relationshipId={} carregado com atributos email={}, name={} e authType={}",
      authenticatedBy.getId(),
      authenticatedBy.getEmail(),
      authenticatedBy.getName(),
      authenticatedBy.getAuthType()
    );
  }

  public IsAuthenticatedBy getIsAuthenticatedBy(Long personId, String server) {
    List<IsAuthenticatedBy> relationships = isAuthenticatedByRepository.findAllByIdPerson(personId);

    log.info("Foram encontradas {} relacionamentos de IsAuthenticatedBy para a personId={}",
        Optional.ofNullable(relationships).map(List::size).orElse(0),
        personId
    );

    if (relationships != null && !relationships.isEmpty()) {
      for (IsAuthenticatedBy relationship : relationships)
        if (relationship.getAuthService().getServer().equalsIgnoreCase(server)) {
          log.info("Foi encontrado um IsAuthenticatedBy relationshipId={} relacionado a personId={} e server={}",
                  relationship.getId(),
                  personId,
                  server
          );
          return relationship;
        }
    }
    log.info("Não foi encontrado um IsAuthenticatedBy relacionado a personId={} e server={}",
            personId,
            server
    );
    return null;
  }


  public Person complement(Person person, SelfDeclaration selfDeclaration) {
    log.info("Iniciando complemento do cadastro da personId={}, email={}", person.getId(), person.getContactEmail());
    Person personBD = this.havePersonWithLoginEmail(person.getContactEmail(), null, null).orElse(null);
    personBD.setReceiveInformational(person.getReceiveInformational());

    if (personBD == null) {
      log.info("Não foi encontrado uma person com personId={}, email={}", person.getId(), person.getContactEmail());
      personBD = personRepository.save(person);
      log.info(
        "Person com email={} criada com sucesso com personId={}",
        person.getContactEmail(),
        person.getId()
      );
    } else {
      log.info("Foi encontrado uma person com personId={}, email={}", person.getId(), person.getContactEmail());
      personBD.setTelephone(person.getTelephone());
      personRepository.save(personBD);
      log.info(
        "Atributo telephone da personId={} alterado para telephone={} com sucesso",
        personBD.getId(),
        personBD.getTelephone()
      );
    }

    if (selfDeclaration.getPerson() == null) {
      log.info("Criando vinculo entre personId={} e selfDeclarationId={}", personBD.getId(), selfDeclaration.getId());
      selfDeclaration.setPerson(personBD);
    } else {
      log.info(
        "Alterando person do selfDeclarationId={} de oldPersonId={} para newPersonId={}",
        selfDeclaration.getId(),
        selfDeclaration.getPerson().getId(),
        personBD.getId()
      );
      selfDeclaration.getPerson().setId(personBD.getId());
    }

    log.info("Persistindo alterações da selfDeclarationId={}", selfDeclaration.getId());
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

  public PersonKeepCitizenDto findCitizenById(Long personId, Long conferenceId, Long meetingId, Boolean isEdit) {
    Person person = Optional.ofNullable(find(personId))
        .orElseThrow(() -> new IllegalArgumentException(PERSON_ERROR_NOT_FOUND));
    log.info("PersonId={} encontrada", person.getId());

    String authName = "";
    List<LoginAccessDto> loginAccessDtos = personRepository.findAccessByPerson(conferenceId, personId, authName);

    if (loginAccessDtos == null) {
      loginAccessDtos = new ArrayList<>();
      loginAccessDtos.add(new LoginAccessDto(SERVER, 0L));
    }
    log.info("Foram encontrados {} registros de acessos da personId={}", loginAccessDtos.size(), person.getId());

    SelfDeclaration selfDeclaration = this.selfDeclarationService.findByPersonAndConference(personId, conferenceId);

    PersonKeepCitizenDto personCitizen = getPersonKeepCitizenDto(person);

    
     Optional<CheckedInAt> optionalCheckIn = checkedInAtRepository.findByPersonAndMeeting(personId, meetingId);
     optionalCheckIn.ifPresentOrElse(
             checkin -> {
  
                if (checkin.getIsAuthority() != null) {
                    personCitizen.setIsAuthority(Boolean.TRUE.equals(checkin.getIsAuthority()));
                }

                if (checkin.getOrganization() != null) {
                    personCitizen.setOrganization(checkin.getOrganization());
                }

                if (checkin.getRole() != null) {
                    personCitizen.setRole(checkin.getRole());
                }
             },
             () -> {
                 PreRegistration preRegistration = preRegistrationService.findByMeetingAndPerson(meetingId, personId);
                 if (preRegistration != null) {
                    if (preRegistration.getIsAuthority() != null) {
                        personCitizen.setIsAuthority(Boolean.TRUE.equals(preRegistration.getIsAuthority()) ? true : null);
                    }

                    if (preRegistration.getOrganization() != null) {
                        personCitizen.setOrganization(preRegistration.getOrganization());
                    }

                    if (preRegistration.getRole() != null) {
                        personCitizen.setRole(preRegistration.getRole());
                    }
                }         
             });
    
     
    

    personCitizen.setAutentication(loginAccessDtos);

    long total = loginAccessDtos.stream().mapToLong(LoginAccessDto::getAcesses).sum();
    personCitizen.setNumberOfAcesses(total);

    LocalityInfoDto recentLocality = personRepository.findLocalityByPersonAndConference(conferenceId, personId);

    if (recentLocality != null) {
      log.info(
        "RecentLocality encontrado localityId={} localityName={} para personId={}",
        recentLocality.getLocalityId(),
        recentLocality.getLocalityName(),
        person.getId()
      );
      personCitizen.setLocalityId(recentLocality.getLocalityId());
      personCitizen.setLocalityName(recentLocality.getLocalityName());
    }

    personCitizen.setActive(person.getActive() == null || person.getActive());

    personCitizen.setAuthName(personRepository.findPersonAutenticated(personId));

    return personCitizen;
  }
  
  public Optional<Person> getBySubEmail(final String sub, final String acEmail){
      return personRepository.findBySubEmail(sub, acEmail)
                .flatMap(p -> personRepository.findById(p.getId()));
  }

  private PersonKeepCitizenDto getPersonKeepCitizenDto(Person person) {
    PersonKeepCitizenDto personCitizen = new PersonKeepCitizenDto();
    personCitizen.setId(person.getId());
    personCitizen.setCpf(person.getCpf());
    personCitizen.setName(person.getName());
    personCitizen.setEmail(person.getContactEmail());
    personCitizen.setTelephone(person.getTelephone());
    personCitizen.setTypeAuthentication("mail");
    personCitizen.setReceiveInformational(person.getReceiveInformational());
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


  public void delete(Long id) {
    Person person = findRelationships(id);
    List<IsAuthenticatedBy> relationships = isAuthenticatedByRepository.findAllByIdPerson(
        person.getId());
    if (person.getSelfDeclaretions() != null && !person.getSelfDeclaretions().isEmpty()) {
      log.info(
        "Removendo {} SelfDeclarations relacionadas a personId={}",
        person.getSelfDeclaretions().size(),
        person.getId()
      );
      for (SelfDeclaration self : person.getSelfDeclaretions()) {
        selfDeclarationService.delete(self.getId());
      }
    }

    if (relationships != null && !relationships.isEmpty()) {
      log.info(
        "Removendo {} relacionamentos a personId={}",
        person.getSelfDeclaretions().size(),
        person.getId()
      );
      for (IsAuthenticatedBy relationship : relationships) {
        log.info(
          "Removendo authServiceId={} e isAuthenticatedById={}",
          relationship.getAuthService().getId(),
          relationship.getId()
        );
        authServiceRepository.delete(relationship.getAuthService());
        isAuthenticatedByRepository.delete(relationship);
      }
    }

    List<Attend> atts = attendRepository.findAllAttendByIdPerson(person.getId());

    if (atts != null) {
      log.info("Removendo {} Attends relacionados ao personId={}", atts.size(), person.getId());
      attendRepository.deleteAll(atts);
    }

    List<Login> logins = loginRepository.findAllByPerson(id);
    if (logins != null) {
      log.info("Removendo {} Logins relacionados ao personId={}", logins.size(), person.getId());
      loginRepository.deleteAll(logins);
    }
    log.info("Removendo personId={}", person.getId());
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

  public Optional<Person> findByLoginSub(String sub) {
    return personRepository.findByLoginSub(sub);
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
    log.info(
      "Realizando consulta por cidadãos com parâmetros name={}, email={}, authentication={}, active={}, locality={}, conferenceId={}",
      name, email, authentication, active, locality, conferenceId
    );

      Page<PersonKeepCitizenDto> response = personRepository
          .findPersonKeepCitizen(name, conferenceId, email, authentication,
              active, locality, page);


    response.forEach(element -> {
      List<LoginAccessDto> loginAccessDtos = personRepository.findAccessByPerson(
          conferenceId,
          element.getId(),
          authentication);
      if (loginAccessDtos == null || loginAccessDtos.isEmpty()) {
        loginAccessDtos = new ArrayList<>();
        loginAccessDtos.add(new LoginAccessDto(SERVER, 0L));
      }
      element.setAutentication(loginAccessDtos);

      List<LoginAccessDto> loginAccessIconsDtos = personRepository.findAccessByPerson(null,element.getId(),authentication);
      if (loginAccessIconsDtos == null || loginAccessIconsDtos.isEmpty()) {
        loginAccessIconsDtos = new ArrayList<>();
        loginAccessIconsDtos.add(new LoginAccessDto(SERVER, 0L));
      }
      element.setAutenticationIcon(loginAccessIconsDtos);

      List<String> personConferenceList = personRepository.findPersonConferenceList(element.getId());
      element.setConferencesName(personConferenceList);

      long total = 0;
      for (LoginAccessDto loginDto : loginAccessDtos) {
        total = total + loginDto.getAcesses();
      }
      element.setNumberOfAcesses(total);

      LocalityInfoDto recentLocality = personRepository.findLastLocalityByPerson(element.getId());
      if (recentLocality != null) {
        element.setLocalityId(recentLocality.getLocalityId());
        element.setLocalityName(recentLocality.getLocalityName());
      }
    });
    log.info("Foram encontrados {} resultados", response.getSize());
    return response;
  }

  public Person storePersonOperator(PersonParamDto personParam, String profile) throws IOException {
    final boolean emailsIncompativeis =
            (personParam.getContactEmail() != null && personParam.getConfirmEmail() != null) &&
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

    List<PersonDto> personList = acessoCidadaoService.listPersonsByPerfil(
      profileType,
      "",
      personParam.getContactEmail()
    );

    if (personList == null || personList.size() == 0) {
      log.info("Não foi encontrado um usuário com o perfil={} e email={}", profileType, personParam.getContactEmail());
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
       Optional<Person> personEmail = findByContactEmail(personParam.getContactEmail());

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

    boolean notHaveAcessoCidadaoLoginInThisConference = this.validate(personParam.getContactEmail(), null, ACESSOCIDADAO);
    Boolean typeAuthenticationCpf = storePersonValidation(personParam);
    log.info(
      "notHaveAcessoCidadaoLoginInThisConference={} para person={}",
      notHaveAcessoCidadaoLoginInThisConference,
      personParam.getContactEmail()
    );
    if (notHaveAcessoCidadaoLoginInThisConference) {
      Person person = createAcessoCidadaoLogin(personParam, makeLogin, typeAuthenticationCpf);
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
  
  

  private Person createAcessoCidadaoLogin(PersonParamDto personParam, Boolean makeLogin, Boolean typeAuthenticationCpf){
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
      log.info(
        "SelfDeclaration id={} encontrada atualizando com localityId={}",
        selfDeclarationOptional.get().getId(),
        paramSelfDeclaration.getLocality()
      );
      selfDeclaration = this.selfDeclarationService.updateLocality(
              selfDeclarationOptional.get(),
          paramSelfDeclaration.getLocality()
      );
    } else {
      selfDeclaration = new SelfDeclaration(conference, paramSelfDeclaration.getLocality(), id);
      selfDeclaration.setAnswerSurvey(false);

      log.info(
        "SelfDeclaration com id={} não encontrada. Criando com localityId={}, conferenceId={}, personId={}, answerSurvey={}, receiveInformational={}",
        selfDeclaration.getId(),
        paramSelfDeclaration.getLocality(),
        conference,
        id,
        selfDeclaration.getAnswerSurvey()
      );
      selfDeclaration = this.selfDeclarationService.save(selfDeclaration);
    }

    Objects.requireNonNull(selfDeclaration.getId(), "Failed to create or update self declaration");

    this.createRelationshipWithAuthService(
        new RelationshipAuthServiceAuxiliaryDto.RelationshipAuthServiceAuxiliaryDtoBuilder(person)
            .password(null)
            .server(ACESSOCIDADAO)
            .serverId(personParam.getSub())
            .conferenceId(null)
            .resetPassword(false)
            .makeLogin(false)
            .typeAuthentication(OAUTH)
            .build());

    return person;
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
      log.info(
        "SelfDeclaration id={} encontrada atualizando com localityId={}",
        selfDeclarationOptional.get().getId(),
        paramSelfDeclaration.getLocality()
      );
      selfDeclaration = this.selfDeclarationService.updateLocality(
              selfDeclarationOptional.get(),
          paramSelfDeclaration.getLocality()
      );
    } else {
      selfDeclaration = new SelfDeclaration(conference, paramSelfDeclaration.getLocality(), id);
      selfDeclaration.setAnswerSurvey(false);

      log.info(
        "SelfDeclaration com id={} não encontrada. Criando com localityId={}, conferenceId={}, personId={}, answerSurvey={}, receiveInformational={}",
        selfDeclaration.getId(),
        paramSelfDeclaration.getLocality(),
        conference,
        id,
        selfDeclaration.getAnswerSurvey()
      );
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
    log.info("Iniciando alteração da person personId={}, makeLogin={}", personParam.getId(), makeLogin);
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
    person.setReceiveInformational(personParam.isReceiveInformational());
    log.info("Alterando personId={} atributos: active={}, telephone={}, name={} e receiveInformational{}", person.getId(), person.getActive(), person.getTelephone(), person.getName(), person.getReceiveInformational());

    SelfDeclaration sd = selfDeclarationService
        .findByPersonAndConference(person.getId(), personParam.getSelfDeclaration().getConference());

    if (sd == null) {
      log.info(
        "Não foi encontrado SelfDeclaration relacionado a conferenceId={} e personId={}",
        personParam.getSelfDeclaration().getConference(),
        person.getId()
      );
      Person personSdCreation = new Person();
      Conference conferenceSdCreation = new Conference();
      Locality localitySdCreation = new Locality();

      personSdCreation.setId(person.getId());
      conferenceSdCreation.setId(personParam.getSelfDeclaration().getConference());
      localitySdCreation.setId(personParam.getSelfDeclaration().getLocality());
      SelfDeclaration selfDeclaration = new SelfDeclaration(
        conferenceSdCreation,
        localitySdCreation,
        personSdCreation
      );
      selfDeclaration.setAnswerSurvey(false);
      sd = selfDeclarationService.save(selfDeclaration);
    } else if (!personParam.getSelfDeclaration().getLocality().equals(sd.getLocality().getId())) {
      log.info(
        "Foi encontrado uma SelfDeclaration selfDeclarationId={} para a personId={}, mas não está relacionada a localityId={} informada",
        sd.getId(),
        person.getId(),
        personParam.getSelfDeclaration().getLocality()
      );
      sd = selfDeclarationService.updateLocality(
          sd,
          personParam.getSelfDeclaration().getLocality()
      );
    } else {
      log.info(
        "Foi encontrada uma SelfDeclaration selfDeclarationId={} com os parâmetros personId={}, conferenceId={} e localityId={}",
        sd.getId(),
        person.getId(),
        personParam.getSelfDeclaration().getConference(),
        personParam.getSelfDeclaration().getLocality()
      );
      sd = this.selfDeclarationService.save(sd);
    }

    PersonDto response = new PersonDto(person);
    

    // this.createRelationshipWithAuthService(
    //     new RelationshipAuthServiceAuxiliaryDto.RelationshipAuthServiceAuxiliaryDtoBuilder(person)
    //         .password(personParam.getPassword())
    //         .server(SERVER)
    //         .serverId(person.getId().toString())
    //         .conferenceId(personParam.getSelfDeclaration().getConference())
    //         .resetPassword(personParam.isResetPassword())
    //         .makeLogin(makeLogin)
    //         .typeAuthentication(personParam.getTypeAuthentication())
    //         .build());

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
            new RelationshipAuthServiceAuxiliaryDto.RelationshipAuthServiceAuxiliaryDtoBuilder(person)
                .password(personParam.getPassword())
                .server(SERVER)
                .serverId(idPerson.toString())
                .resetPassword(false)
                .makeLogin(makeLogin)
                .typeAuthentication(personParam.getTypeAuthentication())
                .build()
        );

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
  
  public List<AuthorityMeetingDto> findAuthorityForMeeting(final Long meetingId, final String name) {
      return personRepository.findAuthorityByNameForMeeting(meetingId, name);
  }

  public Page<PersonMeetingDto> findPersonForMeeting(Long meetingId, String name, Pageable pageable,
      HttpSession session) {
    if (meetingId == null) {
      throw new IllegalArgumentException(PERSON_ERROR_MEETING_ID_NOT_SPECIFIED);
    }

    List<PersonMeetingDto> personMeetingDtoList = personRepository.findPersonByNameForMeeting(meetingId, name,null, null);

    if (!name.isEmpty()) {
      List<PublicAgentDto> publicAgentsData = (List<PublicAgentDto>) session.getAttribute("publicAgents");

      if (this.onlyNumbers(name)) {
        PublicAgentDto publicAgentDto = acessoCidadaoService.findTheAgentPublicSubByCpfInAcessoCidadaoAPI(name);

        if (publicAgentDto.getSub() != null) {
          List<PersonMeetingDto> personSubMeetingDto = personRepository.findPersonByNameForMeeting(meetingId,"", publicAgentDto.getSub(),null);
          if(!personSubMeetingDto.isEmpty()){
            return new PageImpl<>(personSubMeetingDto, pageable, personSubMeetingDto.size());
          }
          PublicAgentDto publicAgent = acessoCidadaoService.findAgentPublicBySubInAcessoCidadaoAPI(publicAgentDto.getSub());

          personSubMeetingDto = personRepository.findPersonByNameForMeeting(meetingId,"", null,publicAgent.getEmail());
          if(!personSubMeetingDto.isEmpty()){
            return new PageImpl<>(personSubMeetingDto, pageable, personSubMeetingDto.size());
          }

          PersonMeetingDto personMeetingDto = convertToPersonMeetingDto(publicAgent);
          personMeetingDtoList.add(personMeetingDto);
          return new PageImpl<>(personMeetingDtoList, pageable, personMeetingDtoList.size());
        }

        PublicAgentDto personDto = acessoCidadaoService.findSubFromPersonInAcessoCidadaoAPIByCpf(name);

        List<PersonMeetingDto> personSubMeetingDto = personRepository.findPersonByNameForMeeting(meetingId,"", personDto.getSub(),null);

        if(!personSubMeetingDto.isEmpty()){
          return new PageImpl<>(personSubMeetingDto, pageable, personSubMeetingDto.size());
        }

        personDto = acessoCidadaoService.findThePersonEmailBySubInAcessoCidadaoAPI(personDto);
        if(personDto.getEmail() != null){
          personSubMeetingDto = personRepository.findPersonByNameForMeeting(meetingId,"", null,personDto.getEmail());
          if(!personSubMeetingDto.isEmpty()){
            return new PageImpl<>(personSubMeetingDto, pageable, personSubMeetingDto.size());
          }
        }
        personDto.setName("<Novo Usuário>");
        PersonMeetingDto personMeetingDto = convertToPersonMeetingDto(personDto);
        personMeetingDtoList.add(personMeetingDto);
        return new PageImpl<>(personMeetingDtoList, pageable, personMeetingDtoList.size());

      }

      Set<String> emailSet = new HashSet<>();
      for (PersonMeetingDto personMeetingDto : personMeetingDtoList) {
        emailSet.add(personMeetingDto.getEmail());
      }
      String cleanName = cleanSimilarToApoc(name);
      String[] cleanNameList = cleanName.split(" ");
      String[] cleanEmail = name.split("@");

      if (publicAgentsData != null) {
        for (PublicAgentDto publicAgentDto : publicAgentsData) {
          String nomePublicAgent = publicAgentDto.getCleanName();
          Boolean containsName = containsAll(cleanNameList, nomePublicAgent, false);
          Boolean containsEmail = containsAll(cleanEmail, publicAgentDto.getEmail().toLowerCase(), true);
          if (containsName || containsEmail) {
            if (!emailSet.contains(publicAgentDto.getEmail())) {
              PersonMeetingDto personMeetingDto = convertToPersonMeetingDto(publicAgentDto);
              personMeetingDtoList.add(personMeetingDto);
              emailSet.add(publicAgentDto.getEmail());
            }
          }
        }
      }
      Collections.sort(personMeetingDtoList, new Comparator<PersonMeetingDto>() {
        @Override
        public int compare(PersonMeetingDto o1, PersonMeetingDto o2) {
          return o1.getName().compareToIgnoreCase(o2.getName());
        }
      });

      int start = (int) pageable.getOffset();
      int end = Math.min((start + pageable.getPageSize()), personMeetingDtoList.size());
      List<PersonMeetingDto> combinedSublist = personMeetingDtoList.subList(start, end);
      Page<PersonMeetingDto> combinedPage = new PageImpl<>(combinedSublist, pageable, personMeetingDtoList.size());
      return combinedPage;
    }

    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), personMeetingDtoList.size());
    List<PersonMeetingDto> sublist = personMeetingDtoList.subList(start, end);
    Page<PersonMeetingDto> personMeetingDtoPage = new PageImpl<>(sublist, pageable, personMeetingDtoList.size());
    return personMeetingDtoPage;
  }

  private PersonMeetingDto convertToPersonMeetingDto(PublicAgentDto publicAgentDto) {
    PersonMeetingDto personMeetingDto = new PersonMeetingDto();
    personMeetingDto.setSub(publicAgentDto.getSub());
    personMeetingDto.setName(publicAgentDto.getName().toLowerCase());
    personMeetingDto.setEmail(publicAgentDto.getEmail());
    return personMeetingDto;
}


  public Page<PersonMeetingFilteredDto> findPersonOnMeetingByAttendanceFilterPaged(Long meetingId, List<Long> localities, String name, String filter, Pageable pageable) {
    List<PersonMeetingFilteredDto> fullList = findPersonOnMeetingByAttendanceFilter(meetingId, localities, name, filter);

    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), fullList.size());

    List<PersonMeetingFilteredDto> sublist = fullList.subList(start, end);

    return new PageImpl<>(sublist, pageable, fullList.size());
  }


  public Map<String, Long> countTotalParticipantsInMeeting(Long meetingId, List<Long> localities, String name, String filter) {
    Map<String, Long> count = new HashMap<>();

    List<PersonMeetingFilteredDto> personMeetingFilteredDtoList = findPersonOnMeetingByAttendanceFilter(meetingId, localities, name, filter);

    final long[] totalCheckedIn = {0L};

    final long[] totalPreRegistered = {0L};

    personMeetingFilteredDtoList.iterator().forEachRemaining(element -> {
      if (element.getCheckedInDate() != null) {
        totalCheckedIn[0]++;
      }

      if (element.getPreRegisteredDate() != null) {
        totalPreRegistered[0]++;
      }
    });

    count.put("checkedIn", totalCheckedIn[0]);
    count.put("preRegistered", totalPreRegistered[0]);
    return count;
  }

  private List<PersonMeetingFilteredDto> findPersonOnMeetingByAttendanceFilter(Long meetingId, List<Long> localities, String name, String filter) {
    if (meetingId == null) {
      throw new IllegalArgumentException(PERSON_ERROR_MEETING_ID_NOT_SPECIFIED);
    }

    List<PersonMeetingFilteredDto> personMeetingFilteredDtoList;

    switch (filter) {
      case "pres":
       personMeetingFilteredDtoList = personRepository.findPersonsOnMeetingWithCheckIn(
         meetingId,
         localities, name);
        break;

      case "prereg":
        personMeetingFilteredDtoList = personRepository.findPersonsOnMeetingWithPreRegistration(meetingId, localities, name);
        break;
      
      case "prereg_pres":
        personMeetingFilteredDtoList = personRepository.findPersonsOnMeetingWithPreRegistrationAndCheckIn(meetingId, localities, name);
        break;
      
      case "prereg_notpres":
        personMeetingFilteredDtoList = personRepository.findPersonsOnMeetingWithPreRegistrationAndNoCheckIn(meetingId, localities, name);
        break;
        
      case "notprereg_pres":
        personMeetingFilteredDtoList = personRepository.findPersonsOnMeetingWithCheckInAndNoPreRegistration(meetingId, localities, name);
        break;
      
      default:
        personMeetingFilteredDtoList = null;
        break;
    }

	  assert personMeetingFilteredDtoList != null;

	  personMeetingFilteredDtoList.iterator().forEachRemaining(element -> {
      element.setCheckedIn(element.getCheckedInDate() != null);
      element.setPreRegistered(element.getPreRegisteredDate() != null);

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

    return personMeetingFilteredDtoList;
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

  public static String cleanSimilarToApoc(String input) {

    String semAcentos = Normalizer.normalize(input, Normalizer.Form.NFD)
        .replaceAll("[^\\p{ASCII}]", "");

    String semEspeciais = semAcentos.replaceAll("[^a-zA-Z0-9\\s]", "");

    return semEspeciais.toLowerCase();
  }

  public static boolean containsAll(String[] substrings, String target, Boolean isEmail) {

    if(isEmail){
      return target.contains(substrings[0].toLowerCase());
    }
    for (String substring : substrings) {
      if (!target.contains(substring)) {
        return false;
      }
    }
    return true;
  }

  public boolean onlyNumbers(String string) {
        String padrao = "^\\d+$";
        Pattern pattern = Pattern.compile(padrao);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }
  
  public String getSubById(Long idPerson){
      List<AuthService> auths = authServiceRepository.findAllByIdPerson(idPerson);
      
      AuthService acAuthService = auths.stream().filter(as -> as.getServer().equals(AuthService.ACESSO_CIDADAO)).findFirst().orElse(null);
      
      if(acAuthService == null) {
          return null;
      } else {
          return acAuthService.getServerId();
      }
  }
}
