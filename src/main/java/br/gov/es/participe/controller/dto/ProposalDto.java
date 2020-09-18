package br.gov.es.participe.controller.dto;

import java.util.List;

public class ProposalDto {

	private Long commentid;
	private Boolean isLiked;
	private String comment;
	private String personName;
	private String localityPerson;
	private String localityName;
	private String localityTypeName;
	
	private Integer likes;
	
	private List<PlanItemDto> planItens;
	
	public Long getCommentid() {
		return commentid;
	}

	public void setCommentid(Long commentid) {
		this.commentid = commentid;
	}

	public Boolean getIsLiked() {
		return isLiked;
	}

	public void setIsLiked(Boolean isLiked) {
		this.isLiked = isLiked;
	}

	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getPersonName() {
		return personName;
	}
	public void setPersonName(String personName) {
		this.personName = personName;
	}
	public String getLocalityPerson() {
		return localityPerson;
	}
	public void setLocalityPerson(String localityPerson) {
		this.localityPerson = localityPerson;
	}
	public String getLocalityName() {
		return this.localityName;
	}

	public void setLocalityName(String localityName) {
		this.localityName = localityName;
	}

	public String getLocalityTypeName() {
		return this.localityTypeName;
	}

	public void setLocalityTypeName(String localityTypeName) {
		this.localityTypeName = localityTypeName;
	}

	public Integer getLikes() {
		return likes;
	}

	public void setLikes(Integer likes) {
		this.likes = likes;
	}

	public List<PlanItemDto> getPlanItens() {
		return planItens;
	}
	public void setPlanItens(List<PlanItemDto> planItem) {
		this.planItens = planItem;
	}
	
	
}
