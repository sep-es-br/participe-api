package br.gov.es.participe.service;

import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.SelfDeclaration;
import br.gov.es.participe.repository.SelfDeclarationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SelfDeclarationService {

  @Autowired
  private SelfDeclarationRepository selfDeclarationRepository;

  @Autowired
  private ConferenceService conferenceService;

  @Autowired
  private LocalityService localityService;

  @Autowired
  private PersonService personService;

  @Transactional
  public SelfDeclaration save(SelfDeclaration selfDeclaration) {
    if(selfDeclaration.getConference() == null || selfDeclaration.getConference().getId() == null) {
      throw new IllegalArgumentException("Conference is required to create or edit Self Declaration");
    }

    if(selfDeclaration.getLocality() == null || selfDeclaration.getLocality().getId() == null) {
      throw new IllegalArgumentException("Locality is required to create or edit Self Declaration");
    }

    if(selfDeclaration.getPerson() == null || selfDeclaration.getPerson().getId() == null) {
      throw new IllegalArgumentException("Person is required to create or edit Self Declaration");
    }

    SelfDeclaration self = selfDeclarationRepository.findByIdConferenceAndIdPerson(
      selfDeclaration.getConference().getId(), selfDeclaration.getPerson().getId());

    if(self == null) {
      Conference conference = conferenceService.find(selfDeclaration.getConference().getId());
      Locality locality = localityService.find(selfDeclaration.getLocality().getId());
      Person person = personService.find(selfDeclaration.getPerson().getId());

      selfDeclaration.setConference(conference);
      selfDeclaration.setLocality(locality);
      selfDeclaration.setPerson(person);
      selfDeclaration.setAnswerSurvey(false);
      selfDeclaration.setReceiveInformational(false);
    }
    return selfDeclarationRepository.save(selfDeclaration);
  }

  @Transactional
  public SelfDeclaration updateLocality(SelfDeclaration selfDeclaration, Long idLocality) {
    selfDeclaration.setLocality(null);
    selfDeclarationRepository.save(selfDeclaration);
    selfDeclaration.setPerson(personService.find(selfDeclaration.getPerson().getId()));
    selfDeclaration.setConference(conferenceService.find(selfDeclaration.getConference().getId()));
    selfDeclaration.setLocality(localityService.find(idLocality));
    return selfDeclarationRepository.save(selfDeclaration);
  }

  public SelfDeclaration find(Long id) {
    return selfDeclarationRepository
      .findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Self Declaration not found: " + id));
  }

  public SelfDeclaration findByPersonAndConference(Long idPerson, Long idConference) {
    return selfDeclarationRepository.findByIdConferenceAndIdPerson(idConference, idPerson);
  }

  @Transactional
  public void delete(Long id) {
    SelfDeclaration self = find(id);
    selfDeclarationRepository.delete(self);
  }

  public List<SelfDeclaration> findAllByPerson(Long id) {
    return selfDeclarationRepository
      .findAllByIdPerson(id);
  }
}
