package br.gov.es.participe.model;

import br.gov.es.participe.controller.dto.StructureItemDto;
import br.gov.es.participe.controller.dto.StructureItemParamDto;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class StructureItem extends Entity implements Serializable {

    private String name;
    private Boolean logo;
    private Boolean locality;
    private Boolean votes;
    private Boolean comments;

    private String title;
    private String subtitle;
    private String link;

    @Relationship(type = "COMPOSES")
    private Structure structure;

    @Relationship(type = "COMPOSES")
    private StructureItem parent;

    @Relationship(type = "COMPOSES", direction = Relationship.INCOMING)
    private Set<StructureItem> children;

    @Relationship(type = "OBEYS", direction = Relationship.INCOMING)
    private Set<PlanItem> planItems;

    public StructureItem() {
    }

    public StructureItem(StructureItemDto structureItemDto) {
        if (structureItemDto == null)
            return;

        setId(structureItemDto.getId());
        this.locality = structureItemDto.getLocality();
        this.logo = structureItemDto.getLogo();
        this.votes = structureItemDto.getVotes();
        this.name = structureItemDto.getName();
        this.structure = new Structure(structureItemDto.getStructure());
        this.comments = structureItemDto.getComments();
        
        if(structureItemDto.getTitle() != null) {
        	this.title = structureItemDto.getTitle();

        }
        if(structureItemDto.getSubtitle() != null) {
        	this.subtitle = structureItemDto.getSubtitle();
        }
        if(structureItemDto.getLink() != null) {
        	this.link = structureItemDto.getLink();
        }

        if (structureItemDto.getChildren() != null && !structureItemDto.getChildren().isEmpty()) {
            this.children = new HashSet<>();
            structureItemDto.getChildren().forEach(child -> children.add(new StructureItem(child)));
        }

        if (structureItemDto.getParent() != null) {
            this.parent = new StructureItem(structureItemDto.getParent());
        }

        if (structureItemDto.getPlanItems() != null && !structureItemDto.getPlanItems().isEmpty()) {
            this.planItems = new HashSet<>();
            structureItemDto.getPlanItems().forEach(planItem -> planItems.add(new PlanItem(planItem)));
        }
    }

    public StructureItem(StructureItemParamDto structureItemDto) {
        if (structureItemDto == null)
            return;

        setId(structureItemDto.getId());
        this.name = structureItemDto.getName();
        this.logo = structureItemDto.getLogo();
        this.locality = structureItemDto.getLocality();
        this.votes = structureItemDto.getVotes();
        this.comments = structureItemDto.getComments();
        this.structure = new Structure(structureItemDto.getStructure());
        
        if(structureItemDto.getTitle() != null) {
        	this.title = structureItemDto.getTitle();

        }
        if(structureItemDto.getSubtitle() != null) {
        	this.subtitle = structureItemDto.getSubtitle();
        }
        if(structureItemDto.getLink() != null) {
        	this.link = structureItemDto.getLink();
        }

        if (structureItemDto.getParent() != null) {
            this.parent = new StructureItem(structureItemDto.getParent());
        }
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

    public void setLogo(Boolean logo) {
        this.logo = logo;
    }

    public Boolean getLocality() {
        return locality;
    }

    public void setLocality(Boolean locality) {
        this.locality = locality;
    }

    public Boolean getVotes() {
        return votes;
    }

    public void setVotes(Boolean votes) {
        this.votes = votes;
    }

    public Boolean getComments() {
        return comments;
    }

    public void setComments(Boolean comments) {
        this.comments = comments;
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

	public void setChildren(Set<StructureItem> children) {
		this.children = children;
	}

	public void setPlanItems(Set<PlanItem> planItems) {
		this.planItems = planItems;
	}

	public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public StructureItem getParent() {
        return parent;
    }

    public void setParent(StructureItem parent) {
        this.parent = parent;
    }

    public Set<StructureItem> getChildren() {
        if (children == null) {
            return Collections.emptySet();
        }

        return Collections.unmodifiableSet(children);
    }

    public Set<PlanItem> getPlanItems() {
        if (planItems == null) {
            return Collections.emptySet();
        }

        return Collections.unmodifiableSet(planItems);
    }
}
