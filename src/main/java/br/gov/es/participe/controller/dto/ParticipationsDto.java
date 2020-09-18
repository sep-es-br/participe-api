package br.gov.es.participe.controller.dto;

import java.util.List;

public class ParticipationsDto {
	private List<ParticipationDto> participations;
	private Integer totalPages;

	public List<ParticipationDto> getParticipations() {
		return participations;
	}

	public void setParticipations(List<ParticipationDto> participations) {
		this.participations = participations;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

}
