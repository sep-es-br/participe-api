package br.gov.es.participe.controller.dto;

import java.util.List;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class ModerationResultDto {
	private Long commentId;
	private String status;
	private String text;
	private String time;
	private String type;
	private Long localityId;
	private String localityName;
	private Long planItemId;
	private String planItemName;
	private String citizenName;
	private Long moderatorId;
	private String moderatorName;
	private Boolean moderated;
	private String moderateTime;
	private String classification;
	private String localityType;
	private Boolean disableModerate;
	private List<ModerationStructure> commentStructure;
	private Long structureItemId;
	private String structureItemName;

	public ModerationResultDto() {
	}

	public ModerationResultDto(Long commentId, String status, String text, String time, String type, Long localityId,
			String localityName, Long planItemId, String planItemName, String citizenName, String moderatorName,
			Boolean moderated, String moderateTime, Long moderatorId, String classification, String localityType, 
			List<ModerationStructure> commentStructure, Long structureItemId, String structureItemName) {
		this.commentId = commentId;
		this.status = status;
		if (status == null) {
			this.status = "Pendente";
		}
		this.text = text;
		this.time = time;
		this.type = type;
		this.localityId = localityId;
		this.localityName = localityName;
		this.planItemId = planItemId;
		this.planItemName = planItemName;
		this.citizenName = citizenName;
		this.moderatorName = moderatorName;
		this.moderated = moderated;
		this.moderateTime = moderateTime;
		this.moderatorId = moderatorId;
		this.classification = classification;
		this.localityType = localityType;
		this.structureItemId = structureItemId;
		this.structureItemName = structureItemName;
	}

	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getLocalityId() {
		return localityId;
	}

	public void setLocalityId(Long localityId) {
		this.localityId = localityId;
	}

	public String getLocalityName() {
		return localityName;
	}

	public void setLocalityName(String localityName) {
		this.localityName = localityName;
	}

	public Long getPlanItemId() {
		return planItemId;
	}

	public void setPlanItemId(Long planItemId) {
		this.planItemId = planItemId;
	}

	public String getPlanItemName() {
		return planItemName;
	}

	public void setPlanItemName(String planItemName) {
		this.planItemName = planItemName;
	}

	public String getCitizenName() {
		return citizenName;
	}

	public void setCitizenName(String citizenName) {
		this.citizenName = citizenName;
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

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public String getLocalityType() {
		return localityType;
	}

	public void setLocalityType(String localityType) {
		this.localityType = localityType;
	}

	public List<ModerationStructure> getCommentStructure() {
		return commentStructure;
	}

	public void setCommentStructure(List<ModerationStructure> commentStructure) {
		this.commentStructure = commentStructure;
	}

	public String getModerateTime() {
		return moderateTime;
	}

	public void setModerateTime(String moderateTime) {
		this.moderateTime = moderateTime;
	}

	public Long getModeratorId() {
		return moderatorId;
	}

	public void setModeratorId(Long moderatorId) {
		this.moderatorId = moderatorId;
	}

	public Boolean getDisableModerate() {
		return disableModerate;
	}

	public void setDisableModerate(Boolean disableModerate) {
		this.disableModerate = disableModerate;
	}

	public Long getStructureItemId() {
		return structureItemId;
	}

	public void setStructureItemId(Long structureItemId) {
		this.structureItemId = structureItemId;
	}

	public String getStructureItemName() {
		return structureItemName;
	}

	public void setStructureItemName(String structureItemName) {
		this.structureItemName = structureItemName;
	}
}
