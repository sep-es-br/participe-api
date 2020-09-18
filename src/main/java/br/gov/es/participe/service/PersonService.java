package br.gov.es.participe.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.*;
import br.gov.es.participe.util.dto.MessageDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.es.participe.repository.AttendRepository;
import br.gov.es.participe.repository.AuthServiceRepository;
import br.gov.es.participe.repository.IsAuthenticatedByRepository;
import br.gov.es.participe.repository.LoginRepository;
import br.gov.es.participe.repository.PersonRepository;
import br.gov.es.participe.util.domain.TokenType;

@Service
public class PersonService {
	
	private static final String PERSON_ERROR_NOT_FOUND = "person.error.not-found";
	private static final String PARTICIPE = "Participe";
	private static final String SERVER = "Participe";
	private static final String TITLE = "title";
	private static final String SUBTITLE = "subtitle";
	private static final String NAME = "name";
	private static final String PASS_PARAM = "password";
	private static final String EMAIL_RESPONSE_MESSAGE = " - Confirme seu Email";
	
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
	private LocalityService localityService;

    @Autowired
    private TokenService tokenService;
        
    public Boolean forgotPassword(String email, Long conferenceId, String server) {
    	Person personValidate = personRepository.validate(email, server);
    	Optional<Person> person = Optional.ofNullable(personValidate);
    	if(person.isPresent()) {
    		Conference conference = conferenceService.find(conferenceId);
    		String password = genereateTemporaryPassword();
    		
    		List<IsAuthenticatedBy> auth = isAuthenticatedByRepository.findByIdPerson(person.get().getId());
    		
    		for(IsAuthenticatedBy a: auth)
            	if(a.getName().equals(server)) {
            		a.setPassword(password);
            		a.setTemporaryPassword(true);
            		isAuthenticatedByRepository.save(a);
            		break;
            	}
    		
    		HashMap<String, String> model = new HashMap<>();
			model.put(TITLE, conference.getTitleAuthentication());
			model.put(SUBTITLE, conference.getSubtitleAuthentication());
			model.put(NAME, person.get().getName());
			model.put(PASS_PARAM, password);
			sendEmail(person.get().getContactEmail(),conference.getTitleAuthentication()+EMAIL_RESPONSE_MESSAGE, model);
			return true;
    	}
		return false;
    }
    
    public SigninDto authenticate (PersonParamDto user, String server, Long conferenceId) {
    	String userLogin = user.getLogin().contains("@") ? user.getLogin() : user.getLogin() + "@cpf";
    	Optional<Person> person = personRepository.findByContactEmail(userLogin);
    	
    	if(person.isPresent()) {
    		if(person.get().getActive() != null && !person.get().getActive()) {
				return new SigninDto(person.get(), null, null);
			}

    		String authenticationToken = tokenService.generateToken(person.get(), TokenType.AUTHENTICATION);
            String refreshToken = tokenService.generateToken(person.get(), TokenType.REFRESH);
            person.get().setAccessToken(authenticationToken);

            SigninDto singniDto = new SigninDto(person.get(), authenticationToken, refreshToken);
            singniDto.setCompleted(true);
            List<IsAuthenticatedBy> auth = isAuthenticatedByRepository.findByIdPerson(person.get().getId());
            
            for(IsAuthenticatedBy a: auth)
            	if(a.getName().equals(server)) {
            		if(!user.getPassword().equals(a.getPassword()))
            			return null;
            		singniDto.setTemporaryPassword(a.getTemporaryPassword());
            	}
            createRelationshipWithAthService(person.get(), user.getPassword(), server, person.get().getId().toString(),
					conferenceId, false, true, null);
            return singniDto;
    	}
		return null;
    }

    @Transactional
    public Person save(Person person, boolean isLike) {
		Person personBD = null;
		if(!isLike) {
			personBD = personRepository.findByEmailOrCpf(person.getContactEmail(), person.getCpf());
			
			if(personBD != null)
				return personBD;
		}
		personBD = personRepository.save(person);

		return personBD;
	}

