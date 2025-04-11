package br.gov.es.participe.service;

import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.SelfDeclaration;
import br.gov.es.participe.repository.SelfDeclarationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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

  private static final Logger log = LoggerFactory.getLogger(SelfDeclarationService.class);

  public SelfDeclaration save(SelfDeclaration selfDeclaration) {

    if (selfDeclaration.getConference() == null || selfDeclaration.getConference().getId() == null) {
      throw new IllegalArgumentException("Conference is required to create or edit Self Declaration");
    }

    if (selfDeclaration.getLocality() == null || selfDeclaration.getLocality().getId() == null) {
      throw new IllegalArgumentException("Locality is required to create or edit Self Declaration");
    }

    if (selfDeclaration.getPerson() == null || selfDeclaration.getPerson().getId() == null) {
      throw new IllegalArgumentException("Person is required to create or edit Self Declaration");
    }
    log.info(
      "Iniciando criação/alteração da SelfDeclaration com parâmetros selfDeclarationId={}, conferenceId={}, localityId={}, personId={}, answerSurvey={}, receiveInformational={}",
      selfDeclaration.getId(),
      selfDeclaration.getConference().getId(),
      selfDeclaration.getLocality().getId(),
      selfDeclaration.getPerson().getId(),
      selfDeclaration.getAnswerSurvey()
    );

    log.info(
      "Consultando SelfDeclaration relacionada a conferenceId={} e personId={}",
      selfDeclaration.getConference().getId(),
      selfDeclaration.getPerson().getId()
    );
    SelfDeclaration self = selfDeclarationRepository.findByConferenceIdAndPersonId(
        selfDeclaration.getConference().getId(), selfDeclaration.getPerson().getId());
    if (self == null) {
      log.info("SelfDeclaration com conferenceId={} e personId={} não encontrada",
        selfDeclaration.getConference().getId(),
        selfDeclaration.getPerson().getId()
      );
      Conference conference = conferenceService.find(selfDeclaration.getConference().getId());
      Locality locality = localityService.find(selfDeclaration.getLocality().getId());
      Person person = personService.find(selfDeclaration.getPerson().getId());


      selfDeclaration.setConference(conference);
      selfDeclaration.setLocality(locality);
      selfDeclaration.setPerson(person);
      selfDeclaration.setAnswerSurvey(false);

    }
    log.info(
      "Criando/Atualizando SelfDeclaration selfDeclarationId={} com conferenceId={}, localityId={}, personId={}, answerSurvey={}, receiveInformational={}",
      selfDeclaration.getId(),
      selfDeclaration.getConference().getId(),
      selfDeclaration.getLocality().getId(),
      selfDeclaration.getPerson().getId(),
      selfDeclaration.getAnswerSurvey()
    );
    if (Objects.isNull(selfDeclaration.getAnswerSurvey())) {
       throw new IllegalArgumentException("AnswerSurvey required to create or edit Self Declaration");
    }
    final var updatedSelfDeclaration = selfDeclarationRepository.save(selfDeclaration);
    log.info(
      "SelfDeclaration selfDeclarationId={} criado/atualizado com sucesso conferenceId={}, localityId={}, personId={}, answerSurvey={}, receiveInformational={}",
      selfDeclaration.getId(),
      selfDeclaration.getConference().getId(),
      selfDeclaration.getLocality().getId(),
      selfDeclaration.getPerson().getId(),
      selfDeclaration.getAnswerSurvey()
    );
    return updatedSelfDeclaration;
  }

  // @Transactional
  public SelfDeclaration updateLocality(SelfDeclaration selfDeclaration, Long idLocality) {
    selfDeclaration.setLocality(null);
    log.info("Alterando locality para nulo da SelfDeclaration com id={}", selfDeclaration.getId());
    selfDeclarationRepository.save(selfDeclaration);
    selfDeclaration.setPerson(personService.find(selfDeclaration.getPerson().getId()));
    selfDeclaration.setConference(conferenceService.find(selfDeclaration.getConference().getId()));
    selfDeclaration.setLocality(localityService.find(idLocality));
    log.info(
      "Atualizado atributos da SelfDeclaration com id={} novos atributos = personId={}, conferenceId={}, localityId={}",
      selfDeclaration.getId(),
      selfDeclaration.getPerson().getId(),
      selfDeclaration.getConference().getId(),
      selfDeclaration.getLocality().getId()
    );
    return selfDeclarationRepository.save(selfDeclaration);
  }

  public SelfDeclaration find(Long id) {
    return selfDeclarationRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Self Declaration not found: " + id));
  }

  public SelfDeclaration findByPersonAndConference(Long idPerson, Long idConference) {
    return selfDeclarationRepository.findByConferenceIdAndPersonId(idConference, idPerson);
  }


  public void delete(Long id) {
    SelfDeclaration self = find(id);
    log.info("Removendo SelfDeclarationId={}", self.getId());
    selfDeclarationRepository.delete(self);
  }

  public List<SelfDeclaration> findAllByPerson(Long id) {
    return selfDeclarationRepository
        .findAllByIdPerson(id);
  }
}
