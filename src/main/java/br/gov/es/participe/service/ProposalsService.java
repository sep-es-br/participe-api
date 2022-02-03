package br.gov.es.participe.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.participe.controller.dto.LocalityDto;
import br.gov.es.participe.controller.dto.PlanItemDto;
import br.gov.es.participe.controller.dto.ProposalsFilterDto;
import br.gov.es.participe.model.Comment;
import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.LocalityType;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.Plan;
import br.gov.es.participe.model.PlanItem;

@Service
public class ProposalsService {

	@Autowired
	private LocalityService localityService; 
	
	@Autowired
	private ConferenceService conferenceService;
	
	@Autowired
	private PlanService planService;
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private PersonService personService;
	
	@Autowired
	private LocalityTypeService localityTypeService;
	
	public ProposalsFilterDto getFilters(Long idConference) {
		String structureItemName = null;
		List<PlanItemDto> itens = new ArrayList<>();
		Conference conference = conferenceService.find(idConference);
		List<Locality> localities = localityService.findByIdConference(idConference);
		LocalityType typeLocalities = localityTypeService.findByIdLocality(localities.get(0).getId());
		
    	List<LocalityDto> localitiesDto = new ArrayList<>();
    	localities.forEach(locality -> localitiesDto.add(new LocalityDto(locality, null, false, false)));
		
		Plan plan = planService.findFilesById(conference.getPlan().getId());
		Set<PlanItem> planItems = plan.getItems();
		if(planItems != null) {
			for(PlanItem planItem: planItems) {
				structureItemName = planItem.getStructureItem().getName();
				PlanItemDto planItemDto = new PlanItemDto(planItem, null, false);
				
				if(planItemDto.getStructureItem().getId() == null)
					planItemDto.setStructureItem(null);
				
				planItemDto.setParent(null);
				planItemDto.setLocalities(null);
				planItemDto.setFile(null);
				itens.add(planItemDto);
			}
		}
		
		ProposalsFilterDto filters = new ProposalsFilterDto();
		filters.setItemName(structureItemName);
		filters.setRegionName(typeLocalities.getName());
		filters.setItens(itens);
		filters.setLocalities(localitiesDto);
    	
		return filters;
	}

	public Integer makeLike(Long idPerson, Long idComment) {
		Comment comment = commentService.findPersonLiked(idComment);
		Person person = personService.likesComments(idPerson);

		if (comment.getPersonMadeBy().getId() != person.getId()) {
		
			if(comment.getPersonLiked() == null) {
				comment.setPersonLiked(new HashSet<>());
			}
			if(person.getComments() == null) {
				person.setComments(new HashSet<>());
			}
			
			Person findResult = comment.getPersonLiked().stream().filter(personL -> personL.getContactEmail().equals(person.getContactEmail()))
											.findFirst()
											.orElse(null);
			if(findResult == null) {
				person.getComments().add(comment);
				personService.save(person, true);
			} else {
				person.getComments().remove(comment);
				personService.save(person, true);
			}
			
			comment = commentService.findPersonLiked(idComment);
			if(comment.getPersonLiked() == null) {
				comment.setPersonLiked(new HashSet<>());
			}
		}
		return comment.getPersonLiked().size();
	}
}
