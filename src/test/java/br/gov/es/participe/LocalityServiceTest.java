package br.gov.es.participe;

import java.util.Collection;

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
import br.gov.es.participe.model.Domain;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.LocalityType;
import br.gov.es.participe.repository.DomainRepository;
import br.gov.es.participe.repository.LocalityRepository;
import br.gov.es.participe.repository.LocalityTypeRepository;
import br.gov.es.participe.service.LocalityService;

@Testcontainers
@SpringBootTest
class LocalityServiceTest extends BaseTest {

    @Autowired
    private LocalityService localityService;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private LocalityController localityController;

    @Autowired
    private LocalityTypeRepository localityTypeRepository;

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
    public void clearLocalitys() {
        localityRepository.deleteAll();
    }

    @Test
    public void shouldListAllLocalitys() {
        Collection<Locality> list = localityService.findAll();
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void shouldCreateLocality() {
        LocalityParamDto localityParamDto = createLocalityParamDto("Test");

        ResponseEntity response = localityController.store(localityParamDto);

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldUpdateLocality() {
        LocalityParamDto localityParamDto = createLocalityParamDto("Test");
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
        LocalityParamDto localityParamDto = createLocalityParamDto("Test");
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
        LocalityParamDto localityParamDto = createLocalityParamDto("Test");
        LocalityDto localityDto = (LocalityDto) localityController.store(localityParamDto).getBody();

        localityController.destroy(localityDto.getId(),localityDto.getDomains().get(0).getId());

        Assertions.assertThrows(IllegalArgumentException.class, () -> localityController.show(localityDto.getId()));
    }

    private LocalityParamDto createLocalityParamDto(String name) {
        LocalityParamDto localityParamDto = new LocalityParamDto();
        localityParamDto.setName(name);
        Domain domain = domainRepository.save(new Domain("Test Domain"));
        DomainParamDto domainParamDto = new DomainParamDto();
        domainParamDto.setId(domain.getId());
        domainParamDto.setName(domain.getName());
        localityParamDto.setDomain(domainParamDto);
        localityParamDto.setType(new LocalityTypeDto(getLocalityTypeTest("Coutry")));
        return localityParamDto;
    }

    private LocalityParamDto getLocalityParamDto(String nameLocality, String nameType, LocalityParamDto locality) {
        LocalityParamDto localityParamDto = new LocalityParamDto();
        localityParamDto.setName(nameLocality);
        localityParamDto.setDomain(locality.getDomain());
        localityParamDto.setType(new LocalityTypeDto(getLocalityTypeTest(nameType)));
        return localityParamDto;
    }

    private LocalityType getLocalityTypeTest(String name) {
        LocalityType localityType = new LocalityType();
        localityType.setName(name);
        return localityTypeRepository.save(localityType);
    }
}