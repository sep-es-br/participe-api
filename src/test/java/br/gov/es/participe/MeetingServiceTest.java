package br.gov.es.participe;

import br.gov.es.participe.controller.MeetingController;
import br.gov.es.participe.controller.PersonController;
import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Meeting;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.repository.*;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Testcontainers
@SpringBootTest
class MeetingServiceTest extends BaseTest {

    @Autowired
    private PersonController personController;

    @Autowired
    private MeetingController meetingController;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private SelfDeclarationRepository selfDeclarationRepository;

    @Autowired
    private ConferenceRepository conferenceRepository;

    @Autowired
    private LocalityRepository localityRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @TestConfiguration
    static class Config {

        @Bean
        public Configuration configuration() {
            return new Configuration.Builder().uri(databaseServer.getBoltUrl()).build();
        }
    }

    @BeforeEach
    public void clearDataBase() {
        personRepository.deleteAll();
        selfDeclarationRepository.deleteAll();
        conferenceRepository.deleteAll();
        localityRepository.deleteAll();
        meetingRepository.deleteAll();
    }

    @Test
    public void shouldCreateMeeting() throws ParseException {
        MeetingParamDto meetingParamDto = getMeetingParamDto();
        ResponseEntity response = meetingController.store(meetingParamDto);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFindMeeting() throws ParseException {
        MeetingParamDto meetingParamDto = getMeetingParamDto();
        meetingController.store(meetingParamDto);

        ResponseEntity response = meetingController.index(PageRequest.of(0, 30),
                meetingParamDto.getConference(), "", null, null, null);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldFindDashboard() throws ParseException {
        MeetingParamDto meetingParamDto = getMeetingParamDto();
        meetingController.store(meetingParamDto);

        ResponseEntity response = meetingController.dashboardIndex(meetingParamDto.getConference());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldUpdateMeeting() throws ParseException {
        MeetingParamDto meetingParamDto = getMeetingParamDto();
        ResponseEntity<MeetingDto> meetingEntity = meetingController.store(meetingParamDto);

        Optional<Meeting> meetingOpt = meetingRepository.findById(meetingEntity.getBody().getId());
        Meeting meeting = meetingOpt.get();
        Person person = getPerson();
        Set<Person> personSet = new HashSet<>();
        personSet.add(person);
        meeting.setReceptionists(personSet);
        meeting = meetingRepository.save(meeting);

        meetingParamDto.setName("updated");

        ResponseEntity response = meetingController.update(meetingEntity.getBody().getId(), meetingParamDto);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldUpdateMeetingInformingReceptionist() throws ParseException {
        MeetingParamDto meetingParamDto = getMeetingParamDto();
        ResponseEntity<MeetingDto> meetingEntity = meetingController.store(meetingParamDto);

        Optional<Meeting> meetingOpt = meetingRepository.findById(meetingEntity.getBody().getId());
        Meeting meeting = meetingOpt.get();
        Person person = getPerson();
        Set<Person> personSet = new HashSet<>();
        personSet.add(person);
        meeting.setReceptionists(personSet);
        meeting = meetingRepository.save(meeting);

        meetingParamDto.setName("updated");
        Person person2 = getPerson();
        List<Long> idsPersonList = new ArrayList<>();
        idsPersonList.add(person2.getId());
        meetingParamDto.setReceptionists(idsPersonList);

        ResponseEntity response = meetingController.update(meetingEntity.getBody().getId(), meetingParamDto);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldDeleteMeeting() throws ParseException {
        MeetingParamDto meetingParamDto = getMeetingParamDto();
        ResponseEntity<MeetingDto> meetingEntity = meetingController.store(meetingParamDto);

        ResponseEntity response = meetingController.destroy(meetingEntity.getBody().getId());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldStoreParticipationOnMeeting() throws ParseException {
        MeetingParamDto meetingParamDto = getMeetingParamDto();
        PersonParamDto personParamDto = getPersonParamDto();

        ResponseEntity<PersonDto> personEntity = personController.store(personParamDto);
        ResponseEntity<MeetingDto> meetingEntity = meetingController.store(meetingParamDto);

        CheckInParamDto checkinParamDto = new CheckInParamDto(personEntity.getBody().getId(),
                meetingEntity.getBody().getId());
        ResponseEntity response = meetingController.checkInOnMeeting(checkinParamDto);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldNotAllowDuplicateParticipations() throws ParseException {
        MeetingParamDto meetingParamDto = getMeetingParamDto();
        PersonParamDto personParamDto = getPersonParamDto();

        ResponseEntity<PersonDto> personEntity = personController.store(personParamDto);
        ResponseEntity<MeetingDto> meetingEntity = meetingController.store(meetingParamDto);

        CheckInParamDto checkinParamDto = new CheckInParamDto(personEntity.getBody().getId(),
                meetingEntity.getBody().getId());
        ResponseEntity<CheckedInAtDto> checkinEntity = meetingController
                .checkInOnMeeting(checkinParamDto);

        Assertions.assertThrows(IllegalArgumentException.class, () -> meetingController
                .checkInOnMeeting(checkinParamDto));
    }

    @Test
    public void shouldNotDeleteMeetingWithPeopleParticipating() throws ParseException {
        MeetingParamDto meetingParamDto = getMeetingParamDto();
        PersonParamDto personParamDto = getPersonParamDto();

        ResponseEntity<PersonDto> personEntity = personController.store(personParamDto);
        ResponseEntity<MeetingDto> meetingEntity = meetingController.store(meetingParamDto);

        CheckInParamDto checkinParamDto = new CheckInParamDto(personEntity.getBody().getId(),
                meetingEntity.getBody().getId());
        ResponseEntity<CheckedInAtDto> checkinEntity = meetingController
                .checkInOnMeeting(checkinParamDto);

        Assertions.assertThrows(IllegalArgumentException.class, () -> meetingController
                .destroy(checkinEntity.getBody().getMeeting().getId()));
    }

    @Test
    public void shouldFindMeetingParticipants() throws ParseException {
        MeetingParamDto meetingParamDto = getMeetingParamDto();
        PersonParamDto personParamDto = getPersonParamDto();

        ResponseEntity<PersonDto> personEntity = personController.store(personParamDto);
        ResponseEntity<MeetingDto> meetingEntity = meetingController.store(meetingParamDto);

        ResponseEntity<Page<PersonMeetingDto>> emptyResponse = meetingController
                .findMeetingParticipants(meetingEntity.getBody().getId(),
                        new ArrayList<Long>(), "", PageRequest.of(0, 30));
        Assert.assertEquals(0, emptyResponse.getBody().getTotalElements());

        CheckInParamDto checkinParamDto = new CheckInParamDto(personEntity.getBody().getId(),
                meetingEntity.getBody().getId());
        ResponseEntity checkedInEntity = meetingController.checkInOnMeeting(checkinParamDto);

        ResponseEntity responseOk = meetingController.findMeetingParticipants(meetingEntity.getBody().getId(),
                new ArrayList<Long>(), "", PageRequest.of(0, 30));
        Assert.assertEquals(200, responseOk.getStatusCodeValue());
    }

    @Test
    public void shouldFindNumberOfParticipants() throws ParseException {
        MeetingParamDto meetingParamDto = getMeetingParamDto();
        PersonParamDto personParamDto = getPersonParamDto();

        ResponseEntity<PersonDto> personEntity = personController.store(personParamDto);
        ResponseEntity<MeetingDto> meetingEntity = meetingController.store(meetingParamDto);

        ResponseEntity<Long> emptyResponse = meetingController
                .findMeetingParticipantsNumber(meetingEntity.getBody().getId());
        Assert.assertEquals(200, emptyResponse.getStatusCodeValue());

        CheckInParamDto checkinParamDto = new CheckInParamDto(personEntity.getBody().getId(),
                meetingEntity.getBody().getId());
        ResponseEntity checkedInEntity = meetingController.checkInOnMeeting(checkinParamDto);

        ResponseEntity responseOk = meetingController.findMeetingParticipantsNumber(meetingEntity.getBody().getId());
        Assert.assertEquals(200, responseOk.getStatusCodeValue());
    }

    @Test
    public void removeParticipation() throws ParseException {
        MeetingParamDto meetingParamDto = getMeetingParamDto();
        PersonParamDto personParamDto = getPersonParamDto();

        ResponseEntity<PersonDto> personEntity = personController.store(personParamDto);
        ResponseEntity<MeetingDto> meetingEntity = meetingController.store(meetingParamDto);

        CheckInParamDto checkinParamDto = new CheckInParamDto(personEntity.getBody().getId(),
                meetingEntity.getBody().getId());
        ResponseEntity checkedInEntity = meetingController.checkInOnMeeting(checkinParamDto);

        ResponseEntity response = meetingController.removeMeetingParticipation(personEntity.getBody().getId(), meetingEntity.getBody().getId());
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    private MeetingParamDto getMeetingParamDto() throws ParseException {
        Conference conference = conferenceRepository.save(new Conference());
        ConferenceDto conferenceDto = new ConferenceDto();
        conferenceDto.setId(conference.getId());
        Locality locality = localityRepository.save(new Locality());
        LocalityDto localityDto = new LocalityDto();
        localityDto.setId(locality.getId());

        SelfDeclarationParamDto selfDeclarationDto = new SelfDeclarationParamDto();
        selfDeclarationDto.setConference(conferenceDto.getId());
        selfDeclarationDto.setLocality(localityDto.getId());

        PersonParamDto personParamDto = new PersonParamDto();
        personParamDto.setLogin("p1");
        personParamDto.setContactEmail("email1@gmail.com");
        personParamDto.setConfirmEmail("email1@gmail.com");
        personParamDto.setCpf("12345678901");
        personParamDto.setTelephone("991191199");
        personParamDto.setPassword("senha123");
        personParamDto.setConfirmPassword("senha123");
        personParamDto.setSelfDeclaration(selfDeclarationDto);

        MeetingParamDto meetingParamDto = new MeetingParamDto();
        meetingParamDto.setName("meeting");
        meetingParamDto.setAddress("endereco");
        meetingParamDto.setPlace("lugar");
        meetingParamDto.setLocalityPlace(locality.getId());
        List<Long> localityIds = new ArrayList<>();
        localityIds.add(locality.getId());
        meetingParamDto.setLocalityCovers(localityIds);
        meetingParamDto.setConference(conference.getId());
        Date beginDate = new SimpleDateFormat("dd/MM/yyyy").parse("01/08/2020");
        Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse("03/08/2020");
        meetingParamDto.setBeginDate(beginDate);
        meetingParamDto.setEndDate(endDate);

        return meetingParamDto;
    }

    private PersonParamDto getPersonParamDto() {
        Conference conference = conferenceRepository.save(new Conference());
        ConferenceDto conferenceDto = new ConferenceDto();
        conferenceDto.setId(conference.getId());
        Locality locality = localityRepository.save(new Locality());
        LocalityDto localityDto = new LocalityDto();
        localityDto.setId(locality.getId());

        SelfDeclarationParamDto selfDeclarationDto = new SelfDeclarationParamDto();
        selfDeclarationDto.setConference(conferenceDto.getId());
        selfDeclarationDto.setLocality(localityDto.getId());

        PersonParamDto personParamDto = new PersonParamDto();
        personParamDto.setTypeAuthentication("mail");
        personParamDto.setName("pessoa1");
        personParamDto.setLogin("p1");
        personParamDto.setContactEmail("email1@gmail.com");
        personParamDto.setConfirmEmail("email1@gmail.com");
        personParamDto.setCpf("12345678901");
        personParamDto.setTelephone("991191199");
        personParamDto.setPassword("senha123");
        personParamDto.setConfirmPassword("senha123");
        personParamDto.setSelfDeclaration(selfDeclarationDto);

        return personParamDto;
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