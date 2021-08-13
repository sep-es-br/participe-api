package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.*;

public class HowItWorkStepDto {
    private Long id;
    private Integer order;
    private String title;
    private String text;

    public HowItWorkStepDto(){}

    public HowItWorkStepDto(Topic topic) {
        setId(topic.getId());
        setOrder(topic.getStep());
        setTitle(topic.getTitle());
        setText(topic.getText());
    }

    public HowItWorkStepDto(Long id, Integer order, String title, String text) {
        setId(id);
        setOrder(order);
        setTitle(title);
        setText(text);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title != null) {
            this.title = title.trim();
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text != null) {
            this.text = text.trim();
        }
    }
}
