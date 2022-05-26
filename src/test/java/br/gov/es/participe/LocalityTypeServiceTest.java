package br.gov.es.participe;

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

import br.gov.es.participe.controller.LocalityTypeController;
import br.gov.es.participe.repository.LocalityTypeRepository;

@Testcontainers
@SpringBootTest
class LocalityTypeServiceTest extends BaseTest {

    @Autowired
    private LocalityTypeController localityTypeController;

    @Autowired
    private LocalityTypeRepository localityTypeRepository;

    @TestConfiguration
    static class Config {

        @Bean
        public Configuration configuration() {
            return new Configuration.Builder().uri(databaseServer.getBoltUrl()).build();
        }
    }

    @BeforeEach
    public void clearLocalityTypes() {
        localityTypeRepository.deleteAll();
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void shouldListAllLocalityTypes() {
        ResponseEntity response = localityTypeController.index();
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

}