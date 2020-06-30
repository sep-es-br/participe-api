package br.gov.es.participe.controller.dto;

import java.util.List;

public class BodyParticipationDto {

	String image;
	
	List<PlanItemDto> itens;
	StructureItemDto structureitem;
	
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public List<PlanItemDto> getItens() {
		return itens;
	}
	public void setItens(List<PlanItemDto> itens) {
		this.itens = itens;
	}
	public StructureItemDto getStructureitem() {
		return structureitem;
	}
	public void setStructureitem(StructureItemDto structureitem) {
		this.structureitem = structureitem;
	}
}
