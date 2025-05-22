/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.util.dto.acessoCidadao;

/**
 *
 * @author desenvolvimento
 */
public class AcOrganizationInfoDto {
    
    private String guid;
    private String razaoSocial;
    private String nomeFantasia;
    private String sigla;
    private String guidOrganizacaoPai;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getGuidOrganizacaoPai() {
        return guidOrganizacaoPai;
    }

    public void setGuidOrganizacaoPai(String guidOrganizacaoPai) {
        this.guidOrganizacaoPai = guidOrganizacaoPai;
    }
            
    
    
}
