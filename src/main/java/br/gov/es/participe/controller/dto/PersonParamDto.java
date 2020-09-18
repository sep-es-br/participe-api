package br.gov.es.participe.controller.dto;

public class PersonParamDto {

	private Long id;
    private String name;
    private String login;
    private String contactEmail;
    private String confirmEmail;
    private String cpf;
    private String telephone;
    private String password;
    private String confirmPassword;
    private String typeAuthentication;
    private SelfDeclarationParamDto selfDeclaration;
    private boolean resetPassword;
    private Boolean active;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	public String getConfirmEmail() {
		return confirmEmail;
	}
	public void setConfirmEmail(String confirmEmail) {
		this.confirmEmail = confirmEmail;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public String getTypeAuthentication() {
		return typeAuthentication;
	}
	public void setTypeAuthentication(String typeAuthentication) {
		this.typeAuthentication = typeAuthentication;
	}
	public SelfDeclarationParamDto getSelfDeclaration() {
		return selfDeclaration;
	}
	public void setSelfDeclaration(SelfDeclarationParamDto selfDeclaration) {
		this.selfDeclaration = selfDeclaration;
	}
	public boolean isResetPassword() {
		return resetPassword;
	}
	public void setResetPassword(boolean resetPassword) {
		this.resetPassword = resetPassword;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
}
