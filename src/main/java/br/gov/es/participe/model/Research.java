package br.gov.es.participe.model;

import br.gov.es.participe.util.domain.DisplayModeType;
import br.gov.es.participe.util.domain.ResearchDisplayStatusType;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.DateString;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import br.gov.es.participe.util.interfaces.Transient;

@Node
public class Research extends Entity implements Serializable {

    @DateString
    private Date endDate;

    @DateString
    private Date beginDate;

    private String link;

    private String estimatedTime;

    private String displayMode;

    @Relationship(type = "APPLIES_TO")
    private Conference conference;

    @Transient
    private DisplayModeType modeType;

    @Transient
    private ResearchDisplayStatusType statusType;

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(String displayMode) {
        this.displayMode = displayMode;
    }

    public DisplayModeType getModeType() {
        if (modeType == null && this.displayMode != null) {
            modeType = Arrays.stream(DisplayModeType.values()).filter(f -> this.displayMode.contains(f.name())).findFirst().orElse(null);
        }
        return modeType;
    }

    public ResearchDisplayStatusType getStatusType() {
        if (statusType == null && this.displayMode != null) {
            statusType = Arrays.stream(ResearchDisplayStatusType.values()).filter(f -> this.displayMode.contains(f.name())).findFirst().orElse(null);
        }
        return statusType;
    }

    public void setStatusType(ResearchDisplayStatusType statusType) {
        this.getModeType();
        this.statusType = statusType;
        this.updateDisplayMode();
    }

    private void updateDisplayMode() {
        if (modeType != null && statusType != null) {
            this.displayMode = String.format("%s %s", modeType.name(), statusType.name());
        }
    }

    public Conference getConference() {
        return conference;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }
}
