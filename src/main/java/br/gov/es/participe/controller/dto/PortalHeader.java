package br.gov.es.participe.controller.dto;

public class PortalHeader {

	private String image;
	private String title;
	private String subtitle;
	private boolean answerSurvey;
	private ResearchConfigurationDto research;

	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
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
	public boolean isAnswerSurvey() {
		return answerSurvey;
	}
	public void setAnswerSurvey(boolean answerSurvey) {
		this.answerSurvey = answerSurvey;
	}
	public ResearchConfigurationDto getResearch() {
		return research;
	}

	public void setResearch(ResearchConfigurationDto research) {
		this.research = research;
	}
}
