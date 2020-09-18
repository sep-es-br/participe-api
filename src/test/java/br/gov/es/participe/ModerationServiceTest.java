package br.gov.es.participe;

import br.gov.es.participe.controller.CommentController;
import br.gov.es.participe.controller.ModerationController;
import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.*;
import br.gov.es.participe.service.TokenService;
import br.gov.es.participe.util.domain.TokenType;
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

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Testcontainers
@SpringBootTest
class ModerationServiceTest extends BaseTest {
	
    @Autowired
    private ModerationController moderationController;

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
    private PlanRepository planRepository;

    @Autowired
    private PlanItemRepository planItemRepository;

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
    public void clearDataBase() {
        commentRepository.deleteAll();
        personRepository.deleteAll();
        localityRepository.deleteAll();
        conferenceRepository.deleteAll();
        planRepository.deleteAll();
        planItemRepository.deleteAll();
        structureRepository.deleteAll();
        structureItemRepository.deleteAll();
    }

    @Test
    public void shouldListAllModerations() throws IOException {
        Person person = getPerson();
        String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

        IdsDto ids = createCommentParamDto("Test");
        commentController.store(token, ids.getCommentParamDto());

        ResponseEntity<List<ModerationResultDto>> response = moderationController
                .findAllCommentsByStatus(token,1L, "", "","", null,
                        null, null, null, null);
        Assert.assertEquals(200, response.getStatusCodeValue());

        ResponseEntity<List<ModerationResultDto>> response2 = moderationController
                .findAllCommentsByStatus(token,ids.getCommentParamDto().getConference(), "", "",
                        "", null, null, null, null, null);
        Assert.assertEquals(200, response2.getStatusCodeValue());

        IdsDto ids2 = createCommentParamDto("Test2");
        ids2.getCommentParamDto().setConference(ids.getCommentParamDto().getConference());

        commentController.store(token, ids2.getCommentParamDto());

        ResponseEntity<List<ModerationResultDto>> response3 = moderationController
                .findAllCommentsByStatus(token, ids.getCommentParamDto().getConference(), "", "",
                        "", null, null, null, null, null);
        Assert.assertEquals(200, response3.getStatusCodeValue());
    }

