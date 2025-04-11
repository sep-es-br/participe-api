package br.gov.es.participe.controller.dto;

public class ModerationParamDto {

	private Long id;
	private String text;
	private String type;
	private String from;
	private String status;
	private Long planItem;
	private Long locality;
	private String classification;
	private boolean duplicated;


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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getPlanItem() {
		return planItem;
	}

	public void setPlanItem(Long planItem) {
		this.planItem = planItem;
	}

	public Long getLocality() {
		return locality;
	}

	public void setLocality(Long locality) {
		this.locality = locality;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public boolean getDuplicated() {
		return duplicated;
	}

	public void setDuplicated(boolean duplicated) {
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

}
