package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.*;
import br.gov.es.participe.repository.AttendRepository;
import br.gov.es.participe.repository.ConferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.*;

@Service
public class ConferenceService {

    @Autowired
    private ConferenceRepository conferenceRepository;

    @Autowired
    private PlanService planService;
    
    @Autowired
    private FileService fileService;
    
    @Autowired
    private LocalityTypeService localityTypeService;
    
    @Autowired
    private PlanItemService planItemService;
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private HighlightService highlightService;
    
    @Autowired
    private AttendRepository attendRepository;
    
    @Autowired
    private LocalityService localityService;

    @Autowired
    private PersonService personService;

    public void generateAuthenticationScreen(Long id, AuthenticationScreenDto auth, UriComponentsBuilder uriComponentsBuilder) {
		Conference conference = find(id);
		
		auth.setTitleAuthentication(conference.getTitleAuthentication());
		auth.setSubtitleAuthentication(conference.getSubtitleAuthentication());
		auth.setFileAuthentication(new FileDto(conference.getFileAuthentication()));
		
		String url = uriComponentsBuilder.path("/files/").build().toUri().toString();
		auth.getFileAuthentication().setUrl(url + conference.getFileAuthentication().getId());
		auth.setProposal(0);
		auth.setHighlights(0);
		Plan plan = planService.findByConference(conference.getId());
		if(plan.getlocalitytype() != null) {
			auth.setLocalityType(plan.getlocalitytype().getName());
		}
		auth.setProposal(commentService.countCommentByConference(id));
		auth.setHighlights(highlightService.countHighlightByConference(id));
//		for(PlanItem planitem: plan.getItems()) {
//			if(planitem.getStructureItem().getComments()) 
//				auth.setProposal(auth.getProposal() + commentService.countCommentByPlanItem(planitem.getId()));
//			
//			if(planitem.getStructureItem().getVotes()) 
//				auth.setHighlights(auth.getHighlights() + highlightService.countHighlightByPlanItem(planitem.getId()));
//			
//			if(planitem.getChildren() != null)
//				count(planitem.getChildren(), auth);
//		}
		
		auth.setParticipations(attendRepository.countParticipationByConference(conference.getId()));
		auth.setNumberOfLocalities(localityService.countLocalitiesParticipation(conference.getId()));
    }

    public void generateCardScreen(CardScreenDto response, Long id) {
    	Conference conference = find(id);
    	Plan plan = planService.find(conference.getPlan().getId());
    	
    	if(plan.getlocalitytype() != null) {
    		response.setRegionalizable(plan.getlocalitytype().getName());
    	}
    	response.setTitle(conference.getTitleRegionalization());
    	response.setSubtitle(conference.getSubtitleRegionalization());
    }
    
    public boolean validate(String name, Long id) {
        Conference conference = conferenceRepository.findByNameIgnoreCase(name);
        if(conference == null){
            return true;
        }
        return conference.getId().equals(id);
    }

    public List<Conference> findAll(String name, Long plan, Integer month, Integer year) {
        List<Conference> conferences = new ArrayList<>();
        
        conferenceRepository.findAllByQuery(name, plan, month, year).iterator().forEachRemaining(conferences::add);
        for(Conference conference: conferences) {
        	boolean deleteConference = true;
        	if(conference.getMeeting() != null)
	        	for(Meeting m: conference.getMeeting()) {
	        		m.setConference(null);
	        	}
        	if(conference.getPlan() != null) {
	        	List<PlanItem> planItens = planItemService.findAllByIdPlan(conference.getPlan().getId());
	        	for(PlanItem item: planItens) {
	            	if(item.getAttends() != null)
	            		deleteConference = false;
	            }
        	}
        	conference.setHasAttend(deleteConference);
        }
        	
        return conferences;
    }

    public List<Conference> findByPlan(Long id) {
        return conferenceRepository.findByPlan(id);
    }
    
    public List<ConferenceDto> findAllActives(Long idPerson) {
    	Person person = personService.find(idPerson);
    	final boolean adm = person.getRoles() != null && person.getRoles().contains("Administrator");
    	List<ConferenceDto> conferences = new ArrayList<>();
    	LocalDate now = LocalDate.now();
    	conferenceRepository.findAllActives(now.getMonthValue(), now.getYear()).forEach(conference -> {
    		Date date = new Date();
    		if(date.after(conference.getBeginDate()) && date.before(conference.getEndDate())) {
    			if(adm || (conference.getModerators() != null && conference.getModerators().stream().filter(m -> m.getId().equals(idPerson)).findFirst().isPresent())) {
    				ConferenceDto dto = new ConferenceDto(conference);
    				dto.setPlan(null);
    				dto.setLocalityType(null);
    				dto.setFileAuthentication(null);
    				dto.setFileParticipation(null);
    				conferences.add(dto);
    			} 
    		}
    	});
    	return conferences;
    }

