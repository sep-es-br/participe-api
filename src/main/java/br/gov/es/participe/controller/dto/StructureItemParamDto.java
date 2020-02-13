package br.gov.es.participe.controller.dto;

public class StructureItemParamDto {

    private Long id;
    private String name;
    private Boolean logo;
    private Boolean locality;
    private Boolean votes;
    private Boolean comments;
    private StructureParamDto structure;
    private StructureItemParamDto parent;

    public StructureItemParamDto() {
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

    public StructureParamDto getStructure() {
        return structure;
    }

    public void setStructure(StructureParamDto structure) {
        this.structure = structure;
    }

    public StructureItemParamDto getParent() {
        return parent;
    }

    public void setParent(StructureItemParamDto parent) {
        this.parent = parent;
    }

}
