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

import br.gov.es.participe.controller.StructureItemController;
import br.gov.es.participe.controller.dto.StructureDto;
import br.gov.es.participe.controller.dto.StructureItemDto;
import br.gov.es.participe.model.Structure;
import br.gov.es.participe.model.StructureItem;
import br.gov.es.participe.repository.StructureItemRepository;
import br.gov.es.participe.repository.StructureRepository;
import br.gov.es.participe.service.StructureItemService;

@Testcontainers
@SpringBootTest
class StructureItemServiceTest extends BaseTest {

    @Autowired
    private StructureItemService structureItemService;

    @Autowired
    private StructureItemController structureItemController;

    @Autowired
    private StructureItemRepository structureItemRepository;

    @Autowired
    private StructureRepository structureRepository;

    @TestConfiguration
    static class Config {

        @Bean
        public Configuration configuration() {
            return new Configuration.Builder().uri(databaseServer.getBoltUrl()).build();
        }
    }

    @BeforeEach
    public void clearStructureItems() {
        structureItemRepository.deleteAll();
    }

    @Test
    public void shouldListAllStructureItems() {
        Collection<StructureItem> list = structureItemService.findAll();
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void shouldCreateStructureItem() {
        StructureItemDto structureItemParamDto = createStructureItemDto("Test");

        ResponseEntity response = structureItemController.store(structureItemParamDto);

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldUpdateStructureItem() {
        StructureItemDto structureItemParamDto = createStructureItemDto("Test");
        StructureItemDto structureItemDto = (StructureItemDto) structureItemController.store(structureItemParamDto).getBody();

        structureItemParamDto.setId(structureItemDto.getId());
        ResponseEntity response = structureItemController.update(structureItemParamDto.getId(), structureItemParamDto);

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFindStructureItem() {
        StructureItemDto structureItemParamDto = createStructureItemDto("Test");
        StructureItemDto structureItemDto = (StructureItemDto) structureItemController.store(structureItemParamDto).getBody();

        ResponseEntity response = structureItemController.show(structureItemDto.getId());

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFailToFindStructureItemWithoutId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> structureItemController.show(null));
    }

    @Test
    public void shouldFailToFindStructureItemWithWrongId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> structureItemController.show(42L));
    }

    @Test
    public void shouldDeleteStructureItem() {
        StructureItemDto structureItemParamDto = createStructureItemDto("Test");
        StructureItemDto structureItemDto = (StructureItemDto) structureItemController.store(structureItemParamDto).getBody();

        structureItemController.destroy(structureItemDto.getId());

        Assertions.assertThrows(IllegalArgumentException.class, () -> structureItemController.show(structureItemDto.getId()));
    }

    private StructureItemDto createStructureItemDto(String name) {
        StructureItemDto structureItemDto = new StructureItemDto();
        structureItemDto.setName(name);
        Structure structure = new Structure();
        structure.setName("Test Structure");
        StructureDto structureDto = new StructureDto(structureRepository.save(structure), false);
        structureItemDto.setStructure(structureDto);
        return structureItemDto;
    }
}