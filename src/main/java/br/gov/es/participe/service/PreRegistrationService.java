package br.gov.es.participe.service;

import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.DateFormatter;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PreRegistrationService {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private PreRegistrationRepository preRegistrationRepository;

    @Autowired
    private PersonService personService;

    public PreRegistration save(PreRegistration preRegistration){

        PreRegistration preRegistrationStored =  preRegistrationRepository.findByMeetingAndPerson(preRegistration.getMeeting().getId(), preRegistration.getPerson().getId());

        if(preRegistrationStored != null){
            return preRegistrationStored;
        }

        loadAttributes(preRegistration);

        final var createdPreRegistration = preRegistrationRepository.save(preRegistration);

        return createdPreRegistration;
    }

    private void loadAttributes(PreRegistration preRegistration){
        Meeting meeting = meetingService.find(preRegistration.getMeeting().getId());
        if(meeting != null){
            preRegistration.setMeeting(meeting);
        }
        Person person = personService.find(preRegistration.getPerson().getId());
        if(person != null){
            preRegistration.setPerson(person);
        }

    }

    public void saveCheckIn(Long personId, Long meetingId){

        PreRegistration preRegistrationStored =  preRegistrationRepository.findByMeetingAndPerson(meetingId, personId);

        if(preRegistrationStored != null){
            preRegistrationStored.setCheckIn(new Date());
            preRegistrationRepository.save(preRegistrationStored);
        }

    }


    public Map<String, String> buildEmailBody(Meeting meeting, byte[] imageQR){
        String MICROREGIAO = "microregiao";
        String LOCAL = "localDaReuniao";
        String ENDERECO = "enderecoDaReuniao";
        String MUNICIPIO = "municipio";
        String NOMEAUDIENCIA = "nomeDaAudiencia";
        String DATA = "dataDaReuniao";
        String QRCODE = "qrcode";

        Map<String, String> data = new HashMap<>();

        data.put(MICROREGIAO, meeting.getName());
        data.put(LOCAL, meeting.getPlace());
        data.put(ENDERECO, meeting.getAddress());
        data.put(MUNICIPIO, meeting.getLocalityPlace().getName());
        data.put(NOMEAUDIENCIA, meeting.getConference().getName());
        data.put(QRCODE, Base64.getEncoder().encodeToString(imageQR));
        
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
        Instant beginDateInstant = meeting.getBeginDate().toInstant();
        Instant endDateInstant = meeting.getEndDate().toInstant();
        LocalDateTime beginDate = LocalDateTime.ofInstant(beginDateInstant, ZoneId.systemDefault());
        LocalDateTime endDate = LocalDateTime.ofInstant(endDateInstant, ZoneId.systemDefault());
        
        data.put(DATA, beginDate.format(formatter)+" a "+endDate.format(formatter));


        return data;

    }
}
