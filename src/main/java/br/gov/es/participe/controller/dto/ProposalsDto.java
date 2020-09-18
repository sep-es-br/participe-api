package br.gov.es.participe.controller.dto;

import java.util.List;

public class ProposalsDto {

	private List<ProposalDto> proposals;
	private Integer totalPages;
	
	public List<ProposalDto> getProposals() {
		return proposals;
	}
	public void setProposals(List<ProposalDto> proposals) {
		this.proposals = proposals;
	}
	public Integer getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}
	
}