    @Test
    public void shouldFindModerationById() throws IOException {
        Person person = getPerson();
        String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

        IdsDto ids = createCommentParamDto("Test");
        commentController.store(token, ids.getCommentParamDto());

        ResponseEntity<ModerationResultDto> response = moderationController
                .findModerationResultById(ids.getCommentParamDto().getId(), ids.getConferenceId());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFailToFindModerationWithWrongId() {
        ResponseEntity<ModerationResultDto> response = moderationController.findModerationResultById(42L,42L);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFindPlanByCommentId() throws IOException {
        Person person = getPerson();
        String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

        IdsDto ids = createCommentParamDto("Test");
        ResponseEntity<CommentDto> commentDtoResponseEntity = commentController.store(token, ids.getCommentParamDto());
        CommentDto commentDto = commentDtoResponseEntity.getBody();

        ResponseEntity<PlanDto> response = moderationController
                .findPlanByCommentId(commentDto.getId());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFailToFindPlanWithoutCommentId() throws IOException {
        Person person = getPerson();
        String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

        IdsDto ids = createCommentParamDto("Test");
        commentController.store(token, ids.getCommentParamDto());

        Assertions.assertThrows(IllegalArgumentException.class, () -> moderationController
                .findPlanByCommentId(ids.getCommentParamDto().getId()));
    }

    @Test
    public void shouldFailToFindPlanWithWrongCommentId() {
        ResponseEntity<PlanDto> response = moderationController.findPlanByCommentId(42L);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldUpdateModeration() throws IOException {
        Person person = getPerson();
        String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

        IdsDto ids = createCommentParamDto("Test");
        ResponseEntity<CommentDto> commentDtoResponseEntity = commentController.store(token, ids.getCommentParamDto());
        CommentDto commentDto = commentDtoResponseEntity.getBody();

        ModerationParamDto moderationParamDto = new ModerationParamDto();
        moderationParamDto.setId(commentDto.getId());
        moderationParamDto.setText("Teste2");

        ResponseEntity<ModerationResultDto> response = moderationController
                .update(commentDto.getId(), token, moderationParamDto);
        Assert.assertEquals(200, response.getStatusCodeValue());

        ModerationParamDto moderationParamDto2 = new ModerationParamDto();
        moderationParamDto2.setId(commentDto.getId());
        moderationParamDto2.setText("Teste2");
        moderationParamDto2.setStatus("Pending");
        moderationParamDto2.setType("Presential");
        moderationParamDto2.setClassification("comment");
        moderationParamDto2.setLocality(ids.getCommentParamDto().getLocality());
        moderationParamDto2.setPlanItem(ids.getCommentParamDto().getPlanItem());

        ResponseEntity<ModerationResultDto> response2 = moderationController
                .update(commentDto.getId(), token, moderationParamDto2);
        Assert.assertEquals(200, response2.getStatusCodeValue());
    }

    @Test
    public void shouldBeginModeration() throws IOException {
        Person person = getPerson();
        String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

        IdsDto ids = createCommentParamDto("Test");
        ResponseEntity<CommentDto> commentDtoResponseEntity = commentController.store(token, ids.getCommentParamDto());
        CommentDto commentDto = commentDtoResponseEntity.getBody();

        ResponseEntity<ModerationResultDto> response = moderationController
                .begin(commentDto.getId(), token);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldBeginModerationWithModerator() throws IOException {
        Person person = getPerson();
        String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

        IdsDto ids = createCommentParamDto("Test");
        ResponseEntity<CommentDto> commentDtoResponseEntity = commentController.store(token, ids.getCommentParamDto());
        CommentDto commentDto = commentDtoResponseEntity.getBody();

        Optional<Comment> commentOpt = commentRepository.findById(commentDto.getId());
        Comment comment = commentOpt.get();
        Person moderator = getPerson();
        comment.setModerator(moderator);
        comment = commentRepository.save(comment);

        ResponseEntity<ModerationResultDto> response = moderationController
                .begin(commentDto.getId(), token);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldEndModeration() throws IOException {
        Person person = getPerson();
        String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

        IdsDto ids = createCommentParamDto("Test");
        ResponseEntity<CommentDto> commentDtoResponseEntity = commentController.store(token, ids.getCommentParamDto());
        CommentDto commentDto = commentDtoResponseEntity.getBody();

        ResponseEntity<ModerationResultDto> response = moderationController
                .end(commentDto.getId(), token);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFindConferences() throws IOException {
        Person person = getPerson();
        String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

        ResponseEntity<List<ConferenceDto>> response = moderationController
                .findConferencesAtctives(token);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFindLocalityByIdConference() throws IOException {
        Person person = getPerson();
        String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

        IdsDto ids = createCommentParamDto("Test");
        commentController.store(token, ids.getCommentParamDto());

        ResponseEntity<LeanLocalityResultDto> response = moderationController
                .findLocByIdConference(ids.getConferenceId());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFindPlanItemsByIdConference() throws IOException {
        Person person = getPerson();
        String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

        IdsDto ids = createCommentParamDto("Test");
        commentController.store(token, ids.getCommentParamDto());

        ResponseEntity<LeanPlanItemResultDto> response = moderationController
                .findPlanItemsByConference(ids.getConferenceId());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    private IdsDto createCommentParamDto(String text) throws IOException {
        Structure structure = structureRepository.save(new Structure());
        Plan plan = new Plan();
        plan.setStructure(structure);
        plan = planRepository.save(plan);

        Conference conference = new Conference();
        conference.setPlan(plan);
        conference = conferenceRepository.save(conference);

        StructureItem structureItem1 = new StructureItem();
        structureItem1.setStructure(structure);
        structureItem1 = structureItemRepository.save(structureItem1);
        StructureItem structureItem2 = new StructureItem();
        structureItem2.setParent(structureItem1);
        structureItem2 = structureItemRepository.save(structureItem2);
        StructureItem structureItem3 = new StructureItem();
        structureItem3.setParent(structureItem2);
        structureItem3 = structureItemRepository.save(structureItem3);

        PlanItem planItem1 = new PlanItem();
        planItem1.setStructureItem(structureItem1);
        planItem1.setPlan(plan);
        planItem1 = planItemRepository.save(planItem1);
        PlanItem planItem2 = new PlanItem();
        planItem2.setParent(planItem1);
        planItem2.setStructureItem(structureItem2);
        planItem2.setPlan(plan);
        planItem2 = planItemRepository.save(planItem2);
        PlanItem planItem3 = new PlanItem();
        planItem3.setParent(planItem2);
        planItem3.setStructureItem(structureItem3);
        planItem3.setPlan(plan);
        planItem3 = planItemRepository.save(planItem3);

        Locality locality =  localityRepository.save(new Locality());
        CommentParamDto commentParamDto = new CommentParamDto();
        commentParamDto.setPlanItem(planItem3.getId());
        commentParamDto.setConference(conference.getId());
        commentParamDto.setLocality(locality.getId());
        commentParamDto.setText(text);
        commentParamDto.setStatus("pen");
        commentParamDto.setType("pre");
        commentParamDto.setClassification("proposal");

        IdsDto ids = new IdsDto(conference.getId(), plan.getId(), structure.getId(), planItem3.getId(), commentParamDto);
        return ids;
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

    class IdsDto {
        Long conferenceId;
        Long planId;
        Long structureId;
        Long planItemId;
        CommentParamDto commentParamDto;

        public IdsDto() {
        }

        public IdsDto(Long conferenceId, Long planId, Long structureId, Long planItemId,
                      CommentParamDto commentParamDto) {
            this.conferenceId = conferenceId;
            this.planId = planId;
            this.structureId = structureId;
            this.planItemId = planItemId;
            this.commentParamDto = commentParamDto;
        }

        public Long getConferenceId() {
            return conferenceId;
        }

        public void setConferenceId(Long conferenceId) {
            this.conferenceId = conferenceId;
        }

        public Long getPlanId() {
            return planId;
        }

        public void setPlanId(Long planId) {
            this.planId = planId;
        }

        public Long getStructureId() {
            return structureId;
        }

        public void setStructureId(Long structureId) {
            this.structureId = structureId;
        }

        public Long getPlanItemId() {
            return planItemId;
        }

        public void setPlanItemId(Long planItemId) {
            this.planItemId = planItemId;
        }

        public CommentParamDto getCommentParamDto() {
            return commentParamDto;
        }

        public void setCommentParamDto(CommentParamDto commentParamDto) {
            this.commentParamDto = commentParamDto;
        }
    }
}