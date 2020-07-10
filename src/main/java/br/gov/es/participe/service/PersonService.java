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

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.es.participe.controller.dto.PersonParamDto;
import br.gov.es.participe.controller.dto.SigninDto;
import br.gov.es.participe.model.Attend;
import br.gov.es.participe.model.AuthService;
import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.IsAuthenticatedBy;
import br.gov.es.participe.model.Login;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.SelfDeclaration;
import br.gov.es.participe.repository.AttendRepository;
import br.gov.es.participe.repository.AuthServiceRepository;
import br.gov.es.participe.repository.IsAuthenticatedByRepository;
import br.gov.es.participe.repository.LoginRepository;
import br.gov.es.participe.repository.PersonRepository;
import br.gov.es.participe.util.domain.TokenType;

@Service
public class PersonService {

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
    
    public Boolean forgotPassword(String email, Long conferenceId, String server) {
    	//Optional<Person> person = personRepository.findByContactEmail(email);
    	Person person_ = personRepository.validate(email, server);
    	Optional<Person> person = Optional.ofNullable(person_);
    	if(person.isPresent()) {
    		Conference conference = conferenceService.find(conferenceId);
    		String password = genereateTemporaryPassword();
    		
    		List<IsAuthenticatedBy> auth = isAuthenticatedByRepository.findByIdPerson(person.get().getId());
    		
    		for(IsAuthenticatedBy a: auth)
            	if(a.getName().equals(server)) {
            		a.setPassword(password);
            		a.setTemporary_password(true);
            		isAuthenticatedByRepository.save(a);
            		break;
            	}
    		
    		HashMap<String, String> model = new HashMap<>();
			model.put("title", conference.getTitleAuthentication());
			model.put("subtitle", conference.getSubtitleAuthentication());
			model.put("name", person.get().getName());
			model.put("password", password);
			sendEmail(person.get().getContactEmail(),conference.getTitleAuthentication()+" - Confirme seu Email", model);
			return true;
    	}
		return false;
    }
    
