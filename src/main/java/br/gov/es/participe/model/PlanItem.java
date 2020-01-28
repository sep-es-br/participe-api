package br.gov.es.participe.model;

import br.gov.es.participe.controller.dto.PlanItemDto;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class PlanItem extends Entity {

    private String name;

    private String description;

    @Relationship(type = "BELONGS_TO")
    private Plan plan;

    @Relationship(type = "BELONGS_TO")
    private StructureItem structureItem;

    @Relationship(type = "BELONGS_TO")
    private File file;

    @Relationship(type = "APPLIES_TO", direction = Relationship.INCOMING)
    private Set<Locality> localities;

    @Relationship(type = "BELONGS_TO")
    private PlanItem parent;

    @Relationship(type = "BELONGS_TO", direction = Relationship.INCOMING)
    private Set<PlanItem> children;

    private transient Set<Long> localitiesIds;

    public PlanItem() {
    }

    public PlanItem(PlanItemDto planItemDto) {
        if (planItemDto == null) return;

        setId(planItemDto.getId());
        this.name = planItemDto.getName();
        this.description = planItemDto.getDescription();
        this.localitiesIds = planItemDto.getLocalitiesIds();

        if (planItemDto.getStructureItem() != null) {
            this.structureItem = new StructureItem(planItemDto.getStructureItem());
        }
        if(planItemDto.getPlan() != null) {
            this.plan = new Plan(planItemDto.getPlan());
        }
        if (planItemDto.getFile() != null) {
            this.file = new File(planItemDto.getFile());
        }

        if (planItemDto.getLocalities() != null && !planItemDto.getLocalities().isEmpty()) {
            this.localities = new HashSet<>();
            planItemDto.getLocalities().forEach(locality -> this.localities.add(new Locality(locality)));
        }

        if (planItemDto.getParent() != null) {
            this.parent = new PlanItem(planItemDto.getParent());
        }

        if (planItemDto.getChildren() != null && !planItemDto.getChildren().isEmpty()) {
            this.children = new HashSet<>();
            planItemDto.getChildren().forEach(child -> this.children.add(new PlanItem(child)));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public StructureItem getStructureItem() {
        return structureItem;
    }

    public void setStructureItem(StructureItem structureItem) {
        this.structureItem = structureItem;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Set<Locality> getLocalities() {
        return localities;
    }

    public void setLocalities(Set<Locality> localities) {
        this.localities = localities;
    }

    public PlanItem getParent() {
        return parent;
    }

    public void setParent(PlanItem parent) {
        this.parent = parent;
    }

    public Set<PlanItem> getChildren() {
        return children;
    }

    public void setChildren(Set<PlanItem> children) {
        this.children = children;
    }

    public Set<Long> getLocalitiesIds() {
        return localitiesIds;
    }

    public void setLocalitiesIds(Set<Long> localitiesIds) {
        this.localitiesIds = localitiesIds;
    }
}