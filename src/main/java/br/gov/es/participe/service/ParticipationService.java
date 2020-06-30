package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ParticipationService {
	
	@Autowired
	private ConferenceService conferenceService;
	
	@Autowired
	private PlanService planService;
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private PlanItemService planItemService;
	
	@Autowired
	private StructureService structureService;
	
	@Autowired
	private StructureItemService structureItemService;
	
	@Autowired
	private HighlightService highlightService;

	public BodyParticipationDto body(Long idPlanItem, 
									 Long idLocality, 
									 Long idConference, 
									 Long idPerson,
									 String text,
									 UriComponentsBuilder uriComponentsBuilder) {
		Conference conference = conferenceService.find(idConference);
		
		Plan plan;
		PlanItem planI = null;
		Set<PlanItem> PlanItems;
		StructureItem structureItem = null;
		List<PlanItemDto> itens = new ArrayList<>();
		
		if(idPlanItem == null) {
			plan = planService.findFilesById(conference.getPlan().getId());
			PlanItems = plan.getItems();
		}
		else {
			planI = planItemService.findByIdWithLocalities(idPlanItem);
			PlanItems = planI.getChildren();
		}
		
		if(PlanItems != null) {
			if (PlanItems.size() > 0 && text != null && !text.isEmpty()) {
				PlanItems = PlanItems.stream().filter(planItem -> {
					if (planItem.getName() != null &&
							!planItem.getName().isEmpty() &&
							planItem.getName().toLowerCase().contains(text.toLowerCase())) {
						return true;
					}
					if (planItem.getDescription() != null &&
							!planItem.getDescription().isEmpty() &&
							planItem.getDescription().toLowerCase().contains(text.toLowerCase())) {
						return true;
					}
					return false;
				}).collect(Collectors.toSet());
			}

			for(PlanItem planItem: PlanItems) {
				PlanItemDto planItemDto = generatePlanItemDtoFront(planItem, idPerson, idConference, idLocality);
				if(planItem.getLocalities() == null) {
					itens.add(planItemDto);
				} else {
					if(planItem.getLocalities().stream().filter(l -> l.getId().equals(idLocality)).findFirst().isPresent()) {
						itens.add(planItemDto);
					}
				}
			}
			if (itens != null && itens.size() > 0) {
				structureItem = structureItemService.findByIdPlanItem(itens.get(0).getId());
			}
		} else {
			structureItem = structureItemService.findChild(planI.getStructureItem().getId());
		}

		StructureItemDto structureItemDto = new StructureItemDto(structureItem, null, false, false);
		
		structureItemDto.setParent(null);
		structureItemDto.setLocality(null);
		structureItemDto.setStructure(null);
		
		LinkParentDto link = new LinkParentDto();
		
		if(structureItem != null && structureItem.getLink() != null && planI != null) {
			link.setIdParent(planI.getId());
			link.setTextLink(structureItem.getLink());
			structureItemDto.setLink(null);
			structureItemDto.setParentLink(link);
		}
		
		String url = uriComponentsBuilder.path("/files/").build().toUri().toString();
		itens.forEach(item -> {
			if(structureItemDto.getLogo() && item.getFile() != null && item.getFile().getId() != null) {
				item.setImage(url + item.getFile().getId());
				item.setFile(null);
			}
		});
		
		BodyParticipationDto body = new BodyParticipationDto();
		body.setItens(itens);
		body.setStructureitem(structureItemDto);
		
		return body;
	}
	
	public PlanItemDto generatePlanItemDtoFront(PlanItem planItem, Long idPerson, Long idConference, Long idLocality) {
		PlanItemDto planItemDto = new PlanItemDto(planItem, null, false);
		
		if(planItemDto.getStructureItem().getId() == null)
			planItemDto.setStructureItem(null);
		
		planItemDto.setParent(null);
		planItemDto.setLocalities(null);
		
		List<Comment> comments = commentService.find(idPerson, planItem.getId(), idConference, idLocality);
		
		if(comments != null && comments.size() > 0) {
			List<CommentDto> commentsDto = new ArrayList<>();
			planItemDto.setCommentsMade(comments.size());
			comments.forEach(comment -> commentsDto.add(new CommentDto(comment, false, true)));
			planItemDto.setComments(commentsDto);
		}
		else {
			planItemDto.setCommentsMade(0);
		}
		
		List<Highlight> high = highlightService.findAll(idPerson, planItem.getId(), idConference, idLocality);
		if(high == null|| high.isEmpty())
			planItemDto.setVotes(false);
		else
			planItemDto.setVotes(true);
		
		return planItemDto;	
	}
	
	public PortalHeader header(Long id, UriComponentsBuilder uriComponentsBuilder) {
		Conference conference = conferenceService.find(id);
		
		PortalHeader header = new PortalHeader();
		header.setTitle(conference.getTitleParticipation());
		header.setSubtitle(conference.getSubtitleParticipation());
		
		String url = uriComponentsBuilder.path("/files/").build().toUri().toString();
		header.setImage(url + conference.getFileParticipation().getId());
		
		return header;
	}
	
	public ConferenceDto getConferenceDto(Long id, UriComponentsBuilder uriComponentsBuilder) {
		Conference conference = conferenceService.find(id);
		ConferenceDto conferenceDto = new ConferenceDto(conference);
		if (conference.getPlan() != null && conference.getPlan().getId() != null) {
			Plan plan = planService.find(conference.getPlan().getId());
			PlanDto planDto = new PlanDto(plan, true);
			if (plan.getStructure() != null && plan.getStructure().getId() != null) {
				Structure structure = structureService.find(plan.getStructure().getId());
				StructureDto structureDto = new StructureDto(structure, true);
				planDto.setStructure(structureDto);
			}
			conferenceDto.setPlan(planDto);
		}
		
		String url = uriComponentsBuilder.path("/files/").build().toUri().toString();
		if (conferenceDto.getFileAuthentication() != null && conferenceDto.getFileAuthentication().getId() != null) {
			conferenceDto.getFileAuthentication().setUrl(url + conferenceDto.getFileAuthentication().getId());
		}
		if (conferenceDto.getFileParticipation() != null && conferenceDto.getFileParticipation().getId() != null) {
			conferenceDto.getFileParticipation().setUrl(url + conferenceDto.getFileParticipation().getId());
		}
				
		return conferenceDto;	
	}
	
}
