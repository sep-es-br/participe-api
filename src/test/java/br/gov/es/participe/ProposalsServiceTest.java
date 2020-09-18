package br.gov.es.participe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.gov.es.participe.controller.CommentController;
import br.gov.es.participe.controller.dto.CommentDto;
import br.gov.es.participe.controller.dto.CommentParamDto;
import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.*;
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
import br.gov.es.participe.controller.ProposalsController;
import br.gov.es.participe.service.TokenService;
import br.gov.es.participe.util.domain.TokenType;

@Testcontainers
@SpringBootTest
class ProposalsServiceTest extends BaseTest {

    @Autowired
    private CommentController commentController;

    @Autowired
    private ProposalsController proposalsController;
    
    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private PersonRepository personRepository;
    
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LocalityRepository localityRepository;
    
    @Autowired
    private ConferenceRepository conferenceRepository;
    
    @Autowired
    private PlanItemRepository planItemRepository;

    @Autowired
    private LocalityTypeRepository localityTypeRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private PlanRepository planRepository;

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
    public void clearProposalss() {
        personRepository.deleteAll();
        localityRepository.deleteAll();
        conferenceRepository.deleteAll();
        planItemRepository.deleteAll();
        commentRepository.deleteAll();
        localityTypeRepository.deleteAll();
        domainRepository.deleteAll();
        planRepository.deleteAll();
        structureItemRepository.deleteAll();
    }

    @Test
    public void shouldListProposal() {
    	Person person = getPerson();
    	String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

        CommentParamDto commentParamDto = createCommentParamDto("teste", null);
        List<Long> localities = new ArrayList<>();
        localities.add(commentParamDto.getLocality());
        List<Long> planItems = new ArrayList<>();
        planItems.add(commentParamDto.getPlanItem());

        Long[] localitiesArray = localities.toArray(new Long[0]);
        Long[] planItemsArray = planItems.toArray(new Long[0]);

        ResponseEntity response = proposalsController.listProposal(commentParamDto.getConference(), token, "",
                localitiesArray, planItemsArray, 0);
        Assert.assertEquals(200, response.getStatusCodeValue());

        commentController.store(token, commentParamDto);
        ResponseEntity response2 = proposalsController.listProposal(commentParamDto.getConference(), token, "",
                localitiesArray, planItemsArray, 0);
        Assert.assertEquals(200, response2.getStatusCodeValue());
    }

    @Test
    public void shouldGetFilters() {
        CommentParamDto commentParamDto = createCommentParamDto("teste", null);

        ResponseEntity response = proposalsController.getFilters(commentParamDto.getConference());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldMakelike() {
    	Person person = getPerson();
    	String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);
        CommentParamDto commentParamDto = createCommentParamDto("teste", null);
        ResponseEntity<CommentDto> commentResponseEntity = commentController.store(token, commentParamDto);
        CommentDto commentDto = commentResponseEntity.getBody();

        ResponseEntity response = proposalsController.makeLike(token, commentDto.getId());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldListMultipleProposals() {
        Person person = getPerson();
        String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

        CommentParamDto commentParamDto = createCommentParamDto("teste", null);
        CommentParamDto commentParamDto2 = createCommentParamDto("teste2", commentParamDto.getConference());
        List<Long> localities = new ArrayList<>();
        List<Long> planItems = new ArrayList<>();

        localities.add(commentParamDto.getLocality());
        localities.add(commentParamDto2.getLocality());
        planItems.add(commentParamDto.getPlanItem());
        planItems.add(commentParamDto2.getPlanItem());

        Long[] localitiesArray = localities.toArray(new Long[0]);
        Long[] planItemsArray = planItems.toArray(new Long[0]);

        commentController.store(token, commentParamDto);
        commentController.store(token, commentParamDto2);
        ResponseEntity response = proposalsController.listProposal(commentParamDto.getConference(), token, "",
                localitiesArray, planItemsArray, 0);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    private CommentParamDto createCommentParamDto(String text, Long conferenceId) {
        Domain domain = domainRepository.save(new Domain());

        LocalityType localityType = localityTypeRepository.save(new LocalityType());
        Locality locality = new Locality();
        locality.setType(localityType);
        locality.addDomain(domain);
        locality = localityRepository.save(locality);

        Plan plan = new Plan();
        plan.setDomain(domain);
        plan = planRepository.save(plan);

        Conference conference;
        if(conferenceId == null) {
            conference = new Conference();
            conference.setPlan(plan);
            conference = conferenceRepository.save(conference);
        } else {
            Optional<Conference> confOpt = conferenceRepository.findById(conferenceId);
            conference = confOpt.get();
        }

        StructureItem structureItem = structureItemRepository.save(new StructureItem());
        StructureItem structureItemParent = structureItemRepository.save(new StructureItem());

        PlanItem planItemParent = new PlanItem();
        planItemParent.setPlan(plan);
        planItemParent.setStructureItem(structureItemParent);
        planItemParent = planItemRepository.save(planItemParent);

        PlanItem planItem = new PlanItem();
        planItem.setParent(planItemParent);
        planItem.setPlan(plan);
        planItem.setStructureItem(structureItem);
        planItem = planItemRepository.save(planItem);

        CommentParamDto commentParamDto = new CommentParamDto();
        commentParamDto.setPlanItem(planItem.getId());
        commentParamDto.setConference(conference.getId());
        commentParamDto.setLocality(locality.getId());
        commentParamDto.setText(text);
        commentParamDto.setStatus("pub");
        return commentParamDto;
    }
    
    private Person getPerson() {
    	Person person = personRepository.findById(1L).orElse(null);
    	if (person == null) {
    		person = new Person();
    		person.setName("Person Teste");
    		person = personRepository.save(person);
    	}
    	return person;
    }
}