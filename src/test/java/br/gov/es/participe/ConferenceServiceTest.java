package br.gov.es.participe;

import java.util.Date;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import br.gov.es.participe.controller.ConferenceController;
import br.gov.es.participe.controller.dto.ConferenceDto;
import br.gov.es.participe.controller.dto.ConferenceParamDto;
import br.gov.es.participe.controller.dto.PlanParamDto;
import br.gov.es.participe.model.Plan;
import br.gov.es.participe.repository.ConferenceRepository;
import br.gov.es.participe.repository.PlanRepository;

@Testcontainers
@SpringBootTest
class ConferenceServiceTest extends BaseTest {

    @Autowired
    private ConferenceController conferenceController;

    @Autowired
    private ConferenceRepository conferenceRepository;

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
    public void clearConferences() {
        conferenceRepository.deleteAll();
    }

    @Test
    public void shouldListAllConferences() {
        ResponseEntity response = conferenceController.index("", null, null,null);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldCreateConference() {
        ConferenceParamDto conferenceParamDto = createConferenceParamDto("Test");

        ResponseEntity response = conferenceController.store(conferenceParamDto);

        Assert.assertEquals(200, response.getStatusCodeValue());
    }


    @Test
    public void shouldUpdateConference() {
        ConferenceParamDto conferenceParamDto = createConferenceParamDto("Test");
        ConferenceDto conferenceDto = (ConferenceDto) conferenceController.store(conferenceParamDto).getBody();
        conferenceDto.setName("Test 2");
        ResponseEntity response = conferenceController.update(conferenceParamDto.getId(), conferenceDto);

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFindConference() {
        ConferenceParamDto conferenceParamDto = createConferenceParamDto("Test");
        ConferenceDto conferenceDto = (ConferenceDto) conferenceController.store(conferenceParamDto).getBody();

        ResponseEntity response = conferenceController.show(conferenceDto.getId());

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFailToFindConferenceWithoutId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> conferenceController.show(null));
    }

    @Test
    public void shouldFailToFindConferenceWithWrongId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> conferenceController.show(42L));
    }

    @Test
    public void shouldDeleteConference() {
        ConferenceParamDto conferenceParamDto = createConferenceParamDto("Test");
        ConferenceDto conferenceDto = (ConferenceDto) conferenceController.store(conferenceParamDto).getBody();

        conferenceController.destroy(conferenceDto.getId());

        Assertions.assertThrows(IllegalArgumentException.class, () -> conferenceController.show(conferenceDto.getId()));
    }

    private ConferenceParamDto createConferenceParamDto(String name) {
        ConferenceParamDto conferenceParamDto = new ConferenceParamDto();
        conferenceParamDto.setDescription("Description test");
        conferenceParamDto.setBeginDate(new Date());
        conferenceParamDto.setEndDate(new Date());
        conferenceParamDto.setName(name);
        Plan plan = planRepository.save(new Plan());
        conferenceParamDto.setPlan(new PlanParamDto());
        conferenceParamDto.getPlan().setId(plan.getId());
        return conferenceParamDto;
    }
}