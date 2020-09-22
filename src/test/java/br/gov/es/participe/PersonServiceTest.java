package br.gov.es.participe;

import br.gov.es.participe.controller.PersonController;
import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.repository.*;
import br.gov.es.participe.service.TokenService;
import br.gov.es.participe.util.domain.TokenType;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

@Testcontainers
@SpringBootTest
class PersonServiceTest extends BaseTest {

    private static final String EMAIL = "participesep@gmail.com";

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PersonController personController;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private SelfDeclarationRepository selfDeclarationRepository;

    @Autowired
    private ConferenceRepository conferenceRepository;

    @Autowired
    private LocalityRepository localityRepository;

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
    }

    @Test
    public void shouldCreatePerson() {
        PersonParamDto personParamDto = getPersonParamDto();
        ResponseEntity response = personController.store(personParamDto);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldValidatePerson() {
        ResponseEntity response1 = personController.validate(EMAIL, "12345678901", null);
        Assert.assertEquals(200, response1.getStatusCodeValue());

        PersonParamDto personParamDto = getPersonParamDto();
        personController.store(personParamDto);

        ResponseEntity response2 = personController.validate(EMAIL, "12345678901", null);
        Assert.assertEquals(200, response2.getStatusCodeValue());
    }

    @Test
    public void shouldDeletePerson() {
        PersonParamDto personParamDto = getPersonParamDto();
        ResponseEntity<PersonDto> personResponse = personController.store(personParamDto);
        PersonDto personDto = personResponse.getBody();

        ResponseEntity response = personController.destroy(personDto.getId());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldComplementPerson() {
        PersonParamDto personParamDto = getPersonParamDto();
        personController.store(personParamDto);

        ResponseEntity response = personController.complement(personParamDto);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldUpdatePerson() {
        PersonParamDto personParamDto = getPersonParamDto();
        ResponseEntity<PersonDto> personResponse = personController.store(personParamDto);
        PersonDto personDto = personResponse.getBody();

        Optional<Person> person = personRepository.findById(personDto.getId());
        String token = "Bearer " + tokenService.generateToken(person.get(), TokenType.AUTHENTICATION);

        personParamDto.setId(person.get().getId());
        ResponseEntity response = personController.update(token, personParamDto, person.get().getId());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    private PersonParamDto getPersonParamDto() {
        Conference conference = getConference();
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
        Conference conference = new Conference();
        conference.setTitleAuthentication("titulo");
        conference.setSubtitleAuthentication("subtitulo");
        conference.setTitleParticipation("titulo");
        conference.setSubtitleParticipation("subtitulo");
        conference.setTitleRegionalization("titulo");
        conference.setSubtitleRegionalization("subtitulo");
        return conferenceRepository.save(conference);
    }
}