	@Transactional
	public Person createRelationshipWithAthService(Person person, String password, String server, String serverid,
												   Long conferenceId, boolean resetPassword, Boolean makeLogin,
												   String typeAuthentication) {
		boolean newAuthService = true;
		if(person.getId() == null) {
			person = personRepository.save(person);
		} else {
			loadSelfDeclaration(person, conferenceId);
		}
		AuthService authService = null;
		IsAuthenticatedBy authenticatedBy = getIsAuthenticatedBy(person.getId(), server);
		if(authenticatedBy != null) {
			authService = authenticatedBy.getAuthService();
			newAuthService = false;
		}

		Conference conference = null;
		if(conferenceId != null) {
			conference = conferenceService.find(conferenceId);
		}

		if(newAuthService) {
			authService = new AuthService();
			authService.setServer(server);
			authService.setServerId(serverid);
			if(makeLogin)
				authService.setNumberOfAccesses(1);
			else
				authService.setNumberOfAccesses(0);
			authService = authServiceRepository.save(authService);
			authenticatedBy = createAuthenticatedBy(server, person, conference, password, authService, typeAuthentication);
		} else {
			if(authService.getNumberOfAccesses() == null) {
				authService.setNumberOfAccesses(0);
			}
			authService.setNumberOfAccesses(makeLogin ? authService.getNumberOfAccesses() + 1 :
					authService.getNumberOfAccesses());
			authServiceRepository.save(authService);
			loadAuthenticatedBy(authenticatedBy, person, serverid, password, conferenceId, typeAuthentication);
		}
		if(resetPassword && !newAuthService) {
			resetPassword(person, conference, authenticatedBy);
		}
		if(makeLogin) {
			createLogin(person, authService, conferenceId);
		}
		isAuthenticatedByRepository.save(authenticatedBy);
		return person;
	}
	
