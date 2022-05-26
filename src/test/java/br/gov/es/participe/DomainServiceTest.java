package br.gov.es.participe;

import br.gov.es.participe.controller.DomainController;
import br.gov.es.participe.controller.dto.DomainDto;
import br.gov.es.participe.controller.dto.DomainParamDto;
import br.gov.es.participe.model.Domain;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.repository.LocalityRepository;
import br.gov.es.participe.service.DomainService;
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

import java.util.Collection;

@Testcontainers
@SpringBootTest
class DomainServiceTest extends BaseTest {

    @Autowired
    private DomainService domainService;

    @Autowired
    private DomainController domainController;

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
    public void clearDomains() {
        domainService.deleteAll();
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void shouldListDomainsController() {
        ResponseEntity response = domainController.index(null);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldListAllDomains() {
        Collection<Domain> list = domainService.findAll(null);
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void shouldCreateDomain() {
        DomainParamDto domainParamDto = createDomainParamDto("Test");

        ResponseEntity response = domainController.store(domainParamDto);

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFailWhenCreatingDomainWithNullName() {
        DomainParamDto domainParamDto = createDomainParamDto(null);

        Assertions.assertThrows(IllegalArgumentException.class, () -> domainController.store(domainParamDto));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void shouldUpdateDomain() {
        DomainParamDto domainParamDto = createDomainParamDto("Test");
        DomainDto domainDto = (DomainDto) domainController.store(domainParamDto).getBody();
        Domain domain = new Domain(domainDto);
        Locality locality = localityRepository.save(new Locality());
        domain.addLocality(locality);
        Assert.assertFalse(domain.getLocalities().isEmpty());
        domainDto = new DomainDto(domain, false);
        domain.removeLocality(locality.getId());
        Assert.assertTrue(domain.getLocalities().isEmpty());
        domainParamDto.setId(domainDto.getId());
        ResponseEntity response = domainController.update(domainParamDto.getId(), domainParamDto);

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void shouldFindDomain() {
        DomainParamDto domainParamDto = createDomainParamDto("Test");
        DomainDto domainDto = (DomainDto) domainController.store(domainParamDto).getBody();

        ResponseEntity response = domainController.show(domainDto.getId());

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFailToFindDomainWithoutId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> domainController.show(null));
    }

    @Test
    public void shouldFailToFindDomainWithWrongId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> domainController.show(42L));
    }

    @Test
    public void shouldDeleteDomain() {
        DomainParamDto domainParamDto = createDomainParamDto("Test");
        DomainDto domainDto = (DomainDto) domainController.store(domainParamDto).getBody();

        domainController.destroy(domainDto.getId());

        Assertions.assertThrows(IllegalArgumentException.class, () -> domainController.show(domainDto.getId()));
    }

    private DomainParamDto createDomainParamDto(String name) {
        DomainParamDto domainParamDto = new DomainParamDto();
        domainParamDto.setName(name);

        return domainParamDto;
    }
}