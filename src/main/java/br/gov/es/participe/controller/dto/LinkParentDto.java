package br.gov.es.participe.controller.dto;

public class LinkParentDto {

	private Long idParent;
	private String textLink;
	
	public LinkParentDto() {
	}

	public Long getIdParent() {
		return idParent;
	}

	public void setIdParent(Long idParent) {
		this.idParent = idParent;
	}

	public String getTextLink() {
		return textLink;
	}

	public void setTextLink(String textLink) {
		this.textLink = textLink;
	}
}
