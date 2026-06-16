/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package br.gov.es.participe.controller.dto;

/**
 *
 * @author gean.carneiro
 */
public class PersonListItemsResponse {
    private String sub;
    private String name;
    private String role;
    private String lotacao;

    public PersonListItemsResponse() {
    }

    public PersonListItemsResponse(String sub, String name, String role, String lotacao) {
        this.sub = sub;
        this.name = name;
        this.role = role;
        this.lotacao = lotacao;
    }
    
    

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLotacao() {
        return lotacao;
    }

    public void setLotacao(String lotacao) {
        this.lotacao = lotacao;
    }
    
    
}
