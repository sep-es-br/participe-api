package br.gov.es.participe;

import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.*;
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

import br.gov.es.participe.controller.HighlightController;
import br.gov.es.participe.repository.HighlightRepository;
import br.gov.es.participe.repository.ConferenceRepository;
import br.gov.es.participe.repository.LocalityRepository;
import br.gov.es.participe.repository.PersonRepository;
import br.gov.es.participe.repository.PlanItemRepository;

@Testcontainers
@SpringBootTest
class HighlightServiceTest extends BaseTest {

    @Autowired
    private HighlightController highlightController;

    @Autowired
    private HighlightRepository highlightRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private LocalityRepository localityRepository;

    @Autowired
    private ConferenceRepository conferenceRepository;

    @Autowired
    private PlanItemRepository planItemRepository;

    @TestConfiguration
    static class Config {

        @Bean
        public Configuration configuration() {
            return new Configuration.Builder().uri(databaseServer.getBoltUrl()).build();
        }
    }

    @BeforeEach
    public void clearHighlights() {
        highlightRepository.deleteAll();
        personRepository.deleteAll();
        localityRepository.deleteAll();
        conferenceRepository.deleteAll();
        planItemRepository.deleteAll();
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void shouldCreateHighlight() {
        HighlightParamDto highlightParamDto = createHighlightParamDto("Test");
        ResponseEntity response = highlightController.store(highlightParamDto);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void shouldDeleteHighlight() {
        HighlightParamDto highlightParamDto = createHighlightParamDto("test");
        ResponseEntity<HighlightDto> highlightDto = highlightController.store(highlightParamDto);
        HighlightDto selfDto = highlightDto.getBody();

        HighlightParamDto highlightToBeDeleted = new HighlightParamDto();
        highlightToBeDeleted.setLocality(selfDto.getLocality());
        highlightToBeDeleted.setPersonMadeBy(selfDto.getPersonMadeBy());
        highlightToBeDeleted.setPlanItem(selfDto.getPlanItem());
        highlightToBeDeleted.setConference(highlightParamDto.getConference());
        highlightToBeDeleted.setFrom(selfDto.getFrom());
        highlightToBeDeleted.setId(selfDto.getId());
        highlightToBeDeleted.setMeeting(selfDto.getMeeting());

        ResponseEntity response = highlightController.delete(highlightToBeDeleted);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void shouldDeleteHighlightByPersonId() {
        HighlightParamDto highlightParamDto = createHighlightParamDto("Test");
        Person person = this.getPerson();

        highlightParamDto.setPersonMadeBy(new PersonDto(person));
        highlightController.store(highlightParamDto).getBody();

        ResponseEntity<Void> response = highlightController.deleteAll(person.getId());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    private HighlightParamDto createHighlightParamDto(String text) {
        Locality locality = localityRepository.save(new Locality());
        Conference conference = conferenceRepository.save(new Conference());

        PlanItem planItemParent = planItemRepository.save(new PlanItem());

        PlanItem planItem = new PlanItem();
        planItem.setParent(planItemParent);

        planItem = planItemRepository.save(planItem);
        Person person = this.getPerson();

        HighlightParamDto highlightParamDto = new HighlightParamDto();
        highlightParamDto.setPlanItem(new PlanItemDto(planItem));
        highlightParamDto.setConference(conference.getId());
        highlightParamDto.setFrom(text);
        highlightParamDto.setPersonMadeBy(new PersonDto(person));
        highlightParamDto.setLocality(new LocalityDto(locality));
        return highlightParamDto;
    }

    private Person getPerson() {
        Person person = personRepository.findById(1L).orElse(null);
        if (person == null) {
            person = new Person();
            person.setName("Person Teste");
            person = personRepository.save(person);
        }
        return person;
    }
}