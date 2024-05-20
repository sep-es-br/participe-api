package br.gov.es.participe.controller.dto;

public class EvaluatorDto {

    private String guid;
    
    private String name;

    public EvaluatorDto(){

    }

    public EvaluatorDto(String guid, String name){
        this.guid = guid;
        this.name = name;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
}


