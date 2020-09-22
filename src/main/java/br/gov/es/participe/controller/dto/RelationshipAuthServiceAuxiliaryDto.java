package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.Person;

public class RelationshipAuthServiceAuxiliaryDto {

    private Person person;
    private String password;
    private String server;
    private String serverId;
    private Long conferenceId;
    private Boolean resetPassword;
    private Boolean makeLogin;
    private String typeAuthentication;

    /* no-arg constructor */
    public RelationshipAuthServiceAuxiliaryDto() {
        // no-arg constructor
    }

    public static class RelationshipAuthServiceAuxiliaryDtoBuilder {
        private Person person;
        private String password;
        private String server;
        private String serverId;
        private Long conferenceId;
        private Boolean resetPassword;
        private Boolean makeLogin;
        private String typeAuthentication;

        public RelationshipAuthServiceAuxiliaryDtoBuilder(Person person){
            this.person = person;
        }

        public RelationshipAuthServiceAuxiliaryDtoBuilder password(String password){
            this.password = password;
            return this;
        }

        public RelationshipAuthServiceAuxiliaryDtoBuilder server(String server){
            this.server = server;
            return this;
        }

        public RelationshipAuthServiceAuxiliaryDtoBuilder serverId(String serverId){
            this.serverId = serverId;
            return this;
        }

        public RelationshipAuthServiceAuxiliaryDtoBuilder conferenceId(Long conferenceId){
            this.conferenceId = conferenceId;
            return this;
        }

        public RelationshipAuthServiceAuxiliaryDtoBuilder resetPassword(Boolean resetPassword){
            this.resetPassword = resetPassword;
            return this;
        }

        public RelationshipAuthServiceAuxiliaryDtoBuilder makeLogin(Boolean makeLogin){
            this.makeLogin = makeLogin;
            return this;
        }

        public RelationshipAuthServiceAuxiliaryDtoBuilder typeAuthentication(String typeAuthentication){
            this.typeAuthentication = typeAuthentication;
            return this;
        }

        public RelationshipAuthServiceAuxiliaryDto build(){
            RelationshipAuthServiceAuxiliaryDto builtClass = new RelationshipAuthServiceAuxiliaryDto();
            builtClass.setPerson(person);
            builtClass.setPassword(password);
            builtClass.setServer(server);
            builtClass.setServerId(serverId);
            builtClass.setConferenceId(conferenceId);
            builtClass.setResetPassword(resetPassword);
            builtClass.setMakeLogin(makeLogin);
            builtClass.setTypeAuthentication(typeAuthentication);
            return builtClass;
        }
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public Long getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(Long conferenceId) {
        this.conferenceId = conferenceId;
    }

    public Boolean getResetPassword() {
        return resetPassword;
    }

    public void setResetPassword(Boolean resetPassword) {
        this.resetPassword = resetPassword;
    }

    public Boolean getMakeLogin() {
        return makeLogin;
    }

    public void setMakeLogin(Boolean makeLogin) {
        this.makeLogin = makeLogin;
    }

    public String getTypeAuthentication() {
        return typeAuthentication;
    }

    public void setTypeAuthentication(String typeAuthentication) {
        this.typeAuthentication = typeAuthentication;
    }
}
