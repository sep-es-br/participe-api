package br.gov.es.participe.model;

import br.gov.es.participe.controller.dto.*;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Node;
import java.io.*;

@Node
public class Topic extends Entity implements Serializable {
    private Integer step;
    private String title;
    private String text;

    @Relationship(type = "GUIDES_HOW_TO_PARTICIPATE_IN", direction = Relationship.Direction.OUTGOING)
    private Conference conferenceTopic;

    public Topic () {}

    public Topic(HowItWorkStepDto work) {
        this.setId(work.getId());
        this.step = work.getOrder();
        this.title = work.getTitle();
        this.text = work.getText();
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Topic update(HowItWorkStepDto work) {
        this.setId(work.getId());
        this.step = work.getOrder();
        this.title = work.getTitle();
        this.text = work.getText();
        return this;
    }

    public Conference getConferenceTopic() {
        return conferenceTopic;
    }

    public Topic setConferenceTopic(Conference conferenceTopic) {
        this.conferenceTopic = conferenceTopic;
        return this;
    }
}
