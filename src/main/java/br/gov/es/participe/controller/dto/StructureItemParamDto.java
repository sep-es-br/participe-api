package br.gov.es.participe.controller.dto;

public class StructureItemParamDto {

    private Long id;
    private Boolean comments;
    private String name;
    private Boolean votes;
    private Boolean locality;
    private StructureParamDto structure;
    private Boolean logo;
    private StructureItemParamDto parent;
    private String title;
    private String subtitle;
    private String link;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Boolean getLogo() {
        return logo;
    }

    public String getName() {
        return name;
    }

    public void setLogo(Boolean logo) {
        this.logo = logo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVotes(Boolean votes) {
        this.votes = votes;
    }

    public Boolean getVotes() {
        return votes;
    }

    public void setLocality(Boolean locality) {
        this.locality = locality;
    }

    public Boolean getLocality() {
        return locality;
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

}
