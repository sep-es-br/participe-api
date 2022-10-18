package br.gov.es.participe.controller.dto.controlPanel;

import br.gov.es.participe.util.interfaces.QueryResult;

@QueryResult
public class MicroregionChartQueryDto {
    private Long id;
    private String name;
    private Long quantityComment;
    private Long quantityHighlight;
    private Long quantityParticipation;
    private Long idMeeting;
    private Long idPlanItem;
    private String planItemName;
    private Long idStructureItem;
    private Long idPlanItemParent;
    private String origin;
    private String latitudeLongitude;

    public Long getIdPlanItemParent() {
        return idPlanItemParent;
    }

    public void setIdPlanItemParent(Long idPlanItemParent) {
        this.idPlanItemParent = idPlanItemParent;
    }

    public String getPlanItemName() {
        return planItemName;
    }

    public String getLatitudeLongitude() {
        return latitudeLongitude;
    }

    public void setLatitudeLongitude(String latitudeLongitude) {
        this.latitudeLongitude = latitudeLongitude;
    }

    public void setPlanItemName(String planItemName) {
        this.planItemName = planItemName;
    }

    public Long getId() {
        return id;
    }

    public Long getQuantityParticipation() {
        return quantityParticipation;
    }

    public void setQuantityParticipation(Long quantityParticipation) {
        this.quantityParticipation = quantityParticipation;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Long getIdStructureItem() {
        return idStructureItem;
    }

    public void setIdStructureItem(Long idStructureItem) {
        this.idStructureItem = idStructureItem;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getQuantityComment() {
        return quantityComment;
    }

    public void setQuantityComment(Long quantityComment) {
        this.quantityComment = quantityComment;
    }

    public Long getQuantityHighlight() {
        return quantityHighlight;
    }

    public void setQuantityHighlight(Long quantityHighlight) {
        this.quantityHighlight = quantityHighlight;
    }

    public Long getIdMeeting() {
        return idMeeting;
    }

    public void setIdMeeting(Long idMeeting) {
        this.idMeeting = idMeeting;
    }

    public Long getIdPlanItem() {
        return idPlanItem;
    }

    public void setIdPlanItem(Long idPlanItem) {
        this.idPlanItem = idPlanItem;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
