package br.gov.es.participe.model;

import br.gov.es.participe.controller.dto.StructureItemDto;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class StructureItem extends Entity {

    private String name;

    private Boolean logo;

    private Boolean locality;

    private Boolean votes;

    private Boolean comments;

    @Relationship(type = "BELONGS_TO")
    private Structure structure;

    @Relationship(type = "BELONGS_TO")
    private StructureItem parent;

    @Relationship(type = "BELONGS_TO", direction = Relationship.INCOMING)
    private Set<StructureItem> children;

    @Relationship(type = "BELONGS_TO", direction = Relationship.INCOMING)
    private Set<PlanItem> planItems;

    public StructureItem(StructureItemDto structureItemDto) {
        if (structureItemDto == null) return;

        setId(structureItemDto.getId());
        this.name = structureItemDto.getName();
        this.logo = structureItemDto.getLogo();
        this.locality = structureItemDto.getLocality();
        this.votes = structureItemDto.getVotes();
        this.comments = structureItemDto.getComments();
        this.structure = new Structure(structureItemDto.getStructure());

        if (structureItemDto.getParent() != null) {
            this.parent = new StructureItem(structureItemDto.getParent());
        }

        if (structureItemDto.getChildren() != null && !structureItemDto.getChildren().isEmpty()) {
            this.children = new HashSet<>();
            structureItemDto.getChildren().forEach(child -> children.add(new StructureItem(child)));
        }

        if (structureItemDto.getPlanItems() != null && !structureItemDto.getPlanItems().isEmpty()) {
            this.planItems = new HashSet<>();
            structureItemDto.getPlanItems().forEach(planItem -> planItems.add(new PlanItem(planItem)));
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
        if (children == null) return Collections.emptySet();
        return Collections.unmodifiableSet(children);
    }

    public void addItem(StructureItem item) {
        if (children == null) new HashSet<>();
        this.children.add(item);
    }

    public Set<PlanItem> getPlanItems() {
        if (planItems == null) return Collections.emptySet();
        return Collections.unmodifiableSet(planItems);
    }

    public void addPlanItem(PlanItem planItem) {
        if (planItems == null) planItems = new HashSet<>();
        planItems.add(planItem);
    }

    public void removePlanItem(Long id) {
        PlanItem planItemToRemove = null;
        for (PlanItem planItem : planItems) {
            if (planItem.getId().equals(id)) {
                planItemToRemove = planItem;
                break;
            }
        }

        if (planItemToRemove != null) {
            planItems.remove(planItemToRemove);
        }
    }
}
