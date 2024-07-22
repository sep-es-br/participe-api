package br.gov.es.participe.controller.dto;

import java.text.Normalizer;

import org.apache.catalina.valves.rewrite.InternalRewriteMap.LowerCase;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PublicAgentDto {

    @JsonAlias({"sub", "Sub"})
    private String sub;

    @JsonProperty("Nome")
    private String name;

    @JsonAlias({"email", "Email"})
    private String email;

    @JsonProperty("SubDescontinuado")
    private String discontinuedSub;

    @JsonProperty("Apelido")
    private String nickName;

    private String cleanName;

    
    public String getCleanName() {
        return cleanName;
    }

    public void setCleanName(String cleanName) {

        this.cleanName = cleanName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setCleanName(cleanSimilarToApoc(name));
    }

    public String getDiscontinuedSub() {
        return discontinuedSub;
    }

    public void setDiscontinuedSub(String discontinuedSub) {
        this.discontinuedSub = discontinuedSub;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static String cleanSimilarToApoc(String input) {
        
        String semAcentos = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");

        String semEspeciais = semAcentos.replaceAll("[^a-zA-Z0-9\\s]", "");

        return semEspeciais.toLowerCase();
    }
    
}
