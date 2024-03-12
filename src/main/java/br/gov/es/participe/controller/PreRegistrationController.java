package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.CheckedInAt;
import br.gov.es.participe.model.Meeting;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.PreRegistration;
import br.gov.es.participe.service.EmailService;
import br.gov.es.participe.service.MeetingService;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.service.PreRegistrationService;
import br.gov.es.participe.service.QRCodeService;

import br.gov.es.participe.util.interfaces.ApiPageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.logging.Level;
import java.util.logging.Logger;
/* Início das importações */
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.ContentDisposition;
/* Fim das importações */
import javax.imageio.ImageIO;
import javax.mail.MessagingException;

import com.google.zxing.WriterException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping(value = "/pre-registration")
public class PreRegistrationController {
    
    @Autowired
    private MeetingService meetingService;

    @Autowired
    private PersonService personService;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private PreRegistrationService preRegistrationService;

    @Autowired
    private EmailService emailService;

    @Transactional
    @PostMapping()
    public ResponseEntity<PreRegistrationDto> meetPreRegistration(
        @RequestBody PreRegistrationParamDto preRegistationDto) throws WriterException, IOException, MessagingException {
 
      Person person = personService.find(preRegistationDto.getPersonId());
      Meeting meeting = meetingService.find(preRegistationDto.getMeetingId());
      PreRegistration preRegistration = new PreRegistration(meeting, person);
      PreRegistration savedPreRegistration = preRegistrationService.save(preRegistration);
      byte[] imageQR = qrCodeService.generateQRCode(savedPreRegistration.getId().toString(), 300, 300);

      Map<String, String> emailBody = preRegistrationService.buildEmailBody(meeting, imageQR);
      String to = person.getContactEmail();
      String title =  meeting.getConference().getName()+" - Pré-Credenciamento";
      emailService.sendEmailPreRegistration(to, title, emailBody, imageQR);


      return ResponseEntity.status(200).body(new PreRegistrationDto(savedPreRegistration,imageQR) );
    }

    @Transactional
    @PostMapping("check-in")
    public ResponseEntity<CheckedInAtDto> preRegistrationCheckin(
      @RequestBody CheckInPreRegistrationParamDto checkInPreRegistationDto){
      PreRegistration preRegistration = preRegistrationService.find(checkInPreRegistationDto.getPreRegistrationId());
      Meeting meeting = meetingService.find(checkInPreRegistationDto.getMeetingId()); 
      CheckedInAt checkedInAt = meetingService.checkInOnMeeting(preRegistration.getPerson().getId(), meeting.getId(),null);
      if(preRegistration.getMeeting().getId() == meeting.getId()){
        preRegistrationService.saveCheckIn(preRegistration.getPerson().getId(), meeting.getId());
      }
      if (checkedInAt != null) {
        return ResponseEntity.ok().body(new CheckedInAtDto(checkedInAt));
      }
      return ResponseEntity.noContent().build();
    }

}
