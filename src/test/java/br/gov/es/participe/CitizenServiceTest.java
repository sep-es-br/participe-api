package br.gov.es.participe;

import br.gov.es.participe.controller.CitizenController;
import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.*;
import br.gov.es.participe.service.TokenService;
import br.gov.es.participe.util.domain.TokenType;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Testcontainers
@SpringBootTest
class CitizenServiceTest extends BaseTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CitizenController citizenController;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private SelfDeclarationRepository selfDeclarationRepository;

    @Autowired
    private ConferenceRepository conferenceRepository;

    @Autowired
    private LocalityRepository localityRepository;

    @Autowired
    private AuthServiceRepository authServiceRepository;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private LocalityTypeRepository localityTypeRepository;

    @Autowired
    private PlanRepository planRepository;

    @TestConfiguration
    static class Config {

        @Bean
        public Configuration configuration() {
            return new Configuration.Builder().uri(databaseServer.getBoltUrl()).build();
        }
    }

    @BeforeEach
    public void clearDataBase() {
        personRepository.deleteAll();
        selfDeclarationRepository.deleteAll();
        conferenceRepository.deleteAll();
        localityRepository.deleteAll();
        authServiceRepository.deleteAll();
        loginRepository.deleteAll();
        domainRepository.deleteAll();
        localityTypeRepository.deleteAll();
        planRepository.deleteAll();
    }

    @Test
    public void shouldCreateCitizen() {
        PersonParamDto personParamDto = getPersonParamDto();
        ResponseEntity response = citizenController.store(personParamDto);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldDeleteCitizen() {
        PersonParamDto personParamDto = getPersonParamDto();
        ResponseEntity<PersonDto> personResponse = citizenController.store(personParamDto);
        PersonDto personDto = personResponse.getBody();

        ResponseEntity response = citizenController.destroy(personDto.getId());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldUpdateCitizen() {
        PersonParamDto personParamDto = getPersonParamDto();
        ResponseEntity<PersonDto> personResponse = citizenController.store(personParamDto);
        PersonDto personDto = personResponse.getBody();

        Optional<Person> person = personRepository.findById(personDto.getId());
        String token = "Bearer " + tokenService.generateToken(person.get(), TokenType.AUTHENTICATION);

        ResponseEntity response = citizenController.update(token, person.get().getId(), personParamDto);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFailToListCitizenWithoutConferenceId() {
        PersonParamDto personParamDto = getPersonParamDto();
        citizenController.store(personParamDto);

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                citizenController.listCitizen("", "", "", null,
                new ArrayList<>(), null, PageRequest.of(0,30)));
    }

    @Test
    public void shouldListCitizen() {
        Conference conference = getConference();
        Locality locality = getLocality();
        PersonParamDto personParamDto1 = getPersonParamDtoWithLoginInformation("1", conference, locality);
        PersonParamDto personParamDto2 = getPersonParamDtoWithLoginInformation("2", conference, locality);

        citizenController.store(personParamDto1);
        citizenController.store(personParamDto2);

        ResponseEntity response = citizenController.listCitizen("", "", "", null,
                new ArrayList<>(), conference.getId(), PageRequest.of(0,30));
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldListCitizenById() {
        Conference conference = getConference();
        Locality locality = getLocality();
        PersonParamDto personParamDto1 = getPersonParamDtoWithLoginInformation("1", conference, locality);
        PersonParamDto personParamDto2 = getPersonParamDtoWithLoginInformation("2", conference, locality);

        citizenController.store(personParamDto1);
        citizenController.store(personParamDto2);

        ResponseEntity response = citizenController.getCitizenById(personParamDto1.getId(), conference.getId());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldGetLocalities() {
        Long conferenceId = createTestBase();

        ResponseEntity response = citizenController.getLocalities(conferenceId, "");
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    private PersonParamDto getPersonParamDto() {
        Conference conference = conferenceRepository.save(new Conference());
        ConferenceDto conferenceDto = new ConferenceDto();
        conferenceDto.setId(conference.getId());
        Locality locality = localityRepository.save(new Locality());
        LocalityDto localityDto = new LocalityDto();
        localityDto.setId(locality.getId());

        SelfDeclarationParamDto selfDeclarationDto = new SelfDeclarationParamDto();
        selfDeclarationDto.setConference(conferenceDto.getId());
        selfDeclarationDto.setLocality(localityDto.getId());

        PersonParamDto personParamDto = new PersonParamDto();
        personParamDto.setName("pessoa1");
        personParamDto.setLogin("p1");
        personParamDto.setContactEmail("email1@gmail.com");
        personParamDto.setConfirmEmail("email1@gmail.com");
        personParamDto.setCpf("12345678901");
        personParamDto.setTelephone("991191199");
        personParamDto.setPassword("senha123");
        personParamDto.setConfirmPassword("senha123");
        personParamDto.setSelfDeclaration(selfDeclarationDto);

        return personParamDto;
    }

    private Conference getConference() {
        return conferenceRepository.save(new Conference());
    }

    private Locality getLocality() {
        return localityRepository.save(new Locality());
    }

    private PersonParamDto getPersonParamDtoWithLoginInformation(String uniqueEmailString, Conference conference, Locality locality) {
        ConferenceDto conferenceDto = new ConferenceDto();
        conferenceDto.setId(conference.getId());
        LocalityDto localityDto = new LocalityDto();
        localityDto.setId(locality.getId());

        SelfDeclarationParamDto selfDeclarationDto = new SelfDeclarationParamDto();
        selfDeclarationDto.setConference(conferenceDto.getId());
        selfDeclarationDto.setLocality(localityDto.getId());

        AuthService authService = new AuthService();
        authService.setServer("Participe");
        authService = authServiceRepository.save(authService);

        Login login = new Login();
        login.setConference(conference);
        login.setAuthService(authService);
        loginRepository.save(login);

        PersonParamDto personParamDto = new PersonParamDto();
        personParamDto.setName("pessoa1");
        personParamDto.setLogin("p1");
        personParamDto.setContactEmail("email"+uniqueEmailString+"@gmail.com");
        personParamDto.setConfirmEmail("email"+uniqueEmailString+"@gmail.com");
        personParamDto.setCpf("1234567"+uniqueEmailString+"8901");
        personParamDto.setTelephone("991191199");
        personParamDto.setPassword("senha123");
        personParamDto.setConfirmPassword("senha123");
        personParamDto.setSelfDeclaration(selfDeclarationDto);

        ResponseEntity<PersonDto> response = citizenController.store(personParamDto);
        Optional<Person> personOpt = personRepository.findById(response.getBody().getId());

        Set<AuthService> authServicesSet = new HashSet<>();
        authServicesSet.add(authService);
        personOpt.get().setAuthServices(authServicesSet);
        Person person = personRepository.save(personOpt.get());

        personParamDto.setId(person.getId());
        return personParamDto;
    }

    private Long createTestBase() {
        Domain domain = domainRepository.save(new Domain());
        Set<Domain> domainsSet = new HashSet<>();
        domainsSet.add(domain);

        LocalityType localityType = localityTypeRepository.save(new LocalityType());
        Plan plan = new Plan();
        plan.setDomain(domain);
        plan.setlocalitytype(localityType);
        plan = planRepository.save(plan);
        Conference conference = new Conference();
        conference.setPlan(plan);
        conference = conferenceRepository.save(conference);

        Locality locality1 = new Locality();
        locality1.setType(localityType);
        locality1.setDomains(domainsSet);
        locality1 = localityRepository.save(locality1);
        Locality locality2 = new Locality();
        locality2.setType(localityType);
        locality2.setDomains(domainsSet);
        locality2 = localityRepository.save(locality2);

        return conference.getId();
    }
}