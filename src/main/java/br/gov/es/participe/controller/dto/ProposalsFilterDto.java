package br.gov.es.participe.controller.dto;

import java.util.List;

public class ProposalsFilterDto {
	private String itemName;
	private String regionName;
	private List<PlanItemDto> itens;
	private List<LocalityDto> localities;
	
	public ProposalsFilterDto() {
	}
	
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	public List<PlanItemDto> getItens() {
		return itens;
	}
	public void setItens(List<PlanItemDto> itens) {
		this.itens = itens;
	}
	public List<LocalityDto> getLocalities() {
		return localities;
	}
	public void setLocalities(List<LocalityDto> localities) {
		this.localities = localities;
	}
	
}
