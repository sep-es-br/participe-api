package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.PreRegistrationDto;
import br.gov.es.participe.exception.QRCodeGenerateException;
import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.*;

import java.io.IOException;
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

import com.google.zxing.WriterException;

@Service
public class PreRegistrationService {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private PreRegistrationRepository preRegistrationRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private QRCodeService qrCodeService;

    public PreRegistration save(PreRegistration preRegistration, boolean update){

        PreRegistration preRegistrationStored =  preRegistrationRepository.findByMeetingAndPerson(preRegistration.getMeeting().getId(), preRegistration.getPerson().getId());

        if(preRegistrationStored != null && !update){
            
            if(preRegistrationStored.getMadeBy() == null) {
                preRegistrationStored.setMadeBy(preRegistration.getMadeBy());
                preRegistrationStored = preRegistrationRepository.save(preRegistrationStored);
            }
            
            
            
            return preRegistrationStored;
        }
        loadAttributes(preRegistration);


        final var createdPreRegistration = preRegistrationRepository.save(preRegistration);

        return createdPreRegistration;
    }
    
    
    public PreRegistration save(PreRegistration preRegistration){

        return this.save(preRegistration, false);
    }

    public PreRegistration findByMeetingAndPerson(Long meetingId, Long personId) {
        return preRegistrationRepository.findByMeetingAndPerson(meetingId, personId);
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
        if(preRegistration.getMadeBy() != null) {
            Person madeByPerson = personService.find(preRegistration.getMadeBy().getId());
            if(madeByPerson != null) {
                preRegistration.setMadeBy(madeByPerson);
            }
        }
        

    }

    public PreRegistration find(Long id) {
        return preRegistrationRepository.findPreRegistrationWithRelationshipsById(id)
            .orElseThrow(() -> new IllegalArgumentException("Meeting not found: " + id));
      }

    
    public void saveCheckIn(Long personId, Long meetingId){

        PreRegistration preRegistrationStored =  preRegistrationRepository.findByMeetingAndPerson(meetingId, personId);

        if(preRegistrationStored != null){
            preRegistrationStored.setCheckIn(new Date());
            preRegistrationRepository.save(preRegistrationStored);
        }

    }


    public Map<String, String> buildEmailBody(Meeting meeting){
        String MICROREGIAO = "microregiao";
        String LOCAL = "localDaReuniao";
        String ENDERECO = "enderecoDaReuniao";
        String MUNICIPIO = "municipio";
        String NOMEAUDIENCIA = "nomeDaAudiencia";
        String DATA = "dataDaReuniao";

        Map<String, String> data = new HashMap<>();

        data.put(MICROREGIAO, meeting.getName());
        data.put(LOCAL, meeting.getPlace());
        data.put(ENDERECO, meeting.getAddress());
        data.put(MUNICIPIO, meeting.getLocalityPlace().getName());
        data.put(NOMEAUDIENCIA, meeting.getConference().getName());
        
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
        Instant beginDateInstant = meeting.getBeginDate().toInstant();
        Instant endDateInstant = meeting.getEndDate().toInstant();
        LocalDateTime beginDate = LocalDateTime.ofInstant(beginDateInstant, ZoneId.systemDefault());
        LocalDateTime endDate = LocalDateTime.ofInstant(endDateInstant, ZoneId.systemDefault());
        
        data.put(DATA, beginDate.format(formatter)+" a "+endDate.format(formatter));


        return data;

    }

    public PreRegistrationDto getPreRegistrationByMeetingAndPerson(Long meetingId, Long personId){
            Meeting meeting = meetingService.find(meetingId);
            Person person = personService.find(personId);
            PreRegistration preRegistration = preRegistrationRepository.findByMeetingAndPerson(meeting.getId(), person.getId());
            byte[] imageQR;
            try {
                imageQR = qrCodeService.generateQRCode(preRegistration.getId().toString(), 300, 300);
            } catch (WriterException | IOException e) {
                throw new QRCodeGenerateException("Erro ao tentar recuperar seu pré-credenciamento. ");
            }
            return new PreRegistrationDto(preRegistration, imageQR);
    }
}
