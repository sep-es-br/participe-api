package br.gov.es.participe.controller.dto;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class LoginAccessDto {
    private String loginName;
    private Long acesses;

    public LoginAccessDto(String loginName, Long acesses) {
        this.loginName = loginName;
        this.acesses = acesses;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Long getAcesses() {
        return acesses;
    }

    public void setAcesses(Long acesses) {
        this.acesses = acesses;
    }
}
