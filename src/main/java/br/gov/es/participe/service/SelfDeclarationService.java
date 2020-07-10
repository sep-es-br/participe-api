package br.gov.es.participe.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.SelfDeclaration;
import br.gov.es.participe.repository.SelfDeclarationRepository;

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
		
		SelfDeclaration self = selfDeclarationRepository.findByIdConferenceAndIdPerson(selfDeclaration.getConference().getId(), selfDeclaration.getPerson().getId());
		
		if(self == null) {
			Conference conference = conferenceService.find(selfDeclaration.getConference().getId());
			Locality locality = localityService.find(selfDeclaration.getLocality().getId());
			Person person = personService.find(selfDeclaration.getPerson().getId());
	
			selfDeclaration.setConference(conference);
			selfDeclaration.setLocality(locality);
			selfDeclaration.setPerson(person);
			return selfDeclarationRepository.save(selfDeclaration);
		}
		return self;
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
	
	public List<SelfDeclaration> findAll(Long id){
		return selfDeclarationRepository
				.findAllByIdPerson(id);
	}

	public Set<SelfDeclaration> findAllAsSet(Long id){
		List<SelfDeclaration> list = findAll(id);
		Set<SelfDeclaration> set = new HashSet<>();
		list.forEach(l -> set.add(l));
		return set;
	}
}
