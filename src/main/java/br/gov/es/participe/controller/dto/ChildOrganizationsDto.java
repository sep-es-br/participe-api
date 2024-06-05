package br.gov.es.participe.controller.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class ChildOrganizationsDto {


    @JsonProperty("guid")
    String guid;
    
    @JsonProperty("cnpj")
    String cnpj;
    
    @JsonProperty("filial")
    String filial;
    
    @JsonProperty("razaoSocial")
    String razaoSocial;
    
    @JsonProperty("nomeFantasia")
    String nomeFantasia;
    
    @JsonProperty("sigla")
    String sigla;
    
    @JsonProperty("contatos")
    List<Contact> contatos;
    
    @JsonProperty("emails")
    List<Email> emails;
    
    @JsonProperty("endereco")
    Address endereco;
    
    @JsonProperty("esfera")
    Esfera esfera;
    
    @JsonProperty("poder")
    Poder poder;
    
    @JsonProperty("organizacaoPai")
    OrganizacaoPai organizacaoPai;
    
    @JsonProperty("sites")
    List<Website> sites;
    
    @JsonProperty("tipoOrganizacao")
    TipoOrganizacao tipoOrganizacao;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getFilial() {
        return filial;
    }

    public void setFilial(String filial) {
        this.filial = filial;
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

    public List<Contact> getContatos() {
        return contatos;
    }

    public void setContatos(List<Contact> contatos) {
        this.contatos = contatos;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    public Address getEndereco() {
        return endereco;
    }

    public void setEndereco(Address endereco) {
        this.endereco = endereco;
    }

    public Esfera getEsfera() {
        return esfera;
    }

    public void setEsfera(Esfera esfera) {
        this.esfera = esfera;
    }

    public Poder getPoder() {
        return poder;
    }

    public void setPoder(Poder poder) {
        this.poder = poder;
    }

    public OrganizacaoPai getOrganizacaoPai() {
        return organizacaoPai;
    }

    public void setOrganizacaoPai(OrganizacaoPai organizacaoPai) {
        this.organizacaoPai = organizacaoPai;
    }

    public List<Website> getSites() {
        return sites;
    }

    public void setSites(List<Website> sites) {
        this.sites = sites;
    }

    public TipoOrganizacao getTipoOrganizacao() {
        return tipoOrganizacao;
    }

    public void setTipoOrganizacao(TipoOrganizacao tipoOrganizacao) {
        this.tipoOrganizacao = tipoOrganizacao;
    }

    
}

class Contact {
    @JsonProperty("telefone")
    String telefone;
    
    @JsonProperty("tipoContato")
    TipoContato tipoContato;

    
}

class TipoContato {
    @JsonProperty("descricao")
    String descricao;

    
}

class Email {
    @JsonProperty("endereco")
    String endereco;

    
}

class Address {
    @JsonProperty("logradouro")
    String logradouro;
    
    @JsonProperty("numero")
    String numero;
    
    @JsonProperty("complemento")
    String complemento;
    
    @JsonProperty("bairro")
    String bairro;
    
    @JsonProperty("cep")
    String cep;
    
    @JsonProperty("municipio")
    Municipio municipio;

    
}

class Municipio {
    @JsonProperty("codigoIbge")
    int codigoIbge;
    
    @JsonProperty("nome")
    String nome;
    
    @JsonProperty("uf")
    String uf;
    
    @JsonProperty("guid")
    String guid;

    
}

class Esfera {
    @JsonProperty("descricao")
    String descricao;

    
}

class Poder {
    @JsonProperty("descricao")
    String descricao;

    
}

class OrganizacaoPai {
    @JsonProperty("guid")
    String guid;
    
    @JsonProperty("razaoSocial")
    String razaoSocial;
    
    @JsonProperty("sigla")
    String sigla;

    
}

class Website {
    @JsonProperty("url")
    String url;

    
}

class TipoOrganizacao {
    @JsonProperty("descricao")
    String descricao;

    
}
    


