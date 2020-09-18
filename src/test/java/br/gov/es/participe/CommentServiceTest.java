package br.gov.es.participe;

import java.util.List;

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

import br.gov.es.participe.controller.CommentController;
import br.gov.es.participe.controller.dto.CommentDto;
import br.gov.es.participe.controller.dto.CommentParamDto;
import br.gov.es.participe.controller.dto.PersonDto;
import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.repository.CommentRepository;
import br.gov.es.participe.repository.ConferenceRepository;
import br.gov.es.participe.repository.LocalityRepository;
import br.gov.es.participe.repository.PersonRepository;
import br.gov.es.participe.repository.PlanItemRepository;
import br.gov.es.participe.service.TokenService;
import br.gov.es.participe.util.domain.TokenType;

@Testcontainers
@SpringBootTest
class CommentServiceTest extends BaseTest {
	
    @Autowired
    private CommentController commentController;

    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private PersonRepository personRepository;
    
    @Autowired
    private LocalityRepository localityRepository;
    
    @Autowired
    private ConferenceRepository conferenceRepository;
    
    @Autowired
    private PlanItemRepository planItemRepository;

    @TestConfiguration
    static class Config {

        @Bean
        public Configuration configuration() {
            return new Configuration.Builder().uri(databaseServer.getBoltUrl()).build();
        }
    }

    @BeforeEach
    public void clearComments() {
        commentRepository.deleteAll();
        personRepository.deleteAll();
        localityRepository.deleteAll();
        conferenceRepository.deleteAll();
        planItemRepository.deleteAll();
    }

    @Test
    public void shouldListAllComments() {
        ResponseEntity<List<CommentDto>> response = commentController.index(1L, 1L);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldCreateComment() {
    	Person person = getPerson();
    	String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);
        CommentParamDto commentParamDto = createCommentParamDto("Test");
        ResponseEntity<CommentDto> response = commentController.store(token, commentParamDto);

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldDeleteComment() {
    	CommentParamDto commentParamDto = createCommentParamDto("Test");
    	commentParamDto.setPerson(new PersonDto(personRepository.save(new Person())));
        CommentDto commentDto = (CommentDto) commentController.storeFatherPlanItemNode(commentParamDto).getBody();

        ResponseEntity<Void> response = commentController.delete(commentDto.getId());

        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    private CommentParamDto createCommentParamDto(String text) {
        CommentParamDto commentParamDto = new CommentParamDto();
        Locality locality =  localityRepository.save(new Locality());
        Conference conference = conferenceRepository.save(new Conference());
        PlanItem planItemParent = planItemRepository.save(new PlanItem());
        PlanItem planItem = new PlanItem();
        planItem.setParent(planItemParent);
        planItem = planItemRepository.save(planItem);
        commentParamDto.setPlanItem(planItem.getId());
        commentParamDto.setConference(conference.getId());
        commentParamDto.setLocality(locality.getId());
        commentParamDto.setText(text);
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