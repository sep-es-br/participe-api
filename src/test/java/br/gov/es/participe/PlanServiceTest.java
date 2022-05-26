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

import br.gov.es.participe.controller.PlanController;
import br.gov.es.participe.controller.dto.DomainParamDto;
import br.gov.es.participe.controller.dto.PlanDto;
import br.gov.es.participe.controller.dto.PlanParamDto;
import br.gov.es.participe.controller.dto.StructureParamDto;
import br.gov.es.participe.model.Domain;
import br.gov.es.participe.model.Plan;
import br.gov.es.participe.model.Structure;
import br.gov.es.participe.repository.DomainRepository;
import br.gov.es.participe.repository.PlanRepository;
import br.gov.es.participe.repository.StructureRepository;
import br.gov.es.participe.service.PlanService;

@Testcontainers
@SpringBootTest
class PlanServiceTest extends BaseTest {

    @Autowired
    private PlanService planService;

    @Autowired
    private PlanController planController;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private DomainRepository domainRepository;

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
    public void clearPlans() {
        planRepository.deleteAll();
    }

    @Test
    public void shouldListAllPlans() {
        Collection<Plan> list = planService.findAll(null);
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void shouldCreatePlan() {
        PlanParamDto planParamDto = createPlanParamDto("Test");

        ResponseEntity response = planController.store(planParamDto);

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void shouldUpdatePlan() {
        PlanParamDto planParamDto = createPlanParamDto("Test");
        PlanDto planDto = (PlanDto) planController.store(planParamDto).getBody();

        planParamDto.setId(planDto.getId());
        ResponseEntity response = planController.update(planParamDto.getId(), planParamDto);

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void shouldFindPlan() {
        PlanParamDto planParamDto = createPlanParamDto("Test");
        PlanDto planDto = (PlanDto) planController.store(planParamDto).getBody();

        ResponseEntity response = planController.show(planDto.getId());

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFailToFindPlanWithoutId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> planController.show(null));
    }

    @Test
    public void shouldFailToFindPlanWithWrongId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> planController.show(42L));
    }

    @Test
    public void shouldDeletePlan() {
        PlanParamDto planParamDto = createPlanParamDto("Test");
        PlanDto planDto = (PlanDto) planController.store(planParamDto).getBody();

        planController.destroy(planDto.getId());

        Assertions.assertThrows(IllegalArgumentException.class, () -> planController.show(planDto.getId()));
    }

    private PlanParamDto createPlanParamDto(String name) {
        PlanParamDto planParamDto = new PlanParamDto();
        planParamDto.setName(name);

        Domain domain = domainRepository.save(new Domain("Test Domain"));
        DomainParamDto domainParamDto = new DomainParamDto();
        domainParamDto.setId(domain.getId());
        planParamDto.setDomain(domainParamDto);

        Structure structure = structureRepository.save(new Structure());
        StructureParamDto structureParamDto = new StructureParamDto();
        structureParamDto.setId(structure.getId());
        planParamDto.setStructure(structureParamDto);

        return planParamDto;
    }

}