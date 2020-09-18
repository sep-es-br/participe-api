package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.Structure;
import br.gov.es.participe.model.StructureItem;

import java.util.ArrayList;
import java.util.List;

public class StructureItemDto {

    private Long id;
    private Boolean logo;
    private Boolean locality;
    private Boolean votes;
    private Boolean comments;
    private Boolean hasNext;
    
    private String name;
    private String title;
    private String subtitle;
    private String link;
    private LinkParentDto parentLink;
    
    private StructureDto structure;
    private StructureItemDto parent;
    private List<StructureItemDto> children;
    private List<PlanItemDto> planItems;

    public StructureItemDto() {
    }

    public StructureItemDto(StructureItem structureItem, Structure parentStructure, boolean loadChildren, boolean loadPlanItems) {
        if (structureItem == null) {
        	return;
        }

        this.logo = structureItem.getLogo();
        this.id = structureItem.getId();
        this.name = structureItem.getName();
        this.comments = structureItem.getComments();
        this.structure = new StructureDto(parentStructure, false);
        this.parent = new StructureItemDto(structureItem.getParent(), parentStructure, false, loadPlanItems);
        this.locality = structureItem.getLocality();
        this.votes = structureItem.getVotes();
        
        loadStructureItem(structureItem);

        if (loadChildren && structureItem.getChildren() != null && !structureItem.getChildren().isEmpty()) {
            this.children = new ArrayList<>();
            structureItem.getChildren().forEach(child -> {
                StructureItemDto childStructureItemDto = new StructureItemDto(child, parentStructure, loadChildren, loadPlanItems);
                if (childStructureItemDto.getId() != null) {
                    this.children.add(childStructureItemDto);
                }
            });
        }

        if (loadPlanItems && structureItem.getPlanItems() != null && !structureItem.getPlanItems().isEmpty()) {
            this.planItems = new ArrayList<>();
            structureItem.getPlanItems().forEach(planItem -> {
                PlanItemDto planItemDto = new PlanItemDto(planItem, planItem.getPlan(), loadChildren);
                if (planItemDto != null) {
                    this.planItems.add(planItemDto);
                }
            });
        }
    }
    
    private void loadStructureItem(StructureItem structureItem) {
    	if(structureItem.getTitle() != null) 
        	this.title = structureItem.getTitle();
        
        if(structureItem.getSubtitle() != null) 
        	this.subtitle = structureItem.getSubtitle();
        
        if(structureItem.getLink() != null) 
        	this.link = structureItem.getLink();
        
        
        if(structureItem.getChildren() != null && !structureItem.getChildren().isEmpty())
        	this.hasNext = true;
        else
        	this.hasNext = false;

    }

    public StructureItemDto(StructureItem structureItem) {
        this.id = structureItem.getId();
        this.name = structureItem.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getLogo() {
        return logo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLogo(Boolean logo) {
        this.logo = logo;
    }

    public Boolean getVotes() {
        return votes;
    }

    public void setVotes(Boolean votes) {
        this.votes = votes;
    }

    public Boolean getLocality() {
        return locality;
    }

    public void setLocality(Boolean locality) {
        this.locality = locality;
    }

    public Boolean getComments() {
        return comments;
    }

    public void setComments(Boolean comments) {
        this.comments = comments;
    }

	public Boolean getHasNext() {
		return hasNext;
	}

	public void setHasNext(Boolean hasNext) {
		this.hasNext = hasNext;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public StructureDto getStructure() {
        return structure;
    }

    public void setStructure(StructureDto structure) {
        this.structure = structure;
    }

    public LinkParentDto getParentLink() {
		return parentLink;
	}

	public void setParentLink(LinkParentDto parentLink) {
		this.parentLink = parentLink;
	}

	public StructureItemDto getParent() {
        return parent;
    }

    public void setParent(StructureItemDto parent) {
        this.parent = parent;
    }

    public List<StructureItemDto> getChildren() {
        return children;
    }

    public void setChildren(List<StructureItemDto> children) {
        this.children = children;
    }

    public List<PlanItemDto> getPlanItems() {
        return planItems;
    }

    public void setPlanItems(List<PlanItemDto> planItems) {
        this.planItems = planItems;
    }
}
