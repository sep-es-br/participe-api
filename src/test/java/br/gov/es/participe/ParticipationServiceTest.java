package br.gov.es.participe;

import br.gov.es.participe.controller.CommentController;
import br.gov.es.participe.controller.ParticipationController;
import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.*;
import br.gov.es.participe.service.TokenService;
import br.gov.es.participe.util.domain.TokenType;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashSet;
import java.util.Set;

@Testcontainers
@SpringBootTest
class ParticipationServiceTest extends BaseTest {
	
    @Autowired
    private ParticipationController participationController;

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

    @Autowired
    private FileRepository fileRepository;

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
    public void clearDataBase() {
        commentRepository.deleteAll();
        personRepository.deleteAll();
        localityRepository.deleteAll();
        conferenceRepository.deleteAll();
        planRepository.deleteAll();
        planItemRepository.deleteAll();
        structureRepository.deleteAll();
        structureItemRepository.deleteAll();
        fileRepository.deleteAll();
        localityTypeRepository.deleteAll();
    }

    @Test
    public void shouldListAllParticipations() {
        Person person = getPerson();
        String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

        IdsDto ids = createDataBase("Test");
        commentController.store(token, ids.getCommentParamDto());

        ResponseEntity<ParticipationsDto> response = participationController
                .getParticipation(token,"", ids.getConferenceId(), 0,
                        UriComponentsBuilder.fromPath("https://localhost:8443/participe"));
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldListAllParticipationsWithAttends() {
        Person person = getPerson();
        String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

        IdsDto ids = createDataBase("Test");
        commentController.store(token, ids.getCommentParamDto());

        ResponseEntity<ParticipationsDto> response = participationController
                .getParticipation(token,"", ids.getConferenceId(), 0,
                        UriComponentsBuilder.fromPath("https://localhost:8443/participe"));
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldListAllBodyParticipations() {
        Person person = getPerson();
        String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

        IdsDto ids = createDataBase("Test");
        commentController.store(token, ids.getCommentParamDto());

        ResponseEntity<BodyParticipationDto> response = participationController
                .getBody(token,"", ids.getLocalityId(), null, ids.getConferenceId(),
                        UriComponentsBuilder.fromPath("https://localhost:8443/participe"));
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldListAllHeaderParticipations() {
        Person person = getPerson();
        String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

        IdsDto ids = createDataBase("Test");
        commentController.store(token, ids.getCommentParamDto());

        ResponseEntity<PortalHeader> response = participationController
                .getHeader(ids.getConferenceId(), UriComponentsBuilder.fromPath("https://localhost:8443/participe"));
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldListHighlights() {
        Person person = getPerson();
        String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

        IdsDto ids = createDataBase("Test");
        commentController.store(token, ids.getCommentParamDto());
        commentController.store(token, ids.getCommentParamDto());

        ResponseEntity<PlanItemDto> response = participationController
                .createComment(token,ids.getCommentParamDto());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFindAlternativeProposals() {
        Person person = getPerson();
        String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

        IdsDto ids = createDataBase("Test");
        commentController.store(token, ids.getCommentParamDto());

        ResponseEntity<CommentDto> response = participationController
                .alternativeProposal(token,ids.getCommentParamDto());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFindConference() {
        Person person = getPerson();
        String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

        IdsDto ids = createDataBase("Test");
        commentController.store(token, ids.getCommentParamDto());

        ResponseEntity<ConferenceDto> response = participationController
                .findAll(ids.getConferenceId(), UriComponentsBuilder.fromPath("https://localhost:8443/participe"));
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    private IdsDto createDataBase(String text) {
        Structure structure = structureRepository.save(new Structure());
        Plan plan = new Plan();
        plan.setStructure(structure);
        plan = planRepository.save(plan);

        File filePart = fileRepository.save(new File());
        Conference conference = new Conference();
        conference.setPlan(plan);
        conference.setFileParticipation(filePart);
        conference = conferenceRepository.save(conference);

        StructureItem structureItem1 = new StructureItem();
        structureItem1.setStructure(structure);
        structureItem1.setLogo(false);
        structureItem1 = structureItemRepository.save(structureItem1);
        StructureItem structureItem2 = new StructureItem();
        structureItem2.setParent(structureItem1);
        structureItem2.setLogo(false);
        structureItem2 = structureItemRepository.save(structureItem2);
        StructureItem structureItem3 = new StructureItem();
        structureItem3.setParent(structureItem2);
        structureItem3.setLogo(false);
        structureItem3 = structureItemRepository.save(structureItem3);

        LocalityType localityType = localityTypeRepository.save(new LocalityType());

        File file = fileRepository.save(new File());
        Locality locality = new Locality();
        locality.setType(localityType);
        locality = localityRepository.save(locality);
        Set<Locality> localities = new HashSet<>();
        localities.add(locality);

        PlanItem planItem1 = new PlanItem();
        planItem1.setStructureItem(structureItem1);
        planItem1.setLocalities(localities);
        planItem1.setPlan(plan);
        planItem1.setFile(file);
        planItem1 = planItemRepository.save(planItem1);
        PlanItem planItem2 = new PlanItem();
        planItem2.setParent(planItem1);
        planItem2.setStructureItem(structureItem2);
        planItem2 = planItemRepository.save(planItem2);
        PlanItem planItem3 = new PlanItem();
        planItem3.setParent(planItem2);
        planItem3.setStructureItem(structureItem3);
        planItem3 = planItemRepository.save(planItem3);

        CommentParamDto commentParamDto = new CommentParamDto();
        commentParamDto.setPlanItem(planItem3.getId());
        commentParamDto.setConference(conference.getId());
        commentParamDto.setLocality(locality.getId());
        commentParamDto.setText(text);

        IdsDto ids = new IdsDto(conference.getId(), plan.getId(), structure.getId(), planItem3.getId(),
                locality.getId(), commentParamDto);

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
        Long localityId;
        CommentParamDto commentParamDto;

        public IdsDto() {
        }

        public IdsDto(Long conferenceId, Long planId, Long structureId, Long planItemId, Long localityId,
                      CommentParamDto commentParamDto) {
            this.conferenceId = conferenceId;
            this.planId = planId;
            this.structureId = structureId;
            this.planItemId = planItemId;
            this.localityId = localityId;
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

        public Long getLocalityId() {
            return localityId;
        }

        public void setLocalityId(Long localityId) {
            this.localityId = localityId;
        }

        public CommentParamDto getCommentParamDto() {
            return commentParamDto;
        }

        public void setCommentParamDto(CommentParamDto commentParamDto) {
            this.commentParamDto = commentParamDto;
        }
    }
}