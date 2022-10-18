package br.gov.es.participe.controller.dto;

import java.util.List;

import br.gov.es.participe.util.interfaces.QueryResult;

@QueryResult
public class LeanPlanItemResultDto {
    Long conferenceId;
    String conferenceDescription;
    Long structureItemId;
    String structureItemName;
    List<LeanPlanItem> planItems;

    public class LeanPlanItem {
        Long planItemId;
        String planItemName;

        public LeanPlanItem(Long planItemId, String planItemName) {
            this.planItemId = planItemId;
            this.planItemName = planItemName;
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
    }

    public LeanPlanItemResultDto() {
    }

    public LeanPlanItemResultDto(Long conferenceId, String conferenceDescription, Long structureItemId, String structureItemName, List<LeanPlanItem> planItems) {
        this.conferenceId = conferenceId;
        this.conferenceDescription = conferenceDescription;
        this.structureItemId = structureItemId;
        this.structureItemName = structureItemName;
        this.planItems = planItems;
    }

    public Long getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(Long conferenceId) {
        this.conferenceId = conferenceId;
    }

    public String getConferenceDescription() {
        return conferenceDescription;
    }

    public void setConferenceDescription(String conferenceDescription) {
        this.conferenceDescription = conferenceDescription;
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

    public List<LeanPlanItem> getPlanItems() {
        return planItems;
    }

    public void setPlanItems(List<LeanPlanItem> planItems) {
        this.planItems = planItems;
    }
}
