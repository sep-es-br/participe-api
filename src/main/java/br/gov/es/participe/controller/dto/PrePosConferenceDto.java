package br.gov.es.participe.controller.dto;

//import org.springframework.data.neo4j.annotation.*;

import java.util.*;

public class PrePosConferenceDto {
    private String text;
    private List<Integer> date;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Integer> getDate() {
        return date;
    }

    public void setDate(List<Integer> date) {
        this.date = date;
    }
}
