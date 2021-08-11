// package br.gov.es.participe;

// import java.io.IOException;
// import java.io.InputStream;
// import java.text.*;
// import java.util.*;

// import br.gov.es.participe.controller.PersonController;
// import br.gov.es.participe.controller.dto.*;
// import br.gov.es.participe.model.*;
// import br.gov.es.participe.repository.*;
// import br.gov.es.participe.service.TokenService;
// import br.gov.es.participe.util.domain.*;
// import org.junit.Assert;
// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.neo4j.ogm.config.Configuration;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.context.TestConfiguration;
// import org.springframework.context.annotation.Bean;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.mock.web.MockMultipartFile;
// import org.springframework.web.multipart.MultipartFile;
// import org.springframework.web.util.UriComponentsBuilder;
// import org.testcontainers.junit.jupiter.Testcontainers;

// import br.gov.es.participe.controller.ConferenceController;
// import br.gov.es.participe.service.FileService;

// @Testcontainers
// @SpringBootTest
// class ConferenceServiceTest extends BaseTest {

//     private static final String EMAIL = "participesep@gmail.com";
//     private static String formatDate = "dd/MM/yyyy HH:mm:ss";

//     @Autowired
//     private ConferenceController conferenceController;

//     @Autowired
//     private ConferenceRepository conferenceRepository;

//     @Autowired
//     private PlanRepository planRepository;
    
//     @Autowired
//     private FileService fileService;

//     @Autowired
//     private PersonController personController;

//     @Autowired
//     private PersonRepository personRepository;

//     @Autowired
//     private LocalityRepository localityRepository;

//     @Autowired
//     private TokenService tokenService;

//     @Autowired
//     private MeetingRepository meetingRepository;

//     @Autowired
//     private PlanItemRepository planItemRepository;

//     @TestConfiguration
//     static class Config {

//         @Bean
//         public Configuration configuration() {
//             return new Configuration.Builder().uri(databaseServer.getBoltUrl()).build();
//         }
//     }

//     @BeforeEach
//     public void clearConferences() {
//         personRepository.deleteAll();
//         planRepository.deleteAll();
//         conferenceRepository.deleteAll();
//         localityRepository.deleteAll();
//         meetingRepository.deleteAll();
//     }

//     @Test
//     public void shouldListAllConferences() throws IOException {
//         ConferenceParamDto conferenceParamDto = createConferenceParamDto("Test", false);
//         ConferenceParamDto conferenceParamDto2 = createConferenceParamDto("Test2", true);
//         conferenceController.store(conferenceParamDto);
//         conferenceController.store(conferenceParamDto2);

//         ResponseEntity response = conferenceController.index("", null, null,null);
//         Assert.assertEquals(200, response.getStatusCodeValue());
//     }

//     @Test
//     public void shouldCreateConference() throws IOException {
//         ConferenceParamDto conferenceParamDto = createConferenceParamDto("Test", false);

//         ResponseEntity response = conferenceController.store(conferenceParamDto);
//         Assert.assertEquals(200, response.getStatusCodeValue());
//     }

//     @Test
//     public void shouldCreateConferenceInformingIdNotNull() throws IOException {
//         ConferenceParamDto conferenceParamDto = createConferenceParamDto("Test", false);
//         ResponseEntity<ConferenceDto> conferenceDtoResponseEntity = conferenceController.store(conferenceParamDto);

//         conferenceParamDto.setId(conferenceDtoResponseEntity.getBody().getId());

//         ResponseEntity response = conferenceController.store(conferenceParamDto);
//         Assert.assertEquals(200, response.getStatusCodeValue());
//     }

//     @Test
//     public void shouldValidateConference() throws IOException {
//         ConferenceParamDto conferenceParamDto = createConferenceParamDto("Test", false);

//         ResponseEntity<ConferenceDto> conferenceDtoResponseEntity = conferenceController.store(conferenceParamDto);
//         ConferenceDto conferenceDto = conferenceDtoResponseEntity.getBody();

//         ResponseEntity response = conferenceController.validate(conferenceDto.getName(), conferenceDto.getId());
//         Assert.assertEquals(200, response.getStatusCodeValue());
//     }

//     @Test
//     public void shouldUpdateConference() throws IOException {
//         ConferenceParamDto conferenceParamDto = createConferenceParamDto("Test", false);
//         ConferenceDto conferenceDto = conferenceController.store(conferenceParamDto).getBody();
//         conferenceDto.setName("Test 2");
//         ResearchConfigurationDto research = new ResearchConfigurationDto();
//         research.setDisplayModeResearch(DisplayModeType.AUTOMATIC);
//         research.setResearchDisplayStatus(ResearchDisplayStatusType.INACTIVE);
//         conferenceDto.setResearchConfiguration(research);
//         ResponseEntity response = conferenceController.update(conferenceParamDto.getId(), new ConferenceParamDto(conferenceDto));

