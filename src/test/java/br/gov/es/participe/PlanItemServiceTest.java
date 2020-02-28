package br.gov.es.participe;

import java.util.Collection;
import java.util.HashSet;

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

import br.gov.es.participe.controller.PlanItemController;
import br.gov.es.participe.controller.dto.PlanItemDto;
import br.gov.es.participe.controller.dto.PlanItemParamDto;
import br.gov.es.participe.controller.dto.PlanParamDto;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Plan;
import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.repository.LocalityRepository;
import br.gov.es.participe.repository.PlanItemRepository;
import br.gov.es.participe.repository.PlanRepository;
import br.gov.es.participe.service.PlanItemService;

@Testcontainers
@SpringBootTest
class PlanItemServiceTest extends BaseTest {

    @Autowired
    private PlanItemService planItemService;

    @Autowired
    private PlanItemController planItemController;

    @Autowired
    private PlanItemRepository planItemRepository;

    @Autowired
    private PlanRepository planRepository;

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
    public void clearPlanItems() {
        planItemRepository.deleteAll();
    }

    @Test
    public void shouldListAllPlanItems() {
        Collection<PlanItem> list = planItemService.findAll();
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void shouldCreatePlanItem() {
        PlanItemParamDto planItemParamDto = createPlanItemParamDto("Test");

        ResponseEntity response = planItemController.store(planItemParamDto);

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldUpdatePlanItem() {
        PlanItemParamDto planItemParamDto = createPlanItemParamDto("Test");
        PlanItemDto planItemDto = (PlanItemDto) planItemController.store(planItemParamDto).getBody();

        planItemParamDto.setId(planItemDto.getId());
        ResponseEntity response = planItemController.update(planItemParamDto.getId(), planItemParamDto);

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFindPlanItem() {
        PlanItemParamDto planItemParamDto = createPlanItemParamDto("Test");
        PlanItemDto planItemDto = (PlanItemDto) planItemController.store(planItemParamDto).getBody();

        ResponseEntity response = planItemController.show(planItemDto.getId());

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFailToFindPlanItemWithoutId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> planItemController.show(null));
    }

    @Test
    public void shouldFailToFindPlanItemWithWrongId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> planItemController.show(42L));
    }

    @Test
    public void shouldDeletePlanItem() {
        PlanItemParamDto planItemParamDto = createPlanItemParamDto("Test");
        PlanItemDto planItemDto = (PlanItemDto) planItemController.store(planItemParamDto).getBody();

        planItemController.destroy(planItemDto.getId());

        Assertions.assertThrows(IllegalArgumentException.class, () -> planItemController.show(planItemDto.getId()));
    }

    private PlanItemParamDto createPlanItemParamDto(String name) {
        PlanItemParamDto planItemParamDto = new PlanItemParamDto();
        planItemParamDto.setName(name);

        Plan plan = planRepository.save(new Plan());
        planItemParamDto.setPlan(new PlanParamDto());
        planItemParamDto.getPlan().setId(plan.getId());
        Locality locality = localityRepository.save(new Locality());
        planItemParamDto.setLocalitiesIds(new HashSet<>());
        planItemParamDto.getLocalitiesIds().add(locality.getId());

        return planItemParamDto;
    }
}