	private void resetPassword(Person person, Conference conference, IsAuthenticatedBy authenticatedBy) {
		Date passwordTime = expirationTime((long) 24);
		String password = genereateTemporaryPassword();
		authenticatedBy.setPassword(password);
		authenticatedBy.setPasswordTime(passwordTime);
		
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
	
	private IsAuthenticatedBy createAuthenticatedBy(String server, Person person, Conference conference, String password,
													AuthService authService, String typeAuthentication) {
		IsAuthenticatedBy authenticatedBy = null;
		if(server.equalsIgnoreCase(PARTICIPE)) {
			if(typeAuthentication != null && typeAuthentication.equals("cpf")) {
				authenticatedBy = new IsAuthenticatedBy(PARTICIPE, "participeCpf", password,
						false, null, person, authService);
			} else {
				Date passwordTime = expirationTime((long) 24);
				password = genereateTemporaryPassword();
				authenticatedBy = new IsAuthenticatedBy(PARTICIPE, "participeEmail", password, true, passwordTime, person, authService);
				
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
		} else {
			authenticatedBy = new IsAuthenticatedBy(server, "oauth", null, false, null, person, authService);
		}
		return authenticatedBy;
	}
	
	private void loadSelfDeclaration(Person person, Long conferenceId) {
		if(conferenceId != null) {
			SelfDeclaration sd  = selfDeclarationService.findByPersonAndConference(person.getId(), conferenceId);
			if(sd != null) {
				person.addSelfDeclaration(sd);
			}
		}
	}
	
	private void createLogin(Person person, AuthService authService, Long conferenceId) {
		if(conferenceId != null) {
			Conference conference = conferenceService.find(conferenceId);
			Login login = new Login(person, authService, conference);
			loginRepository.save(login);
		}
	}
	
	private void loadAuthenticatedBy(IsAuthenticatedBy authenticatedBy, Person person, String server, String password,
									 Long conferenceId, String typeAuthentication) {
		if(authenticatedBy.getPassword() != null && !authenticatedBy.getPassword().equals(password)) {
			authenticatedBy.setPassword(password);
		}
		if(authenticatedBy.getEmail() == null) {
			authenticatedBy.setEmail(person.getContactEmail());
		}
		if(authenticatedBy.getName() == null) {
			authenticatedBy.setName(server);
		}
		if(conferenceId == null) {
			authenticatedBy.setTemporaryPassword(false);
		}
		
		if(authenticatedBy.getAuthType() == null) {
			if(server.equalsIgnoreCase(PARTICIPE)) {
				if(typeAuthentication != null && typeAuthentication.equals("cpf")) {
					authenticatedBy.setAuthType("participeCpf");
				} else {
					authenticatedBy.setAuthType("participeEmail");
				}
			} else {
				authenticatedBy.setAuthType("oauth");
			}
		}
	}
	
	private IsAuthenticatedBy getIsAuthenticatedBy(Long idPerson, String server) {
		List<IsAuthenticatedBy> relationships = isAuthenticatedByRepository.findByIdPerson(idPerson);

		if(relationships != null && !relationships.isEmpty()) {
			for(IsAuthenticatedBy relationship: relationships)
				if(relationship.getAuthService().getServer().equalsIgnoreCase(server)) {
					return relationship;					
				}
		}
		
		return null;
	}

	@Transactional
	public Person complement(Person person, SelfDeclaration selfDeclaration) {
		Person personBD = personRepository.findByContactEmail(person.getContactEmail()).orElse(null);
		
		if(personBD == null)
			personBD = personRepository.save(person);
		else {
			personBD.setTelephone(person.getTelephone());
			personRepository.save(personBD);
		}
		
		if(selfDeclaration.getPerson() == null)
			selfDeclaration.setPerson(personBD);
		else
			selfDeclaration.getPerson().setId(personBD.getId());
		
		selfDeclarationService.save(selfDeclaration);
		
		return find(personBD.getId());
	}
	
	public Person findRelationships(Long id) {
		return personRepository.findRelationships(id);
	}
	
	public Person find(Long id) {
		return personRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(PERSON_ERROR_NOT_FOUND));
	}

	public PersonKeepCitizenDto findCitizenById(Long personId, Long conferenceId) {
		Person person = find(personId);

		if(person != null) {
			PersonKeepCitizenDto personCitizen = getPersonKeepCitizenDto(person);
			List<LoginAccessDto> loginAccessDtos = personRepository.findAccessByPerson(conferenceId, personId);
			if(loginAccessDtos == null) {
				loginAccessDtos = new ArrayList<>();
				loginAccessDtos.add(new LoginAccessDto("Participe", 0L));
			}
			personCitizen.setAutentication(loginAccessDtos);

			long total = 0;
			for(LoginAccessDto loginDto : loginAccessDtos) {
				total = total + loginDto.getAcesses();
			}
			personCitizen.setNumberOfAcesses(total);

			LocalityInfoDto recentLocality = personRepository.findRecentLocalityByPerson(conferenceId, personId);
			if(recentLocality == null) {
				recentLocality = personRepository.findLocalityByPerson(conferenceId, personId);
			}
			if(recentLocality != null) {
				personCitizen.setLocalityId(recentLocality.getLocalityId());
				personCitizen.setLocalityName(recentLocality.getLocalityName());
			}

			List<IsAuthenticatedBy> auth = isAuthenticatedByRepository.findByIdPerson(personId);
			for(IsAuthenticatedBy a: auth) {
				if(a.getName().equals("Participe")) {
					personCitizen.setPassword(a.getPassword());
				}
			}

			personCitizen.setActive(person.getActive() == null || person.getActive());
			return personCitizen;
		}
		throw new IllegalArgumentException(PERSON_ERROR_NOT_FOUND);
	}
	
	private PersonKeepCitizenDto getPersonKeepCitizenDto(Person person) {
		PersonKeepCitizenDto personCitizen = new PersonKeepCitizenDto();
		personCitizen.setId(person.getId());
		personCitizen.setCpf(person.getCpf());
		personCitizen.setName(person.getName());
		personCitizen.setEmail(person.getContactEmail());
		personCitizen.setTelephone(person.getTelephone());
		personCitizen.setTypeAuthentication("mail");
		if(person.getCpf() != null && person.getContactEmail() != null 
				&& person.getContactEmail().startsWith(person.getCpf())) {
			personCitizen.setTypeAuthentication("cpf");
		}
		return personCitizen;
	}

	public List<Person> findAll(){
		List<Person> persons = new ArrayList<>();
		
		personRepository.findAll()
						.iterator()
						.forEachRemaining(persons::add);
		
		return persons;
	}
	
	@Transactional
	public void delete(Long id) {
		Person person = findRelationships(id);
		List<IsAuthenticatedBy> relationships = isAuthenticatedByRepository.findByIdPerson(person.getId());
		
		if(person.getSelfDeclaretions() != null && !person.getSelfDeclaretions().isEmpty()) {
			for(SelfDeclaration self: person.getSelfDeclaretions())
				selfDeclarationService.delete(self.getId());
		}
		
		if(relationships != null && !relationships.isEmpty()) {
			for(IsAuthenticatedBy relationship: relationships) {
				authServiceRepository.delete(relationship.getAuthService());
				isAuthenticatedByRepository.delete(relationship);
			}
		}
		
		List<Attend> atts = attendRepository.findAllAttendByIdperson(person.getId());
		
		if(atts != null) {
			for(Attend att: atts)
				attendRepository.delete(att);
		}
		
		List<Login> logins = loginRepository.findAllByPerson(id);
		if(logins != null) {
			for(Login log: logins)
				loginRepository.delete(log);
		}
		personRepository.delete(person);
	}
	
	public boolean validate(String email, String cpf, String server) {
		Person person;
		if(cpf.equalsIgnoreCase(""))
			person = personRepository.validate(email, server);
		else
			person = personRepository.findByCpfIgnoreCase(email);
		
		return person == null;
	}
	
	public Person likescomments(Long idPerson) {
		return personRepository.likescomments(idPerson);
	}
	
	public Person findPersonMadeByIdComment(Long idComment) {
		return personRepository.findPersonMadeByIdComment(idComment);
	}
	
	public List<Person> findPersonLikedByIdComment(Long idComment){
		return personRepository.findPersonLikedByIdComment(idComment);
	}
	
	public Optional<Person> findByContactEmail(String email) {
		return personRepository.findByContactEmail(email);
	}
	
	private Date expirationTime(Long hours) {
		Date in = new Date();
		LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault()).plusHours(hours);
		return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
	}

