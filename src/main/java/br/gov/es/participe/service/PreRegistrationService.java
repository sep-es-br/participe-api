package br.gov.es.participe.service;

import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.*;

import java.util.Date;

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

}
