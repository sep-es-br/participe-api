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

import br.gov.es.participe.controller.StructureController;
import br.gov.es.participe.controller.dto.StructureDto;
import br.gov.es.participe.controller.dto.StructureParamDto;
import br.gov.es.participe.model.Structure;
import br.gov.es.participe.model.StructureItem;
import br.gov.es.participe.repository.StructureItemRepository;
import br.gov.es.participe.repository.StructureRepository;
import br.gov.es.participe.service.StructureService;

@Testcontainers
@SpringBootTest
class StructureServiceTest extends BaseTest {

    @Autowired
    private StructureService structureService;

    @Autowired
    private StructureController structureController;

    @Autowired
    private StructureRepository structureRepository;

    @Autowired
    private StructureItemRepository structureItemRepository;



    @TestConfiguration
    static class Config {

        @Bean
        public Configuration configuration() {
            return new Configuration.Builder().uri(databaseServer.getBoltUrl()).build();
        }
    }

    @BeforeEach
    public void clearStructures() {
        structureRepository.deleteAll();
    }

    @Test
    public void shouldListAllStructures() {
        Collection<Structure> list = structureService.findAll(null);
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void shouldCreateStructure() {
        StructureParamDto structureParamDto = createStructureParamDto("Test");

        ResponseEntity response = structureController.store(structureParamDto);

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldUpdateStructure() {
        StructureParamDto structureParamDto = createStructureParamDto("Test");
        StructureDto structureDto = (StructureDto) structureController.store(structureParamDto).getBody();
        Structure structure = new Structure(structureDto);
        structure.addItem(structureItemRepository.save(new StructureItem()));
        structureDto = new StructureDto(structure, true);
        Assert.assertFalse(structureDto.getItems().isEmpty());
        structure.removePlan(0L);
        structureParamDto.setId(structureDto.getId());
        ResponseEntity response = structureController.update(structureParamDto.getId(), structureParamDto);

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFindStructure() {
        StructureParamDto structureParamDto = createStructureParamDto("Test");
        StructureDto structureDto = (StructureDto) structureController.store(structureParamDto).getBody();

        ResponseEntity response = structureController.show(structureDto.getId());

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFailToFindStructureWithoutId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> structureController.show(null));
    }

    @Test
    public void shouldFailToFindStructureWithWrongId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> structureController.show(42L));
    }

    @Test
    public void shouldDeleteStructure() {
        StructureParamDto structureParamDto = createStructureParamDto("Test");
        StructureDto structureDto = (StructureDto) structureController.store(structureParamDto).getBody();

        structureController.destroy(structureDto.getId());

        Assertions.assertThrows(IllegalArgumentException.class, () -> structureController.show(structureDto.getId()));
    }

    private StructureParamDto createStructureParamDto(String name) {
        StructureParamDto structureParamDto = new StructureParamDto();
        structureParamDto.setName(name);

        return structureParamDto;
    }
}