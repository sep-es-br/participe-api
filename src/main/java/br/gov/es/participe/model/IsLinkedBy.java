package br.gov.es.participe.model;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.io.Serializable;

@RelationshipEntity(type = "IS_LINKED_BY")
public class IsLinkedBy extends Entity implements Serializable {
  private String label;

  @StartNode
  private ExternalContent externalContent;

  @EndNode
  private Conference conference;

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public ExternalContent getExternalContent() {
    return externalContent;
  }

  public void setExternalContent(ExternalContent externalContent) {
    this.externalContent = externalContent;
  }

  public Conference getConference() {
    return conference;
  }

  public void setConference(Conference conference) {
    this.conference = conference;
  }
}