//         Assert.assertEquals(200, response.getStatusCodeValue());
//     }

//     @Test
//     public void shouldFindConference() throws IOException {
//         ConferenceParamDto conferenceParamDto = createConferenceParamDto("Test", false);
//         ConferenceDto conferenceDto = conferenceController.store(conferenceParamDto).getBody();

//         ResponseEntity response = conferenceController.show(conferenceDto.getId());

//         Assert.assertEquals(200, response.getStatusCodeValue());
//     }

//     @Test
//     public void shouldFailToFindConferenceWithoutId() {
//         Assertions.assertThrows(IllegalArgumentException.class, () -> conferenceController.show(null));
//     }

//     @Test
//     public void shouldFailToFindConferenceWithWrongId() {
//         Assertions.assertThrows(IllegalArgumentException.class, () -> conferenceController.show(42L));
//     }

//     @Test
//     public void shouldDeleteConference() throws IOException {
//         ConferenceParamDto conferenceParamDto = createConferenceParamDto("Test", false);
//         ConferenceDto conferenceDto = conferenceController.store(conferenceParamDto).getBody();

//         conferenceController.destroy(conferenceDto.getId());

//         Assertions.assertThrows(IllegalArgumentException.class, () -> conferenceController.show(conferenceDto.getId()));
//     }

//     @Test
//     public void shouldGenerateAuthScreen() throws IOException {
//         ConferenceParamDto conferenceParamDto = createConferenceParamDto("Test", false);
//         ConferenceDto conferenceDto = conferenceController.store(conferenceParamDto).getBody();

//         ResponseEntity response = conferenceController
//                 .getAuthenticationScreen(conferenceDto.getId(),
//                         UriComponentsBuilder.fromPath("https://localhost:8443/participe"));
//         Assert.assertEquals(200, response.getStatusCodeValue());
//     }

//     @Test
//     public void shouldListModerators() throws IOException {
//         PersonParamDto personParamDto = getPersonParamDto();
//         ResponseEntity<PersonDto> personResponse = personController.store(personParamDto);
//         PersonDto personDto = personResponse.getBody();
//         Optional<Person> person = personRepository.findById(personDto.getId());
//         String token = "Bearer " + tokenService.generateToken(person.get(), TokenType.AUTHENTICATION);

//         ResponseEntity response = conferenceController.moderators(token, "", "");
//         Assert.assertEquals(200, response.getStatusCodeValue());
//     }

//     @Test
//     public void shouldListAllConferencesWithMeetings() {
//         Person person = getPerson();
//         String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);

//         getDataWithMeeting(person);

//         ResponseEntity response = conferenceController.findConferencesWithMeeting(token,null);
//         Assert.assertEquals(200, response.getStatusCodeValue());
//     }

//     @Test
//     public void shouldCreateConferenceWithModerators() throws IOException {
//         Person person1 = getPerson();
//         Person person2 = getPerson();
//         List<PersonDto> personDtoList = new ArrayList<>();
//         personDtoList.add(new PersonDto(person1));
//         personDtoList.add(new PersonDto(person2));

//         ConferenceParamDto conferenceParamDto = createConferenceParamDto("Test", false);
//         conferenceParamDto.setModerators(personDtoList);

//         ResponseEntity response = conferenceController.store(conferenceParamDto);
//         Assert.assertEquals(200, response.getStatusCodeValue());
//     }

//     @Test
//     public void shouldDeleteConferenceWithPlanItems() throws IOException {
//         ConferenceParamDto conferenceParamDto = createConferenceParamDto("Test", true);
//         ConferenceDto conferenceDto = conferenceController.store(conferenceParamDto).getBody();

//         conferenceController.destroy(conferenceDto.getId());
//         Assertions.assertThrows(IllegalArgumentException.class, () -> conferenceController.show(conferenceDto.getId()));
//     }

//     private ConferenceParamDto createConferenceParamDto(String name, boolean createPlanItems) throws IOException {
//         ConferenceParamDto conferenceParamDto = new ConferenceParamDto();
//         conferenceParamDto.setDescription("Description test");

//         DateFormat dateFormat = new SimpleDateFormat(formatDate);

//         conferenceParamDto.setBeginDate(dateFormat.format(new Date()));
//         conferenceParamDto.setEndDate(dateFormat.format(new Date()));
//         conferenceParamDto.setName(name);
//         conferenceParamDto.setFileAuthentication(getFileDto("/business.png"));
//         conferenceParamDto.setFileParticipation(getFileDto("/person.png"));

//         ResearchConfigurationParamDto research = new ResearchConfigurationParamDto();
//         research.setDisplayModeResearch(DisplayModeType.AUTOMATIC);
//         research.setResearchDisplayStatus(ResearchDisplayStatusType.INACTIVE);
//         conferenceParamDto.setResearchConfiguration(research);
//         conferenceParamDto.setPostClosureText("<p>Teste</p>");
//         conferenceParamDto.setPreOpeningText("<p>Teste</p>");
//         conferenceParamDto.setHowItWork(createHowItWorkList());
//         conferenceParamDto.setExternalLinks(createExternalLinks());
//         conferenceParamDto.setBackgroundImages(createBackgroundImages());
//         conferenceParamDto.setServerName("https://google.com.br");
//         conferenceParamDto.setDefaultServerConference(true);

