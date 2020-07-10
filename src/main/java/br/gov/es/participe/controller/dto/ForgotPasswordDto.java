package br.gov.es.participe.controller.dto;

public class ForgotPasswordDto {
	
	private String email;
	private Long conference;

	public Long getConference() {
		return conference;
	}
	public void setConference(Long conference) {
		this.conference = conference;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
