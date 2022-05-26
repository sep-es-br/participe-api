package br.gov.es.participe;

import br.gov.es.participe.controller.dto.ConferenceDto;
import br.gov.es.participe.controller.dto.LocalityDto;
import br.gov.es.participe.controller.dto.PersonDto;
import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.repository.ConferenceRepository;
import br.gov.es.participe.repository.LocalityRepository;
import br.gov.es.participe.repository.PersonRepository;
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
import br.gov.es.participe.controller.SelfDeclarationController;
import br.gov.es.participe.controller.dto.SelfDeclarationDto;
import br.gov.es.participe.repository.SelfDeclarationRepository;

@Testcontainers
@SpringBootTest
class SelfDeclarationServiceTest extends BaseTest {

    @Autowired
    private SelfDeclarationController selfdeclarationController;

    @Autowired
    private SelfDeclarationRepository selfdeclarationRepository;

    @Autowired
    private PersonRepository personRepository;

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
    public void clearSelfDeclarations() {
        selfdeclarationRepository.deleteAll();
        personRepository.deleteAll();
        conferenceRepository.deleteAll();
        localityRepository.deleteAll();
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void shouldCreateSelfDeclaration() {
        SelfDeclarationDto selfdeclarationDto = createSelfDeclarationParamDto("Test");

        ResponseEntity response = selfdeclarationController.store(selfdeclarationDto);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void shouldListAllSelfDeclarations() {
        SelfDeclarationDto selfdeclarationDto = createSelfDeclarationParamDto("Test");
        ResponseEntity<SelfDeclarationDto> selfDeclarationDtoResponseEntity = selfdeclarationController
                .store(selfdeclarationDto);
        SelfDeclarationDto selfDeclarationDto = selfDeclarationDtoResponseEntity.getBody();

        ResponseEntity response = selfdeclarationController.findAll(selfDeclarationDto.getId());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void shouldDeleteSelfDeclaration() {
        SelfDeclarationDto selfdeclarationParamDto = createSelfDeclarationParamDto("Test");

        ResponseEntity<SelfDeclarationDto> selfdeclarationDto = selfdeclarationController
                .store(selfdeclarationParamDto);
        SelfDeclarationDto selfDto = selfdeclarationDto.getBody();

        ResponseEntity response = selfdeclarationController.delete(selfDto.getId());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    private SelfDeclarationDto createSelfDeclarationParamDto(String name) {
        Person person = this.getPerson(name);
        Conference conference = conferenceRepository.save(new Conference());
        Locality locality = localityRepository.save(new Locality());

        SelfDeclarationDto selfdeclarationDto = new SelfDeclarationDto();
        selfdeclarationDto.setPerson(new PersonDto(person));
        selfdeclarationDto.setConference(new ConferenceDto(conference));
        selfdeclarationDto.setLocality(new LocalityDto(locality));
        return selfdeclarationDto;
    }

    private Person getPerson(String name) {
        Person person = personRepository.findById(1L).orElse(null);
        if (person == null) {
            person = new Person();
            person.setName(name);
            person = personRepository.save(person);
        }
        return person;
    }
}