//         Plan plan = planRepository.save(new Plan());
//         conferenceParamDto.setPlan(new PlanParamDto());
//         conferenceParamDto.getPlan().setId(plan.getId());

//         if(createPlanItems) {
//             PlanItem planItem1 = planItemRepository.save(new PlanItem());
//             PlanItem planItem2 = planItemRepository.save(new PlanItem());
//             PlanItem planItem3 = planItemRepository.save(new PlanItem());
//             planItem1.setPlan(plan);
//             planItem2.setPlan(plan);
//             planItem3.setPlan(plan);
//         }

//         return conferenceParamDto;
//     }

//     private List<FileDto> createBackgroundImages() throws IOException {
//         return Arrays.asList(
//                 getFileDto("/business.png"),
//                 getFileDto("/business.png"),
//                 getFileDto("/business.png"),
//                 getFileDto("/business.png")
//         );
//     }

//     private List<ExternalLinksDto> createExternalLinks() {
//         return Arrays.asList(
//                 new ExternalLinksDto(null, "Inicio 1", "url 1"),
//                 new ExternalLinksDto(null, "Inicio 2", "url 2"),
//                 new ExternalLinksDto(null, "Inicio 3", "url 3"),
//                 new ExternalLinksDto(null, "Inicio 4", "url 4")
//         );
//     }

//     private List<HowItWorkStepDto> createHowItWorkList() {
//         return Arrays.asList(
//                 new HowItWorkStepDto(null, 1, "Inicio 1", "Inicio Texto 1"),
//                 new HowItWorkStepDto(null, 2, "Inicio 2", "Inicio Texto 2"),
//                 new HowItWorkStepDto(null, 3, "Inicio 3", "Inicio Texto 3"),
//                 new HowItWorkStepDto(null, 4, "Inicio 4", "Inicio Texto 4")
//         );
//     }

//     private FileDto getFileDto(String fileName) throws IOException {
//     	InputStream is = getClass().getResourceAsStream(fileName);
//     	MultipartFile file = new MockMultipartFile(fileName, fileName, MediaType.APPLICATION_PDF_VALUE, is);
//         return fileService.save(file);
//     }

//     private PersonParamDto getPersonParamDto() {
//         Conference conference = getConference();
//         ConferenceDto conferenceDto = new ConferenceDto();
//         conferenceDto.setId(conference.getId());
//         Locality locality = localityRepository.save(new Locality());
//         LocalityDto localityDto = new LocalityDto();
//         localityDto.setId(locality.getId());

//         SelfDeclarationParamDto selfDeclarationDto = new SelfDeclarationParamDto();
//         selfDeclarationDto.setConference(conferenceDto.getId());
//         selfDeclarationDto.setLocality(localityDto.getId());

//         PersonParamDto personParamDto = new PersonParamDto();
//         personParamDto.setName("pessoa1");
//         personParamDto.setLogin("p1");
//         personParamDto.setContactEmail(EMAIL);
//         personParamDto.setConfirmEmail(EMAIL);
//         personParamDto.setCpf("12345678901");
//         personParamDto.setTelephone("991191199");
//         personParamDto.setPassword("senha123");
//         personParamDto.setConfirmPassword("senha123");
//         personParamDto.setSelfDeclaration(selfDeclarationDto);

//         return personParamDto;
//     }

//     private Person getPerson() {
//         Person person = personRepository.findById(1L).orElse(null);
//         if (person == null) {
//             person = new Person();
//             person.setName("Person Teste");
//             person = personRepository.save(person);
//         }
//         return person;
//     }

//     private void getDataWithMeeting(Person person) {
//         Set<Person> personSet = new HashSet<>();
//         personSet.add(person);

//         Conference conference1 = getConference();
//         Conference conference2 = getConference();

//         for(int i = 0; i<4; i++) {
//             Meeting meetingItr = new Meeting();
//             meetingItr.setParticipants(personSet);
//             if(i%2 == 0) {
//                 meetingItr.setConference(conference1);
//             } else {
//                 meetingItr.setConference(conference2);
//             }
//             meetingRepository.save(meetingItr);
//         }
//     }

//     private Conference getConference() {
//         Conference conference = new Conference();
//         conference.setTitleAuthentication("titulo");
//         conference.setSubtitleAuthentication("subtitulo");
//         conference.setTitleParticipation("titulo");
//         conference.setSubtitleParticipation("subtitulo");
//         conference.setTitleRegionalization("titulo");
//         conference.setSubtitleRegionalization("subtitulo");
//         return conferenceRepository.save(conference);
//     }
// }