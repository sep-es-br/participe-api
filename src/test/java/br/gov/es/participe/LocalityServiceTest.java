package br.gov.es.participe;

import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.*;
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

import br.gov.es.participe.controller.LocalityController;
import br.gov.es.participe.controller.dto.DomainParamDto;
import br.gov.es.participe.controller.dto.LocalityDto;
import br.gov.es.participe.controller.dto.LocalityParamDto;
import br.gov.es.participe.controller.dto.LocalityTypeDto;

import java.util.Optional;

@Testcontainers
@SpringBootTest
class LocalityServiceTest extends BaseTest {

    @Autowired
    private LocalityController localityController;

    @Autowired
    private LocalityRepository localityRepository;

    @Autowired
    private LocalityTypeRepository localityTypeRepository;

    @Autowired
    private DomainRepository domainRepository;

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
    public void clearLocalitys() {
        localityRepository.deleteAll();
        localityTypeRepository.deleteAll();
        domainRepository.deleteAll();
        conferenceRepository.deleteAll();
        planRepository.deleteAll();
    }

    @Test
    public void shouldListGetLocalitys() {
        ResponseEntity response = localityController.index(null, null);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldCreateLocality() {
        LocalityParamDto localityParamDto = createLocalityParamDto("Test", null, getLocalityTypeTest("Coutry"));

        ResponseEntity response = localityController.store(localityParamDto);

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldCreateLocalityWhenDomainHasLocalities() {
        LocalityType localityType = getLocalityTypeTest("Coutry");
        LocalityParamDto localityParamDto = createLocalityParamDto("Test",null, localityType);
        localityController.store(localityParamDto);

        Optional<Domain> domainOpt = domainRepository.findById(localityParamDto.getDomain().getId());
        Domain domain = domainOpt.get();

        LocalityParamDto localityParamDto2 = createLocalityParamDto("Test", domain, localityType);
        ResponseEntity response = localityController.store(localityParamDto2);
        Assert.assertEquals(200, response.getStatusCodeValue());

        LocalityParamDto localityParamDto3 = createLocalityParamDto("Test", domain, getLocalityTypeTest("Coutry"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> localityController.store(localityParamDto3));
    }

    @Test
    public void shouldTryToCreateLocalityWhenThereIsHierarchy() {
        LocalityType localityType = getLocalityTypeTest("Country");
        LocalityParamDto localityParamDto = createLocalityParamDto("Test",null, localityType);
        localityController.store(localityParamDto);

        Optional<Domain> domainOpt = domainRepository.findById(localityParamDto.getDomain().getId());
        Domain domain = domainOpt.get();

        LocalityParamDto localityParamDto2 = createLocalityParamDto("Test", domain, localityType);
        localityController.store(localityParamDto2);

        LocalityType localityType2 = getLocalityTypeTest("Municipio");
        LocalityParamDto localityParamDto3 = createFreeLocalityParamDto("IcanSeeTheValkyriesAlready", domain, localityType2);
        LocalityParamDto localityParamDto4 = createFreeLocalityParamDto("Glorious", domain, localityType2);

        localityParamDto3.setParent(localityParamDto);
        Assertions.assertThrows(IllegalArgumentException.class, () -> localityController.store(localityParamDto3));
        localityParamDto4.setParent(localityParamDto2);
        Assertions.assertThrows(IllegalArgumentException.class, () -> localityController.store(localityParamDto4));
    }

    @Test
    public void shouldCreateLocalityCheckingIfExist() {
        LocalityParamDto localityParamDto = createLocalityParamDto("Test", null, getLocalityTypeTest("Coutry"));
        localityController.store(localityParamDto);
        ResponseEntity response = localityController.store(localityParamDto);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldUpdateLocality() {
        LocalityParamDto localityParamDto = createLocalityParamDto("Test", null, getLocalityTypeTest("Coutry"));
        Locality localityParam = new Locality(localityParamDto);
        LocalityDto localityDto = (LocalityDto) localityController.store(localityParamDto).getBody();
        Locality locality = new Locality(localityDto);
        Assert.assertEquals(localityParam.getName(), locality.getName());
        localityParamDto.setId(localityDto.getId());
        ResponseEntity response = localityController.update(localityParamDto.getId(), localityParamDto);

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFindLocality() {
        LocalityParamDto localityParamDto = createLocalityParamDto("Test", null, getLocalityTypeTest("Coutry"));
        LocalityDto localityDto = (LocalityDto) localityController.store(localityParamDto).getBody();
        ResponseEntity response = localityController.show(localityDto.getId());

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFailToFindLocalityWithoutId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> localityController.show(null));
    }

    @Test
    public void shouldFailToFindLocalityWithWrongId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> localityController.show(42L));
    }

    @Test
    public void shouldDeleteLocality() {
        LocalityParamDto localityParamDto = createLocalityParamDto("Test", null, getLocalityTypeTest("Coutry"));
        LocalityDto localityDto = (LocalityDto) localityController.store(localityParamDto).getBody();

        localityController.destroy(localityDto.getId(),localityDto.getDomains().get(0).getId());

        Assertions.assertThrows(IllegalArgumentException.class, () -> localityController.show(localityDto.getId()));
    }

    @Test
    public void shouldFindLocaitiesByDomain() {
        LocalityParamDto localityParamDto = createLocalityParamDto("Test", null, getLocalityTypeTest("Coutry"));
        localityController.store(localityParamDto);

        ResponseEntity response = localityController.findByDomain(localityParamDto.getDomain().getId());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFindLocalitiesByConference() {
        LocalityParamDto localityParamDto = createLocalityParamDto("Test", null, getLocalityTypeTest("Coutry"));
        ResponseEntity<LocalityDto> localityDtoResponseEntity = localityController.store(localityParamDto);
        LocalityDto localityDto = localityDtoResponseEntity.getBody();

        Long conferenceId = getNewConferenceId(new Domain(localityParamDto.getDomain()));

        ResponseEntity response = localityController.findByIdConference(conferenceId);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFindLocalitiesToComplement() {
        LocalityParamDto localityParamDto = createLocalityParamDto("Test", null, getLocalityTypeTest("Coutry"));
        ResponseEntity<LocalityDto> localityDtoResponseEntity = localityController.store(localityParamDto);
        LocalityDto localityDto = localityDtoResponseEntity.getBody();

        Long conferenceId = getNewConferenceId(new Domain(localityParamDto.getDomain()));

        ResponseEntity response = localityController.findLocalitiesToComplement(conferenceId);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    private LocalityParamDto createLocalityParamDto(String name, Domain domain, LocalityType localityType) {
        LocalityParamDto localityParamDto = new LocalityParamDto();
        localityParamDto.setName(name);

        if(domain == null) {
            domain = domainRepository.save(new Domain("Test Domain"));
        }
        DomainParamDto domainParamDto = new DomainParamDto();
        domainParamDto.setId(domain.getId());
        domainParamDto.setName(domain.getName());
        localityParamDto.setDomain(domainParamDto);
        localityParamDto.setType(new LocalityTypeDto(localityType));

        return localityParamDto;
    }

    private LocalityParamDto createFreeLocalityParamDto(String name, Domain domain, LocalityType localityType) {
        LocalityParamDto localityParamDto = new LocalityParamDto();
        localityParamDto.setName(name);
        localityParamDto.setType(new LocalityTypeDto(localityType));
        DomainParamDto domainParamDto = new DomainParamDto();
        domainParamDto.setId(domain.getId());
        domainParamDto.setName(domain.getName());
        localityParamDto.setDomain(domainParamDto);
        return localityParamDto;
    }

    private LocalityType getLocalityTypeTest(String name) {
        LocalityType localityType = new LocalityType();
        localityType.setName(name);
        return localityTypeRepository.save(localityType);
    }

    private Long getNewConferenceId(Domain domain) {
        Plan plan = new Plan();
        plan.setDomain(domain);
        plan = planRepository.save(plan);

        Conference conference = new Conference();
        conference.setPlan(plan);
        conference = conferenceRepository.save(conference);

        return conference.getId();
    }
}