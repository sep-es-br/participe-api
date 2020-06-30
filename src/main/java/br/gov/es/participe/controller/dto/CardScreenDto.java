package br.gov.es.participe.controller.dto;

import java.util.List;

public class CardScreenDto {

	private String regionalizable;
	private String title;
	private String subtitle;
	private List<LocalityDto> localities;
	
	public String getRegionalizable() {
		return regionalizable;
	}
	public void setRegionalizable(String regionalizable) {
		this.regionalizable = regionalizable;
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
	public List<LocalityDto> getLocalities() {
		return localities;
	}
	public void setLocalities(List<LocalityDto> localities) {
		this.localities = localities;
	}
}
