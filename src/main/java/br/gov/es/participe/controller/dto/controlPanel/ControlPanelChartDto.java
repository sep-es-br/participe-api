package br.gov.es.participe.controller.dto.controlPanel;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class ControlPanelChartDto {
    private Long id;
    private String description;
    private Long quantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

}
