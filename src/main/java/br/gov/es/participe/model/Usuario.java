package br.gov.es.participe.model;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Transient;

@NodeEntity
public class Usuario extends Entity {

    private String email;
    private String senha;

    @Relationship(type = "USUARIO_PERFIL")
    private Set<Perfil> perfis = new HashSet<>();

    @Transient
    private Perfil perfilSelecionado;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Set<Perfil> getPerfis() {
        return perfis;
    }

    public void setPerfis(Set<Perfil> perfis) {
        this.perfis = perfis;
    }

    public Perfil getPerfilSelecionado() {
        return perfilSelecionado;
    }

    public void setPerfilSelecionado(Perfil perfilSelecionado) {
        this.perfilSelecionado = perfilSelecionado;
    }
}