    public SigninDto authenticate (PersonParamDto user, String SERVER, Long conferenceId) {
    	Optional<Person> person = personRepository.findByContactEmail(user.getLogin());
    	
    	if(person.isPresent()) {
    		String authenticationToken = tokenService.generateToken(person.get(), TokenType.AUTHENTICATION);
            String refreshToken = tokenService.generateToken(person.get(), TokenType.REFRESH);
            person.get().setAccessToken(authenticationToken);
            
            
            SigninDto singniDto = new SigninDto(person.get(), authenticationToken, refreshToken);
            singniDto.setCompleted(true);
            List<IsAuthenticatedBy> auth = isAuthenticatedByRepository.findByIdPerson(person.get().getId());
            
            for(IsAuthenticatedBy a: auth)
            	if(a.getName().equals(SERVER)) {
            		if(!user.getPassword().equals(a.getPassword()))
            			return null;
            		singniDto.setTemporaryPassword(a.getTemporary_password());
            	}
            createRelationshipWithAthService(person.get(), user.getPassword(), SERVER, person.get().getId().toString(), conferenceId);
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
	public Person createRelationshipWithAthService(Person person, String password, String server, String serverid, Long conferenceId) {
		boolean newAuthService = true;
		SelfDeclaration sd = null;
		if(person.getId() == null) {
			person = personRepository.save(person);
		}else {
			if(conferenceId != null) {
				sd = selfDeclarationService.findByPersonAndConference(person.getId(), conferenceId);
				if(sd != null) {
					person.addSelfDeclaration(sd);
				}
			}
		}
		AuthService authservice = null;
		IsAuthenticatedBy authenticatedBy = null;
		List<IsAuthenticatedBy> relationships = isAuthenticatedByRepository.findByIdPerson(person.getId());

		if(relationships != null && !relationships.isEmpty()) {
			for(IsAuthenticatedBy relationship: relationships)
				if(relationship.getAuthService().getServer().equalsIgnoreCase(server)) {
					authservice = relationship.getAuthService();
					authenticatedBy = relationship;
					newAuthService = false;
				}
		}
		if(newAuthService) {
			authservice = new AuthService();
			authservice.setServer(server);
			authservice.setServerId(serverid);
		}

		if(authservice.getNumberOfAccesses() == null)
			authservice.setNumberOfAccesses(1);
		else
			authservice.setNumberOfAccesses(authservice.getNumberOfAccesses() + 1);

		authServiceRepository.save(authservice);
		
		if(conferenceId != null) {
			Conference conference = conferenceService.find(conferenceId);
			Login login = new Login(person, authservice, conference);
			loginRepository.save(login);
		}

		if(newAuthService) {
			if(server.equalsIgnoreCase("Participe")) {
				if(person.getCpf() != null) {
					authenticatedBy = new IsAuthenticatedBy(authservice.getServerId(), "Participe", "participeCpf", person.getContactEmail(), password, false, null, person, authservice);
				} else {
					Date password_time = expirationTime((long) 24);
					password = genereateTemporaryPassword();
					authenticatedBy = new IsAuthenticatedBy(authservice.getServerId(), "Participe", "participeEmail", person.getContactEmail(), password, true, password_time, person, authservice);
					
					String body = "Senha Provisória: "+password;
					HashMap<String, String> model = new HashMap<>();
					model.put("title", sd.getConference().getTitleAuthentication());
					model.put("subtitle", sd.getConference().getSubtitleAuthentication());
					model.put("name", person.getName());
					model.put("password", password);
					sendEmail(person.getContactEmail(),sd.getConference().getTitleAuthentication()+" - Confirme seu Email", model);
				}
			} else {
				authenticatedBy = new IsAuthenticatedBy(authservice.getServerId(), server, "oauth", person.getContactEmail(), null, false, null, person, authservice);
			}

		} else {
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
				authenticatedBy.setTemporary_password(false);
			}
			
			if(authenticatedBy.getAuthType() == null) {
				if(server.equalsIgnoreCase("Participe")) {
					if(person.getCpf() != null) {
						authenticatedBy.setAuthType("participeCpf");
					}else {
						authenticatedBy.setAuthType("participeEmail");
					}
				} else {
					authenticatedBy.setAuthType("oauth");
				}
			}
			
		}
		isAuthenticatedByRepository.save(authenticatedBy);
		return person;
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
		
		if(selfDeclaration != null) 
			selfDeclarationService.save(selfDeclaration);
		
		return find(personBD.getId());
	}
	
	public Person findRelatioships(Long id) {
		return personRepository.findRelatioships(id);
	}
	
	public Person find(Long id) {
		return personRepository.findById(id).get();
	}

	public Person findByCommentId(Long commentId) {
		return personRepository.findByCommentId(commentId);
	}

	public List<Person> findAll(){
		List<Person> persons = new ArrayList<>();
		
		personRepository.findAll()
						.iterator()
						.forEachRemaining(persons::add);
		
		return persons;
	}
	
	public Optional<Person> findByServerAndContactEmail(String server, String email) {
		return personRepository.findByServerAndContactEmail(server, email);
	}
	
	public void check(Boolean toDelete, Boolean passwordChecked, Long idPerson, String server, String password) {
		List<IsAuthenticatedBy> relationships = isAuthenticatedByRepository.findByIdPerson(idPerson);
		
		if(relationships != null && !relationships.isEmpty()) {
			if(relationships.size() > 1)
				toDelete = false;
			
			for(IsAuthenticatedBy relationship: relationships) {
				if(relationship.getName().equalsIgnoreCase(server) && relationship.getPassword().equals(password))
					passwordChecked = true; 
			}
		}
		else
			toDelete = true;
	}

	@Transactional
	public void delete(Long id) {
		Person person = findRelatioships(id);
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
		
		if(person == null)
			return true;
		
		return false;
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
	    String password = pwdChars.stream()
		      .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
		      .toString();
	    return password;
	}

	private void sendEmail(String to, String title, HashMap<String, String> data) {
		emailService.sendEmail(to, title, data);
	}

}
