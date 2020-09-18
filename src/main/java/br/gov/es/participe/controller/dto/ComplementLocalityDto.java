package br.gov.es.participe.controller.dto;

import java.util.List;

public class ComplementLocalityDto {

	private String nameType;
	private List<LocalityDto> localities;
	
	public ComplementLocalityDto() {
		//Contrutor criado por conveniencia.
	}
	
	public String getNameType() {
		return nameType;
	}
	public void setNameType(String nameType) {
		this.nameType = nameType;
	}
	public List<LocalityDto> getLocalities() {
		return localities;
	}
	public void setLocalities(List<LocalityDto> localities) {
		this.localities = localities;
	}
	
}
