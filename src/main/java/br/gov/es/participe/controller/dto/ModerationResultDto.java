package br.gov.es.participe.controller.dto;

import java.util.List;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class ModerationResultDto {
	private Long conferenceId;
	private Long commentId;
	private String status;
	private String text;
	private String time;
	private String type;
	private String from;
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
	private Long areaEstrategicaId;
	private String nameAreaEstrategica;
	private Boolean duplicated;



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

	public Long getAreaEstrategicaId() {
		return areaEstrategicaId;
	}

	public void setAreaEstrategicaId(Long areaEstrategicaId) {
		this.areaEstrategicaId = areaEstrategicaId;
	}

	public String getNameAreaEstrategica() {
		return nameAreaEstrategica;
	}

	public void setNameAreaEstrategica(String nameAreaEstrategica) {
		this.nameAreaEstrategica = nameAreaEstrategica;
	}

	public Long getConferenceId() {
		return conferenceId;
	}

	public void setConferenceId(Long conferenceId) {
		this.conferenceId = conferenceId;
	}

	public Boolean getDuplicated() {
		return duplicated;
	}

	public void setDuplicated(Boolean duplicated) {
		this.duplicated = duplicated;
	}

    /**
     * @return String return the from
     */
    public String getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * @return Boolean return the moderated
     */
    public Boolean isModerated() {
        return moderated;
    }

    /**
     * @return Boolean return the disableModerate
     */
    public Boolean isDisableModerate() {
        return disableModerate;
    }

}
