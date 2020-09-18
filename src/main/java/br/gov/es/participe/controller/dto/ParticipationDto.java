package br.gov.es.participe.controller.dto;

import java.util.Date;
import java.util.List;

import org.springframework.data.neo4j.annotation.QueryResult;

import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.LocalityType;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.PlanItem;

@QueryResult
public class ParticipationDto {
	private Long id;
	private String text;
	private String status;
	private PlanItem planItem;
	private Locality locality;
	private LocalityType localityType;
	private List<PlanItemDto> planItems;
	private List<Person> personLiked;
	private LocalityDto localityDto;
	private Integer qtdLiked;
	private boolean highlight;
	private String moderatorName;
	private Boolean moderated;
	private String moderateTime;
	private String classification;
	private Date time;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LocalityDto getLocalityDto() {
		return localityDto;
	}

	public void setLocalityDto(LocalityDto localityDto) {
		this.localityDto = localityDto;
	}

	public boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	public List<PlanItemDto> getPlanItems() {
		return planItems;
	}

	public void setPlanItems(List<PlanItemDto> planItems) {
		this.planItems = planItems;
	}

	public Integer getQtdLiked() {
		return qtdLiked;
	}

	public void setQtdLiked(Integer qtdLiked) {
		this.qtdLiked = qtdLiked;
	}

	public PlanItem getPlanItem() {
		return planItem;
	}

	public void setPlanItem(PlanItem planItem) {
		this.planItem = planItem;
	}

	public List<Person> getPersonLiked() {
		return personLiked;
	}

	public void setPersonLiked(List<Person> personLiked) {
		this.personLiked = personLiked;
	}

	public Locality getLocality() {
		return locality;
	}

	public void setLocality(Locality locality) {
		this.locality = locality;
	}

	public LocalityType getLocalityType() {
		return localityType;
	}

	public void setLocalityType(LocalityType localityType) {
		this.localityType = localityType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getModeratorName() {
		return moderatorName;
	}

	public void setModeratorName(String moderatorName) {
		this.moderatorName = moderatorName;
	}

	public Boolean getModerated() {
		return moderated;
	}

	public void setModerated(Boolean moderated) {
		this.moderated = moderated;
	}

	public String getModerateTime() {
		return moderateTime;
	}

	public void setModerateTime(String moderateTime) {
		this.moderateTime = moderateTime;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

}
