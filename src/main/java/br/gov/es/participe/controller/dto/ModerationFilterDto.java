package br.gov.es.participe.controller.dto;

import java.util.Date;

public class ModerationFilterDto {
	private Long idModerator;
	private Long conferenceId;
	private String[] status;
	private String type;
	private String from;
	private String text;
	private Long[] localityIds;
	private Long[] planItemIds;
	private Long[] structureItemIds;
	private Date initialDate;
	private Date endDate;



	
	public Long getIdModerator() {
		return idModerator;
	}

	public void setIdModerator(Long idModerator) {
		this.idModerator = idModerator;
	}

	public Long getConferenceId() {
		return conferenceId;
	}

	public void setConferenceId(Long conferenceId) {
		this.conferenceId = conferenceId;
	}

	public String[] getStatus() {
		return status;
	}

	public void setStatus(String[] status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long[] getLocalityIds() {
		return localityIds;
	}

	public void setLocalityIds(Long[] localityIds) {
		this.localityIds = localityIds;
	}

	public Long[] getPlanItemIds() {
		return planItemIds;
	}

	public void setPlanItemIds(Long[] planItemIds) {
		this.planItemIds = planItemIds;
	}

	public Long[] getStructureItemIds() {
		return structureItemIds;
	}

	public void setStructureItemIds(Long[] structureItemIds) {
		this.structureItemIds = structureItemIds;
	}

	public Date getInitialDate() {
		return initialDate;
	}

	public void setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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

}
