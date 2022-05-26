package br.gov.es.participe;

import br.gov.es.participe.model.Conference;
import br.gov.es.participe.repository.AttendRepository;
import br.gov.es.participe.repository.ConferenceRepository;
import br.gov.es.participe.service.*;
import br.gov.es.participe.util.ParticipeUtils;
import br.gov.es.participe.util.domain.StatusConferenceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class SchedulerTest {

  @Mock
  private ConferenceRepository conferenceRepository;
  @Mock
  private PlanService planService;
  @Mock
  private FileService fileService;
  @Mock
  private LocalityTypeService localityTypeService;
  @Mock
  private PlanItemService planItemService;
  @Mock
  private CommentService commentService;
  @Mock
  private HighlightService highlightService;
  @Mock
  private AttendRepository attendRepository;
  @Mock
  private LocalityService localityService;
  @Mock
  private PersonService personService;
  @Mock
  private ExternalContentService externalContentService;
  @Mock
  private IsLinkedByService isLinkedByService;
  @Mock
  private TopicService topicService;
  @Mock
  private PortalServerService portalServerService;
  @Mock
  private StructureItemService structureItemService;
  @Mock
  private ResearchService researchService;
  @Mock
  private StructureService structureService;
  @Mock
  private ParticipeUtils participeUtils;

  @InjectMocks
  private ConferenceService service;
  private Conference conference;
  // private LocalDateTime now;

  @BeforeEach
  void setUp() {
    this.conference = new Conference();
  }

  @Test
  void shouldChangeConferenceToPreOpening() {
    LocalDateTime now = LocalDateTime.now();
    final Date begin = Date.from(now.plusHours(2).atZone(ZoneId.systemDefault()).toInstant());
    final Date end = Date.from(now.plusHours(5).atZone(ZoneId.systemDefault()).toInstant());

    this.conference.setBeginDate(begin);
    this.conference.setEndDate(end);
    this.conference.setDisplayMode("AUTOMATIC");
    this.conference.setStatusType(StatusConferenceType.OPEN);

    final List<Conference> conferences = asList(this.conference);

    Mockito.when(this.conferenceRepository.saveAll(any())).thenReturn(null);
    Mockito.when(this.conferenceRepository.findAllAutomatic()).thenReturn(conferences);

    this.service.updateAutomaticConference();

    assertEquals(StatusConferenceType.PRE_OPENING, this.conference.getStatusType());
  }

  @Test
  void shouldChangeConferenceToPostClosure() {
    LocalDateTime now = LocalDateTime.now();
    final Date begin = Date.from(now.minusDays(2).atZone(ZoneId.systemDefault()).toInstant());
    final Date end = Date.from(now.minusDays(1).atZone(ZoneId.systemDefault()).toInstant());

    this.conference.setBeginDate(begin);
    this.conference.setEndDate(end);
    this.conference.setDisplayMode("AUTOMATIC");
    this.conference.setStatusType(StatusConferenceType.OPEN);

    final List<Conference> conferences = asList(this.conference);

    Mockito.when(this.conferenceRepository.saveAll(any())).thenReturn(null);
    Mockito.when(this.conferenceRepository.findAllAutomatic()).thenReturn(conferences);

    this.service.updateAutomaticConference();

    assertEquals(StatusConferenceType.POST_CLOSURE, this.conference.getStatusType());
  }

  @Test
  void shouldChangeConferenceToOpen() {
    LocalDateTime now = LocalDateTime.now();
    final Date begin = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
    final Date end = Date.from(now.plusHours(5).atZone(ZoneId.systemDefault()).toInstant());

    this.conference.setBeginDate(begin);
    this.conference.setEndDate(end);
    this.conference.setDisplayMode("AUTOMATIC");
    this.conference.setStatusType(StatusConferenceType.PRE_OPENING);

    final List<Conference> conferences = asList(this.conference);

    Mockito.when(this.conferenceRepository.saveAll(any())).thenReturn(null);
    Mockito.when(this.conferenceRepository.findAllAutomatic()).thenReturn(conferences);

    this.service.updateAutomaticConference();

    assertEquals(StatusConferenceType.OPEN, this.conference.getStatusType());
  }

}
