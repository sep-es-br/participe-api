package br.gov.es.participe.controller.dto;

import br.gov.es.participe.model.Plan;

import java.util.ArrayList;
import java.util.List;

public class PlanDto {

  private Long id;
  private String name;
  private StructureDto structure;
  private DomainDto domain;
  private LocalityTypeDto localitytype;
  private List<PlanItemDto> items;

  public PlanDto() {
  }

  public PlanDto(Plan plan, boolean loadItems) {
    if (plan == null) return;

    this.id = plan.getId();
    this.name = plan.getName();
    this.structure = new StructureDto(plan.getStructure(), true);
    this.domain = new DomainDto(plan.getDomain(), false);
    this.localitytype = new LocalityTypeDto(plan.getlocalitytype());

    if (loadItems && plan.getItems() != null && !plan.getItems().isEmpty()) {
      items = new ArrayList<>();
      plan.getItems().forEach(item -> items.add(new PlanItemDto(item, plan, true)));
    }
  }

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

  public StructureDto getStructure() {
    return structure;
  }

  public void setStructure(StructureDto structure) {
    this.structure = structure;
  }

  public DomainDto getDomain() {
    return domain;
  }

  public void setDomain(DomainDto domain) {
    this.domain = domain;
  }

  public LocalityTypeDto getlocalitytype() {
    return localitytype;
  }

  public void setlocalitytype(LocalityTypeDto type) {
    this.localitytype = type;
  }

  public List<PlanItemDto> getItems() {
    return items;
  }

  public void setItems(List<PlanItemDto> items) {
    this.items = items;
  }
}
