package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.Domain;
import br.gov.es.participe.model.Plan;
import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.model.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlanItemDto implements  Comparable<PlanItemDto> {

    private Long id;
    private String name;    
    private String description;
    private String image;
    private String structureItemName;
    private Boolean votes;
    private Integer commentsMade;
    private List<CommentDto> comments;
    
    private StructureItemDto structureItem;
    private FileDto file;
    private PlanDto plan;
    private List<LocalityDto> localities;
    private PlanItemDto parent;
    private List<PlanItemDto> children;
    private Set<Long> localitiesIds;

    public PlanItemDto() {
    	//Construtor criado por conveniencia.
    }
    
    public PlanItemDto(PlanItem planItem, boolean proposal) {
    	this.id = planItem.getId();
        this.name = planItem.getName();
        
        if(!proposal)
        	this.description = planItem.getDescription();
        else {			
        	this.structureItemName = planItem.getStructureItem().getName();
        }
    }

    public PlanItemDto(PlanItem planItem) {
        this.id = planItem.getId();
        this.name = planItem.getName();
    }

    public PlanItemDto(PlanItem planItem, Plan parentPlan, boolean loadChildren) {
        if (planItem == null) return;

        Structure structure = parentPlan != null ? parentPlan.getStructure() : null;

        this.id = planItem.getId();
        this.description = planItem.getDescription();
        this.name = planItem.getName();
        this.structureItem = new StructureItemDto(planItem.getStructureItem(), structure, false, false);
        this.plan = parentPlan != null ? new PlanDto(parentPlan, false) : null;

        if (planItem.getFile() != null) {
            this.file = new FileDto(planItem.getFile());
        }

        Domain domain = this.plan != null && this.plan.getDomain() != null ? new Domain(this.plan.getDomain()) : null;
        if (planItem.getLocalities() != null && !planItem.getLocalities().isEmpty()) {
            this.localities = new ArrayList<>();
            planItem.getLocalities()
                    .stream()
                    .forEach(locality -> this.localities.add(new LocalityDto(locality, domain, false, true)));
        }

        if (planItem.getParent() != null) {
            this.parent = new PlanItemDto(planItem.getParent(), parentPlan, false);
        }

        if (loadChildren && planItem.getChildren() != null && !planItem.getChildren().isEmpty()) {
            this.children = new ArrayList<>();
            planItem.getChildren().forEach(child -> {
                PlanItemDto childPlanItem = new PlanItemDto(child, null, true);
                if (childPlanItem.getId() != null) {
                    this.children.add(childPlanItem);
                }
            });
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlanDto getPlan() {
        return plan;
    }

    public void setPlan(PlanDto plan) {
        this.plan = plan;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StructureItemDto getStructureItem() {
        return structureItem;
    }

    public void setStructureItem(StructureItemDto structureItem) {
        this.structureItem = structureItem;
    }

    public FileDto getFile() {
        return file;
    }

    public void setFile(FileDto file) {
        this.file = file;
    }

    public List<LocalityDto> getLocalities() {
        return localities;
    }

    public void setLocalities(List<LocalityDto> localities) {
        this.localities = localities;
    }

    public PlanItemDto getParent() {
        return parent;
    }

    public void setParent(PlanItemDto parent) {
        this.parent = parent;
    }

    public List<PlanItemDto> getChildren() {
        return children;
    }

    public void setChildren(List<PlanItemDto> children) {
        this.children = children;
    }

    public Set<Long> getLocalitiesIds() {
        return localitiesIds;
    }

    public void setLocalitiesIds(Set<Long> localitiesIds) {
        this.localitiesIds = localitiesIds;
    }

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getStructureItemName() {
		return structureItemName;
	}

	public void setStructureItemName(String structureItemName) {
		this.structureItemName = structureItemName;
	}

	public Boolean getVotes() {
		return votes;
	}

	public void setVotes(Boolean votes) {
		this.votes = votes;
	}

	public Integer getCommentsMade() {
		return commentsMade;
	}

	public void setCommentsMade(Integer commentsMade) {
		this.commentsMade = commentsMade;
	}

	public List<CommentDto> getComments() {
		return comments;
	}

	public void setComments(List<CommentDto> comments) {
		this.comments = comments;
	}

	@Override
	public int compareTo(PlanItemDto plan) {
		return this.name.compareTo(plan.getName());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof PlanItemDto)) {
			return false;
		}
		PlanItemDto other = (PlanItemDto) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return " Name: "+this.name+" ID: "+this.id;
	}
}