	private String genereateTemporaryPassword() {
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
	public Page<PersonKeepCitizenDto> listKeepCitizen(String name, String email, String autentication, Boolean active,
													  List<Long> locality, Long conferenceId, Pageable page){
    	if(conferenceId == null) {
			throw new IllegalArgumentException("Conference Id must be specified.");
		}

		Page<PersonKeepCitizenDto> response = personRepository
				.findPersonKeepCitizen(name, email, autentication, active, locality, page);

		response.forEach(element -> {
			List<LoginAccessDto> loginAccessDtos = personRepository.findAccessByPerson(conferenceId, element.getId());
			if(loginAccessDtos == null || loginAccessDtos.isEmpty()) {
				loginAccessDtos = new ArrayList<>();
				loginAccessDtos.add(new LoginAccessDto("Participe", 0L));
			}
			element.setAutentication(loginAccessDtos);

			long total = 0;
			for(LoginAccessDto loginDto : loginAccessDtos) {
				total = total + loginDto.getAcesses();
			}
			element.setNumberOfAcesses(total);

			LocalityInfoDto recentLocality = personRepository.findRecentLocalityByPerson(conferenceId, element.getId());
			if(recentLocality == null) {
				recentLocality = personRepository.findLocalityByPerson(conferenceId, element.getId());
			}
			if(recentLocality != null) {
				element.setLocalityId(recentLocality.getLocalityId());
				element.setLocalityName(recentLocality.getLocalityName());
			}
		});

		return response;
	}

	public ResponseEntity storePerson(PersonParamDto personParam, Boolean makeLogin) {
    	Boolean typeAuthenticationCpf = false;
    	if(personParam.getTypeAuthentication() != null && personParam.getTypeAuthentication().equals("cpf")) {
    		if(personParam.getCpf() == null || personParam.getCpf().isEmpty()) {
    			throw new IllegalArgumentException("When authentication type is 'cpf', the cpf must be informed.");
			}
			int size = personParam.getPassword() != null ? personParam.getPassword().length() : 0;
    		if(personParam.getPassword() == null || size < 6 || !personParam.getPassword().matches("[A-Za-z0-9]+")) {
    			throw new IllegalArgumentException("Password must be specified with at least 6 characters. Characters allowed: A-Z, a-z, 0-9.");
			}
			if(personParam.getPassword() == null || personParam.getConfirmPassword() == null ||
					!personParam.getPassword().equals(personParam.getConfirmPassword())) {
				throw new IllegalArgumentException("Parameters password and confirmPassword must match.");
			}
			personParam.setContactEmail(personParam.getCpf() + "@cpf");
			personParam.setConfirmEmail(personParam.getCpf() + "@cpf");
			typeAuthenticationCpf = true;
		}

		if(personParam.getContactEmail().equals(personParam.getConfirmEmail())) {
			if(this.validate(personParam.getContactEmail(), "", SERVER)) {
				Person person = this.save(new Person(personParam,typeAuthenticationCpf), false);

				if(personParam.getSelfDeclaration() == null) {
					throw new IllegalArgumentException("Self Declaration must be informed.");
				}

				SelfDeclaration self = new SelfDeclaration(personParam.getSelfDeclaration());

				person = this.complement(person, self);

				this.createRelationshipWithAthService(person,
						personParam.getPassword(),
						SERVER,
						person.getId().toString(),
						self.getConference().getId(), false, makeLogin,
						personParam.getTypeAuthentication());

				PersonDto res = new PersonDto(person);

				return ResponseEntity.status(200).body(res);
			}
			MessageDto msg = new MessageDto();
			if(personParam.getTypeAuthentication() != null && personParam.getTypeAuthentication().equals("cpf")) {
				msg.setMessage("O CPF informado já existe para outro usuário");
			} else {
				msg.setMessage("E-mail já cadastrado");
			}
			msg.setCode(400);
			return ResponseEntity.status(400).body(msg);
		}

		MessageDto msg = new MessageDto();
		msg.setMessage("E-mails Incompatíveis");
		msg.setCode(422);
		return ResponseEntity.status(422).body(msg);
	}
	
	public ResponseEntity updatePerson(PersonParamDto personParam, Boolean makeLogin) {
		Person person = this.find(personParam.getId());
		if(personParam.getTypeAuthentication() != null) {
			switch (personParam.getTypeAuthentication()) {
			case "mail":
				if(personParam.getContactEmail() == null || personParam.getConfirmEmail().isEmpty()) {
					throw new IllegalArgumentException("E-mails must be informed");
				}
				break;
			case "cpf":
				if(personParam.getCpf() == null || personParam.getCpf().isEmpty()) {
	    			throw new IllegalArgumentException("When authentication type is 'cpf', the cpf must be informed.");
				}
				int size = personParam.getPassword() != null ? personParam.getPassword().length() : 0;
				if(personParam.getPassword() == null || size < 6 || !personParam.getPassword().matches("[A-Za-z0-9]+")) {
					throw new IllegalArgumentException("Password must be specified with at least 6 characters. Characters allowed: A-Z, a-z, 0-9.");
				}
				if(personParam.getPassword() == null || personParam.getConfirmPassword() == null ||
						!personParam.getPassword().equals(personParam.getConfirmPassword())) {
					throw new IllegalArgumentException("Parameters password and confirmPassword must match.");
				}
				personParam.setContactEmail(personParam.getCpf() + "@cpf");
				personParam.setConfirmEmail(personParam.getCpf() + "@cpf");
				break;
			default:
				break;
			}
		}
		if(!personParam.getContactEmail().equals(personParam.getConfirmEmail())) {
			throw new IllegalArgumentException("Contact email and confirm email must match.");
		}
		if(!person.getContactEmail().equals(personParam.getContactEmail()) &&
				!this.validate(personParam.getContactEmail(), "", SERVER)) {
			MessageDto msg = new MessageDto();
			if(personParam.getTypeAuthentication() != null && personParam.getTypeAuthentication().equals("cpf")) {
				msg.setMessage("O CPF informado já existe para outro usuário");
			} else {
				msg.setMessage("E-mail já cadastrado");
			}
			msg.setCode(400);
			return ResponseEntity.status(400).body(msg);
		}

		person.setCpf(personParam.getCpf());
		if(personParam.getTypeAuthentication() != null && personParam.getTypeAuthentication().equals("cpf")) {
			person.setContactEmail(personParam.getCpf() + "@cpf");
		} else {
			person.setContactEmail(personParam.getConfirmEmail());
		}
		person.setActive(personParam.getActive());

		SelfDeclaration sd  = selfDeclarationService.findByPersonAndConference(person.getId(),
				personParam.getSelfDeclaration().getConference());
		if(sd != null && personParam.getSelfDeclaration().getLocality() != sd.getLocality().getId()) {
			Locality locality = localityService.find(personParam.getSelfDeclaration().getLocality());
			sd = selfDeclarationService.update(sd, locality);
		}

		PersonDto response = new PersonDto(person);
		this.createRelationshipWithAthService(person,
				personParam.getPassword(),
				SERVER,
				person.getId().toString(),
				personParam.getSelfDeclaration().getConference(),
				personParam.isResetPassword(),
				makeLogin, personParam.getTypeAuthentication());

		return ResponseEntity.status(200).body(response);
	}

	public ResponseEntity updatePerson(String token, PersonParamDto personParam, Boolean makeLogin) {
		String[] keys = token.split(" ");
		Long idPerson = tokenService.getPersonId(keys[1], TokenType.AUTHENTICATION);

		if(personParam.getConfirmPassword().equals(personParam.getPassword())) {
			int size = personParam.getPassword().length();
			if(size >= 6 && personParam.getPassword().matches("[A-Za-z0-9]+")) {
				Person person = this.find(idPerson);
				PersonDto response = new PersonDto(person);

				this.createRelationshipWithAthService(person,
						personParam.getPassword(),
						SERVER,
						idPerson.toString(),
						null, false, makeLogin, personParam.getTypeAuthentication());

				return ResponseEntity.status(200).body(response);
			}
			MessageDto msg = new MessageDto();
			msg.setMessage("Senha deve conter no mínimo 6 caracteres alfanuméricos");
			msg.setCode(403);
			return ResponseEntity.status(403).body(msg);
		}
		MessageDto msg = new MessageDto();
		msg.setMessage("Senhas fornecidas são incompatíveis");
		msg.setCode(422);
		return ResponseEntity.status(422).body(msg);
	}

	public Page<PersonMeetingDto> findPersonForMeeting(Long meetingId, String name, Pageable pageable) {
		if(meetingId == null) {
			throw new IllegalArgumentException("Meeting Id must be informed.");
		}

		Page<PersonMeetingDto> personMeetingDtoPage = personRepository.findPersonForMeeting(meetingId, name, pageable);

		personMeetingDtoPage.forEach(element -> {
			element.setAuthTypeCpf(element.getEmail().endsWith("@cpf"));
			LocalityRegionalizableDto localityInfo = personRepository.findMostRecentLocality(element.getPersonId(),
					meetingId);
			if(localityInfo != null) {
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
		if(meetingId == null) {
			throw new IllegalArgumentException("Meeting Id must be informed.");
		}


		Page<PersonMeetingDto> personMeetingDtoPage = personRepository.findPersonsCheckedInOnMeeting(meetingId,
				localities, name, pageable);

		personMeetingDtoPage.forEach(element -> {
			LocalityRegionalizableDto localityInfo = personRepository.findMostRecentLocality(element.getPersonId(), meetingId);
			if(localityInfo != null) {
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

	public Optional<Person> findPersonIfParticipatingOnMeetingPresentially(Long personId, Date date) {
		return personRepository.findPersonIfParticipatingOnMeetingPresentially(personId, date);
	}
}
