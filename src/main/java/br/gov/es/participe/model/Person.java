package br.gov.es.participe.model;

import br.gov.es.participe.controller.dto.PersonDto;
import br.gov.es.participe.controller.dto.PersonParamDto;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@NodeEntity
public class Person extends Entity implements UserDetails {

  private String name;

  private String contactEmail;

  private String cpf;

  private Boolean status;

  private String telephone;

  private String accessToken;

  private Boolean active;

  @Relationship(type = "IS_AUTHENTICATED_BY")
  private Set<AuthService> authServices;

  @Relationship(type = "MADE")
  private Set<SelfDeclaration> selfDeclaretions;

  @Relationship(type = "LIKED_BY", direction = Relationship.INCOMING)
  private Set<Comment> comments;

  @Relationship(type = "MADE_BY", direction = Relationship.INCOMING)
  private Set<Attend> attends;

  @Relationship(type = "MODERATED_BY")
  private Set<Comment> moderatedComments;

  @Relationship(type = "IS_RECEPTIONIST_OF")
  private Set<Meeting> welcomesMeetings;

  @Relationship(type = "CHECKED_IN_AT")
  private Set<Meeting> checkedInMeetings;

  private Set<String> roles;

  public Person() {
  }

  public Person(PersonDto person) {
    setId(person.getId());
    this.name = person.getName();
    this.cpf = person.getCpf();
    this.telephone = person.getTelephone();

    if (person.getCpf() != null) {
      this.contactEmail = person.getCpf() + "@cpf";
    } else {
      this.contactEmail = person.getContactEmail();
    }
  }

  public Person(PersonParamDto person) {
    setId(person.getId());
    this.name = person.getName().trim().replaceAll(" +", " ");
    this.cpf = person.getCpf();
    this.telephone = person.getTelephone();

    if (person.getCpf() != null) {
      this.contactEmail = person.getCpf() + "@cpf";
    } else {
      this.contactEmail = person.getContactEmail();
    }
  }

  public Person(PersonParamDto person, Boolean isTypeAuthenticationCpf) {
    setId(person.getId());
    this.name = person.getName().trim().replaceAll(" +", " ");
    this.cpf = person.getCpf();
    this.telephone = person.getTelephone();

    if (isTypeAuthenticationCpf) {
      this.contactEmail = person.getCpf() + "@cpf";
    } else {
      this.contactEmail = person.getContactEmail();
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public String getCpf() {
    return cpf;
  }

  public void setCpf(String cpf) {
    this.cpf = cpf;
  }

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public Set<AuthService> getAuthServices() {
    if (authServices == null) {
      return Collections.emptySet();
    }

    return Collections.unmodifiableSet(authServices);
  }

  public void setAuthServices(Set<AuthService> authServices) {
    this.authServices = authServices;
  }

  public void addAuthService(AuthService authService) {
    if (authServices == null) {
      authServices = new HashSet<>();
    }

    authServices.add(authService);
  }

  public void addSelfDeclaration(final SelfDeclaration selfDeclaration) {
    if (this.selfDeclaretions == null) {
      this.selfDeclaretions = new HashSet<>();
    }

    this.selfDeclaretions.add(selfDeclaration);
  }

  public Set<SelfDeclaration> getSelfDeclaretions() {
    return selfDeclaretions;
  }

  public void setSelfDeclaretions(Set<SelfDeclaration> selfDeclaretions) {
    this.selfDeclaretions = selfDeclaretions;
  }

  public Set<Comment> getComments() {
    return comments;
  }

  public void setComments(Set<Comment> comments) {
    this.comments = comments;
  }

  public Set<Attend> getAttends() {
    return attends;
  }

  public void setAttends(Set<Attend> attends) {
    this.attends = attends;
  }

  public Set<Comment> getModeratedComments() {
    return moderatedComments;
  }

  public void setModeratedComments(Set<Comment> moderatedComments) {
    this.moderatedComments = moderatedComments;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public Set<Meeting> getWelcomesMeetings() {
    return welcomesMeetings;
  }

  public void setWelcomesMeetings(Set<Meeting> welcomesMeetings) {
    this.welcomesMeetings = welcomesMeetings;
  }

  public Set<Meeting> getCheckedInMeetings() {
    return checkedInMeetings;
  }

  public void setCheckedInMeetings(Set<Meeting> checkedInMeetings) {
    this.checkedInMeetings = checkedInMeetings;
  }

  public Set<String> getRoles() {
    if (roles == null) {
      roles = new HashSet<>();
    }
    return roles;
  }

  public void setRoles(Set<String> roles) {
    this.roles = roles;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Set<String> personRoles = getRoles();
    List<String> listRoles = new ArrayList<>();
    if (personRoles != null && !personRoles.isEmpty()) {
      personRoles.forEach(r -> listRoles.add("ROLE_" + r));
    }
    return listRoles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
  }

  @Override
  public String getPassword() {
    return "";
  }

  @Override
  public String getUsername() {
    return getName();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
