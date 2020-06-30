package br.gov.es.participe.controller.dto;

public class AuthenticationScreenDto {
	
	private String localityType;
	private String titleAuthentication;
	private String subtitleAuthentication;
	
	private FileDto fileAuthentication;
	
	private Integer proposal;
	private Integer highlights;
	private Integer participations;
	private Integer numberOfLocalities;
	
	public AuthenticationScreenDto () {
	}
	
	public String getLocalityType() {
		return localityType;
	}

	public void setLocalityType(String LocalityType) {
		this.localityType = LocalityType;
	}

	public String getTitleAuthentication() {
		return titleAuthentication;
	}
	public void setTitleAuthentication(String titleAuthentication) {
		this.titleAuthentication = titleAuthentication;
	}
	public String getSubtitleAuthentication() {
		return subtitleAuthentication;
	}
	public void setSubtitleAuthentication(String subtitleAuthentication) {
		this.subtitleAuthentication = subtitleAuthentication;
	}
	public FileDto getFileAuthentication() {
		return fileAuthentication;
	}
	public void setFileAuthentication(FileDto fileAuthentication) {
		this.fileAuthentication = fileAuthentication;
	}
	public Integer getProposal() {
		return proposal;
	}
	public void setProposal(Integer proposal) {
		this.proposal = proposal;
	}
	public Integer getHighlights() {
		return highlights;
	}
	public void setHighlights(Integer highlights) {
		this.highlights = highlights;
	}
	public Integer getParticipations() {
		return participations;
	}
	public void setParticipations(Integer participations) {
		this.participations = participations;
	}
	public Integer getNumberOfLocalities() {
		return numberOfLocalities;
	}
	public void setNumberOfLocalities(Integer numberOfLocalities) {
		this.numberOfLocalities = numberOfLocalities;
	}
	
}