    @Transactional
    public Conference save(Conference conference) {
        if (conference.getPlan() == null || conference.getPlan().getId() == null) {
            throw new IllegalArgumentException("Plan is required");
        }
        
        if (conference.getFileAuthentication() == null || conference.getFileAuthentication().getId() == null) {
            throw new IllegalArgumentException("Authentication Image is required");
        }
        
        if (conference.getFileParticipation() == null || conference.getFileParticipation().getId() == null) {
            throw new IllegalArgumentException("Participation Image is required");
        }
        
        Conference c = conferenceRepository.findByNameIgnoreCase(conference.getName());
        if (c != null) {
            if (conference.getId() != null) {
                if (!conference.getId().equals(c.getId())) {
                    throw new IllegalArgumentException("This name already exists");
                }
            } else{
                throw new IllegalArgumentException("This name already exists");
            }
        }
        if (conference.getPlan().getId() != null) {
            if (conference.getId() != null) {
                Conference conference1 = find(conference.getId());
                conference1.setPlan(null);
                conferenceRepository.save(conference1);
            }
            conference.setPlan(planService.find(conference.getPlan().getId()));
        }
        
        if (conference.getFileAuthentication().getId() != null) {
            if (conference.getId() != null) {
                Conference conference1 = find(conference.getId());
                conference1.setFileAuthentication(null);
                conferenceRepository.save(conference1);
            }
            conference.setFileAuthentication(fileService.find(conference.getFileAuthentication().getId()));
        }
        
        if (conference.getFileParticipation().getId() != null) {
            if (conference.getId() != null) {
                Conference conference1 = find(conference.getId());
                conference1.setFileParticipation(null);
                conferenceRepository.save(conference1);
            }
            conference.setFileParticipation(fileService.find(conference.getFileParticipation().getId()));
        }
        
        if (conference.getLocalityType() != null && conference.getLocalityType().getId() != null) {
            if (conference.getId() != null) {
                Conference conference1 = find(conference.getId());
                conference1.setLocalityType(null);
                conferenceRepository.save(conference1);
            }
            conference.setLocalityType(localityTypeService.find(conference.getLocalityType().getId()));
        }

        if (conference.getModerators() != null && !conference.getModerators().isEmpty()) {
            HashSet<Person> moderatos = new HashSet<>();
            conference.getModerators().forEach(p -> {
                Optional<Person> find = personService.findByContactEmail(p.getContactEmail());
                moderatos.add(find.isPresent() ? find.get() : personService.save(p, true));
            });
            conference.setModerators(moderatos);
        }
        
        conference.setName(conference.getName().trim().replaceAll(" +", " "));
        return conferenceRepository.save(conference);
    }

    public Conference find(Long id) {

        return conferenceRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conference not found: " + id));
    }

    @Transactional
    public Boolean delete(Long id) {
    	boolean deleteConference = true;
        Conference conference = find(id);
        
        if(conference.getPlan() != null && conference.getPlan().getId() != null) {
	        List<PlanItem> planItens = planItemService.findAllByIdPlan(conference.getPlan().getId());
	        if(planItens != null && !planItens.isEmpty())
		        for(PlanItem item: planItens) {
		        	if(item.getAttends() != null)
		        		deleteConference = false;
		        }
        }
        if(deleteConference)
        	conferenceRepository.delete(conference);
        
        return deleteConference;
    }
    
    public void removeSelfDeclaration(SelfDeclaration selfDeclaration, Conference conference) {
    	SelfDeclaration selfDeclarationToRemove = null;
    	
    	Conference conferenceBD = conferenceRepository.findSelfDeclarationById(conference.getId());
    	Set<SelfDeclaration> selfs = conferenceBD.getSelfDeclaration();
    	
    	for(SelfDeclaration self: selfs) {
    		if(self.getId().equals(selfDeclaration.getId())) {
    			selfDeclarationToRemove = self;
    		}
    	}
    	
    	if(selfDeclarationToRemove != null) {
    		selfs.remove(selfDeclarationToRemove);
    		conferenceRepository.save(conferenceBD);
    	}
    }
    
    public void addSelfDeclaration(SelfDeclaration selfDeclaration, Conference conference) {
    	Conference conferenceBD = conferenceRepository.findSelfDeclarationById(conference.getId());
    	conferenceBD.getSelfDeclaration().add(selfDeclaration);
    	conferenceRepository.save(conferenceBD);
    }

    public List<PersonDto> findModeratorsByConferenceId(Long id) {
        List<PersonDto> persons = new ArrayList<>();
        conferenceRepository.findModeratorsById(id).forEach(p -> persons.add(new PersonDto(p)));
        return persons;
    }

    public Integer countSelfDeclarationById(Long id) {
        return conferenceRepository.countSelfDeclarationById(id);
    }

    private void count(Set<PlanItem> itens, AuthenticationScreenDto auth) {
    	for(PlanItem planitem: itens) {
			if(planitem.getStructureItem().getComments()) {
				auth.setProposal(auth.getProposal() + commentService.countCommentByPlanItem(planitem.getId()));
			}
			
			if(planitem.getStructureItem().getVotes()) {
				auth.setHighlights(auth.getHighlights() + highlightService.countHighlightByPlanItem(planitem.getId()));
			}
			if(planitem.getChildren() != null)
				count(planitem.getChildren(), auth);
		}
    }
}
