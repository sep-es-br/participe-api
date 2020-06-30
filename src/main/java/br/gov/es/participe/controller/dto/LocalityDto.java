package br.gov.es.participe.controller.dto;

import java.util.ArrayList;
import java.util.List;

import br.gov.es.participe.model.Domain;
import br.gov.es.participe.model.Locality;

public class LocalityDto {

    private Long id;
    private String name;
    private LocalityTypeDto type;
    private List<DomainDto> domains;
    private List<LocalityDto> parents;
    private List<LocalityDto> children;
    private List<MeetingDto> meetingPlace;
    private List<MeetingDto> meetingCovers;
    private List<SelfDeclarationDto> selfDeclarations;
    private List<String> mapSplit;

    public LocalityDto() {
    }

    public LocalityDto(Locality locality) {
    	if (locality == null)
    		return;
    	id = locality.getId();
        name = locality.getName();
        if(locality.getType() != null)
        	type = new LocalityTypeDto(locality.getType());
    }
    public LocalityDto(Locality locality, Domain parentDomain, boolean loadChildren, boolean loadParent) {
        if (locality == null ||(parentDomain != null && !locality.getDomains().contains(parentDomain))) return;

        id = locality.getId();
        name = locality.getName();
        if(locality.getType() != null)
        	type = new LocalityTypeDto(locality.getType());
        
        if (!locality.getDomains().isEmpty()) {
            domains = new ArrayList<>();
            locality.getDomains().forEach(domain -> domains.add(new DomainDto(domain, false)));
        }

        if (loadParent && !locality.getParents().isEmpty()) {
            parents = new ArrayList<>();
            locality.getParents().forEach(parent -> parents.add(new LocalityDto(parent, parentDomain, false, loadParent)));
        }

        if (loadChildren && !locality.getChildren().isEmpty()) {
            children = new ArrayList<>();
            locality.getChildren().forEach(child -> {
                LocalityDto childLocalityDto = new LocalityDto(child, parentDomain, true, loadParent);
                if (childLocalityDto.getId() != null) {
                    children.add(childLocalityDto);
                }
            });
        }
        if(locality.getMeetingPlace() != null && !locality.getMeetingPlace().isEmpty()) {
        	meetingPlace = new ArrayList<>();
        	locality.getMeetingPlace().forEach(meet -> meetingPlace.add(new MeetingDto(meet)));
        }
        
        if(locality.getMeetingCovers() != null && !locality.getMeetingCovers().isEmpty()) {
        	meetingCovers = new ArrayList<>();
        	locality.getMeetingCovers().forEach(meet -> meetingCovers.add(new MeetingDto(meet)));
        }
        
        if(locality.getSelfDeclaration() != null && !locality.getSelfDeclaration().isEmpty()) {
        	selfDeclarations = new ArrayList<>();
        	locality.getSelfDeclaration().forEach(self -> selfDeclarations.add(new SelfDeclarationDto(self, true)));
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalityTypeDto getType() {
        return type;
    }

    public void setType(LocalityTypeDto type) {
        this.type = type;
    }

    public List<DomainDto> getDomains() {
        return domains;
    }

    public void setDomains(List<DomainDto> domains) {
        this.domains = domains;
    }

    public List<LocalityDto> getParents() {
        return parents;
    }

    public void setParents(List<LocalityDto> parents) {
        this.parents = parents;
    }

    public List<LocalityDto> getChildren() {
        return children;
    }

    public void setChildren(List<LocalityDto> children) {
        this.children = children;
    }

	public List<MeetingDto> getMeetingPlace() {
		return meetingPlace;
	}

	public void setMeetingPlace(List<MeetingDto> meetingPlace) {
		this.meetingPlace = meetingPlace;
	}

	public List<MeetingDto> getMeetingCovers() {
		return meetingCovers;
	}

	public void setMeetingCovers(List<MeetingDto> meetingCovers) {
		this.meetingCovers = meetingCovers;
	}

	public List<String> getMapSplit() {
		return mapSplit;
	}

	public void setMapSplit(List<String> mapSplit) {
		this.mapSplit = mapSplit;
	}

	public List<SelfDeclarationDto> getSelfDeclarations() {
		return selfDeclarations;
	}

	public void setSelfDeclarations(List<SelfDeclarationDto> selfDeclarations) {
		this.selfDeclarations = selfDeclarations;
	}
}
