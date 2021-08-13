package br.gov.es.participe;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import br.gov.es.participe.controller.dto.ConferenceDto;
import br.gov.es.participe.controller.dto.ConferenceParamDto;
import br.gov.es.participe.controller.dto.DomainParamDto;
import br.gov.es.participe.controller.dto.FileDto;
import br.gov.es.participe.controller.dto.LocalityParamDto;
import br.gov.es.participe.controller.dto.PlanDto;
import br.gov.es.participe.controller.dto.PlanItemDto;
import br.gov.es.participe.controller.dto.PlanItemParamDto;
import br.gov.es.participe.controller.dto.PlanParamDto;
import br.gov.es.participe.controller.dto.StructureDto;
import br.gov.es.participe.controller.dto.StructureItemDto;
import br.gov.es.participe.controller.dto.StructureItemParamDto;
import br.gov.es.participe.controller.dto.StructureParamDto;
import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.File;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Plan;
import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.model.Structure;
import br.gov.es.participe.model.StructureItem;

@SpringBootTest
public class DtoTest {

    // @Test
    // public void shouldConvertConference(){
    //     ConferenceParamDto paramDto = new ConferenceParamDto();
    //     paramDto.setId(getId());
    //     paramDto.setDescription("Test");
    //     paramDto.setBeginDate(new Date().toString());
    //     paramDto.setEndDate(new Date().toString());
    //     paramDto.setName("Conference Test");
    //     paramDto.setPlan(getPlanParamDto());

    //     Conference conference = new Conference(paramDto);

    //     ConferenceDto dto = new ConferenceDto(conference);
    //     Assert.assertEquals(paramDto.getId(), dto.getId());
    //     Assert.assertNotNull(dto.getPlan().getDomain());
    // }

    @Test
    public void shouldConvertPlan() {
        PlanParamDto planParamDto = getPlanParamDto();
        Plan plan = new Plan(planParamDto);
        PlanDto planDto = new PlanDto(plan, false);
        plan = new Plan(planDto);
        Assert.assertEquals(plan.getId(), planParamDto.getId());
        Assert.assertEquals(plan.getName(), planParamDto.getName());

    }

    @Test
    public void shouldConvertPlanItem() {
        PlanParamDto planParamDto = getPlanParamDto();
        PlanItemParamDto planItemParamDto = getPlanItemParamDto(planParamDto);
        Plan plan = new Plan(planParamDto);

        PlanItem planItem = new PlanItem(planItemParamDto);
        planItem.setChildren(new HashSet<>());
        PlanItem child = new PlanItem(getPlanItemParamDto(null));
        child.setParent(planItem);
        planItem.getChildren().add(child);

        Locality locality = new Locality(getLocalityParamDto());
        planItem.setLocalities(new HashSet<>());
        planItem.getLocalities().add(locality);

        PlanItemDto dto = new PlanItemDto(planItem, plan, true);
        planItem = new PlanItem(dto);

        Assert.assertEquals(planItem.getId(), planItemParamDto.getId());
        Assert.assertEquals(planItem.getName(), planItemParamDto.getName());

    }

    @Test
    public void shouldConvertStructure() {
        StructureParamDto structureParamDto = getStructureParamDto();
        Structure structure = new Structure(structureParamDto);

        structure.addItem(new StructureItem(getStructureItemParamDto()));

        StructureDto dto = new StructureDto(structure, true);
        structure = new Structure(dto);

        Assert.assertEquals(structure.getId(), structureParamDto.getId());
        Assert.assertEquals(structure.getName(), structureParamDto.getName());
    }

    @Test
    public void shouldConvertFile() {
        FileDto dto = getFileDto();
        File file = new File(dto);
        Assert.assertEquals(file.getId(), dto.getId());
        Assert.assertEquals(file.getName(), dto.getName());
    }

    private FileDto getFileDto() {
        FileDto dto = new FileDto();
        dto.setId(getId());
        dto.setName("File Name");
        dto.setMimeType("apliccation/pdf");
        dto.setUrl("/opt/img");
        dto.setSubtype("pdt");
        return dto;
    }

    private StructureItemParamDto getStructureItemParamDto() {
        StructureItemParamDto dto = new StructureItemParamDto();
        dto.setId(getId());
        dto.setName("StructureItem Test");
        dto.setLogo(true);
        dto.setVotes(true);
        dto.setLocality(true);
        dto.setComments(true);
        return dto;

    }

    private LocalityParamDto getLocalityParamDto() {
        LocalityParamDto dto = new LocalityParamDto();
        dto.setId(getId());
        dto.setName("Locality Test");
        dto.setDomain(getDomainParamDto());
        return dto;
    }

    private PlanParamDto getPlanParamDto() {
        PlanParamDto planParamDto = new PlanParamDto();
        planParamDto.setId(getId());
        planParamDto.setStructure(getStructureParamDto());
        planParamDto.setDomain(getDomainParamDto());
        return planParamDto;
    }

    private StructureParamDto getStructureParamDto() {
        StructureParamDto dto = new StructureParamDto();
        dto.setId(getId());
        dto.setName("Structure Test");
        return dto;
    }

    private DomainParamDto getDomainParamDto() {
        DomainParamDto dto = new DomainParamDto();
        dto.setId(getId());
        dto.setName("Domain Test");
        return dto;
    }

    private PlanItemParamDto getPlanItemParamDto(PlanParamDto plan) {
        PlanItemParamDto planItem = new PlanItemParamDto();
        planItem.setId(getId());
        planItem.setPlan(plan);
        planItem.setDescription("PlanItem Description");
        planItem.setName("Name Test");
        return planItem;
    }

    private Long getId() {
        long leftLimit = 1L;
        long rightLimit = 1000L;
        return  leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
    }

}
